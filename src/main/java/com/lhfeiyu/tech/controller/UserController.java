package com.lhfeiyu.tech.controller;

import com.alibaba.fastjson.JSONObject;
import com.lhfeiyu.tech.DTO.User;
import com.lhfeiyu.tech.DTO.UserSearchDto;
import com.lhfeiyu.tech.service.IUser1Service;
import com.lhfeiyu.tech.tools.ActionUtil;
import com.lhfeiyu.tech.tools.ReturnCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    /*@Autowired
    private IUserService userService;*/

    @Autowired
    private IUser1Service userService;

    @RequestMapping(value = "/all")
    public JSONObject getUnitAll (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject json = userService.selectAllMsg(requestParams);
        return ReturnCode.success(json, requestParams);
    }

    @RequestMapping(value = "/audio_count")
    public JSONObject audioCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.audioCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/audio_duration")
    public JSONObject audioDuration (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.audioDuration(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/video_count")
    public JSONObject videoCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.videoCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @GetMapping(value = "/video_duration")
    public JSONObject videoDuration (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.videoDuration(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @GetMapping(value = "/photo_count")
    public JSONObject photoCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.photoCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @RequestMapping(value = "/sos_count")
    public JSONObject sosCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.sosCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @RequestMapping(value = "/im_count")
    public JSONObject imCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.imCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @RequestMapping(value = "/online_duration")
    public JSONObject onlineDuration (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.onlineDuration(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @RequestMapping(value = "/onpost_duration")
    public JSONObject onpostDuration (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.onpostDuration(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @RequestMapping(value = "/mileage_count")
    public JSONObject mileageCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.mileageCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/logon_count")
    public JSONObject logonCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.logonCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/tmp_group_count")
    public JSONObject tmpGroupCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = userService.tmpGroupCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/base")
    public JSONObject selectByUnitId (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject json = userService.selectByUnitId(requestParams, request);
        return ReturnCode.success(json, requestParams);
    }

    @RequestMapping(value = "/like")
    public JSONObject select (@ModelAttribute UserSearchDto userSearchDto) {
        JSONObject json = new JSONObject();
        List<User> select = userService.select(userSearchDto);
        json.put("data", select);
        return ReturnCode.success(json);
    }

    @RequestMapping(value = "/userByTime")
    public Map<String, Object> userByTime (@RequestParam("uids") String uids,
                                           @RequestParam(value = "startTime") String startTime,
                                           @RequestParam(value = "endTime") String endTime,
                                           @RequestParam("type") String type) {
        return userService.userByTime(uids, startTime, endTime, type);
    }


    @RequestMapping(value = "/deptByTime")
    public Map<String, Object> deptByTime (@RequestParam("uniqueIds") String uniqueIds,
                                           @RequestParam(value = "deptChain") Integer deptChain,
                                           @RequestParam(value = "startTime") String startTime,
                                           @RequestParam(value = "endTime") String endTime,
                                           @RequestParam("type") String type) {
        return userService.deptByTime(uniqueIds, deptChain, startTime, endTime, type);
    }


    @RequestMapping(value = "/exportExcel")
    public void exportExcel (HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam(value = "uids", required = false) String uids,
                             @RequestParam(value = "uniqueIds", required = false) String uniqueIds,
                             @RequestParam(value = "deptChain") Integer deptChain,
                             @RequestParam(value = "startTime") String startTime,
                             @RequestParam(value = "endTime") String endTime,
                             @RequestParam("type") String type,
                             @RequestParam("columns") String columns) {
        userService.exportExcel(request, response, uids, uniqueIds, deptChain, startTime, endTime, type, columns);
    }
}
