package org.edu.mail.api.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.edu.mail.api.domain.Account;
import org.edu.mail.api.domain.User;
import org.edu.mail.api.service.AccountService;
import org.edu.mail.api.service.UserService;
import org.edu.mail.api.util.MD5;
import org.edu.mail.api.util.ResponseResult;
import org.edu.mail.api.util.TokenUtil;
import org.edu.mail.api.util.UAccountFormat;
import org.edu.mail.usage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import static org.edu.mail.api.util.ResponseCode.*;
import static org.edu.mail.api.util.ResponseMessage.*;

@RestController
@RequestMapping(value = "/api")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;

    @ApiOperation(value="获取验证码", notes="获取验证码")
    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<?> sendVerifyMessage(@RequestParam String email) {
        ResponseResult result = new ResponseResult();
        List<User> users = userService.selectAll();
        User user = null;
        boolean exist = false;
        for (User user0: users){
            if(user0.getEmail().equals(email)){
                exist = true;
                user = user0;
                break;
            }
        }
        if(!exist){
            result.setCode(DB_MANAGER_FAIL_CODE);
            result.setMessage(SAVE_EXIST);
        }else{
            Random random = new Random();
            String code = "";
            for (int i=0; i<6; i++){
                code += random.nextInt(10);
            }
            // 设置验证码
            user.setVerify(code);
            // 设置当前时间
            String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            user.setTime(time);
            userService.update(user);
            try {
                sendMessage(code, email, time);
                result.setCode(OK_CODE);
                result.setMessage(UPDATE_OK);
            }catch (UMailException e0){
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(SEND_MAIL_FAIL);
                result.setData(e0);
            }catch (ParseException e1){
            }
        }
        return ResponseEntity.ok().body(result);
    }

    private void sendMessage(String verify, String email, String time) throws UMailException, ParseException{
        StringBuilder content = new StringBuilder();
        Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(time);
        content.append("亲爱的用户").append(email).append(": 您好！\n\n")
                .append("您收到这封这封电子邮件是因为您 (也可能是某人冒充您的名义) 申请了一个新的密码。假如这不是您本人所申请, 请不用理会这封电子邮件, 但是如果您持续收到这类的信件骚扰, 请您尽快联络管理员。\n\n")
                .append("请使用以下验证码完成后续找回密码流程\n\n")
                .append(verify).append("\n\n")
                .append("注意:请您在收到邮件10分钟内(")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date))
                .append("前)使用，否则该验证码将会失效。\n");
        UMailMessage message = new UMailMessage(UMessageType.PLAIN_TXT,
                "UMAIL官方<1337078409@qq.com>",
                new String[]{email},
                "[UMAIL]找回您的帐户密码",
                content.toString(),
                null);
        UMailSender sender = UMailSender.newInstance("1337078409@qq.com", "15045120");
        sender.doSend(message, UMessageSendWay.NORMAL, null);
    }

    @ApiOperation(value="找回密码", notes="根据验证码找回密码")
    @RequestMapping(value = "/forget", method = RequestMethod.POST)
    public ResponseEntity<?> forgetPassword(@RequestBody User user) {
        ResponseResult result = new ResponseResult();
        List<User> users = userService.selectAll();
        User oldUser = null;
        boolean exist = false;
        for (User user0: users){
            if(user0.getEmail().equals(user.getEmail())){
                exist = true;
                oldUser = user0;
                break;
            }
        }
        if(!exist){
            result.setCode(DB_MANAGER_FAIL_CODE);
            result.setMessage(SAVE_EXIST);
        }else{
            try {
                Date fromTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(oldUser.getTime());
                Date nowTime = new Date();
                // 超过10分钟
                if(oldUser.getVerify().equals(user.getVerify()) && nowTime.getTime()-fromTime.getTime()<10*60*1000){
                    oldUser.setSecret(user.getSecret());
                    userService.update(oldUser);
                    result.setCode(OK_CODE);
                    result.setMessage(UPDATE_OK);
                }else{
                    result.setCode(UPDATE_SECRET_FAIL_CODE);
                    result.setMessage(UPDATE_FAIL);
                }
            }catch (ParseException e){
                result.setCode(UPDATE_SECRET_FAIL_CODE);
                result.setMessage(SAVE_EXIST);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="添加用户", notes="添加用户")
    @ApiImplicitParam(name = "user", value = "用户对象", required = true, dataType = "User")
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        ResponseResult result = new ResponseResult();
        List<User> users = userService.selectAll();
        boolean exist = false;
        for (User user0: users){
            if(user0.getName().equals(user.getName()) || user0.getEmail().equals(user.getEmail())){
                exist = true;
                break;
            }
        }
        if(!exist){
            user.setId(UUID.randomUUID().toString());
            userService.saveUser(user);
            result.setCode(OK_CODE);
            result.setMessage(SAVE_OK);
        }else{
            result.setCode(DB_MANAGER_FAIL_CODE);
            result.setMessage(SAVE_EXIST);
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="换取token", notes="根据用户的账号密码换取token")
    @ApiImplicitParam(name = "user", value = "用户对象", required = true, dataType = "User")
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody User user) {
        ResponseResult result = new ResponseResult();
        String token = userService.login(user);
        if(token != null){
            result.setCode(OK_CODE);
            result.setMessage(AUTHORIZED_OK);
            result.setData(token);
        }else{
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="验证token", notes="验证token是否过期")
    @RequestMapping(value = "/valid", method = RequestMethod.GET)
    public ResponseEntity<?> valid(HttpServletRequest request) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        if(token == null || token.equals("")){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
            return ResponseEntity.ok().body(result);
        }
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            result.setCode(OK_CODE);
            result.setMessage(AUTHORIZED_OK);
            // 定时收取邮件
            List<Account> accounts = accountService.selectAccounts(uid);
            // 开一个线程在后台定时收取邮件
            Runnable beeper = new Runnable() {
                public void run() {
                    try {
                        for (Account acc: accounts) {
                            if(acc.getIsscheduled().equals("true")) {
                                UMailReceiver receiver = UMailReceiver.newInstance(UAccountFormat.format(acc));
                                receiver.syncWithPeriod(TokenUtil.verify(token, "uid"), Long.valueOf(acc.getScheduledperiod()) * 60 * 1000);
                            }
                        }
                    } catch (UMailException e) {
                        result.setCode(MAIL_MANAGER_FAIL_CODE);
                        result.setMessage(SYNC_MAIL_FAIL);
                        result.setData(e);
                    }
                }
            };
            Thread thread = new Thread(beeper);
            thread.start();
        }
        return ResponseEntity.ok().body(result);
    }
}
