package com.hmt.oauth.passport.service.impl;

import com.hmt.oauth.passport.domain.dto.ClientsDTO;
import com.hmt.oauth.passport.entity.ClientsEntity;
import com.hmt.oauth.passport.mapper.ClientsMapper;
import com.hmt.oauth.passport.service.IClientService;
import com.hmt.oauth.passport.service.IClientUserService;
import com.hmt.oauth.passport.utils.BeanUtils;
import com.hmt.oauth.passport.utils.HMACUtil;
import com.hmt.oauth.passport.utils.IPUtil;
import com.hmt.oauth.passport.web.message.MessageResult;
import com.hmt.oauth.passport.web.repones.PageResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Edwin on 2016/12/20.
 */
@Service
public class ClientService implements IClientService {

    @Autowired
    ClientsMapper clientMapper;

    @Autowired
    IClientUserService clientUserService;

    @Override
    public PageResponse<List> queryClients(ClientsDTO clientDTO) {
        ClientsEntity clientEntity = BeanUtils.copyBeanPropertyUtils(clientDTO,ClientsEntity.class);
        PageResponse<List> pageResponse = new PageResponse<>();
        pageResponse.setRows(clientMapper.selectClients(clientEntity));
        pageResponse.setTotal(clientMapper.selectClientsTotal(clientEntity));
        return pageResponse;
    }

    @Override
    public MessageResult createClients(ClientsDTO clientDTO) {
        List<ClientsEntity> list = clientMapper.findByName(clientDTO.getName());
        if(CollectionUtils.isNotEmpty(list) && list.size() > 0){
            return MessageResult.getMessage("E3001");
        }
        ClientsEntity clientEntity = BeanUtils.copyBeanPropertyUtils(clientDTO,ClientsEntity.class);
        clientEntity.setAppKey(RandomStringUtils.randomNumeric(16));
        //appSecret生成: 应用名称 + IP + 系统时间,SHA1 加密
        clientEntity.setAppSecret(RandomStringUtils.random(32,HMACUtil.encryptHMAC(clientEntity.getName()+
                IPUtil.getLocalIP() + System.currentTimeMillis(),HMACUtil.init())));
        clientMapper.insertClients(clientEntity);
        return  MessageResult.getSucMessage();
    }

    @Override
    public MessageResult updateClients(ClientsDTO clientDTO) {
        List<ClientsEntity> list = clientMapper.findByName(clientDTO.getName());
        if(CollectionUtils.isNotEmpty(list) && list.size() > 0){
            return MessageResult.getMessage("E3001");
        }
        ClientsEntity clientEntity = BeanUtils.copyBeanPropertyUtils(clientDTO,ClientsEntity.class);
        clientMapper.updateClients(clientEntity);
        return  MessageResult.getSucMessage();
    }

    /**
     * 删除应用信息
     * @param clientId
     * @return
     */
    @Override
    public MessageResult removeClients(Integer clientId) {
        List list = clientUserService.queryUserId(clientId);
        if(CollectionUtils.isNotEmpty(list)){
            return MessageResult.getMessage("E3000");
        }
        clientMapper.deleteByPrimaryKey(clientId);
        return MessageResult.getSucMessage();
    }

    @Override
    public ClientsEntity findByPrimaryKey(Integer clientId) {
        return clientMapper.selectByPrimaryKey(clientId);
    }

    @Override
    public ClientsEntity findByClientAppkey(String appKey) {
        return clientMapper.findByClientAppKey(appKey);
    }

    @Override
    public ClientsEntity findByClientAppSecret(String appSecret){
        return clientMapper.findByClientAppSecret(appSecret);
    }

    @Override
    public List<ClientsEntity> getClientNames(Integer userId) {
        List<ClientsEntity> entityList = clientMapper.findByUserId(userId);
        if(CollectionUtils.isNotEmpty(entityList)){
           return entityList;
        }
        return null;
    }

}
