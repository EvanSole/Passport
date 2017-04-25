package com.hmt.oauth.passport.web.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    /**
     * 服务器健康检查路径
     * @return 字符串
     */
    @ApiOperation(value = "健康检查API", notes = "服务健康检查API探针")
    @ResponseBody
    @RequestMapping(value = "/status",method = RequestMethod.GET)
    public String status(){
        return "SUCCESS";
    }
}
