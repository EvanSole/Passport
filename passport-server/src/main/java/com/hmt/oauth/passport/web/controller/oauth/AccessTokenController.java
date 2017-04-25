package com.hmt.oauth.passport.web.controller.oauth;

import com.hmt.oauth.passport.service.IOAuthService;
import com.hmt.oauth.passport.web.constants.PassPortConstants;
import com.hmt.oauth.passport.web.repones.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;

/**
 * Created by Edwin on 2016/12/19.
 */
@Api(value = "accessToken")
@RestController
@RequestMapping("/oauth2")
public class AccessTokenController {

    private final static Logger _log = LoggerFactory.getLogger(AccessTokenController.class);

    @Autowired
    IOAuthService oAuthService;

    @ApiOperation(value = "获取accessToken", notes = "根据授权码换取accessToken.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "client_id", value = "应用Id", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "client_secret", value = "客户端secret", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "grant_type", value = "授权类型[authorization_code]", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "redirect_uri", value = "应用回调地址", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "code", value = "授权码", required = true, paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/accessToken",method = RequestMethod.POST )
    public ResponseResult token(HttpServletRequest request) throws URISyntaxException, OAuthSystemException {
        ResponseResult responseResult = new ResponseResult();
        try {
            //构建OAuth请求
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
            //检查提交的客户端id是否正确
            if (!oAuthService.checkClientId(oauthRequest.getClientId())) {
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.CodeResponse.INVALID_REQUEST)
                                .setErrorDescription("The client ID is unknown: "+ oauthRequest.getClientId())
                                .buildJSONMessage();
                _log.info("The client ID is unknown: {}", oauthRequest.getClientId());
                responseResult.setCode(HttpStatus.valueOf(response.getResponseStatus()).toString());
                responseResult.setMessage(response.getBody());
                return responseResult;
            }
            // 检查客户端安全KEY是否正确
            if (!oAuthService.checkClientSecret(oauthRequest.getClientSecret())) {
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.CodeResponse.INVALID_REQUEST)
                                .setErrorDescription("The client Secret is unknown: "+ oauthRequest.getClientSecret())
                                .buildJSONMessage();
                _log.info("The client Secret is unknown: {}", oauthRequest.getClientSecret());
                responseResult.setCode(HttpStatus.valueOf(response.getResponseStatus()).toString());
                responseResult.setMessage(response.getBody());
                return responseResult;
            }
            String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
            String grantType = oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE);
            // 验证授权类型
            if (!GrantType.AUTHORIZATION_CODE.toString().equals(grantType)) {
                    OAuthResponse response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.CodeResponse.UNSUPPORTED_RESPONSE_TYPE)
                            .setErrorDescription("The response type must be '" +
                                    ResponseType.CODE.toString() +"' but was instead: "+ grantType)
                            .buildJSONMessage();
                _log.info("The response type must be \'{}\' but was instead: {} ",ResponseType.CODE.toString(),grantType);
                responseResult.setCode(HttpStatus.valueOf(response.getResponseStatus()).toString());
                responseResult.setMessage(response.getBody());
                return responseResult;
            }

            // 检查验证码是否有效
            if (!oAuthService.checkAuthCode(authCode) ) {
                OAuthResponse response = OAuthASResponse
                        .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
                        .setErrorDescription("Client validation failed, authorization code error or expired.")
                        .buildJSONMessage();
                _log.info("客户端验证失败,authorizationCode授权码错误或已过期.");
                responseResult.setCode(HttpStatus.valueOf(response.getResponseStatus()).toString());
                responseResult.setMessage(response.getBody());
                return responseResult;
            }
            //生成Access Token
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            final String accessToken = oauthIssuerImpl.accessToken();
            oAuthService.createAccessToken(accessToken, oAuthService.getLoginNameByAuthCode(authCode));
            //生成OAuth响应
            OAuthResponse response = OAuthASResponse
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setAccessToken(accessToken)
                    .setExpiresIn(String.valueOf(PassPortConstants.TOKEN_EXPIRES_TIME))
                    .buildJSONMessage();
            _log.info("通过授权码换取令牌成功AccessToken :{}",accessToken);
            responseResult.setCode(HttpStatus.valueOf(response.getResponseStatus()).toString());
            responseResult.setMessage("Get access token success.");
            responseResult.setData(response.getBody());
            return responseResult;

        } catch (OAuthProblemException e) {
            //构建错误响应
            OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .error(e)
                    .setErrorDescription("服务端获取accessToken令牌失败,请核对authorizationCode授权码是否有效!")
                    .buildJSONMessage();
            _log.error("Get Access token failure,exception message:{}",e);
            responseResult.setCode(HttpStatus.valueOf(response.getResponseStatus()).toString());
            responseResult.setMessage(response.getBody());
            return responseResult;
        }
    }

    /**
     * 验证accessToken
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/checkAccessToken", method = RequestMethod.POST)
    public ResponseResult checkAccessToken(@RequestParam("accessToken") String accessToken) {
        ResponseResult responseResult = new ResponseResult();
        boolean flag = oAuthService.checkAccessToken(accessToken);
        _log.info("AccessToken:{}令牌安全验证,状态:{}",accessToken,flag);
        if(flag){
            responseResult.setCode(HttpStatus.valueOf(HttpServletResponse.SC_OK).toString());
            responseResult.setMessage("AccessToken令牌安全验证通过.");
            return responseResult;
        }else{
            responseResult.setCode(HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED).toString());
            responseResult.setMessage("AccessToken令牌错误或已过期.");
            return responseResult;
        }
    }

}
