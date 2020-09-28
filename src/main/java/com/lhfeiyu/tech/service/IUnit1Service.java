package com.lhfeiyu.tech.service;

import com.alibaba.fastjson.JSONObject;
import com.zom.statistics.DTO.InterfaceDoc.DeptRequestParams;
import com.zom.statistics.DTO.RtvUnit;
import com.zom.statistics.DTO.UnitSearchDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface IUnit1Service {

    // ============================================== 全部统计 ========================================
    JSONObject selectAllMsg (Map<String, Object> requestParams);

    JSONObject subAll (Map<String, Object> requestParam);

    JSONObject audioCount (Map<String, Object> requestParams);

    JSONObject audioDuration (Map<String, Object> requestParams);

    JSONObject videoCount (Map<String, Object> requestParams);

    JSONObject videoDuration (Map<String, Object> requestParams);

    JSONObject photoCount (Map<String, Object> requestParams);

    JSONObject sosCount (Map<String, Object> requestParams);

    JSONObject imCount (Map<String, Object> requestParams);

    JSONObject onlineDuration (Map<String, Object> requestParams);

    JSONObject onpostDuration (Map<String, Object> requestParams);

    JSONObject mileageCount (Map<String, Object> requestParams);

    JSONObject logonCount (Map<String, Object> requestParams);

    JSONObject tmpGroupCount (Map<String, Object> requestParams);

    JSONObject currentOnline (DeptRequestParams deptRequestParams);

    int onlineCount (String uniqueId);

    JSONObject selectByUnitId(HttpServletRequest request);

    List<RtvUnit> selectLike (UnitSearchDto unitSearchDto);

    Map<String, Object> deptByTime (String uids, Integer deptChain, String startTime, String endTime, String type);

    void exportExcel (HttpServletRequest request,
                      HttpServletResponse response,
                      String uniqueIds,
                      Integer deptChain,
                      String startTime,
                      String endTime,
                      String type,
                      String columns);

    List<JSONObject> selectUseOfCount (String uniqueIds,
                           Integer deptChain,
                           String startTime,
                           String endTime,
                           String type);
}
