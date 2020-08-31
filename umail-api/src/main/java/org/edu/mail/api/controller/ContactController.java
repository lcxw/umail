package org.edu.mail.api.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.edu.mail.api.domain.Contact;
import org.edu.mail.api.service.ContactService;
import org.edu.mail.api.util.ResponseResult;
import org.edu.mail.api.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.edu.mail.api.util.DbExecuteType.DELETE;
import static org.edu.mail.api.util.DbExecuteType.SAVE;
import static org.edu.mail.api.util.DbExecuteType.UPDATE;
import static org.edu.mail.api.util.ResponseCode.*;
import static org.edu.mail.api.util.ResponseCode.OK_CODE;
import static org.edu.mail.api.util.ResponseMessage.*;

@RestController
@RequestMapping(value = "/api")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @ApiOperation(value="获取联系人", notes="根据request头部token中uid获取联系人")
    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public ResponseEntity<ResponseResult> getContact(HttpServletRequest request) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            result.setCode(OK_CODE);
            result.setMessage(QUERY_OK);
            List<Contact> contacts = contactService.selectAll(uid);
            result.setData(contacts);
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="操作联系人", notes="对联系人进行增删改")
    @ApiImplicitParam(name = "contact", value = "联系人对象", required = true, dataType = "Contact",dataTypeClass = Contact.class)
    @RequestMapping(value = "/contacts", method = RequestMethod.POST)
    public ResponseEntity<ResponseResult> manageContact(HttpServletRequest request, @RequestBody Contact contact) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else {
            contact.setUserId(uid);
            // 验证通过操作
            String executeType = request.getHeader("type");
            switch (executeType){
                case SAVE:{
                    // 存入数据库
                    int res = contactService.save(contact);
                    if (res == 0) {
                        result.setCode(DB_MANAGER_FAIL_CODE);
                        result.setMessage(SAVE_EXIST);
                    }else {
                        result.setCode(OK_CODE);
                        result.setMessage(SAVE_OK);
                    }
                }break;
                case UPDATE:{
                    int res = contactService.update(contact);
                    if (res == 0) {
                        result.setCode(DB_MANAGER_FAIL_CODE);
                        result.setMessage(UPDATE_FAIL);
                    }else {
                        result.setCode(OK_CODE);
                        result.setMessage(UPDATE_OK);
                    }
                }break;
                case DELETE:{
                    int res = contactService.delete(contact);
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
