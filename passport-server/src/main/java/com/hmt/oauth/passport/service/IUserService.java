package com.hmt.oauth.passport.service;

import com.hmt.oauth.passport.domain.CurrentUser;
import com.hmt.oauth.passport.domain.dto.UsersDTO;
import com.hmt.oauth.passport.entity.UsersEntity;
import com.hmt.oauth.passport.web.message.MessageResult;
import com.hmt.oauth.passport.web.repones.PageResponse;

import java.util.List;

/**
 * Created by Edwin on 2016/12/19.
 */
public interface IUserService {

    PageResponse<List> queryUsers(UsersDTO usersDTO);

    UsersEntity findUsers(Integer id);

    MessageResult createUsers(UsersDTO usersDTO);

    MessageResult updateUsers(UsersDTO usersDTO);

    MessageResult removeUsers(Integer id);

    MessageResult changePassword(Integer id,String oldPassword, String newPassword);

    UsersEntity findByLoginName(String loginName);

    /***
     * 检查用户登录
     * @param loginName  登录名
     * @param password   登录密码
     * @param bcryptSalt 加密盐
     * @param validatePassword 验证密码
     * @return
     */
    boolean checkUser(String loginName, String password, String bcryptSalt, String validatePassword);

    /***
     * 密码重置
     * @param id
     * @param currentUser
     * @return
     */
    MessageResult updateResetPassword(Integer id,CurrentUser currentUser);

    MessageResult updateActiveUsers(Integer id, Byte isActive,CurrentUser currentUser);
}
