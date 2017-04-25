package com.hmt.oauth.passport.service.impl;

import com.hmt.oauth.passport.entity.AccessTokensEntity;
import com.hmt.oauth.passport.mapper.AccessTokensMapper;
import com.hmt.oauth.passport.service.IAccessTokenService;
import com.hmt.oauth.passport.web.constants.PassPortConstants;
import com.hmt.oauth.passport.web.message.MessageResult;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Edwin on 2016/12/23.
 */
@Service
public class AccessTokenService implements IAccessTokenService{

    @Autowired
    AccessTokensMapper accessTokensMapper;

    @Override
    public MessageResult createAccessToken(String accessToken, String loginName) {
        AccessTokensEntity accessTokensEntity = new AccessTokensEntity();
        accessTokensEntity.setLoginName(loginName);
        accessTokensEntity.setExpireTime(PassPortConstants.TOKEN_EXPIRES_TIME);
        accessTokensEntity.setAccessToken(accessToken);
        accessTokensMapper.insertAccessTokens(accessTokensEntity);
        return  MessageResult.getSucMessage();
    }

    @Override
    public boolean checkAccessToken(String accessToken) {
        AccessTokensEntity accessTokensEntity = accessTokensMapper.getAccessToken(accessToken);
        if (accessTokensEntity == null){
            return false;
        }
        //授权码过期时间
        Long expiresTime = accessTokensEntity.getTimestamps().getTime() + accessTokensEntity.getExpireTime()*1000;
        Long newDateTime = new DateTime().getMillis();
        if(expiresTime < newDateTime){
            return  false;
        }
        return true;
    }

    @Override
    public String findLoginNameByAccessToken(String accessToken) {
        return accessTokensMapper.findLoginNameByAccessToken(accessToken);
    }
}
