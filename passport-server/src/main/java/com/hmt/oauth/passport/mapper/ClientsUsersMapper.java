package com.hmt.oauth.passport.mapper;

import com.hmt.oauth.passport.entity.ClientsUsersEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClientsUsersMapper {

    Integer deleteByPrimaryKey(Long id);

    Integer insert(ClientsUsersEntity clientsUsersEntity);

    Integer update(ClientsUsersEntity clientsUsersEntity);

    List selectUserId(Integer clientId);

    List selectClientId(Integer userId);

    Integer deleteByUserId(Integer userId);

    Integer insertBatch(List<ClientsUsersEntity> list);

    Integer deleteBatch(@Param("clientId")Integer clientId,@Param("list") List list);

    Integer deleteBatchByUserId(@Param("userId")Integer userId,@Param("list") List list);

}

