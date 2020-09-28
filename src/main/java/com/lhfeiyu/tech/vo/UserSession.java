package com.lhfeiyu.tech.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserSession {

    private Long   uid;
    private Date   logonTime;
    private String token;
    private String wsSessionId;

    public UserSession() {
    }

    public UserSession(Long uid, String token) {
        this.uid = uid;
        this.token = token;
        this.logonTime = new Date();
    }

}
