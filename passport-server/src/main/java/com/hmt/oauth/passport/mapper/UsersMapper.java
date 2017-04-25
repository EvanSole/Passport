package com.hmt.oauth.passport.mapper;

import com.hmt.oauth.passport.entity.UsersEntity;

import java.util.List;

public interface UsersMapper {

    List<UsersEntity> selectUsers(UsersEntity usersEntity);

    Integer selectUsersTotal(UsersEntity usersEntity);

    Integer countUsersEntity(UsersEntity usersEntity);

    Integer deleteByPrimaryKey(Integer id);

    Integer insertUsers(UsersEntity usersEntity);

    UsersEntity selectByPrimaryKey(Integer id);

    Integer updateUsers(UsersEntity usersEntity);

    UsersEntity selectByLoginName(String loginName);

    UsersEntity findByLoginName(String loginName);

    UsersEntity findByUserName(String userName);
}

