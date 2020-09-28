package com.lhfeiyu.tech.dao.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yuronghua-airson
 * @description PO: StaDayDept
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 部门按天汇总统计表
 * @time 2020-02-27 09:57:44
 */
@Data
@Accessors(chain = true)
public class StaDayDept implements Serializable {

    /**
     * 表项主键
     */
    private Long id;

    /**
     * 唯一ID：StaDayUser:timeYmd+uid, StaDayDept:timeYmd+deptUniqueId
     */
    private String uniqueId;

    /**
     * 组织ID
     */
    private Integer corpid;

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
     * 在线时长,单位：秒
     */
    private long onlineDuration;

    /**
     * 登录时间，若未登录或已退出登录值为null，退出登录时，计算此次登录时长，更新总在线时长
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logonTime;

    /**
     * 通话次数（等于单呼+组呼）
     */
    private int talkCount;

    /**
     * 单呼通话次数
     */
    private int individualTalkCount;

    /**
     * 组呼通话次数
     */
    private int groupTalkCount;

    /**
     * 通话时间，单位：秒
     */
    private long talkDuration;

    // 新增单呼通话时长
    private int individualTalkDuration;

    // 新增组呼通话时长
    private int groupTalkDuration;

    /**
     * 所有视频业务次数
     */
    private int videoAllCount;

    /**
     * 视频通话次数
     */
    private int videoCallCount;

    /**
     * 视频上传次数
     */
    private int videoUploadCount;

    /**
     * 发起视频点名次数
     */
    private int videoRollcallCount;

    /**
     * 发起视频会商次数
     */
    private int videoConfCount;

    // 新增视频通话时长
    private int videoDuration;

    /**
     * 一键报警次数
     */
    private int sosCount;

    /**
     * 回传照片次数
     */
    private int photoUploadCount;

    /**
     * 即时消息次数，包括IM 中的文件，包括下面的个人和群组即时消息次数
     */
    private int imCount;

    /**
     * 个人即时消息次数
     */
    private int individualImCount;

    /**
     * 群组即时消息次数
     */
    private int groupImCount;

    /**
     * 在岗时长,单位：秒
     */
    private long onpostDuration;

    /**
     * 里程数,单位：米
     */
    private int mileage;

    /**
     * 临时组总数
     */
    private int tmpGroupCount;

    /**
     * 临时组归档数
     */
    private int tmpGroupFileCount;

    /**
     * 最后更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private int state;

    private String onlineDurationH;

    private String individualTalkDurationH;

    private String groupTalkDurationH;

    private String talkDurationH;

    private String videoDurationH;

    private String onpostDurationH;

    private String mileageKm;

}