package com.hmt.oauth.passport.service;

import com.hmt.oauth.passport.domain.dto.ClientsDTO;
import com.hmt.oauth.passport.entity.ClientsEntity;
import com.hmt.oauth.passport.web.message.MessageResult;
import com.hmt.oauth.passport.web.repones.PageResponse;

import java.util.List;

/**
 * Created by Edwin on 2016/12/20.
 */
public interface IClientService {

    PageResponse<List> queryClients(ClientsDTO clientsDTO);

    MessageResult createClients(ClientsDTO clientsDTO);

    MessageResult updateClients(ClientsDTO clientsDTO);

    MessageResult removeClients(Integer clientId);

    ClientsEntity findByPrimaryKey(Integer clientId);

    ClientsEntity findByClientAppkey(String appKey);

    ClientsEntity findByClientAppSecret(String clientSecret);

    List<ClientsEntity> getClientNames(Integer userId);
}
