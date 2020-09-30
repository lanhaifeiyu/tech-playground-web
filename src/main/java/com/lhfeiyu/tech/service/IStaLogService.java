package com.lhfeiyu.tech.service;

import com.alibaba.fastjson.JSONObject;
import com.lhfeiyu.tech.DTO.ContinueBusiness;
import com.lhfeiyu.tech.dao.po.StaUserLogonRecordOrigin;

import java.util.List;

@Deprecated
public interface IStaLogService {

    List<StaUserLogonRecordOrigin> parseLog();

    void parseLog2 ();

    void parseLog3 ();

    void parseLog4 ();

    List<ContinueBusiness> getOngoing (int type, Integer uid, String uniqueId);

    JSONObject interruputBusiness (int type, Integer flag, Integer uid);

}
