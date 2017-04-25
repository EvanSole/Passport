package com.hmt.oauth.passport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hmt.oauth.passport.utils.Md5Util;
import com.hmt.oauth.passport.web.vo.CurrentUserEntity;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.JSONUtils;
import org.springframework.http.HttpStatus;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by Edwin on 2017/3/7.
 */
public class OauthClientTest {

    public static final String CLIENT_ID = "877645414466310"; // 应用id client_id
    public static final String CLIENT_SECRET = "7210878826c026946ffcd485b61700862"; // 应用secret client_secret
    public static final String USERNAME = "admin"; // 用户名
    public static final String PASSWORD = "123456"; // 密码
    public static final String BASE_PATH = "http://localhost:8081";
    public static final String OAUTH_SERVER_URL = BASE_PATH + "/oauth2/authorize"; // 授权地址
    public static final String OAUTH_SERVER_TOKEN_URL = BASE_PATH + "/oauth2/accessToken"; // ACCESS_TOKEN换取地址
    public static final String OAUTH_SERVER_REDIRECT_URI = "http://huamengtong.com"; // 回调地址
    public static final String OAUTH_SERVICE_API = BASE_PATH + "/api/v1/userInfo"; // 测试开放数据api

    /**
     * 获取授权码
     *
     * @return
     * @throws OAuthSystemException
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    private static String testAuthorizeCode() throws OAuthSystemException, MalformedURLException, URISyntaxException {
        String code = "";
        //构建请求
        OAuthClientRequest oAuthClientRequest = OAuthClientRequest
                .authorizationLocation(OAUTH_SERVER_URL) // oauth2 认证授权地址
                .setClientId(CLIENT_ID) // CLIENT_ID
                .setRedirectURI(OAUTH_SERVER_REDIRECT_URI) // 回调地址
                .setResponseType(ResponseType.CODE.toString()) // 返回类型值
                .setParameter("loginName", USERNAME)
                .setParameter("password", Md5Util.decryptMd5(PASSWORD))
                .buildQueryMessage();
        //实例化OAuthClient
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthResourceResponse resourceResponse = null;
        try {
            resourceResponse = oAuthClient.resource(oAuthClientRequest, OAuth.HttpMethod.POST, OAuthResourceResponse.class);
            String resourceResponseBody = resourceResponse.getBody();
            System.out.println("resourceResponseBody, body{}" + resourceResponseBody);
            JSONObject jsonObject = JSON.parseObject(resourceResponseBody);
            String location = (String) jsonObject.get("data");
            code = location.substring(location.indexOf("=") + 1);
            System.out.println("Authorize code:" + code);
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 根据授权码获取accessToken
     *
     * @return
     * @throws OAuthProblemException
     * @throws OAuthSystemException
     */
    private static String testAccessToken() throws OAuthProblemException, OAuthSystemException, MalformedURLException, URISyntaxException {
        String accessToken = "";
        try {
            //获取授权码
            String authorizeCode = testAuthorizeCode();
            OAuthClientRequest oAuthClientRequest = OAuthClientRequest
                    .tokenLocation(OAUTH_SERVER_TOKEN_URL)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(CLIENT_ID)
                    .setClientSecret(CLIENT_SECRET)
                    .setCode(authorizeCode)
                    .setRedirectURI(OAUTH_SERVER_REDIRECT_URI)
                    .buildBodyMessage();
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthResourceResponse resourceResponse = oAuthClient.resource(oAuthClientRequest, OAuth.HttpMethod.POST, OAuthResourceResponse.class);
            //获取oAuthClient请求返回的resourceResponse
            String resourceResponseBody = resourceResponse.getBody();
            System.out.println("resourceResponseBody, body{}" + resourceResponseBody);
            Map responseMap = JSONUtils.parseJSON(resourceResponseBody);
            Map<String, Object> paramMap = JSONUtils.parseJSON(MapUtils.getString(responseMap, "data"));
            accessToken = MapUtils.getString(paramMap, "access_token");
            String expiresIn = MapUtils.getString(paramMap, "expires_in");
            System.out.println("Access Token: " + accessToken);
            System.out.println("Expires In: " + expiresIn);
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    /***
     * 根据accessToken获取用户
     */
    private static void testUserInfo() throws MalformedURLException, URISyntaxException {
        try {
            String accessToken = testAccessToken();
            OAuthClientRequest oAuthClientRequest = new OAuthBearerClientRequest(OAUTH_SERVICE_API)
                    .setAccessToken(accessToken)
                    .buildQueryMessage();
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthResourceResponse resourceResponse = oAuthClient.resource(oAuthClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            //获取oAuthClient请求返回的resourceResponse
            String resourceResponseBody = resourceResponse.getBody();
            System.out.println("resourceResponseBody, body{}" + resourceResponseBody);
            Map responseMap = JSONUtils.parseJSON(resourceResponseBody);
            Map<String, Object> paramMap = JSONUtils.parseJSON(MapUtils.getString(responseMap, "data"));
            System.out.println("loginName: " + MapUtils.getString(paramMap,"loginName"));
            System.out.println("userName: " + MapUtils.getString(paramMap,"userName"));
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        //testAuthorizeCode();
        //testAccessToken();
        testUserInfo();
    }

}
