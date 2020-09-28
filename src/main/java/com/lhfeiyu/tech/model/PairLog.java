package com.lhfeiyu.tech.model;

import com.zom.statistics.dao.po.StaUserAudioRecordOrigin;
import com.zom.statistics.dao.po.StaUserLogonRecordOrigin;
import com.zom.statistics.dao.po.StaUserSosRecordOrigin;
import com.zom.statistics.dao.po.StaUserVideoRecordOrigin;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class PairLog implements Serializable {

    private List<StaUserLogonRecordOrigin> logonRecordOriginList;
    private List<StaUserSosRecordOrigin> sosRecordOriginList;
    private List<StaUserAudioRecordOrigin> audioRecordOriginList;
    private List<StaUserVideoRecordOrigin> videoRecordOriginList;

}
