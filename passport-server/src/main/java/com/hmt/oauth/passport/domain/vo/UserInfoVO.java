package com.hmt.oauth.passport.domain.vo;

import java.util.Date;

/**
 * Created by Edwin on 2016/12/28.
 */
public class UserInfoVO {

    private Integer id;

    private Integer tenantId;

    private String loginName;

    private String userName;

    private String realName;

    private String cardId;

    private Date birthday;

    private String email;

    private String mobilePhone;

    private String sex;

    private Byte isAdmin;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Byte getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Byte isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "UserInfoVO{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", loginName='" + loginName + '\'' +
                ", userName='" + userName + '\'' +
                ", realName='" + realName + '\'' +
                ", cardId='" + cardId + '\'' +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", sex='" + sex + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
