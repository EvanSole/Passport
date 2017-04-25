package com.hmt.oauth.passport.web.controller.openapi;

import com.alibaba.fastjson.JSON;
import com.hmt.oauth.passport.domain.vo.UserInfoVO;
import com.hmt.oauth.passport.entity.UsersEntity;
import com.hmt.oauth.passport.service.impl.OAuthService;
import com.hmt.oauth.passport.service.impl.UserService;
import com.hmt.oauth.passport.utils.BeanUtils;
import com.hmt.oauth.passport.web.repones.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Edwin on 2016/12/27.
 */
@RestController
@RequestMapping("/api/v1")
public class OpenApiUserController {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "获取用户信息", notes = "根据授权Token获取用户信息.")
    @ApiImplicitParam(name = "access_token", value = "授权Token", required = true, paramType = "query", dataType = "String")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = ResponseResult.class)})
    @RequestMapping(value = "/userInfo",method = RequestMethod.GET )
    public ResponseResult userInfo(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException {
        ResponseResult responseResult = new ResponseResult();
        try {
            //构建OAuth资源请求
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
            //TODO 获取用户数据前，需要验证accessToken是否有效，目前使用Oauth2TokenFilter过滤器统一验证处理
            String accessToken = oauthRequest.getAccessToken();//获取Access Token
//            if(!oAuthService.checkAccessToken(accessToken)){
//                OAuthResponse oauthResponse = OAuthRSResponse
//                        .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
//                        .setErrorDescription("AccessToken令牌错误或已过期.")
//                        .buildJSONMessage();
//                responseResult.setCode(HttpStatus.valueOf(oauthResponse.getResponseStatus()).toString());
//                responseResult.setMessage(oauthResponse.getBody());
//                return responseResult;
//            }
            //获取用户名
            String loginName = oAuthService.findLoginNameByAccessToken(accessToken);
            if (StringUtils.isNotBlank(loginName)) {
                UsersEntity user = userService.findByLoginName(loginName);
                UserInfoVO userInfoVO = BeanUtils.copyBeanPropertyUtils(user, UserInfoVO.class);
                responseResult.setCode(HttpStatus.OK.toString());
                responseResult.setData(JSON.toJSON(userInfoVO));
                return responseResult;
            }
        } catch (OAuthProblemException e) {
            //检查是否设置了错误码
            String errorCode = e.getError();
            if (OAuthUtils.isEmpty(errorCode)) {
                OAuthResponse oauthResponse = OAuthRSResponse
                        .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .buildJSONMessage();
                responseResult.setCode(HttpStatus.valueOf(oauthResponse.getResponseStatus()).toString());
                responseResult.setMessage(oauthResponse.getBody());
                return responseResult;
            }
            OAuthResponse oauthResponse = OAuthRSResponse
                    .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                    .setError(e.getError())
                    .setErrorDescription(e.getDescription())
                    .setErrorUri(e.getUri())
                    .buildJSONMessage();
            responseResult.setCode(HttpStatus.valueOf(oauthResponse.getResponseStatus()).toString());
            responseResult.setMessage(oauthResponse.getBody());
            return responseResult;
        }
        return null;
    }
}
