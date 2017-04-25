package com.hmt.oauth.passport.domain.dto;

import com.hmt.oauth.passport.entity.po.BasePO;

import java.io.Serializable;
import java.util.Date;

public class ClientsUsersDTO extends BasePO implements Serializable{

    private Integer id;

    private Integer clientId;

    private Integer userId;

    private Byte revoked;

    private Date timestamps;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Byte getRevoked() {
        return revoked;
    }

    public void setRevoked(Byte revoked) {
        this.revoked = revoked;
    }

    public Date getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Date timestamps) {
        this.timestamps = timestamps;
    }
}