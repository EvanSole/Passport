package com.hmt.oauth.passport.service.impl;

import com.hmt.oauth.passport.domain.CurrentUser;
import com.hmt.oauth.passport.domain.dto.UsersDTO;
import com.hmt.oauth.passport.entity.ClientsEntity;
import com.hmt.oauth.passport.entity.UsersEntity;
import com.hmt.oauth.passport.mapper.UsersMapper;
import com.hmt.oauth.passport.service.IClientService;
import com.hmt.oauth.passport.service.IClientUserService;
import com.hmt.oauth.passport.service.IUserService;
import com.hmt.oauth.passport.utils.BeanUtils;
import com.hmt.oauth.passport.utils.Md5Util;
import com.hmt.oauth.passport.web.constants.PassPortConstants;
import com.hmt.oauth.passport.web.message.MessageResult;
import com.hmt.oauth.passport.web.repones.PageResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Edwin on 2016/12/19.
 */
@Service
public class UserService implements IUserService {

    @Autowired
    UsersMapper usersMapper;
    @Autowired
    IClientUserService clientUserService;
    @Autowired
    IClientService clientService;

    @Override
    public PageResponse<List> queryUsers(UsersDTO usersDTO) {
        UsersEntity usersEntity = BeanUtils.copyBeanPropertyUtils(usersDTO,UsersEntity.class);
        List<UsersEntity> list = usersMapper.selectUsers(usersEntity);
        list.forEach(x->{
            //密码特殊处理
            x.setPassword("******");
            x.setBcryptSalt("******");
            if("client".equals(x.getTypeCode())){
                List<ClientsEntity> entityList = clientService.getClientNames(x.getId());
                List<String> clientNames = new ArrayList<String>();
                List<Integer> clientIds = new ArrayList<Integer>();
                if(CollectionUtils.isNotEmpty(entityList)){
                    entityList.forEach(c -> {
                        clientIds.add(c.getId());
                        clientNames.add(c.getName());
                    });
                }
                x.setClientName(clientNames.toString());
                x.setClientIds(clientIds);
            }
            x.setBooleanIsActive(x.getIsActive() == 1 ? true : false);
        });
        PageResponse<List> pageResponse = new PageResponse<>();
        pageResponse.setRows(list);
        Integer totalSize = usersMapper.selectUsersTotal(usersEntity);
        pageResponse.setTotal(totalSize);
        return pageResponse;
    }



    @Override
    public UsersEntity findUsers(Integer id) {
        return usersMapper.selectByPrimaryKey(id);
    }

    @Override
    public MessageResult createUsers(UsersDTO usersDTO) {
        UsersEntity user = usersMapper.findByUserName(usersDTO.getUserName());
        if(user != null){
            return MessageResult.getMessage("E00105");
        }
        user = usersMapper.findByLoginName(usersDTO.getLoginName());
        if(user != null){
            return MessageResult.getMessage("E00106");
        }
        UsersEntity usersEntity = BeanUtils.copyBeanPropertyUtils(usersDTO,UsersEntity.class);
        String bcryptSalt = new SecureRandomNumberGenerator().nextBytes().toHex(); //加密盐
        usersEntity.setBcryptSalt(bcryptSalt);
        String password = Md5Util.decryptMd5(StringUtils.isEmpty(usersEntity.getPassword())? PassPortConstants.DEFAULT_PASSWORD : usersEntity.getPassword());
        usersEntity.setPassword(Md5Util.decryptMd5(password + usersEntity.getLoginName() + bcryptSalt));
        usersMapper.insertUsers(usersEntity);
        user = usersMapper.findByLoginName(usersDTO.getLoginName());
        clientUserService.createOrDeleteUserClient(user.getId(),usersDTO.getClientIds());
        return MessageResult.getSucMessage();
    }

    @Override
    public MessageResult updateUsers(UsersDTO usersDTO) {
        UsersEntity oldUser = usersMapper.selectByPrimaryKey(usersDTO.getId());
        UsersEntity user = usersMapper.findByUserName(usersDTO.getUserName());
        if(!oldUser.getUserName().equals(usersDTO.getUserName()) && user != null){
            return MessageResult.getMessage("E00105");
        }
        user = usersMapper.findByLoginName(usersDTO.getLoginName());
        if(!oldUser.getLoginName().equals(usersDTO.getLoginName()) && user != null){
            return MessageResult.getMessage("E00106");
        }
        UsersEntity usersEntity = BeanUtils.copyBeanPropertyUtils(usersDTO,UsersEntity.class);
        usersMapper.updateUsers(usersEntity);
        clientUserService.createOrDeleteUserClient(usersEntity.getId(),usersDTO.getClientIds());
        return MessageResult.getSucMessage();
    }

    @Override
    public MessageResult removeUsers(Integer id) {
        clientUserService.deleteByUserId(id);
        usersMapper.deleteByPrimaryKey(id);
        return MessageResult.getSucMessage();
    }

    @Override
    public MessageResult changePassword(Integer id, String oldPassword, String newPassword) {
        if(StringUtils.isEmpty(oldPassword)){
            return MessageResult.getMessage("E2000");
        }
        if(StringUtils.isEmpty(newPassword)){
            return MessageResult.getMessage("E2001");
        }
        if(oldPassword.equals(newPassword)){
            return MessageResult.getMessage("E2002");
        }
        UsersEntity usersEntity = findUsers(id);
        if(usersEntity == null){
            return MessageResult.getMessage("E2003");
        }
        //验证输入的旧密码与原密码是否一致
        String password = Md5Util.decryptMd5(oldPassword + usersEntity.getLoginName() + usersEntity.getBcryptSalt());
        if(!usersEntity.getPassword().equals(password)){
            return MessageResult.getMessage("E2004");
        }
        usersEntity.setPassword(Md5Util.decryptMd5(newPassword + usersEntity.getLoginName() + usersEntity.getBcryptSalt())); //加密
        usersEntity.setBcryptSalt(usersEntity.getBcryptSalt());
        usersEntity.setUpdateTime(new Date().getTime());
        usersMapper.updateUsers(usersEntity);
        return MessageResult.getSucMessage();
    }

    @Override
    public UsersEntity findByLoginName(String loginName) {
        return usersMapper.selectByLoginName(loginName);
    }

    @Override
    public boolean checkUser(String loginName, String password, String bcryptSalt, String validatePassword) {
        String decryptPassword = Md5Util.decryptMd5(password + loginName + bcryptSalt);
        return validatePassword.equals(decryptPassword);
    }

    @Override
    public MessageResult updateResetPassword(Integer id,CurrentUser currentUser) {
        if( 1 != currentUser.getIsAdmin()){
            return MessageResult.getMessage("E2005");
        }
        UsersEntity usersEntity = this.findUsers(id);
        String bcryptSalt = new SecureRandomNumberGenerator().nextBytes().toHex(); //加密盐
        usersEntity.setPassword(Md5Util.decryptMd5(Md5Util.decryptMd5(PassPortConstants.DEFAULT_PASSWORD) + usersEntity.getLoginName() + bcryptSalt)); //加密
        usersEntity.setBcryptSalt(bcryptSalt);
        usersEntity.setUpdateTime(new Date().getTime());
        usersEntity.setUpdateUser(currentUser.getUserName());
        usersMapper.updateUsers(usersEntity);
        return MessageResult.getSucMessage();
    }

    @Override
    public MessageResult updateActiveUsers(Integer id, Byte isActive ,CurrentUser currentUser) {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setId(id);
        usersEntity.setIsActive(isActive);
        usersEntity.setUpdateTime(new Date().getTime());
        usersEntity.setUpdateUser(currentUser.getUserName());
        usersMapper.updateUsers(usersEntity);
        return MessageResult.getSucMessage();
    }

}
