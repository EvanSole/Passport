package com.hmt.oauth.passport.web.filter;

import com.hmt.oauth.passport.utils.SystemConfigUtil;
import com.hmt.oauth.passport.web.vo.CurrentUserEntity;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

/***
 * Passport资源访问拦截器
 */
public class HmtSessionFilter implements Filter {

    private static final Logger _log = LoggerFactory.getLogger(HmtSessionFilter.class);

    public static final String SESSION_KEY = "token_current_user";

    private Pattern exceptUrlPattern; //过滤的URL
    private String ignoreUrlPattern; //过滤静态文件

    private String passport;
    private String clientId; //客户端KEY
    private String clientSecret; //客户端Secret
    private String redirectUrl; //回调地址

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        _log.info("Initialize filterConfig ...");
        String exceptUrlRegex = filterConfig.getInitParameter("exceptUrlRegex");
        if (!StringUtils.isBlank(exceptUrlRegex)) {
            exceptUrlPattern = Pattern.compile(exceptUrlRegex);
        }
        ignoreUrlPattern = filterConfig.getInitParameter("ignoreUrlPattern");
        passport= SystemConfigUtil.get("passport.url").trim();
        clientId = SystemConfigUtil.get("passport.client.id").trim();
        clientSecret = SystemConfigUtil.get("passport.client.secret").trim();
        redirectUrl= SystemConfigUtil.get("passport.redirect.url").trim();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String servletPath = request.getRequestURI();
        // 如果请求的路径与redirectUrl相同，或请求的路径是排除的URL时，则直接放行
        if (servletPath.equals(redirectUrl) || exceptUrlPattern.matcher(servletPath).matches()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //静态资源目录
        if(isRequestUrlExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final HttpSession session = request.getSession(false);
        //从session获取用户信息
        CurrentUserEntity sessionUser = session != null ? (CurrentUserEntity) WebUtils.getSessionAttribute(request, SESSION_KEY) : null;
        if (sessionUser != null) {
            filterChain.doFilter(request, response);
            return;
        }
        final String code = request.getParameter("code");
        if (!StringUtils.isBlank(code)) {
            //通过code获取accessToken
            String accessToken = getAccessToken(code);
            if(StringUtils.isEmpty(accessToken)){
                processFailureRedirect(response,clientId,redirectUrl);
                return ;
            }
            //通过accessToken换取用户信息
            sessionUser = getSessionUserInfo(accessToken);
            //将用户信息存储到session
            WebUtils.setSessionAttribute(request, SESSION_KEY, sessionUser);
            _log.info(" date:{} || user:{} || remoteIP:{} || request address:{}", System.currentTimeMillis(), sessionUser.getUserName(), getRealClientIP(request), request.getServletPath());
            final String urlToRedirectTo = request.getRequestURL().toString();
            processSuccessRedirect(request, response, urlToRedirectTo);
            return;
        }
        //重定向到Passport统一登录页
        processFailureRedirect(response,clientId,redirectUrl);
    }

    @Override
    public void destroy() {

    }

    /***
     * 通过URL拼接方式从定向到回调页
     * @param request
     * @param response
     * @param urlToRedirectTo
     * @throws IOException
     */
    private void processSuccessRedirect(HttpServletRequest request , HttpServletResponse response , String urlToRedirectTo) throws IOException{
        if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equals("XMLHttpRequest")) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset= UTF-8 ");
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }else{
            response.sendRedirect(urlToRedirectTo);
        }
    }

    /***
     * 通过OAuthClient客户端从定向到Passport统一登录页
     * @param response
     * @param clientId
     * @param redirectUri
     * @throws IOException
     */
    protected void processFailureRedirect(HttpServletResponse response ,String clientId, String redirectUri) throws IOException {
        try {
            OAuthClientRequest authRequest = OAuthClientRequest
                    .authorizationLocation("/oauth2/authorize")
                    .setResponseType(ResponseType.CODE.toString())
                    .setClientId(clientId)
                    .setRedirectURI(redirectUri)
                    .buildQueryMessage();
            // send the client to the authentication process
            _log.info("Authorization request location URI: {0}",authRequest.getLocationUri());
            response.sendRedirect( passport + authRequest.getLocationUri());
            return ;
        } catch (OAuthSystemException e) {
            _log.error("Failed to extract client request for Client credentials grant flow . exception message " + e );
        }
    }


    /***
     * 根据Code获取accessToken
     * @param code
     * @return
     */
    private String getAccessToken(String code){
        String accessToken = "";
        try {
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthClientRequest oAuthClientRequest = OAuthClientRequest.tokenLocation(passport + "/oauth2/accessToken")
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setCode(code)
                    .setRedirectURI(redirectUrl).buildBodyMessage();
            OAuthResourceResponse  resourceResponse = oAuthClient.resource(oAuthClientRequest, OAuth.HttpMethod.POST, OAuthResourceResponse.class);
            //获取oAuthClient请求返回的resourceResponse
            String resourceResponseBody = resourceResponse.getBody();
            _log.info(" OAuthResourceResponse, body{}" + resourceResponseBody);
            if(StringUtils.isNotEmpty(resourceResponseBody)) {
                Map responseMap = JSONUtils.parseJSON(resourceResponseBody);
                String httpStatus = MapUtils.getString(responseMap, "code");
                if (HttpStatus.OK.toString().equals(httpStatus)) {
                    Map<String, Object> paramMap = JSONUtils.parseJSON(MapUtils.getString(responseMap, "data"));
                    responseMap.clear(); //清理原集合数据
                    responseMap.putAll(paramMap);//存放目的数据
                }
                //获取Access Token
                accessToken = MapUtils.getString(responseMap,"access_token");
            }
        } catch (OAuthSystemException e) {
            _log.error("Failed to extract client request for Client credentials grant flow.{}", e);
        } catch (OAuthProblemException ex) {
            _log.error("Get Access token failure,error trying to access oauth server. {}", ex);
        }
        return accessToken;
    }


    /***
     * 通过accessToken换取用户信息
     * @param accessToken
     * @return
     */
    private CurrentUserEntity getSessionUserInfo(String accessToken){
        CurrentUserEntity currentUserEntity = new CurrentUserEntity();
        try {
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthClientRequest oAuthClientRequest = new OAuthBearerClientRequest(passport + "/api/v1/userInfo")
                    .setAccessToken(accessToken).buildQueryMessage();
            OAuthResourceResponse  resourceResponse = oAuthClient.resource(oAuthClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            //获取oAuthClient请求返回的resourceResponse
            String resourceResponseBody = resourceResponse.getBody();
            if(StringUtils.isNotEmpty(resourceResponseBody)) {
                Map responseMap = JSONUtils.parseJSON(resourceResponseBody);
                String code = MapUtils.getString(responseMap,"code");
                if(HttpStatus.OK.toString().equals(code)){
                    Map<String, Object> paramMap = JSONUtils.parseJSON(MapUtils.getString(responseMap,"data"));
                    responseMap.clear(); //清理原集合数据
                    responseMap.putAll(paramMap);//存放目的数据
                }
                //将用户信息存放到currentUserEntity
                currentUserEntity.setId(MapUtils.getLong(responseMap,"id"));
                currentUserEntity.setTenantId(MapUtils.getLong(responseMap,"tenantId"));
                currentUserEntity.setUserName(MapUtils.getString(responseMap,"loginName"));
                currentUserEntity.setIsAdmin(MapUtils.getByte(responseMap,"isAdmin"));
                currentUserEntity.setRealName(MapUtils.getString(responseMap,"userName"));
                currentUserEntity.setAccessToken(accessToken);
            }
        } catch (OAuthSystemException e) {
            _log.error(" Get userInfo failed!", e);
        } catch (OAuthProblemException e) {
            _log.error(" Get userInfo failed!", e);
        }
        return currentUserEntity;
    }



    /***
     * 获取登录IP地址
     * @param request
     * @return
     */
    private String getRealClientIP(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /***
     * 静态资源过滤
     * @param request
     * @return
     */
    protected boolean isRequestUrlExcluded(final HttpServletRequest request) {
        boolean flag = false;
        String requestPath = request.getRequestURI();
        PathMatcher matcher = new AntPathMatcher();
        //需要过滤的目录
        String[] ignoreArray = ignoreUrlPattern.split(",");
        for(String ignoreUrlPattern : ignoreArray){
            if(matcher.match(ignoreUrlPattern, requestPath)){
                flag = true;
                break;
            }
        }
        return flag;
    }


    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
