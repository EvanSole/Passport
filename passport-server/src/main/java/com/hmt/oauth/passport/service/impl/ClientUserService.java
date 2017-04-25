package com.hmt.oauth.passport.service.impl;

import com.hmt.oauth.passport.domain.dto.ClientsUsersDTO;
import com.hmt.oauth.passport.domain.dto.UsersDTO;
import com.hmt.oauth.passport.entity.ClientsUsersEntity;
import com.hmt.oauth.passport.mapper.ClientsUsersMapper;
import com.hmt.oauth.passport.service.IClientUserService;
import com.hmt.oauth.passport.service.IUserService;
import com.hmt.oauth.passport.utils.BeanUtils;
import com.hmt.oauth.passport.web.message.MessageResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by could.hao on 2017/2/20.
 */
@Service
public class ClientUserService implements IClientUserService {

    @Autowired
    ClientsUsersMapper clientsUsersMapper;
    @Autowired
    IUserService userService;

    @Override
    public MessageResult createOrDeleteClientUser(Map map) {
        //应用Id
        Integer clientId = MapUtils.getInteger(map,"clientId");
        //需要分配用户Id集合
        List<Integer> newUserIds = (List<Integer>) MapUtils.getObject(map,"user");
        //已分配的用户Id集合
        List<Integer> oldUserIds = queryUserId(clientId);
        if(CollectionUtils.isEmpty(newUserIds)){
            newUserIds = new ArrayList();
        }
        //合并后需要删除的用户
        List<Integer>  delUserIds = ListUtils.subtract(oldUserIds,ListUtils.intersection(oldUserIds,newUserIds));
        if(CollectionUtils.isNotEmpty(delUserIds)) {
            clientsUsersMapper.deleteBatch(clientId, delUserIds);
        }
        //新增的用户
        List<Integer> newUsers = ListUtils.subtract(newUserIds,oldUserIds);
        if(CollectionUtils.isNotEmpty(newUsers)) {
            //合并集合
            List<ClientsUsersEntity> list = new ArrayList<ClientsUsersEntity>();
            for (Integer id : newUsers) {
                ClientsUsersEntity c = new ClientsUsersEntity();
                c.setClientId(clientId);
                c.setUserId(id);
                c.setTimestamps(new Date());
                c.setRevoked(new Byte("1"));
                list.add(c);
            }
            clientsUsersMapper.insertBatch(list);
        }
        return MessageResult.getSucMessage();
    }



    @Override
    public List queryUserId(Integer clientId) {
        return clientsUsersMapper.selectUserId(clientId);
    }

    @Override
    public MessageResult createClientUser(ClientsUsersDTO clientsUsersDTO) {
        ClientsUsersEntity clientsUsersEntity = BeanUtils.copyBeanPropertyUtils(clientsUsersDTO,ClientsUsersEntity.class);
         clientsUsersMapper.insert(clientsUsersEntity);
        return MessageResult.getSucMessage();
    }

    @Override
    public MessageResult updateClientUser(ClientsUsersDTO clientsUsersDTO) {
        ClientsUsersEntity clientsUsersEntity = BeanUtils.copyBeanPropertyUtils(clientsUsersDTO,ClientsUsersEntity.class);
        clientsUsersMapper.update(clientsUsersEntity);
        return MessageResult.getSucMessage();
    }

    @Override
    public List queryClientId(Integer userId) {
        return clientsUsersMapper.selectClientId(userId);
    }

    @Override
    public MessageResult deleteByUserId(Integer userId) {
        clientsUsersMapper.deleteByUserId(userId);
        return MessageResult.getSucMessage();
    }

    @Override
    public Map findClientUsers(Map paramsMap) {
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setTypeCode(MapUtils.getString(paramsMap,"typeCode"));
        usersDTO.setPage(0);
        usersDTO.setPageSize(0);
        //未分配的用户
        List<UsersDTO> unallocatedUsers = userService.queryUsers(usersDTO).getRows();
        //已分配用户
        List<Integer> allocatedUsers = clientsUsersMapper.selectUserId(MapUtils.getInteger(paramsMap,"id"));
        Map map = new HashMap();
        map.put("unallocatedUsers",unallocatedUsers);
        map.put("allocatedUsers",allocatedUsers);
        return map;
    }

    @Override
    public MessageResult createOrDeleteUserClient(Integer userId,List clientIds){
        //已分配的用户Id集合
        List<Integer> oldClientIds = queryClientId(userId);
        if(CollectionUtils.isEmpty(clientIds)){
            clientIds = new ArrayList();
        }
        //合并后需要删除的用户
        List<Integer>  delClientIds = ListUtils.subtract(oldClientIds,ListUtils.intersection(oldClientIds,clientIds));
        if(CollectionUtils.isNotEmpty(delClientIds)) {
            clientsUsersMapper.deleteBatchByUserId(userId, delClientIds);
        }
        List<Integer> newClients = ListUtils.subtract(clientIds,oldClientIds);
        if(CollectionUtils.isNotEmpty(newClients)) {
            //合并集合
            List<ClientsUsersEntity> list = new ArrayList<ClientsUsersEntity>();
            for (Integer id : newClients) {
                ClientsUsersEntity c = new ClientsUsersEntity();
                c.setClientId(id);
                c.setUserId(userId);
                c.setTimestamps(new Date());
                c.setRevoked(new Byte("1"));
                list.add(c);
            }
            clientsUsersMapper.insertBatch(list);
        }
        return MessageResult.getSucMessage();
    }


}
