package com.hmt.oauth.passport.web.controller;

import com.hmt.oauth.passport.domain.dto.AuthCodesDTO;
import com.hmt.oauth.passport.service.IOAuthService;
import com.hmt.oauth.passport.web.repones.ResponseResult;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by could.hao on 2017/2/16.
 */
@RestController
@RequestMapping("/api")
public class AuthCodesController extends BaseController {
    @Autowired
    private IOAuthService oAuthService;

    @RequestMapping(value = "/oauthLog" , method = RequestMethod.GET)
    public ResponseResult queryOAuthCodes(@RequestParam Map map) throws Exception{
        AuthCodesDTO authCodesDTO = new AuthCodesDTO();
        authCodesDTO.setLoginName( MapUtils.getString(map,"loginName") != null ? MapUtils.getString(map,"loginName") : "");
        authCodesDTO.setPage(MapUtils.getInteger(map,"page"));
        authCodesDTO.setPageSize(MapUtils.getInteger(map,"pageSize"));
        authCodesDTO.setStartTime(MapUtils.getLong(map,"startTime"));
        authCodesDTO.setEndTime(MapUtils.getLong(map,"endTime"));
        return  getSucResultData(oAuthService.queryAuthCodes(authCodesDTO));
    }
}
