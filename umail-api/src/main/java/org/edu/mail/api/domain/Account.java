package org.edu.mail.api.domain;

public class Account {
    private String account;

    private String password;

    private String recprotocol;

    private String rechost;

    private String recport;

    private String recisssl;

    private String sendprotocol;

    private String sendhost;

    private String sendport;

    private String sendisssl;

    private String alias;

    private String isscheduled;

    private String scheduledperiod;

    private String userid;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getRecprotocol() {
        return recprotocol;
    }

    public void setRecprotocol(String recprotocol) {
        this.recprotocol = recprotocol == null ? null : recprotocol.trim();
    }

    public String getRechost() {
        return rechost;
    }

    public void setRechost(String rechost) {
        this.rechost = rechost == null ? null : rechost.trim();
    }

    public String getRecport() {
        return recport;
    }

    public void setRecport(String recport) {
        this.recport = recport == null ? null : recport.trim();
    }

    public String getRecisssl() {
        return recisssl;
    }

    public void setRecisssl(String recisssl) {
        this.recisssl = recisssl == null ? null : recisssl.trim();
    }

    public String getSendprotocol() {
        return sendprotocol;
    }

    public void setSendprotocol(String sendprotocol) {
        this.sendprotocol = sendprotocol == null ? null : sendprotocol.trim();
    }

    public String getSendhost() {
        return sendhost;
    }

    public void setSendhost(String sendhost) {
        this.sendhost = sendhost == null ? null : sendhost.trim();
    }

    public String getSendport() {
        return sendport;
    }

    public void setSendport(String sendport) {
        this.sendport = sendport == null ? null : sendport.trim();
    }

    public String getSendisssl() {
        return sendisssl;
    }

    public void setSendisssl(String sendisssl) {
        this.sendisssl = sendisssl == null ? null : sendisssl.trim();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias == null ? null : alias.trim();
    }

    public String getIsscheduled() {
        return isscheduled;
    }

    public void setIsscheduled(String isscheduled) {
        this.isscheduled = isscheduled == null ? null : isscheduled.trim();
    }

    public String getScheduledperiod() {
        return scheduledperiod;
    }

    public void setScheduledperiod(String scheduledperiod) {
        this.scheduledperiod = scheduledperiod == null ? null : scheduledperiod.trim();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }
}