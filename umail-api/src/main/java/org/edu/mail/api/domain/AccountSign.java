package org.edu.mail.api.domain;

public class AccountSign {
    private String account;

    private String sendsign;

    private String replysign;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getSendsign() {
        return sendsign;
    }

    public void setSendsign(String sendsign) {
        this.sendsign = sendsign == null ? null : sendsign.trim();
    }

    public String getReplysign() {
        return replysign;
    }

    public void setReplysign(String replysign) {
        this.replysign = replysign == null ? null : replysign.trim();
    }
}