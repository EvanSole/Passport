package com.hmt.oauth.passport.web.controller.oauth;

import com.hmt.oauth.passport.entity.ClientsEntity;
import com.hmt.oauth.passport.entity.UsersEntity;
import com.hmt.oauth.passport.service.IClientService;
import com.hmt.oauth.passport.service.IOAuthService;
import com.hmt.oauth.passport.service.IUserService;
import com.hmt.oauth.passport.utils.SystemConfigUtil;
import com.hmt.oauth.passport.web.constants.PassPortConstants;
import com.hmt.oauth.passport.web.repones.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URLEncoder;

/**
 * Created by Edwin on 2016/12/19.
 */
@RestController
@RequestMapping("/oauth2")
public class AuthorizeController {

    private final static Logger _log = LoggerFactory.getLogger(AuthorizeController.class);

    @Autowired
    IOAuthService oAuthService;

    @Autowired
    IClientService clientService;

    @Autowired
    IUserService userService;


    /***
     * 请求获取授权码
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/authorize",method = { RequestMethod.GET })
    public ResponseResult authorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResponseResult responseResult = new ResponseResult();
        try {
            _log.info("Create the OAuth request from the HTTP request.");
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            String clientId = oauthRequest.getClientId();//获取clientId
            String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);//获取应用回调地址
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);//responseType目前仅支持CODE，另外还有TOKEN
            //验证参数是否缺失
            responseResult = validateInvalid(clientId,redirectURI,responseType,oauthRequest);
            if(responseResult.getCode().equals(HttpStatus.OK.toString())){
                ClientsEntity clientsEntity = clientService.findByClientAppkey(clientId);
                String basePath = SystemConfigUtil.get("passport.basePath");
                final String redirect_uri = basePath +"/index.html#/oauthlogin?client_id=" + clientId + "&redirect_uri="
                        + oauthRequest.getRedirectURI() + "&response_type=" + responseType + "&client_name=" + URLEncoder.encode(clientsEntity.getName(), "UTF-8");
                response.sendRedirect(redirect_uri);//登录页
                return null;
            }
        } catch (OAuthProblemException e) {
            String redirectUri = e.getRedirectUri();
            final OAuthResponse authResponse =
                    OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                            .error(e).setErrorDescription("请求登录认证服务失败,请核对参数是否正确!")
                            .location(redirectUri)
                            .buildQueryMessage();
            _log.error("OAuth request failure ,exception message:"+ e);
            // Set the status and return the error message.
            responseResult.setCode(HttpStatus.valueOf(authResponse.getResponseStatus()).toString());
            responseResult.setMessage("请求登录认证服务失败,请核对参数是否正确!");
            return responseResult;
        }
        return responseResult;
    }



    /***
     * 请求获取授权码
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "获取授权码", notes = "根据客户端Id请求获取授权码.", httpMethod = "POST")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "client_id", value = "应用Id", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "redirect_uri", value = "应用回调地址", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "response_type", value = "授权类型", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "loginName", value = "登录名", paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "password", value = "密码", paramType = "query", dataType = "String")
    })
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = ResponseResult.class)})
    @RequestMapping(value = "/authorize",method = { RequestMethod.POST })
    public ResponseResult authenticate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResponseResult responseResult = new ResponseResult();
        try {
            _log.info("Create the OAuth request from the HTTP request.");
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            String clientId = oauthRequest.getClientId();//获取clientId
            String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);//获取应用回调地址
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);//responseType目前仅支持CODE，另外还有TOKEN
            //验证参数是否缺失
            responseResult = validateInvalid(clientId,redirectURI,responseType,oauthRequest);
            if(responseResult.getCode().equals(HttpStatus.OK.toString())) {
                //验证用户
                if (!validateLogin(request)) {
                    OAuthResponse oauthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_FORBIDDEN)
                            .setError(OAuthError.CodeResponse.INVALID_REQUEST)
                            .setErrorDescription("用户名或密码输入错误.")
                            .setState(oauthRequest.getState())
                            .buildJSONMessage();
                    _log.error("username or password is not mismatch.");
                    // Set the status and return the error message.
                    responseResult.setCode(HttpStatus.valueOf(oauthResponse.getResponseStatus()).toString());
                    responseResult.setMessage("用户名或密码输入错误.");
                    return responseResult;
                }
                String loginName = request.getParameter("loginName"); //获取用户登录名
                //生成授权码
                String authorizationCode = null;
                if (responseType.equals(ResponseType.CODE.toString())) {
                    OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                    authorizationCode = oauthIssuerImpl.authorizationCode();
                    oAuthService.addAuthCode(authorizationCode, oauthRequest.getClientId(), loginName,
                            PassPortConstants.AUTHORIZATION_CODE_EXPIRES_TIME);
                    _log.info("Create the temporary code to be granted or rejected by the user ,authorization code:{}", authorizationCode);
                }
                //build OAuth response
                OAuthResponse oAuthResponse = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_OK)
                        .setCode(authorizationCode)
                        .location(redirectURI)
                        .buildQueryMessage();//设置授权码
                responseResult.setCode(HttpStatus.valueOf(oAuthResponse.getResponseStatus()).toString());
                responseResult.setData(oAuthResponse.getLocationUri());
                responseResult.setMessage("");
                return responseResult;
            }
        } catch (OAuthProblemException e) {
            String redirectUri = e.getRedirectUri();
            final OAuthResponse authResponse =
                    OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                            .error(e).setErrorDescription("请求登录认证服务失败,请核对参数是否正确!")
                            .location(redirectUri)
                            .buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(authResponse.getLocationUri()));
            _log.error("OAuth request failure ,exception message:"+ e);
            responseResult.setCode(HttpStatus.valueOf(authResponse.getResponseStatus()).toString());
            responseResult.setMessage(authResponse.getBody());
            return responseResult;
        }
        return  responseResult;
    }



    /***
     * 验证授权参数
     * @param clientId
     * @param redirectURI
     * @param responseType
     * @param oauthRequest
     * @return
     * @throws OAuthSystemException
     * @throws OAuthProblemException
     */
    private ResponseResult validateInvalid(String clientId,String redirectURI,String responseType,OAuthAuthzRequest oauthRequest)
            throws OAuthSystemException,OAuthProblemException {
        ResponseResult responseResult = new ResponseResult();
        //验证回调地址
        if (StringUtils.isEmpty(redirectURI)) {
            OAuthResponse oauthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(OAuthError.CodeResponse.INVALID_REQUEST)
                    .setErrorDescription("redirect_uri must not be null.")
                    .setState(oauthRequest.getState())
                    .buildJSONMessage();
            _log.error("redirect_uri must not be null.");
            // Set the status and return the error message.
            responseResult.setCode(HttpStatus.valueOf(oauthResponse.getResponseStatus()).toString());
            responseResult.setMessage(oauthResponse.getBody());
            return responseResult;
        }
        //验证授权类型
        if (!ResponseType.CODE.toString().equals(responseType)) {
            OAuthResponse oauthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(OAuthError.CodeResponse.UNSUPPORTED_RESPONSE_TYPE)
                    .setErrorDescription("The response type must be '" +ResponseType.CODE.toString() + " ' but was instead: "
                            + oauthRequest.getResponseType())
                    .setState(oauthRequest.getState())
                    .buildJSONMessage();
            _log.info("The response type must be \'{}\',but was instead:{}",ResponseType.CODE.toString(),oauthRequest.getResponseType());
            // Set the status and return the error message.
            responseResult.setCode(HttpStatus.valueOf(oauthResponse.getResponseStatus()).toString());
            responseResult.setMessage(oauthResponse.getBody());
            return responseResult;
        }
        //验证客户端key,clientId指的appkey
        if (StringUtils.isEmpty(clientId) || !oAuthService.checkClientId(clientId)) {
            OAuthResponse oauthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(OAuthError.CodeResponse.INVALID_REQUEST)
                    .setErrorDescription("The client ID is unknown: " + oauthRequest.getClientId())
                    .setState(oauthRequest.getState())
                    .buildJSONMessage();
            // Set the status and return the error message.
            _log.info("The client ID is unknown:{}",oauthRequest.getClientId());
            // Set the status and return the error message.
            responseResult.setCode(HttpStatus.valueOf(oauthResponse.getResponseStatus()).toString());
            responseResult.setMessage(oauthResponse.getBody());
            return responseResult;
        }
        responseResult.setCode(HttpStatus.OK.toString());
        responseResult.setMessage("The Parameter validation Success.");
        return responseResult;
    }

    /***
     * 登录密码验证
     * @param request
     * @return
     */
    private boolean validateLogin(HttpServletRequest request) throws OAuthProblemException{
        //第一次302到登录页无需验证用户信息，直接跳转到统一登录页，
        if("get".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");
        if (StringUtils.isNotBlank(loginName) && StringUtils.isNotBlank(password)) {
            // 根据登录名获取用户
            UsersEntity usersEntity = userService.findByLoginName(loginName);
            if(usersEntity != null && userService.checkUser(loginName,password,usersEntity.getBcryptSalt(),usersEntity.getPassword())){
                return true;
            }else{
                _log.info("username or password is incorrect.");
                return false;
            }
        }else {
            _log.info("username or password is empty.");
            return false;
        }
    }
}
