package com.hmt.oauth.passport.web.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Edwin on 2016/12/19.
 * accessToken 过滤器，主要用来验证token是否有效及有效时间
 */
public class Oauth2TokenFilter implements Filter {

    public static final Logger _log = LoggerFactory.getLogger(Oauth2TokenFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getRequestURI();
        _log.info(" Oauth2TokenFilter action ,request servletPath {}",servletPath);
        try {
            //如果是get请求直接过滤
            if("get".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }
            String accessToken = request.getParameter("accessToken");
            if(StringUtils.isNotBlank(accessToken) && !checkAccessToken(accessToken)) {
                // 如果不存在/过期了，返回未验证错误，需重新验证
                oAuthFailureResponse(response);
                return;
            }
            filterChain.doFilter(request, response);

        }catch (OAuthProblemException e){
            // 如果不存在/过期了，返回未验证错误，需重新验证
            try {
                oAuthFailureResponse(response);
            } catch (OAuthSystemException ez) {
                _log.error("Error trying to access oauth server {}", ez);
            }
        } catch (OAuthSystemException ex) {
            _log.error("Error trying to access oauth server {}", ex);
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * oAuth认证失败时的输出
     * @param response
     * @throws OAuthSystemException
     * @throws IOException
     */
    private void oAuthFailureResponse(HttpServletResponse response) throws OAuthSystemException, IOException {
        OAuthResponse oauthResponse = OAuthRSResponse
                .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
                .buildHeaderMessage();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        response.addHeader(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
        PrintWriter writer = response.getWriter();
        //返回令牌无效或者已过期
        Map resultMap = new HashMap<>();
        resultMap.put("code",HttpStatus.UNAUTHORIZED.value());
        resultMap.put("message","AccessToken is invalid or expired, verify that the token is correct!");
        _log.info("AccessToken is invalid or expired, verify that the token is correct!");
        Gson gson = new GsonBuilder().create();
        writer.write(gson.toJson(resultMap));
        writer.flush();
        writer.close();
    }

    /**
     * 验证accessToken
     * @param accessToken
     * @return
     * @throws IOException
     */
    private boolean checkAccessToken(String accessToken) throws OAuthSystemException, OAuthProblemException, IOException {
        OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest("/checkAccessToken?accessToken=" + accessToken)
                .setAccessToken(accessToken)
                .buildQueryMessage();
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.POST, OAuthResourceResponse.class);
        InputStream inputStream = resourceResponse.getBodyAsInputStream();
        String result = IOUtils.toString(inputStream, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(result);
        String responseCode = (String)jsonObject.get("code");
        //http请求正常且验证AccessToken返回200,则表示验证通过
        assertEquals(HttpServletResponse.SC_OK , resourceResponse.getResponseCode());
        return HttpServletResponse.SC_OK == resourceResponse.getResponseCode() && HttpServletResponse.SC_OK  == Integer.parseInt(responseCode);
    }
}
