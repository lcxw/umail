package org.edu.mail.api.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.edu.mail.api.domain.Account;
import org.edu.mail.api.domain.AccountSign;
import org.edu.mail.api.domain.Sign;
import org.edu.mail.api.service.AccountService;
import org.edu.mail.api.service.AccountSignService;
import org.edu.mail.api.service.SignService;
import org.edu.mail.api.util.ResponseResult;
import org.edu.mail.api.util.TokenUtil;
import org.edu.mail.usage.util.UMailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.edu.mail.api.util.DbExecuteType.DELETE;
import static org.edu.mail.api.util.DbExecuteType.SAVE;
import static org.edu.mail.api.util.DbExecuteType.UPDATE;
import static org.edu.mail.api.util.ResponseCode.*;
import static org.edu.mail.api.util.ResponseCode.DB_MANAGER_FAIL_CODE;
import static org.edu.mail.api.util.ResponseMessage.*;
import static org.edu.mail.api.util.ResponseMessage.DELETE_FAIL;
import static org.edu.mail.api.util.ResponseMessage.DELETE_OK;

@RestController
@RequestMapping(value = "/api")
public class SignController {
    @Autowired
    private SignService signService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountSignService accountSignService;

    @ApiOperation(value="获取所有签名", notes="根据request头部token中uid获取所有签名")
    @RequestMapping(value = "/signs", method = RequestMethod.GET)
    public ResponseEntity<ResponseResult> getAccount(HttpServletRequest request) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            result.setCode(OK_CODE);
            result.setMessage(QUERY_OK);
            List<Sign> signs = signService.selectAll(uid);
            result.setData(signs);
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="管理签名", notes="对签名进行增删改")
    @ApiImplicitParam(name = "sign", value = "签名对象", required = true, dataType = "Sign")
    @RequestMapping(value = "/signs", method = RequestMethod.POST)
    public ResponseEntity<ResponseResult> manageSign(HttpServletRequest request, @RequestBody Sign sign) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            result.setCode(OK_CODE);
            result.setMessage(QUERY_OK);
            sign.setUserid(uid);
            String executeType = request.getHeader("type");
            switch (executeType){
                case SAVE:{
                    int res = signService.save(sign);
                    if (res == 0) {
                        result.setCode(DB_MANAGER_FAIL_CODE);
                        result.setMessage(SAVE_EXIST);
                    }else {
                        result.setCode(OK_CODE);
                        result.setMessage(SAVE_OK);
                    }
                }break;
                case UPDATE:{
                    int res = signService.update(sign);
                    if (res == 0) {
                        result.setCode(DB_MANAGER_FAIL_CODE);
                        result.setMessage(UPDATE_FAIL);
                    }else {
                        result.setCode(OK_CODE);
                        result.setMessage(UPDATE_OK);
                    }
                }break;
                case DELETE:{
                    String signName = sign.getName();
                    // 先查找账户签名表中是否有该签名,有的话改变默认签名
                    List<AccountSign> accountSignList = new ArrayList<>();
                    List<Account> accountList = accountService.selectAccounts(uid);
                    for (Account acc: accountList) {
                        accountSignList.add(accountSignService.selectAll(acc.getAccount()));
                    }
                    for (AccountSign accountSign: accountSignList) {
                        String acc = accountSign.getAccount();
                        boolean changed = false;
                        if(accountSign.getSendsign().equals(signName)){
                            accountSign.setSendsign(acc.substring(0, acc.indexOf('@')));
                            changed =  true;
                        }
                        if(accountSign.getReplysign().equals(signName)){
                            accountSign.setReplysign(acc.substring(0, acc.indexOf('@')));
                            changed =  true;
                        }
                        if(changed) {
                            accountSignService.update(accountSign);
                        }
                    }
                    // 删除签名
                    int res = signService.delete(sign);
                    if (res == 0) {
                        result.setCode(DB_MANAGER_FAIL_CODE);
                        result.setMessage(DELETE_FAIL);
                    }else {
                        result.setCode(OK_CODE);
                        result.setMessage(DELETE_OK);
                    }
                }break;
            }
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="获取所有账户签名", notes="获取所有账户签名")
    @RequestMapping(value = "/accountSigns", method = RequestMethod.GET)
    public ResponseEntity<ResponseResult> getAccountSigns(HttpServletRequest request) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            result.setCode(OK_CODE);
            result.setMessage(QUERY_OK);
            List<AccountSign> accountSignList = new ArrayList<>();

            List<Account> accountList = accountService.selectAccounts(uid);
            for (Account acc: accountList) {
                accountSignList.add(accountSignService.selectAll(acc.getAccount()));
            }

            result.setData(accountSignList);
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="获取某个账户签名", notes="根据账户名、方式和类型获取账户签名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "邮箱账户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "way", value = "签名用于发送或者回复", required = true, dataType = "String"),
            @ApiImplicitParam(name = "type", value = "签名是html或plain", required = true, dataType = "String")
    })
    @RequestMapping(value = "/accountSign", method = RequestMethod.GET)
    public ResponseEntity<ResponseResult> getAccountSign(HttpServletRequest request,
                                            @RequestParam String account,
                                            @RequestParam String way,
                                            @RequestParam String type) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            AccountSign accountSign = accountSignService.selectAll(account);
            Sign sign = null;
            if(way.equals("send")){
                sign = signService.selectByName(accountSign.getSendsign());
            }else {
                sign = signService.selectByName(accountSign.getReplysign());
            }
            if(type.equalsIgnoreCase("plain")){
                sign.setContent(UMailUtils.html2Str(sign.getContent()));
            }
            result.setCode(OK_CODE);
            result.setMessage(QUERY_OK);
            result.setData(sign);
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="更新账户签名", notes="更新账户签名")
    @ApiImplicitParam(name = "raw", value = "账户签名原始JSON字符串", required = true, dataType = "String")
    @RequestMapping(value = "/accountSigns", method = RequestMethod.POST)
    public ResponseEntity<ResponseResult> updateAccountSigns(HttpServletRequest request, @RequestBody String raw) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            Map obj = (Map)JSONObject.parse(raw);
            Map data = (Map)JSONObject.parse((String)obj.get("raw"));
            // 查询新的AccountSign
            List<JSONObject> newAccountSignList = (List)data.get("accountSigns");
            // 查询新的Sign
            List<JSONObject> newSigns = (List)data.get("signs");
            for(JSONObject obj0: newSigns){
                Sign sign =  obj0.toJavaObject(Sign.class);
                sign.setUserid(uid);
                signService.update(sign);
                System.out.println("更新签名"+sign.getName());
            }
            for(JSONObject obj0: newAccountSignList){
                AccountSign accountSign =  obj0.toJavaObject(AccountSign.class);
                accountSignService.update(accountSign);
                System.out.println("更新账号签名"+accountSign.getAccount());
            }
            result.setCode(OK_CODE);
            result.setMessage(UPDATE_OK);
        }
        return ResponseEntity.ok().body(result);
    }
}
