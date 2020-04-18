package org.edu.mail.api.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.edu.mail.api.domain.Account;
import org.edu.mail.api.service.AccountService;
import org.edu.mail.api.util.ResponseResult;
import org.edu.mail.api.util.TokenUtil;
import org.edu.mail.api.util.UAccountFormat;
import org.edu.mail.usage.*;
import org.edu.mail.usage.search.LuceneSearcher;
import org.edu.mail.usage.search.UMailFileReader;
import org.edu.mail.usage.search.UMailFileWriter;
import org.edu.mail.usage.util.UMailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import static org.edu.mail.api.util.ResponseCode.*;
import static org.edu.mail.api.util.ResponseCode.OK_CODE;
import static org.edu.mail.api.util.ResponseMessage.*;
import static org.edu.mail.api.util.ResponseMessage.DELETE_FAIL;
import static org.edu.mail.api.util.ResponseMessage.DELETE_OK;
import static org.edu.mail.usage.search.IndexTerm.BOX;
import static org.edu.mail.usage.search.IndexTerm.MESSAGE_ID;
import static org.edu.mail.usage.search.IndexTerm.MESSAGE_UID;

@RestController
@RequestMapping(value = "/api")
public class MessageController extends UMailConfigure{
    @Autowired
    private AccountService accountService;

    private Logger logger = Logger.getLogger(MessageController.class.getName());

    @ApiOperation(value="获取所有邮件", notes="根据账户和邮箱目录获取所有邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "邮箱账号名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "box", value = "邮箱目录名", required = true, dataType = "String")
    })
    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public ResponseEntity<?> getMessages(HttpServletRequest request, @RequestParam String account, @RequestParam String box) {
        logger.info("path:"+"/message");
        logger.info("args:"+account);
        logger.info("args:"+box);
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            try {
                UMailFileReader reader = UMailFileReader.newInstance(uid, account);
                result.setCode(OK_CODE);
                result.setMessage(QUERY_OK);
                result.setData(reader.listMessages(box));
            } catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(QUERY_NOT_EXIST);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="已读邮件", notes="对邮件进行已读标识")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "邮箱所属账户", required = true, dataType = "String"),
            @ApiImplicitParam(name = "box", value = "邮箱所在目录", required = true, dataType = "String"),
            @ApiImplicitParam(name = "msgUid", value = "邮件的Uid", required = true, dataType = "String")
    })
    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public ResponseEntity<?> readMessage(HttpServletRequest request, @RequestParam String account,
            @RequestParam String box,
            @RequestParam String msgUid) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            try {
                UMailFileReader reader = UMailFileReader.newInstance(uid, account);
                result.setCode(OK_CODE);
                result.setMessage(READ_MAIL_OK);
                result.setData(reader.readMessage(box, msgUid));
            } catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(READ_MAIL_FAIL);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="保存邮件", notes="对邮件保存，可下次编辑")
    @ApiImplicitParam(name = "raw", value = "邮件原始json字符串", required = true, dataType = "String")
    @RequestMapping(value = "/draft", method = RequestMethod.POST)
    public ResponseEntity<?> saveMessageToDraft(HttpServletRequest request, @RequestBody String raw) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            try {
                JSONObject obj = (JSONObject) JSONObject.parse(raw);
                JSONObject rawObj = obj.getJSONObject("raw");
                JSONObject msg = rawObj.getJSONObject("msg");
                String account = (String)msg.get("from");

                UMailFileWriter writer = UMailFileWriter.newInstance(uid, account);
                result.setCode(OK_CODE);
                result.setMessage(SAVE_DRAFT_OK);
                result.setData(writer.saveMessage(msg.toJSONString()));
            } catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(SAVE_DRAFT_FAIL);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="发送邮件", notes="发送邮件")
    @ApiImplicitParam(name = "raw", value = "邮件原始json字符串", required = true, dataType = "String")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<?> sendMessages(HttpServletRequest request, @RequestBody String raw) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            try {
                JSONObject obj = (JSONObject) JSONObject.parse(raw);
                JSONObject data = obj.getJSONObject("raw");
                JSONObject msg = data.getJSONObject("msg");
                String account = (String)msg.get("from");

                Account dbAccount = new Account();
                dbAccount.setUserid(uid);
                dbAccount.setAccount(account);
                dbAccount = accountService.selectAccount(dbAccount);
                // 设置发信人名称
                msg.put("from", dbAccount.getAlias() +"<"+dbAccount.getAccount()+">");

                Account acc = accountService.selectAccount(dbAccount);
                UMailSender sender = UMailSender.newInstance(UAccountFormat.format(acc));
                sender.send(data.toJSONString());
                result.setCode(OK_CODE);
                result.setMessage(SEND_MAIL_OK);
            }catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(SEND_MAIL_FAIL);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="同步邮件", notes="对指定邮箱账号进行同步")
    @ApiImplicitParam(name = "account", value = "邮箱账号名", required = true, dataType = "String")
    @RequestMapping(value = "/sync", method = RequestMethod.GET)
    public ResponseEntity<?> syncMessages(HttpServletRequest request, @RequestParam String account) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            try {
                Account dbAccount = new Account();
                dbAccount.setUserid(uid);
                dbAccount.setAccount(account);

                Account acc = accountService.selectAccount(dbAccount);
                UMailReceiver receiver = UMailReceiver.newInstance(UAccountFormat.format(acc));
                receiver.doSync(uid);
            } catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(SYNC_MAIL_FAIL);
                result.setData(e);
            }
            // 开一个线程在后台删除邮件
//            Runnable beeper = new Runnable() {
//                public void run() {
//                    try {
//                        Account dbAccount = new Account();
//                        dbAccount.setUserid(uid);
//                        dbAccount.setAccount(account);
//
//                        Account acc = accountService.selectAccount(dbAccount);
//                        UMailReceiver receiver = UMailReceiver.newInstance(format(acc));
//                        receiver.doSync(uid);
//                    } catch (UMailException e) {
//                        result.setCode(MAIL_MANAGER_FAIL_CODE);
//                        result.setMessage(SEND_MAIL_FAIL);
//                        result.setData(e);
//                    }
//                }
//            };
//            Thread thread = new Thread(beeper);
//            thread.start();

            result.setCode(OK_CODE);
            result.setMessage(SYNC_MAIL_OK);
        }
        return ResponseEntity.ok().body(result);
    }

    // 下载附件
    /**
     * HTTP/1.1 200 OK
     * Server: nginx
     * Date: Tue, 12 Mar 2019 08:15:45 GMT
     * Content-Type: application/x-png
     * Transfer-Encoding: chunked
     * Connection: keep-alive
     * Content-Disposition: attachment; filename="΢�Ž�ͼ_20190309124707.png"
     */
    @ApiOperation(value="下载邮件附件", notes="根据附件路径下载文件")
    @ApiImplicitParam(name = "attach", value = "BASE64编码后的附件路径", required = true, dataType = "String")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadAttach(@RequestParam String attach, HttpServletResponse response) {
        String realPath = conf.getProperty("attach.directory")+UMailUtils.decode64(attach);
        try{
            //获取服务器文件
            File file = new File(realPath);
            InputStream fis = new FileInputStream(file);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(file.getName(), "UTF-8"));
            ServletOutputStream os = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            while((len = fis.read(b)) > 0){
                os.write(b,0, len);
            }
            os.flush();
            os.close();
            fis.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    // 将附件写入本地，并返回路径
    Object writeToStorage(InputStream is, String fileName) throws IOException{
        Map<String, String> map = new HashMap();
        map.put("charset", "UTF-8");
        map.put("name", fileName);
        // 输出附件
        String tempDir = "Temp-" + System.currentTimeMillis() + "//";
        String savePath = conf.getProperty("attach.directory") + tempDir;
        if (!Files.exists(Paths.get(savePath))) {
            Files.createDirectories(Paths.get(savePath));
        }
        File attachFile = new File(savePath + fileName);
        FileOutputStream fos = new FileOutputStream(attachFile);
        // 一次读取多个字字节，解决一次读取一个字节速度较慢问题
        byte[] bytes = new byte[1024];
        int len = 0;
        while(-1 != (len = is.read(bytes))){
            fos.write(bytes, 0, len);
        }
        is.close();
        fos.close();
        // 文件大小使用49673712代替47.3M
        map.put("size", UMailUtils.sizeFile(attachFile));
        map.put("len", String.valueOf(attachFile.length()));
        map.put("path", UMailUtils.encode64((tempDir + map.get("name")).getBytes("UTF-8")));
        return map;
    }

    @ApiOperation(value="上传附件", notes="上传附件到uid所在目录中")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<?> uploadAttach(HttpServletRequest request) {
        ResponseResult result = new ResponseResult();
        try {
            List list = new ArrayList();
            Collection<Part> collection = request.getParts();
            Part[] parts = collection.toArray(new Part[collection.size()]);
            for (Part part : parts) {
                list.add(writeToStorage(part.getInputStream(), part.getSubmittedFileName()));
            }
            result.setCode(OK_CODE);
            result.setMessage(QUERY_OK);
            System.out.println(list);
            result.setData(list);
        }catch (IOException e1){

        }catch (ServletException e2){

        }finally {
            return ResponseEntity.ok().body(result);
        }
    }

    @ApiOperation(value="回复邮件", notes="对邮件进行回复并返回JSON格式的新邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "邮箱所属账户", required = true, dataType = "String"),
            @ApiImplicitParam(name = "box", value = "邮箱所在目录", required = true, dataType = "String"),
            @ApiImplicitParam(name = "msgUid", value = "邮件的Uid", required = true, dataType = "String"),
            @ApiImplicitParam(name = "type", value = "邮件格式html或plain", required = true, dataType = "String")
    })
    @RequestMapping(value = "/reply", method = RequestMethod.GET)
    public ResponseEntity<?> reply(HttpServletRequest request, @RequestParam String account,
                                   @RequestParam String box,
                                   @RequestParam String msgUid,
                                   @RequestParam String type) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            try {
                UMailFileReader reader = UMailFileReader.newInstance(uid, account);
                result.setCode(OK_CODE);
                result.setMessage(REPLY_MAIL_OK);
                result.setData(reader.reply(box, msgUid, type));
            } catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(REPLY_MAIL_FAIL);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="回复全部邮件", notes="对邮件进行全部回复并返回JSON格式的新邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "邮箱所属账户", required = true, dataType = "String"),
            @ApiImplicitParam(name = "box", value = "邮箱所在目录", required = true, dataType = "String"),
            @ApiImplicitParam(name = "msgUid", value = "邮件的Uid", required = true, dataType = "String"),
            @ApiImplicitParam(name = "type", value = "邮件格式html或plain", required = true, dataType = "String")
    })
    @RequestMapping(value = "/replyAll", method = RequestMethod.GET)
    public ResponseEntity<?> replayAll(HttpServletRequest request, @RequestParam String account,
                                       @RequestParam String box,
                                       @RequestParam String msgUid,
                                       @RequestParam String type) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            try {
                UMailFileReader reader = UMailFileReader.newInstance(uid, account);
                result.setCode(OK_CODE);
                result.setMessage(REPLY_ALL_MAIL_OK);
                result.setData(reader.replyAll(box, msgUid, type));
            } catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(REPLY_ALL_MAIL_FAIL);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="转发邮件", notes="对邮件进行转发并返回JSON格式的新邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "邮箱所属账户", required = true, dataType = "String"),
            @ApiImplicitParam(name = "box", value = "邮箱所在目录", required = true, dataType = "String"),
            @ApiImplicitParam(name = "msgUid", value = "邮件的Uid", required = true, dataType = "String")
    })
    @RequestMapping(value = "/forward", method = RequestMethod.GET)
    public ResponseEntity<?> forward(HttpServletRequest request, @RequestParam String account,
                                     @RequestParam String box,
                                     @RequestParam String msgUid) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            try {
                UMailFileReader reader = UMailFileReader.newInstance(uid, account);
                result.setCode(OK_CODE);
                result.setMessage(FORWARD_MAIL_OK);
                result.setData(reader.forward(box, msgUid));
            } catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(FORWARD_MAIL_FAIL);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }

    // 删除某封信
    @ApiOperation(value="删除邮件", notes="对指定邮件进行删除")
    @ApiImplicitParam(name = "raw", value = "邮件原始json字符串", required = true, dataType = "String")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteMessage(HttpServletRequest request, @RequestBody String raw) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{

            JSONObject obj = (JSONObject) JSONObject.parse(raw);
            JSONObject data = obj.getJSONObject("raw");
            JSONObject msg = data.getJSONObject("msg");
            String account = (String)data.get("account");

            Account dbAccount = new Account();
            dbAccount.setUserid(uid);
            dbAccount.setAccount(account);

            Account acc = accountService.selectAccount(dbAccount);
            try {
                UMailFileWriter writer = UMailFileWriter.newInstance(uid, account);
                writer.deleteMessages((String)msg.get(BOX), new String[]{msg.get(MESSAGE_UID).toString()});
                UMailManager manager = UMailManager.newInstance(UAccountFormat.format(acc));
                manager.delete((String)msg.get(BOX), new String[]{(String)msg.get(MESSAGE_UID)});
                UMailReceiver receiver = UMailReceiver.newInstance(acc.getAccount(), acc.getPassword());
                receiver.doSync(uid);
                // 开一个线程在后台删除邮件
//                Runnable beeper = new Runnable() {
//                    public void run() {
//                        try {
//                            UMailManager manager = UMailManager.newInstance(UAccountFormat.format(acc));
//                            manager.delete((String)msg.get(BOX), new String[]{(String)msg.get(MESSAGE_ID)});
//                            UMailReceiver receiver = UMailReceiver.newInstance(acc.getAccount(), acc.getPassword());
//                            receiver.doSync(uid);
//                        } catch (UMailException e) {
//                            result.setCode(MAIL_MANAGER_FAIL_CODE);
//                            result.setMessage(DELETE_MAIL_FAIL);
//                            result.setData(e);
//                        }
//                    }
//                };
//                Thread thread = new Thread(beeper);
//                thread.start();

                result.setCode(OK_CODE);
                result.setMessage(DELETE_MAIL_OK);
            }catch (UMailException e){
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(DELETE_MAIL_FAIL);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }
}

