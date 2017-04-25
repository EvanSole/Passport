package com.hmt.oauth.passport.service.impl;

import com.hmt.oauth.passport.common.SpringTxTestCase;
import com.hmt.oauth.passport.domain.dto.ClientsDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by Edwin on 2016/12/21.
 */
public class ClientServiceTest extends SpringTxTestCase {

    @Autowired
    ClientService clientService;

    @Test
    public void selectPageLists(){
        ClientsDTO clientsDTO = new ClientsDTO();
        System.out.print("================>>>>>"+clientService.queryClients(clientsDTO));
    }

    @Test
    public void selectByPrimaryKey(){
//        System.out.print(clientService.findByPrimaryKey(1).toString());
    }


    @Test
    public void createClients(){
        ClientsDTO clientsDTO = new ClientsDTO();
        clientsDTO.setName("WMS");
        clientsDTO.setDescription("WMS仓储管理平台!");
        clientsDTO.setOwner("admin");
        clientsDTO.setOwnerContact("13654245845");
        clientsDTO.setOwnerEmail("admin@hmt.com");
        clientsDTO.setRedirectUri("http://127.0.0.1:8080/app/index.html");
        clientsDTO.setCreateUser("system");
        clientsDTO.setCreateTime(new Date().getTime());
        clientsDTO.setUpdateUser("system");
        clientsDTO.setUpdateTime(new Date().getTime());
        clientService.createClients(clientsDTO);
    }

    @Test
    public void updateClients(){
        ClientsDTO clientsDTO = new ClientsDTO();
        clientsDTO.setId(2);
        clientsDTO.setAppIcon("hxty");
        clientService.updateClients(clientsDTO);
    }

}
