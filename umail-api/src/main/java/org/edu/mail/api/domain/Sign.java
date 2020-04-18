package org.edu.mail.api.domain;

public class Sign {
    private String name;

    private String content;

    private String userid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    @Override
    public boolean equals(Object obj) {
        Sign sign = (Sign)obj;
        return sign.name.equals(name);
    }
}