package com.lhfeiyu.tech.dao.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author yuronghua-airson
 * @description PO: StaUserVideoRecordOrigin
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 视频通话记录表
 * @time 2020-02-27 09:57:44
 */
@Data
@Accessors(chain = true)
public class StaUserVideoRecordOrigin {

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
	 * 业务产生时间：年月(202002)
	 */
	private Integer timeYm;
	
	/**
	 * 业务产生时间：年月日(20200208)
	 */
	private Integer timeYmd;
	
	/**
	 * 业务产生时间：年月日时(2020020809)
	 */
	private Integer timeYmdh;
	
	/**
	 * 部门ID
	 */
	private String deptUniqueId;
	
	/**
	 * 部门名称
	 */
	private String deptName;
	
	/**
	 * 视频通话：对方uid
	 */
	private Long targetId;
	
	/**
	 * 视频通话：对方的名称
	 */
	private String targetName;
	
	/**
	 * 视频类型：1视频通话，2视频回传，3视频点名，4视频会商
	 */
	private Integer type;
	
	/**
	 * 开始时间（flag=1），结束时间（flag=2）
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date time;
	
	/**
	 * 操作标识：1开始，2结束
	 */
	private Integer flag;
	
	/**
	 * 会话ID（非必填）
	 */
	private String session;
	
	/**
	 * 会议ID（非必填）
	 */
	private String conferenceId;
	
	/**
	 * 统计状态：1需要统计，2不需统计(领导数据)
	 */
	private Integer state;

	/** insert time str directly */
	private String timeStr;
	
}