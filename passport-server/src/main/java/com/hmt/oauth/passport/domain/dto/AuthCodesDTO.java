package com.hmt.oauth.passport.domain.dto;

import com.hmt.oauth.passport.entity.po.BasePO;

import java.io.Serializable;
import java.util.Date;

public class AuthCodesDTO extends BasePO implements Serializable{

    private String loginName;

    private String appKey;

    private String code;

    private Byte revoked;

    private Long expiresTime;

    private Date timestamps;

    private Long startTime;

    private Long endTime;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey == null ? null : appKey.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Byte getRevoked() {
        return revoked;
    }

    public void setRevoked(Byte revoked) {
        this.revoked = revoked;
    }

    public Long getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Long expiresTime) {
        this.expiresTime = expiresTime;
    }

    public Date getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Date timestamps) {
        this.timestamps = timestamps;
    }
}