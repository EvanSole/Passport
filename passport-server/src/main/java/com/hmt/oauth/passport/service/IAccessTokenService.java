package com.hmt.oauth.passport.service;

import com.hmt.oauth.passport.web.message.MessageResult;

/**
 * Created by Edwin on 2016/12/23.
 */
public interface IAccessTokenService {

    MessageResult createAccessToken(String accessToken, String loginName);

    boolean checkAccessToken(String accessToken);

    String findLoginNameByAccessToken(String accessToken);
}
