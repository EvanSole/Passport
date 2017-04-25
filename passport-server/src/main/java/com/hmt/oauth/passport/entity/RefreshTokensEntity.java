package com.hmt.oauth.passport.entity;

public class RefreshTokensEntity {

    private Integer id;

    private String accessToken;

    private String refreshToken;

    private Byte revoked;

    private Long expireTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken == null ? null : accessToken.trim();
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken == null ? null : refreshToken.trim();
    }

    public Byte getRevoked() {
        return revoked;
    }

    public void setRevoked(Byte revoked) {
        this.revoked = revoked;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}