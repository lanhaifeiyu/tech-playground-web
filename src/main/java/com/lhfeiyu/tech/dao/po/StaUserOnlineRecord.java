package com.lhfeiyu.tech.dao.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author yuronghua-airson
 * @description PO: StaUserOnlineRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 用户在线时长计算表(base on logon_record)
 * @time 2020-02-27 09:57:44
 */
@Data
@Accessors(chain = true)
public class StaUserOnlineRecord {

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
	 * 登录时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date logonTime;
	
	/**
	 * 退出登录时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date logoffTime;

	/**
	 * 状态为3时，上一次操作时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastTime;
	
	/**
	 * 此次登录在线时长，单位：秒
	 */
	private int onlineDuration;
	
	/**
	 * 是否绑定岗位：0否，1是
	 */
	private Integer isOnduty;
	
	/**
	 * 结束标识：1已结束，3已开始，4已结束需更新
	 */
	private Integer endFlag;
	
	/**
	 * 会话ID（非必填）
	 */
	private String session;
	
	/**
	 * 统计状态：1需要统计，2不需统计（隐藏数据）
	 */
	private Integer state;

	/**
	 * 真实登录值为1
	 */
	private int real;
	
	
}