package com.hmt.oauth.passport.web.controller;

import com.hmt.oauth.passport.service.IClientUserService;
import com.hmt.oauth.passport.web.repones.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by could.hao on 2017/2/20.
 */
@RestController
@RequestMapping("/api/clientUser")
public class ClientsUsersController extends BaseController {
    @Autowired
    IClientUserService clientUserService;

    /***
     * 查询应用下用户信息
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{id}" , method = RequestMethod.GET)
    public ResponseResult queryClients(@RequestParam Map paramsMap) throws Exception{
        return  getSucResultData(clientUserService.findClientUsers(paramsMap));
    }

    /***
     * 应用分配用户
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "" , method = RequestMethod.POST)
    public ResponseResult bindingClientUsers(@RequestBody Map params) throws Exception{
        return getMessage(clientUserService.createOrDeleteClientUser(params));
    }
}
