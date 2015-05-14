package com.sunrise.cgb.schedule;

import java.util.Date;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class ScheduleInfo {
	/**
	 * 拆分包ID
	 */
	private long subpackage_id;
	/**
	 * 渠道类型
	 */
	private int task_type;
	/**
	 * 触发时间
	 */
	private Date startTime;
	/**
	 * 时间格式
	 */
	private String timeExp;
	
	public static final String FLAG_SUBPACKAGE_ID = "subpackage_id";
	
	public static final String FLAG_TASK_TYPE = "task_type";
	
	public ScheduleInfo(){
		
	}
	
	public ScheduleInfo(long subpackage_id,int task_type){
		this.subpackage_id = subpackage_id;
		this.task_type = task_type;
	}
	
	public ScheduleInfo(long subpackage_id,int task_type,Date time){
		this.subpackage_id = subpackage_id;
		this.task_type = task_type;
		this.startTime = time;
		this.timeExp = CronExpHelper.date2Exp(time);
	}
	
	public ScheduleInfo(long subpackage_id,int task_type,String exp){
		this.subpackage_id = subpackage_id;
		this.task_type = task_type;
		this.timeExp = exp;
	}
	
	public long getSubpackage_id() {
		return subpackage_id;
	}
	public void setSubpackage_id(long subpackage_id) {
		this.subpackage_id = subpackage_id;
	}
	public int getTask_type() {
		return task_type;
	}
	public void setTask_type(int task_type) {
		this.task_type = task_type;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getTimeExp() {
		return timeExp;
	}

	public void setTimeExp(String timeExp) {
		this.timeExp = timeExp;
	}
}
