package com.lhfeiyu.tech.service;

import com.alibaba.fastjson.JSONObject;
import com.lhfeiyu.tech.DTO.User;
import com.lhfeiyu.tech.DTO.UserSearchDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface IUser1Service {

    JSONObject selectAllMsg (Map<String, Object> requestParams);

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

    JSONObject selectByUnitId(Map<String, Object> requestParams, HttpServletRequest request);

    List<User> select (UserSearchDto userSearchDto);

    Map<String, Object> userByTime (String uids, String startTime, String endTime, String type);

    Map<String, Object> deptByTime (String uniqueId, Integer deptChain, String startTime, String endTime, String type);

    void exportExcel (HttpServletRequest request,
                      HttpServletResponse response,
                      String uids,
                      String uniqueIds,
                      Integer deptChain,
                      String startTime,
                      String endTime,
                      String type,
                      String columns);
}
