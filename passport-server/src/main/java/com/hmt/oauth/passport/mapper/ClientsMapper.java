package com.hmt.oauth.passport.mapper;

import com.hmt.oauth.passport.entity.ClientsEntity;

import java.util.List;

public interface ClientsMapper {

    Integer countClientsEntity(ClientsEntity clientsEntity);

    List<ClientsEntity> selectClients(ClientsEntity clientsEntity);

    Integer selectClientsTotal(ClientsEntity clientsEntity);

    Integer deleteByPrimaryKey(Integer id);

    Integer insertClients(ClientsEntity clientsEntity);

    ClientsEntity selectByPrimaryKey(Integer id);

    Integer updateClients(ClientsEntity clientsEntity);

    ClientsEntity findByClientAppSecret(String appSecret);

    ClientsEntity findByClientAppKey(String appKey);

    List<ClientsEntity> findByName(String name);

    List findByUserId(Integer userId);

}

