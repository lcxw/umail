package org.edu.mail.api.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.edu.mail.api.domain.Account;
import org.edu.mail.api.service.AccountService;
import org.edu.mail.api.util.ResponseResult;
import org.edu.mail.api.util.TokenUtil;
import org.edu.mail.api.util.UAccountFormat;
import org.edu.mail.usage.*;
import org.edu.mail.usage.search.IndexTerm;
import org.edu.mail.usage.search.LuceneSearcher;
import org.edu.mail.usage.search.UMailFileReader;
import org.edu.mail.usage.search.UMailFileWriter;
import org.edu.mail.usage.util.UMailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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

import static org.edu.mail.api.util.DbExecuteType.DELETE;
import static org.edu.mail.api.util.DbExecuteType.SAVE;
import static org.edu.mail.api.util.DbExecuteType.UPDATE;
import static org.edu.mail.api.util.ResponseCode.*;
import static org.edu.mail.api.util.ResponseMessage.*;
import static org.edu.mail.usage.search.IndexTerm.*;

@RestController
@RequestMapping(value = "/api")
public class AccountController{

    @Autowired
    private AccountService accountService;

    @ApiOperation(value="获取全部邮箱账号", notes="根据request头部token中uid获取全部邮箱账号")
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            result.setCode(OK_CODE);
            result.setMessage(QUERY_OK);
            List<Account> accounts = accountService.selectAccounts(uid);
            result.setData(accounts);
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="操作邮箱账号", notes="对邮箱账号进行增删改")
    @ApiImplicitParam(name = "account", value = "账号对象", required = true, dataType = "Account")
    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    public ResponseEntity<?> manageAccount(HttpServletRequest request, @RequestBody Account account) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            // 先判断账号是否合法
            UMailAccount acc = UAccountFormat.format(account);
            if(!acc.isValid()){
                result.setCode(VALID_ACCOUNT_FAIL_CODE);
                result.setMessage(VALID_ACCOUNT_FAIL);
                return ResponseEntity.ok().body(result);
            }
            // 验证通过操作
            account.setUserid(uid);
            String executeType = request.getHeader("type");
            switch (executeType){
                case SAVE:{
                    // 存入数据库
                    int res = accountService.saveIfNotExist(account);
                    if (res == -1) {
                        result.setCode(DB_MANAGER_FAIL_CODE);
                        result.setMessage(SAVE_EXIST);
                    }else {
                        // 开一个线程在后台删除邮件
                        Runnable beeper = new Runnable() {
                            public void run() {
                                try {
                                    UMailReceiver receiver = UMailReceiver.newInstance(acc);
                                    receiver.doSync(uid);
                                } catch (UMailException e) {
                                    result.setCode(MAIL_MANAGER_FAIL_CODE);
                                    result.setMessage(SYNC_MAIL_FAIL);
                                    result.setData(e);
                                }
                            }
                        };
                        Thread thread = new Thread(beeper);
                        thread.start();

                        result.setCode(OK_CODE);
                        result.setMessage(SAVE_OK);
                    }
                }break;
                case UPDATE:{
                    int res = accountService.update(account);
                    if (res == 0) {
                        result.setCode(DB_MANAGER_FAIL_CODE);
                        result.setMessage(UPDATE_FAIL);
                    }else {
                        result.setCode(OK_CODE);
                        result.setMessage(UPDATE_OK);
                    }
                }break;
                case DELETE:{
                    int res = accountService.delete(account);
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
}
