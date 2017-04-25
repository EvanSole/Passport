package com.hmt.oauth.passport.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Form;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;


/**
 * Created by Edwin on 2016/12/30.
 */
public class AccessTokenTest {

    public final static String passport = "http://127.0.0.1:8081";
    public final static String clientId = "877645414466310"; //客户端KEY
    public final static String clientSecret = "7210878826c026946ffcd485b6170086cb935cc9"; //客户端Secret
    public final static String redirectUrl = "http://www.baidu.com"; //回调地址

    private String userName = "admin";
    private String password = "123456";


    @Test
    public void TestAuthorizationCode() throws OAuthSystemException, OAuthProblemException,MalformedURLException {
        // 创建表单，模拟填充表单并提交表单
        Form form = new Form();
        form.param("loginName",userName);
        form.param("password",password);
        form.param("client_id",clientId);
        form.param("response_type", ResponseType.CODE.toString());
        form.param("redirect_uri",redirectUrl);

        ResteasyClient client = new ResteasyClientBuilder().build();
        Response response = client.target(passport+"/auth2/authorize")
                .request()
                .post(Entity.form(form));
        String location = response.getLocation().toURL().toString();
        String authCode = location.substring(location.lastIndexOf("=")+1);
        System.out.println("status:"+ response.getStatus()+ "\nlocation:"+ response.getLocation() + "\ncode:"+ authCode +"\n");

    }

    @Test
    public void  getAccessToken() throws OAuthSystemException ,OAuthProblemException{
        String authCode = "ba1b77911878172db801a6ea9fca7a75";
        OAuthClientRequest accessTokenRequest = OAuthClientRequest.tokenLocation(passport + "/auth2/accessToken")
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(redirectUrl)
                .setCode(authCode)
                .buildBodyMessage();
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest,OAuthJSONAccessTokenResponse.class);
        System.out.println("accessToken:"+ oAuthResponse.getAccessToken()+ "\nlocation:"+ oAuthResponse.getExpiresIn());
    }

    @Test
    public void validateAccessToken() throws OAuthSystemException, OAuthProblemException, IOException {
        String accessToken = "f30ad1adc299b1f51b5a4f2287a4817e";
        OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(passport + "/auth2/checkAccessToken?accessToken=" + accessToken)
                .buildQueryMessage();
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.POST, OAuthResourceResponse.class);
        InputStream inputStream = resourceResponse.getBodyAsInputStream();
        String result = IOUtils.toString(inputStream, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(result);
        String responseCode = (String)jsonObject.get("code");
        assertEquals(HttpServletResponse.SC_OK , resourceResponse.getResponseCode());
        assertEquals(HttpServletResponse.SC_OK , Integer.parseInt(responseCode));
    }
}
