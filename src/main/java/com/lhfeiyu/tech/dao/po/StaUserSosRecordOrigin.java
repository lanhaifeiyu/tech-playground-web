package com.lhfeiyu.tech.dao.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author yuronghua-airson
 * @description PO: StaUserSosRecordOrigin
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 一键告警记录表
 * @time 2020-02-27 09:57:44
 */
@Data
@Accessors(chain = true)
public class StaUserSosRecordOrigin {

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
	 * 发起时间（flag=1）、取消时间（flag=2）、处理时间（flag=3）
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date time;
	
	/**
	 * 处理人uid（flag=3时有效）
	 */
	private Long handleUid;
	
	/**
	 * 处理人名字（flag=3时有效）
	 */
	private String handleName;
	
	/**
	 * 发起告警时位置（经纬度）
	 */
	private String location;
	
	/**
	 * 定位设备ID（室内定位）
	 */
	private String marker;
	
	/**
	 * 操作标识：1发起，2取消，3处理
	 */
	private Integer flag;
	
	/**
	 * 会话ID（非必填）
	 */
	private String session;
	
	/**
	 * 统计状态：1需要统计，2不需统计(领导数据)
	 */
	private Integer state;

	/** insert time str directly */
	private String timeStr;
	
}