package com.lhfeiyu.tech.model;

import com.lhfeiyu.tech.dao.po.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SumCommon implements Serializable {

    /**
     * 表项主键
     */
    private Long id;

    /**
     * 组织ID
     */
    private Integer corpid;

    /**
     * 人员ID
     */
    private Long uid;

    /**
     * 人员姓名
     */
    private String name;

    /**
     * 业务产生时间：年月日(20200208)
     */
    private Integer timeYmd;

    /**
     * 部门ID
     */
    private String deptUniqueId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 视频或语音的通话时间，单位：秒
     */
    private int duration;

    /**
     * 视频类型：1视频通话，2视频回传，3视频点名，4视频会商
     * 语音类型：1个呼，2组呼
     * IM类型： 1个人，2群组
     */
    private Integer type;

    /**
     * 此次更新的里程数，单位：米
     */
    private Integer mileage;

    /**
     * StaUserOnlineRecord 是否绑定岗位：0否，1是
     */
    private Integer isOnduty;

    public SumCommon(StaUserSosRecord data) {
        this.corpid = data.getCorpid();
        this.uid = data.getUid();
        this.name = data.getName();
        this.timeYmd = data.getTimeYmd();
        this.deptUniqueId = data.getDeptUniqueId();
        this.deptName = data.getDeptName();
    }

    public SumCommon(StaUserOnlineRecord data) {
        this.corpid = data.getCorpid();
        this.uid = data.getUid();
        this.name = data.getName();
        this.timeYmd = data.getTimeYmd();
        this.deptUniqueId = data.getDeptUniqueId();
        this.deptName = data.getDeptName();
        this.duration = data.getOnlineDuration();
        this.isOnduty = data.getIsOnduty();
    }

    public SumCommon(StaUserAudioRecord data) {
        this.corpid = data.getCorpid();
        this.uid = data.getUid();
        this.name = data.getName();
        this.timeYmd = data.getTimeYmd();
        this.deptUniqueId = data.getDeptUniqueId();
        this.deptName = data.getDeptName();
        this.duration = data.getDuration();
        this.type = data.getType();
    }

    public SumCommon(StaUserVideoRecord data) {
        this.corpid = data.getCorpid();
        this.uid = data.getUid();
        this.name = data.getName();
        this.timeYmd = data.getTimeYmd();
        this.deptUniqueId = data.getDeptUniqueId();
        this.deptName = data.getDeptName();
        this.duration = data.getDuration();
        this.type = data.getType();
    }

    public SumCommon(StaUserPhotoRecord data) {
        this.corpid = data.getCorpid();
        this.uid = data.getUid();
        this.name = data.getName();
        this.timeYmd = data.getTimeYmd();
        this.deptUniqueId = data.getDeptUniqueId();
        this.deptName = data.getDeptName();
    }

    public SumCommon(StaUserImRecord data) {
        this.corpid = data.getCorpid();
        this.uid = data.getUid();
        this.name = data.getName();
        this.timeYmd = data.getTimeYmd();
        this.deptUniqueId = data.getDeptUniqueId();
        this.deptName = data.getDeptName();
        this.type = data.getType();
    }

    public SumCommon(StaUserTmpgroupRecord data) {
        this.corpid = data.getCorpid();
        this.uid = data.getUid();
        this.name = data.getName();
        this.timeYmd = data.getTimeYmd();
        this.deptUniqueId = data.getDeptUniqueId();
        this.deptName = data.getDeptName();
    }

    public SumCommon(StaUserMileageRecord data) {
        this.corpid = data.getCorpid();
        this.uid = data.getUid();
        this.name = data.getName();
        this.timeYmd = data.getTimeYmd();
        this.deptUniqueId = data.getDeptUniqueId();
        this.deptName = data.getDeptName();
        this.mileage = data.getMileage();
    }

}
