package com.lhfeiyu.tech.model;

import com.lhfeiyu.tech.dao.po.StaUserAudioRecord;
import com.lhfeiyu.tech.dao.po.StaUserOnlineRecord;
import com.lhfeiyu.tech.dao.po.StaUserSosRecord;
import com.lhfeiyu.tech.dao.po.StaUserVideoRecord;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Data
@Accessors(chain = true)
public class PairLogOngoing implements Serializable {

    private Map<Long, StaUserOnlineRecord> onlineRecordMap;
    private Map<Long, StaUserSosRecord> sosRecordMap;
    private Map<Long, StaUserAudioRecord> audioRecordMap;
    private Map<Long, StaUserVideoRecord> videoRecordMap;

}