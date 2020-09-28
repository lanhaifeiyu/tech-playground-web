package com.lhfeiyu.tech.controller;

import com.alibaba.fastjson.JSONObject;
import com.zom.statistics.DTO.InterfaceDoc.DeptRequestParams;
import com.zom.statistics.DTO.RtvUnit;
import com.zom.statistics.DTO.UnitSearchDto;
import com.zom.statistics.service.IUnit1Service;
import com.zom.statistics.tools.ActionUtil;
import com.zom.statistics.tools.ReturnCode;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/dept")
public class UnitController {

    /*@Autowired
    private IUnitService unitService;*/
    @Autowired
    private IUnit1Service unitService;

    @RequestMapping(value = "/all")
    public JSONObject getUnitAll (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject json = unitService.selectAllMsg(requestParams);
        return ReturnCode.success(json, requestParams);
    }

    @RequestMapping(value = "/sub_all")
    public JSONObject getSubAll (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        /*DeptRequestParams param = new DeptRequestParams();
        param.setDept_id(requestParams.get("dept_id") == null ? null : requestParams.get("dept_id").toString())
                .setDept_chain(3)// 该接口只有能用于查询下一级部门信息
                .setStart_time(Integer.parseInt(requestParams.get("start_time").toString()))
                .setEnd_time(requestParams.get("end_time") == null
                        ? null
                        : Integer.parseInt(requestParams.get("end_time").toString()))
                .setTime_level(requestParams.get("time_level").toString());*/
        requestParams.put("dept_chain", 2);
        JSONObject json = unitService.subAll(requestParams);
        return ReturnCode.success(json, requestParams);
    }

    @RequestMapping(value = "/audio_count")
    public JSONObject audioCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.audioCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/audio_duration")
    public JSONObject audioDuration (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.audioDuration(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/video_count")
    public JSONObject videoCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.videoCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @GetMapping(value = "/video_duration")
    public JSONObject videoDuration (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.videoDuration(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @GetMapping(value = "/photo_count")
    public JSONObject photoCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.photoCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @RequestMapping(value = "/sos_count")
    public JSONObject sosCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.sosCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/im_count")
    public JSONObject imCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.imCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/online_duration")
    public JSONObject onlineDuration (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.onlineDuration(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @RequestMapping(value = "/onpost_duration")
    public JSONObject onpostDuration (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.onpostDuration(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/mileage_count")
    public JSONObject mileageCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.mileageCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @RequestMapping(value = "/logon_count")
    public JSONObject logonCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.logonCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }


    @RequestMapping(value = "/tmp_group_count")
    public JSONObject tmpGroupCount (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.tmpGroupCount(requestParams);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/sta_cur_online_count")
    public JSONObject currentOnline (@RequestParam(value = "dept_id") String deptUniqueId,
                                     @RequestParam(value = "dept_chain", required = false) Integer chain,
                                     @RequestParam(value = "min", required = false) Integer min,
                                     @RequestParam(value = "max", required = false) Integer max,
                                     HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        DeptRequestParams param = new DeptRequestParams();
        param.setDept_id(deptUniqueId)
                .setDept_chain(chain == null ? 0 : chain)
                .setMin(min)
                .setMax(max);
        JSONObject result = unitService.currentOnline(param);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }

    @RequestMapping(value = "/online_count")
    public JSONObject onlineCount (@RequestParam(value = "dept_id") String deptUniqueId) {
        JSONObject json = new JSONObject();
        int i = unitService.onlineCount(deptUniqueId);
        json.put("count", i);
        return ReturnCode.success(json);
    }


    @RequestMapping(value = "/base")
    public JSONObject base (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject result = unitService.selectByUnitId(request);
        if (result.get("error") != null) {
            return ReturnCode.failure(result, result.get("error").toString(), requestParams);
        }
        return ReturnCode.success(result, requestParams);
    }



    // -------------------------------------------------- 统计多少天的综合数据 ----------------------------------------------
    @RequestMapping(value = "/selectAll")
    public JSONObject selectAll (HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
        JSONObject json = unitService.selectAllMsg(requestParams);
        return ReturnCode.success(json, requestParams);
    }


    private DeptRequestParams getRequestParams (HttpServletRequest request, Map<String, Object> requestParams) {
        DeptRequestParams param = new DeptRequestParams();
        param.setDept_id(requestParams.get("dept_id") == null ? null : requestParams.get("dept_id").toString())
                .setDept_chain(requestParams.get("dept_chain") == null
                        ? 0 : Integer.parseInt(requestParams.get("dept_chain").toString()))
                .setType(requestParams.get("type") == null
                        ? 0 : Integer.parseInt(requestParams.get("type").toString()))
                .setMin(requestParams.get("min") == null
                        ? null : Integer.parseInt(requestParams.get("min").toString()))
                .setMax(requestParams.get("max") == null
                        ? null : Integer.parseInt(requestParams.get("max").toString()))
                .setStart_time(Integer.parseInt(requestParams.get("start_time").toString()))
                .setEnd_time(requestParams.get("end_time") == null
                        ? null
                        : Integer.parseInt(requestParams.get("end_time").toString()))
                .setTime_level(requestParams.get("time_level").toString());
        return param;
    }

    @RequestMapping(value = "/like")
    public JSONObject selectLike (@ModelAttribute UnitSearchDto unitSearchDto) {
        JSONObject json = new JSONObject();
        List<RtvUnit> rtvUnits = unitService.selectLike(unitSearchDto);
        json.put("data", rtvUnits);
        return json;
    }

    @RequestMapping(value = "/deptByTime")
    public Map<String, Object> deptByTime (@RequestParam("uniqueIds") String uniqueIds,
                                  @RequestParam(value = "deptChain") Integer deptChain,
                                  @RequestParam(value = "startTime") String startTime,
                                  @RequestParam(value = "endTime") String endTime,
                                  @RequestParam("type") String type) {
        return unitService.deptByTime(uniqueIds, deptChain, startTime, endTime, type);
    }

    @RequestMapping(value = "/exportExcel")
    public void exportExcel (HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam("uniqueIds") String uniqueIds,
                             @RequestParam(value = "deptChain") Integer deptChain,
                             @RequestParam(value = "startTime") String startTime,
                             @RequestParam(value = "endTime") String endTime,
                             @RequestParam("type") String type,
                             @RequestParam("columns") String columns) {
        unitService.exportExcel(request, response, uniqueIds, deptChain, startTime, endTime, type, columns);
    }

    @RequestMapping(value = "/useOfCount")
    public JSONObject useOfCount (@RequestParam("uniqueIds") String uniqueIds,
                                  @RequestParam(value = "deptChain") Integer deptChain,
                                  @RequestParam(value = "startTime") String startTime,
                                  @RequestParam(value = "endTime") String endTime,
                                  @RequestParam("type") String type) {
        JSONObject jsonObject = new JSONObject();
        List<JSONObject> list = unitService.selectUseOfCount(uniqueIds, deptChain, startTime, endTime, type);
        jsonObject.put("data", list);
        return ReturnCode.success(jsonObject);
    }


}
