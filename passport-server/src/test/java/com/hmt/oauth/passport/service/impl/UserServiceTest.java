package com.hmt.oauth.passport.service.impl;

import com.hmt.oauth.passport.common.SpringTxTestCase;
import com.hmt.oauth.passport.domain.dto.UsersDTO;
import com.hmt.oauth.passport.service.IUserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Edwin on 2016/12/23.
 */
public class UserServiceTest extends SpringTxTestCase {

    @Autowired
    IUserService userService;

    @Test
    public void  createUsers(){
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setTenantId(88L);
        usersDTO.setLoginName("test2");
        usersDTO.setUserName("测试2");
        usersDTO.setCardId("323134366123435412");
        usersDTO.setBirthday(new Date());
        usersDTO.setEmail("test2@hmt.com");
        usersDTO.setMobilePhone("13564254245");
        usersDTO.setCreateUser("system");
        usersDTO.setCreateTime(new Date().getTime());
        usersDTO.setUpdateUser("system");
        usersDTO.setUpdateTime(new Date().getTime());
        userService.createUsers(usersDTO);
    }

}
