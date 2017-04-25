package com.hmt.oauth.passport.mapper;

import com.hmt.oauth.passport.entity.AccessTokensEntity;

public interface AccessTokensMapper {

    Integer insertAccessTokens(AccessTokensEntity accessTokensEntity);

    AccessTokensEntity getAccessToken(String accessToken);

    String findLoginNameByAccessToken(String accessToken);
}

