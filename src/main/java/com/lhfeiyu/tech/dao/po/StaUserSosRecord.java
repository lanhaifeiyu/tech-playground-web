package com.lhfeiyu.tech.dao.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author yuronghua-airson
 * @description PO: StaUserSosRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 一键告警记录表
 * @time 2020-02-27 09:57:44
 */
@Data
@Accessors(chain = true)
public class StaUserSosRecord {

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
	 * 开始时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	
	/**
	 * 结束时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;
	
	/**
	 * 处理时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date handleTime;
	
	/**
	 * 处理人uid
	 */
	private Long handleUid;
	
	/**
	 * 处理人名字
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
	 * 告警状态：1已发起，2已取消，3已处理，4已被覆盖（发起后又发起）
	 */
	private Integer sosState;
	
	/**
	 * 会话ID（非必填）
	 */
	private String session;

	/**
	 * 结束标识：1已结束，3已开始，4已结束需更新
	 */
	private Integer endFlag;

	/**
	 * 统计状态：1需要统计，2不需统计(领导数据)
	 */
	private Integer state;
	
	
	
}