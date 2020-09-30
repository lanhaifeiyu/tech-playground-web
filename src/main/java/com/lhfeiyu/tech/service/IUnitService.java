package com.lhfeiyu.tech.service;

import com.alibaba.fastjson.JSONObject;
import com.lhfeiyu.tech.DTO.InterfaceDoc.DeptRequestParams;

import java.util.Map;

public interface IUnitService {

    JSONObject getCurrentUnit (DeptRequestParams param);

    JSONObject audioCount (DeptRequestParams deptRequestParams);

    JSONObject audioDuration (DeptRequestParams deptRequestParams);

    JSONObject videoCount (DeptRequestParams deptRequestParams);

    JSONObject videoDuration (DeptRequestParams deptRequestParams);

    JSONObject photoCount (DeptRequestParams deptRequestParams);

    JSONObject sosCount (DeptRequestParams deptRequestParams);

    JSONObject imCount (DeptRequestParams deptRequestParams);

    JSONObject onlineDuration (DeptRequestParams deptRequestParams);

    JSONObject onpostDuration (DeptRequestParams deptRequestParams);

    JSONObject mileageCount (DeptRequestParams deptRequestParams);

//    JSONObject logonCount (DeptRequestParams deptRequestParams);

    JSONObject tmpGroupCount (DeptRequestParams deptRequestParams);

    JSONObject currentOnline (DeptRequestParams deptRequestParams);

    JSONObject selectByUnitId (Map<String, Object> requestParams);

    // ============================================== 全部统计 ========================================
    JSONObject selectAllMsg (Map<String, Object> requestParams);

}
