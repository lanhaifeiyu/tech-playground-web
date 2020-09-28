package com.lhfeiyu.tech.service;

import com.alibaba.fastjson.JSONObject;
import com.zom.statistics.DTO.InterfaceDoc.UserAllDto;
import com.zom.statistics.DTO.InterfaceDoc.UserRequestParams;

import java.util.Map;

public interface IUserService {

    JSONObject getAllUser (UserAllDto param);

    JSONObject audioCount (UserRequestParams param);

    JSONObject audioDuration (UserRequestParams param);

    JSONObject videoCount (UserRequestParams param);

    JSONObject videoDuration (UserRequestParams param);

    JSONObject photoCount (UserRequestParams param);

    JSONObject sosCount (UserRequestParams param);

    JSONObject imCount (UserRequestParams param);

    JSONObject onlineDuration (UserRequestParams param);

    JSONObject onpostDuration (UserRequestParams param);

    JSONObject mileageCount (UserRequestParams param);

    JSONObject tmpGroupCount (UserRequestParams param);

    JSONObject selectByUnitId (Map<String, Object> requestParams);
}
