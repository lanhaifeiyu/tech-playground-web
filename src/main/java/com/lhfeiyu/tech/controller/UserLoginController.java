package com.lhfeiyu.tech.controller;

import com.alibaba.fastjson.JSONObject;
import com.lhfeiyu.tech.DTO.RtvConsoleUser;
import com.lhfeiyu.tech.service.IConsoleUserService;
import com.lhfeiyu.tech.tools.JwtTokenUtil;
import com.lhfeiyu.tech.tools.ReturnCode;
import com.lhfeiyu.tech.tools.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping(value = "/api/v1")
public class UserLoginController {

    @Autowired
    private IConsoleUserService consoleUserService;

    @Value(value = "${jwt.expire}")
    private String expire;

    private static Logger logger = LoggerFactory.getLogger(UserLoginController.class);


    @PostMapping(value = "/logon")
    public JSONObject userLogin (@RequestParam("account") String account,
                                 @RequestParam("password") String password,
                                 @RequestParam("type") int type) {
        logger.info("login request params: name:{}, password:{}", account, password);
        JSONObject json = new JSONObject();
        String password1 = SecurityUtil.encryptPassword(password);
        RtvConsoleUser consoleUser = consoleUserService.selectByUserNameAndPsw(account, password1);
        if (consoleUser == null) {
            return ReturnCode.failure(json, "账号或密码错误");
        }
        String role = "admin";

        // 创建token
        String token = JwtTokenUtil.createJWT(consoleUser, consoleUser.getDepartmentId(), role, expire);

        json.put("token", token);
        json.put("unitId", consoleUser.getDepartmentId());// 用户所在部门ID
        json.put("corpId", consoleUser.getCorpId());// 用户所在组织ID
        json.put("uid", consoleUser.getId());// 用户ID

        return ReturnCode.success(json);
    }


    @PostMapping(value = "/logoff")
    public JSONObject userLoginOff (@ModelAttribute RtvConsoleUser user) {
        JSONObject json = new JSONObject();
        return ReturnCode.success(json);
    }


}
