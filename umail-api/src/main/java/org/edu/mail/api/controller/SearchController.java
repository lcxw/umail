package org.edu.mail.api.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.edu.mail.api.util.ResponseResult;
import org.edu.mail.api.util.TokenUtil;
import org.edu.mail.usage.UMailException;
import org.edu.mail.usage.search.IndexTerm;
import org.edu.mail.usage.search.LuceneSearcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.edu.mail.api.util.ResponseCode.MAIL_MANAGER_FAIL_CODE;
import static org.edu.mail.api.util.ResponseCode.OK_CODE;
import static org.edu.mail.api.util.ResponseCode.UN_AUTHORIZED_CODE;
import static org.edu.mail.api.util.ResponseMessage.QUERY_NOT_EXIST;
import static org.edu.mail.api.util.ResponseMessage.QUERY_OK;
import static org.edu.mail.api.util.ResponseMessage.UN_AUTHORIZED;

@RestController
@RequestMapping(value = "/api")
public class SearchController {

    @ApiOperation(value="搜索往来邮件", notes="搜索并返回查询到的邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "邮箱所属账户", required = true, dataType = "String"),
            @ApiImplicitParam(name = "origin", value = "查询项", required = true, dataType = "String"),
            @ApiImplicitParam(name = "queryKey", value = "查询关键词", required = true, dataType = "String")
    })
    @RequestMapping(value = "/search0", method = RequestMethod.GET)
    public ResponseEntity<?> searchMsgByAddress(HttpServletRequest request,
                                                @RequestParam String account,
                                                @RequestParam String origin,
                                                @RequestParam String queryKey) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            try {
                LuceneSearcher searcher = LuceneSearcher.newInstance(uid, account);
                LuceneSearcher.Builder builder = LuceneSearcher.newInstance(uid, account).create();
                if(origin.equals(IndexTerm.FROM)){
                    builder.withFrom(queryKey);
                }else if(origin.equals(IndexTerm.TO)){
                    builder.withTo(queryKey);
                }else if(origin.equals(IndexTerm.SUBJECT)){
                    builder.withSubject(queryKey);
                }else if(origin.equals(IndexTerm.ATTACHMENTS)){
                    builder.withAttachment(queryKey);
                }
                Object[] res = builder.search(1);
                result.setCode(OK_CODE);
                result.setMessage(QUERY_OK);
                result.setData(res);
            }catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(QUERY_NOT_EXIST);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value="全文搜索邮件", notes="全文搜索并返回查询到的邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "邮箱所属账户", required = true, dataType = "String"),
            @ApiImplicitParam(name = "queryKey", value = "查询关键词", required = true, dataType = "String")
    })
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<?> search(HttpServletRequest request,
                                    @RequestParam String account,
                                    @RequestParam String queryKey) {
        ResponseResult result = new ResponseResult();
        String token = request.getHeader("token");
        String uid = TokenUtil.verify(token, "uid");
        if(uid == null){
            result.setCode(UN_AUTHORIZED_CODE);
            result.setMessage(UN_AUTHORIZED);
        }else{
            try {
                LuceneSearcher searcher = LuceneSearcher.newInstance(uid, account);
                Object[] res = searcher.searchAll(queryKey, 1);
                result.setCode(OK_CODE);
                result.setMessage(QUERY_OK);
                result.setData(res);
            }catch (UMailException e) {
                result.setCode(MAIL_MANAGER_FAIL_CODE);
                result.setMessage(QUERY_NOT_EXIST);
                result.setData(e);
            }
        }
        return ResponseEntity.ok().body(result);
    }
}
