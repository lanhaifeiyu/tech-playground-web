package com.lhfeiyu.tech.dao.po;

import lombok.Data;

/**
 * @author yuronghua-airson
 * @description PO: StaOriginalLog
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 原始业务日志记录表（定期清理）
 * @time 2020-02-27 09:57:44
 */
@Data
public class StaOriginalLog {

	/**
	 * 表项主键
	 */
	private Long id;
	
	/**
	 * 日志内容（JSON格式）
	 */
	private String content;
	
	
	
}