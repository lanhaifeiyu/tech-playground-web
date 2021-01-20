package com.lhfeiyu.tech.controller;

import com.alibaba.fastjson.JSONObject;
import com.lhfeiyu.tech.DTO.ContinueBusiness;
import com.lhfeiyu.tech.service.IStaLogService;
import com.lhfeiyu.tech.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author airson
 */
@RequestMapping(value = "/api/v1/index")
@RestController
public class IndexController {

    /*@Autowired
    private IStaLogService staLogService;*/

    @RequestMapping(value = {"", "/", "/index"})
    public JSONObject index() {
        JSONObject json = new JSONObject();
        json.put("tip", "this is the statistics server");
        return Result.success(json);
    }

    @RequestMapping(value = "/parseLog", method = RequestMethod.GET)
    public String parseLog() {
        System.out.println("===========");
        //staLogService.parseLog();
        return "123";
    }

    /**
     * 查询未结束的持续业务
     * @param type  1;在线    2：语音    3：视频
     * @param uid
     * @param uniqueId
     * @return
     */
    @RequestMapping(value = "/ongoing", method = RequestMethod.GET)
    public List<ContinueBusiness> ongoing (@RequestParam("type") int type,
                                           @RequestParam(value = "uid", required = false) Integer uid,
                                           @RequestParam(value = "uniqueId", required = false) String uniqueId) {
        //return staLogService.getOngoing(type, uid, uniqueId);
        return null;
    }

    /**
     * 中断持续业务
     * @param type  1;在线    2：语音通话    3：视频通话
     * @param flag  语音通话：1：个人语音 2：群组语音      视频通话：1：视频通话   2：视频回传  3：视频点名  4：视频会商
     * @param uid   用户ID
     * @return
     */
    @RequestMapping(value = "/interruput", method = RequestMethod.GET)
    public JSONObject interruputBusiness (@RequestParam("type") int type,
                                          @RequestParam(value = "flag", required = false) Integer flag,
                                          @RequestParam(value = "uid") Integer uid) {
        //return staLogService.interruputBusiness(type, flag, uid);
        return null;
    }

}