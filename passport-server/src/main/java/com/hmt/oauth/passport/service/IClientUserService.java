package com.hmt.oauth.passport.service;

import com.hmt.oauth.passport.domain.dto.ClientsUsersDTO;
import com.hmt.oauth.passport.web.message.MessageResult;

import java.util.List;
import java.util.Map;

/**
 * Created by could.hao on 2017/2/20.
 */
public interface IClientUserService {

    MessageResult createOrDeleteClientUser(Map map);

    List queryUserId(Integer clientId);

    List queryClientId(Integer userId);

    MessageResult createClientUser(ClientsUsersDTO clientsUsersDTO);

    MessageResult updateClientUser(ClientsUsersDTO clientsUsersDTO);

    MessageResult deleteByUserId(Integer userId);

    Map findClientUsers(Map paramsMap);

    MessageResult createOrDeleteUserClient(Integer userId,List clientIds);
}
