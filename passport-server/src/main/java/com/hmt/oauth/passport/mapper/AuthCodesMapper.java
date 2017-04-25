package com.hmt.oauth.passport.mapper;

import com.hmt.oauth.passport.entity.AuthCodesEntity;

import java.util.List;

public interface AuthCodesMapper {

    Integer insertAuthCodes(AuthCodesEntity authCodesEntity);

    AuthCodesEntity getAuthCode(String authCode);

    String findLoginNameByAuthCode(String authCode);

    List<AuthCodesEntity> selectAuthCodes(AuthCodesEntity authCodesEntity);

    Integer selectAuthCodesTotal(AuthCodesEntity authCodesEntity);

}

