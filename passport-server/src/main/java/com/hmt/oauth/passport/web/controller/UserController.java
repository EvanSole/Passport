package com.hmt.oauth.passport.web.controller;

import com.hmt.oauth.passport.domain.CurrentUser;
import com.hmt.oauth.passport.domain.dto.UsersDTO;
import com.hmt.oauth.passport.service.IUserService;
import com.hmt.oauth.passport.utils.BeanUtils;
import com.hmt.oauth.passport.web.repones.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * Created by Edwin on 2016/12/19.
 */
@RestController
@RequestMapping("/api")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/user" , method = RequestMethod.GET)
    public ResponseResult queryUsers(@RequestParam Map map) throws Exception{
        UsersDTO usersDTO = new UsersDTO();
        BeanUtils.transMapToBean(map,usersDTO);
        return  getSucResultData(userService.queryUsers(usersDTO));
    }

    @ApiOperation(value = "添加用户", notes = "添加用户信息.")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = ResponseResult.class)})
    @RequestMapping(value = "/user",method = RequestMethod.POST)
    public ResponseResult createUsers(@RequestBody UsersDTO usersDTO) throws Exception{
        Long time = new Date().getTime();
        usersDTO.setCreateUser(this.getSessionCurrentUser().getUserName());
        usersDTO.setCreateTime(time);
        usersDTO.setUpdateUser(this.getSessionCurrentUser().getUserName());
        usersDTO.setUpdateTime(time);
        return  getMessage(userService.createUsers(usersDTO));
    }

    @ApiOperation(value = "编辑用户", notes = "根据应用Id编辑用户信息.")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = ResponseResult.class)})
    @RequestMapping(value="/user/{id}",method = RequestMethod.PUT)
    public ResponseResult modifyUsers(@PathVariable("id") Integer id,@RequestBody UsersDTO usersDTO) throws Exception{
        usersDTO.setUpdateUser(getSessionCurrentUser().getUserName());
        usersDTO.setUpdateTime(new Date().getTime());
        return getMessage(userService.updateUsers(usersDTO));
    }

    @ApiOperation(value = "删除用户", notes = "根据用户Id删除用户.")
    @ApiImplicitParam(name = "id", value = "用户Id", required = true, paramType = "path", dataType = "Integer")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = ResponseResult.class)})
    @RequestMapping(value = "/user/{id}",method = RequestMethod.DELETE)
    public ResponseResult deleteUsers(@PathVariable("id") Integer id) throws Exception{
        return getMessage(userService.removeUsers(id));
    }

    @ApiOperation(value = "激活用户", notes = "激活/禁用用户")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = ResponseResult.class)})
    @RequestMapping(value="/user/active/{id}",method = RequestMethod.PUT)
    public ResponseResult activeUsers(@RequestBody UsersDTO usersDTO) throws Exception{
        if(getSessionCurrentUser().getIsAdmin() != 1){
            return getFaultMessage("E2006");
        }
        CurrentUser currentUser = this.getSessionCurrentUser();
        return getMessage(userService.updateActiveUsers(usersDTO.getId(),usersDTO.getIsActive(),currentUser));
    }

    /****
     * 修改用户密码
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/{id}/changePwd",method = RequestMethod.PUT)
    public ResponseResult changeUsersPassword(@PathVariable("id") Integer id,@RequestBody Map map) throws Exception{
        String oldPassword = MapUtils.getString(map,"oldPassword");
        String newPassword = MapUtils.getString(map,"newPassword");
        return getMessage(userService.changePassword(id,oldPassword,newPassword));
    }

    /***
     * 重置用户密码
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/{id}/restPwd",method = RequestMethod.POST)
    public ResponseResult resetUsersPassword(@PathVariable("id") Integer id ) throws Exception{
        CurrentUser currentUser = this.getSessionCurrentUser();
        return getMessage(userService.updateResetPassword(id,currentUser));
    }



}
