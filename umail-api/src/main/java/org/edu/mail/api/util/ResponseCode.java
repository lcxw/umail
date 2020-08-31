package org.edu.mail.api.util;

/**
 * Response返回状态吗
 */
public interface ResponseCode {
    String OK_CODE = "200";
    String UN_AUTHORIZED_CODE = "201";
    String VALID_ACCOUNT_FAIL_CODE = "202";
    String DB_MANAGER_FAIL_CODE = "203";
    String MAIL_MANAGER_FAIL_CODE = "204";
    String UPDATE_SECRET_FAIL_CODE = "205";
}
