package com.hmt.oauth.passport.web.controller;

import com.hmt.oauth.passport.domain.CurrentUser;
import com.hmt.oauth.passport.entity.UsersEntity;
import com.hmt.oauth.passport.service.IUserService;
import com.hmt.oauth.passport.utils.BeanUtils;
import com.hmt.oauth.passport.utils.IPUtil;
import com.hmt.oauth.passport.utils.Md5Util;
import com.hmt.oauth.passport.web.constants.PassPortConstants;
import com.hmt.oauth.passport.web.repones.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class LoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private IUserService userService;

    /***
     * 用户登录
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/login",method = RequestMethod.POST)
    public ResponseResult login(@RequestParam("username") String username,@RequestParam("password") String password) throws Exception{
        String loginIp = IPUtil.getIPAddress(this.getRequest());
        logger.info(" user {} login ,this login ip is {}  ", username,loginIp);

        if(StringUtils.isBlank(username)){
            return getFaultMessage("E00100");
        }
        if(StringUtils.isBlank(password)){
            return getFaultMessage("E00101");
        }
        UsersEntity userEntity = userService.findByLoginName(username);
        if(userEntity == null){
            return getFaultMessage("E00102");
        }
        String validatePassword = Md5Util.decryptMd5(password + userEntity.getLoginName() + userEntity.getBcryptSalt());
        if(!userEntity.getLoginName().equals(username) || !validatePassword.equals(userEntity.getPassword())){
            return getFaultMessage("E00103");
       }
        if("0".equals(String.valueOf(userEntity.getIsActive()))|| "1".equals(String.valueOf(userEntity.getIsDel()))){
            logger.info("Did not find the account or the account is not activated!");
            return getFaultMessage("E00104");
        }
        //对象拷贝
        CurrentUser currentUser = BeanUtils.copyBeanPropertyUtils(userEntity, CurrentUser.class);
        ResponseResult responseResult = getSucMessage();
        //将用户登录信息存储到Session
        this.getSession().setAttribute(PassPortConstants.SESSION_KEY,currentUser);
        responseResult.setData(currentUser);
        return  responseResult;
    }

    /**
     * 用户退出
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public void loginOut(HttpServletResponse response)throws IOException{
        getRequest().getSession().removeAttribute(PassPortConstants.SESSION_KEY);
        response.sendRedirect("/#/login");
    }



}
