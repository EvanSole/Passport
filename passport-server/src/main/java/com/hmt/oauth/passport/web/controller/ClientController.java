package com.hmt.oauth.passport.web.controller;

import com.hmt.oauth.passport.domain.dto.ClientsDTO;
import com.hmt.oauth.passport.service.IClientService;
import com.hmt.oauth.passport.web.repones.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by Edwin on 2016/12/19.
 */
@RestController
@RequestMapping("/api")
public class ClientController extends BaseController {

    @Autowired
    private IClientService clientService;

    @RequestMapping(value = "/client" , method = RequestMethod.GET)
    public ResponseResult queryClients(ClientsDTO clientsDTO) throws Exception{
        return  getSucResultData(clientService.queryClients(clientsDTO));
    }

    @ApiOperation(value = "添加应用", notes = "添加应用信息.")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = ResponseResult.class)})
    @RequestMapping(value = "/client",method = RequestMethod.POST)
    public ResponseResult createClients(@RequestBody ClientsDTO clientsDTO) throws Exception{
        Long time = new Date().getTime();
        clientsDTO.setUpdateUser(getSessionCurrentUser().getUserName());
        clientsDTO.setCreateUser(getSessionCurrentUser().getUserName());
        clientsDTO.setCreateTime(time);
        clientsDTO.setUpdateTime(time);
        return  getMessage(clientService.createClients(clientsDTO));
    }

    @ApiOperation(value = "编辑应用", notes = "根据应用Id编辑应用信息.")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = ResponseResult.class)})
    @RequestMapping(value="/client/{id}",method = RequestMethod.PUT)
    public ResponseResult modifyClients(@PathVariable("id") Integer id ,@RequestBody ClientsDTO clientsDTO) throws Exception{
        clientsDTO.setUpdateUser(getSessionCurrentUser().getUserName());
        clientsDTO.setUpdateTime(new Date().getTime());
        return getMessage(clientService.updateClients(clientsDTO));
    }

    @ApiOperation(value = "删除应用", notes = "根据应用Id删除应用.")
    @ApiImplicitParam(name = "id", value = "应用Id", required = true, paramType = "path", dataType = "Integer")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = ResponseResult.class)})
    @RequestMapping(value = "/client/{id}",method = RequestMethod.DELETE)
    public ResponseResult deleteClients(@PathVariable("id") Integer id) throws Exception{
        return getMessage(clientService.removeClients(id));
    }

}
