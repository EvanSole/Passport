package com.hmt.oauth.passport.service.impl;

import com.hmt.oauth.passport.domain.dto.AuthCodesDTO;
import com.hmt.oauth.passport.entity.AuthCodesEntity;
import com.hmt.oauth.passport.mapper.AuthCodesMapper;
import com.hmt.oauth.passport.service.IAccessTokenService;
import com.hmt.oauth.passport.service.IClientService;
import com.hmt.oauth.passport.service.IOAuthService;
import com.hmt.oauth.passport.utils.BeanUtils;
import com.hmt.oauth.passport.web.message.MessageResult;
import com.hmt.oauth.passport.web.repones.PageResponse;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Edwin on 2016/12/23.
 */
@Service
public class OAuthService implements IOAuthService {

    @Autowired
    IClientService clientService;

    @Autowired
    IAccessTokenService accessTokenService;

    @Autowired
    AuthCodesMapper authCodesMapper;

    @Override
    public boolean checkClientId(String clientId) {
        return clientService.findByClientAppkey(clientId) != null;
    }

    @Override
    public boolean checkClientSecret(String clientSecret) {
        return clientService.findByClientAppSecret(clientSecret) != null;
    }

    @Override
    public boolean checkAuthCode(String authCode) {
        AuthCodesEntity authCodesEntity = authCodesMapper.getAuthCode(authCode);
        if (authCodesEntity == null){
            return false;
        }
        //授权码过期时间
        Long expiresTime = authCodesEntity.getTimestamps().getTime() + authCodesEntity.getExpiresTime()*1000;
        Long newDateTime = new DateTime().getMillis();
        if(expiresTime < newDateTime){
            return  false;
        }
        return true;
    }

    /***
     * 保存授权码authorizationCode
     * @param authorizationCode  授权码
     * @param appkey  应用appkey
     * @param loginName 登录名
     * @param expiresTime 过期时间(毫秒)
     * @return
     */
    @Override
    public MessageResult addAuthCode(String authorizationCode, String appkey,String loginName,Long expiresTime) {
        AuthCodesEntity authCodesEntity = new AuthCodesEntity();
        authCodesEntity.setLoginName(loginName);
        authCodesEntity.setCode(authorizationCode);
        authCodesEntity.setAppKey(appkey);
        authCodesEntity.setExpiresTime(expiresTime);
        authCodesMapper.insertAuthCodes(authCodesEntity);
        return MessageResult.getSucMessage();
    }

    @Override
    public String getLoginNameByAuthCode(String authCode) {
        return authCodesMapper.findLoginNameByAuthCode(authCode);
    }

    @Override
    public MessageResult createAccessToken(String accessToken, String loginName) {
        return accessTokenService.createAccessToken(accessToken,loginName);
    }

    @Override
    public boolean checkAccessToken(String accessToken) {
        return accessTokenService.checkAccessToken(accessToken);
    }

    @Override
    public String findLoginNameByAccessToken(String accessToken) {
        return accessTokenService.findLoginNameByAccessToken(accessToken);
    }

    @Override
    public PageResponse<List> queryAuthCodes(AuthCodesDTO authCodesDTO) {
        AuthCodesEntity authCodesEntity = BeanUtils.copyBeanPropertyUtils(authCodesDTO,AuthCodesEntity.class);
        PageResponse<List> pageResponse = new PageResponse<>();
        pageResponse.setRows(authCodesMapper.selectAuthCodes(authCodesEntity));
        pageResponse.setTotal(authCodesMapper.selectAuthCodesTotal(authCodesEntity));
        return pageResponse;
    }
}
