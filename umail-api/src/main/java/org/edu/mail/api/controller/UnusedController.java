package org.edu.mail.api.controller;

import org.edu.mail.api.domain.Account;
import org.edu.mail.api.service.AccountService;
import org.edu.mail.api.util.ResponseResult;
import org.edu.mail.api.util.TokenUtil;
import org.edu.mail.usage.UMailException;
import org.edu.mail.usage.search.UMailFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

import static org.edu.mail.api.util.ResponseCode.*;
import static org.edu.mail.api.util.ResponseMessage.QUERY_NOT_EXIST;
import static org.edu.mail.api.util.ResponseMessage.QUERY_OK;
import static org.edu.mail.api.util.ResponseMessage.UN_AUTHORIZED;

@RestController
@RequestMapping(value = "/api")
public class UnusedController {
    @Autowired
    private AccountService accountService;
    // 获取单个邮箱账号
    @ApiIgnore
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public ResponseEntity<?> getAccount(HttpServletRequest request, @RequestParam String account) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            Account dbAccount = new Account();
            dbAccount.setUserid(uid);
            dbAccount.setAccount(account);

            Account acc = accountService.selectAccount(dbAccount);
            if(acc != null){
                result.setCode(OK_CODE);
                result.setMessage(QUERY_OK);
                result.setData(acc);
            }else {
                result.setCode(DB_MANAGER_FAIL_CODE);
                result.setMessage(account + QUERY_NOT_EXIST);
            }
        }
        return ResponseEntity.ok().body(result);
    }
    // 获取邮箱列表
    @ApiIgnore
    @RequestMapping(value = "/boxes", method = RequestMethod.GET)
    public ResponseEntity<?> getBoxes(HttpServletRequest request, @RequestParam String account) {
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
                result.setData(reader.listFolders());
            } catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(QUERY_NOT_EXIST);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }
}
