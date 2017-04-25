package com.hmt.oauth.passport.web.filter;

import com.hmt.oauth.passport.domain.CurrentUser;
import com.hmt.oauth.passport.web.constants.PassPortConstants;
import org.apache.commons.lang3.StringUtils;
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
import java.util.regex.Pattern;


public class PassportSessionFilter implements Filter {

    private static final Logger _log = LoggerFactory.getLogger(PassportSessionFilter.class);

    private Pattern exceptUrlPattern; //过滤URL,正则表达式
    private String  ignoreUrlRegex; //排除静态资源文件,多个文件或文件夹使用逗号","分隔

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        _log.info(" initialize filterConfig ...");
        String exceptUrlRegex = filterConfig.getInitParameter("exceptUrlPattern");
        if (!StringUtils.isBlank(exceptUrlRegex)) {
            exceptUrlPattern = Pattern.compile(exceptUrlRegex);
        }
        ignoreUrlRegex = filterConfig.getInitParameter("ignoreUrlRegex");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getRequestURI();
        _log.info(" PassportSessionFilter action ,request servletPath {}.",servletPath);
        //如果请求地址是静态资源文件路径直接放行
        if(isRequestUrlExcluded(servletPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 如果请求路径是排除的URL时，则直接放行
        if (exceptUrlPattern.matcher(servletPath).matches() || "/".equals(servletPath) || servletPath.endsWith(PassPortConstants.INDEX_ACTIVITY) ||
                servletPath.endsWith(PassPortConstants.LOGIN_ACTIVITY) || servletPath.endsWith(PassPortConstants.OAUTHLOGIN_ACTIVITY)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        final HttpSession session = request.getSession(false);
        //从session获取用户信息
        CurrentUser sessionUser = session != null ? (CurrentUser) WebUtils.getSessionAttribute(request, PassPortConstants.SESSION_KEY) : null;
        if (sessionUser != null) {
            filterChain.doFilter(request, response);
            return;
        }
        //重定向到Passport登录页
        processRedirect(request, response, PassPortConstants.LOGIN_ACTIVITY);
    }

    @Override
    public void destroy() {
    }

    /***
     * 通过URL拼接方式从定向到Passport登录页
     * @param request
     * @param response
     * @param urlToRedirectTo
     * @throws IOException
     */
    private void processRedirect(HttpServletRequest request , HttpServletResponse response , String urlToRedirectTo) throws IOException{
        if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equals("XMLHttpRequest")) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset= UTF-8 ");
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }else{
            _log.info("redirectUri:{}",urlToRedirectTo);
            response.sendRedirect(urlToRedirectTo);
        }
    }

    /***
     * 过滤静态资源文件
     * @return
     */
    private boolean isRequestUrlExcluded(String requestPath) {
        boolean flag = false;
        PathMatcher matcher = new AntPathMatcher();
        //需要过滤的目录，多个使用逗号分隔
        String[] ignoreArray = ignoreUrlRegex.split(",");
        for(String ignoreUrlPattern : ignoreArray){
            if(matcher.match(ignoreUrlPattern, requestPath)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public Pattern getExceptUrlPattern() {
        return exceptUrlPattern;
    }

    public void setExceptUrlPattern(Pattern exceptUrlPattern) {
        this.exceptUrlPattern = exceptUrlPattern;
    }

    public String getIgnoreUrlRegex() {
        return ignoreUrlRegex;
    }

    public void setIgnoreUrlRegex(String ignoreUrlRegex) {
        this.ignoreUrlRegex = ignoreUrlRegex;
    }
}
