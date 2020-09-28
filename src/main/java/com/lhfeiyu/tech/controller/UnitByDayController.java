package com.lhfeiyu.tech.controller;

import com.alibaba.fastjson.JSONObject;
import com.zom.statistics.DTO.InterfaceDoc.DeptRequestParams;
import com.zom.statistics.service.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
@RequestMapping(value = "/api/v1/deptByDay")
/**
 * @auth fl
 * 获取某段时间，各个部门的情况。
 */
public class UnitByDayController {

    @Autowired
    private IUnitService unitService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public JSONObject getCurrentUnit (@ModelAttribute DeptRequestParams param) {
        return unitService.getCurrentUnit(param);
    }

    @RequestMapping(value = "/audio_count")
    public JSONObject getSubAll (@ModelAttribute DeptRequestParams param) {
        return unitService.audioCount(param);
    }

    @RequestMapping(value = "/audio_duration")
    public JSONObject audioDuration (@ModelAttribute DeptRequestParams param) {
        return unitService.audioDuration(param);
    }

    @RequestMapping(value = "/video_count")
    public JSONObject videoCount (@ModelAttribute DeptRequestParams param) {
        return unitService.videoCount(param);
    }

    @GetMapping(value = "/video_duration")
    public JSONObject videoDuration (@ModelAttribute DeptRequestParams param) {
        return unitService.videoDuration(param);
    }

    @GetMapping(value = "/photo_count")
    public JSONObject photoCount (@ModelAttribute DeptRequestParams param) {
        return unitService.photoCount(param);
    }

    @RequestMapping(value = "/sos_count")
    public JSONObject sosCount (@ModelAttribute DeptRequestParams param) {
        return unitService.sosCount(param);
    }

    @RequestMapping(value = "/im_count")
    public JSONObject imCount (@ModelAttribute DeptRequestParams param) {
        return unitService.imCount(param);
    }

    @RequestMapping(value = "/online_duration")
    public JSONObject onlineDuration (@ModelAttribute DeptRequestParams param) {
        return unitService.onlineDuration(param);
    }

    @RequestMapping(value = "/onpost_duration")
    public JSONObject onpostDuration (@ModelAttribute DeptRequestParams param) {
        return unitService.onpostDuration(param);
    }

    @RequestMapping(value = "/mileage_count")
    public JSONObject mileageCount (@ModelAttribute DeptRequestParams param) {
        return unitService.mileageCount(param);
    }

    @RequestMapping(value = "/tmp_group_count")
    public JSONObject tmpGroupCount (@ModelAttribute DeptRequestParams param) {
        return unitService.tmpGroupCount(param);
    }

    @RequestMapping(value = "/test1")
    public String test1 (HttpServletRequest request, @RequestParam("value") String value) {
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String parameter = request.getParameter(key);
            System.out.println(parameter);
        }

        return value;
    }

}
