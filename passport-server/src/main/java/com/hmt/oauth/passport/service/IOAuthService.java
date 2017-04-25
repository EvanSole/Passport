package com.hmt.oauth.passport.service;

import com.hmt.oauth.passport.domain.dto.AuthCodesDTO;
import com.hmt.oauth.passport.web.message.MessageResult;
import com.hmt.oauth.passport.web.repones.PageResponse;

import java.util.List;

/**
 * Created by Edwin on 2016/12/23.
 */
public interface IOAuthService {

    boolean checkClientId(String clientId);

    boolean checkClientSecret(String clientSecret);

    boolean checkAuthCode(String authCode);

    MessageResult addAuthCode(String authorizationCode, String appkey,String loginName,Long expiresTime);

    String getLoginNameByAuthCode(String authCode);

    MessageResult createAccessToken(String accessToken, String loginName);

    boolean checkAccessToken(String accessToken);

    String findLoginNameByAccessToken(String accessToken);

    PageResponse<List> queryAuthCodes(AuthCodesDTO authCodesDTO);
}
