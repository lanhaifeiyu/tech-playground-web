package com.lhfeiyu.tech.dao.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author yuronghua-airson
 * @description PO: StaUserAudioRecord
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 语音通话记录表
 * @time 2020-02-27 09:57:44
 */
@Data
@Accessors(chain = true)
public class StaUserAudioRecord {

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
	 * 个呼：对方uid，组呼：通话组ID
	 */
	private Long targetId;
	
	/**
	 * 个呼：对方的名称，组呼：通话组名称
	 */
	private String targetName;
	
	/**
	 * 通话类型：1个呼，2组呼
	 */
	private Integer type;
	
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
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastTime;
	
	/**
	 * 通话时间，单位：秒
	 */
	private int duration;
	
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