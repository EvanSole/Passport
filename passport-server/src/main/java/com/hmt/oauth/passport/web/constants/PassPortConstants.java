package com.hmt.oauth.passport.web.constants;

/**
 * Created by Edwin on 2016/12/20.
 */
public class PassPortConstants {

    public static final String SESSION_KEY = "session_key";

    public static final String DEFAULT_PASSWORD = "123456";

    public static final String INVALID_CLIENT_ID = "客户端验证失败,错误的client_id/client_secret.";

    public static final String INVALID_ACCESS_TOKEN = "accessToken无效或已过期.";

    public static final String INVALID_REDIRECT_URI = "缺少授权成功后的回调地址.";

    public static final String INVALID_AUTH_CODE = "错误的授权码.";

    public static final String LOGIN_ACTIVITY = "/index.html#/login";

    public static final String OAUTHLOGIN_ACTIVITY = "/index.html#/oauthlogin";

    public static final String INDEX_ACTIVITY = "/index.html";

    //授权码过期时间，单位:毫秒
    public static final Long AUTHORIZATION_CODE_EXPIRES_TIME = 5*60*1000L;

    public static final Long TOKEN_EXPIRES_TIME = 3600L ;
}
