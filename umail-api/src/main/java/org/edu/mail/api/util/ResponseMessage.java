package org.edu.mail.api.util;

/**
 * Response返回信息
 */
public interface ResponseMessage {
    /* 授权信息 */
    String UN_AUTHORIZED = "未授权或token已过期";
    String AUTHORIZED_OK = "授权成功";

    /* 账号合法性 */
    String VALID_ACCOUNT_FAIL = "账号验证失败";

    /* 数据库操作信息 */
    String SAVE_OK = "资源添加成功";
    String SAVE_EXIST = "资源已存在";
    String QUERY_OK = "资源查询成功";
    String QUERY_NOT_EXIST = "资源不存在";
    String DELETE_OK = "资源不存在";
    String DELETE_FAIL = "删除资源失败";
    String UPDATE_OK = "资源更新成功";
    String UPDATE_FAIL = "资源更新失败";

    /* 邮件操作信息 */
    String SEND_MAIL_OK = "发送邮件成功";
    String SEND_MAIL_FAIL = "发送邮件失败";
    String DELETE_MAIL_OK = "删除邮件成功";
    String DELETE_MAIL_FAIL = "删除邮件失败";
    String SYNC_MAIL_OK = "同步邮件成功";
    String SYNC_MAIL_FAIL = "同步邮件失败";
    String READ_MAIL_OK = "已读邮件成功";
    String READ_MAIL_FAIL = "已读邮件失败";
    String SAVE_DRAFT_OK = "保存邮件成功";
    String SAVE_DRAFT_FAIL = "保存邮件失败";
    String REPLY_MAIL_OK = "回复邮件成功";
    String REPLY_MAIL_FAIL = "回复邮件失败";
    String FORWARD_MAIL_OK = "转发邮件成功";
    String FORWARD_MAIL_FAIL = "转发邮件失败";
    String REPLY_ALL_MAIL_OK = "回复全部邮件成功";
    String REPLY_ALL_MAIL_FAIL = "回复全部邮件失败";
}
