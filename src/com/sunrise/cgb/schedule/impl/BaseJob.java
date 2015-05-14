package com.sunrise.cgb.schedule.impl;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sunrise.cgb.schedule.ScheduleInfo;
import com.sunrise.cgb.schedule.ScheduleManager;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class BaseJob implements Job {

	@Override
	public void execute(JobExecutionContext cxt) throws JobExecutionException {
		long subpackage_id = cxt.getJobDetail().getJobDataMap().getLong(ScheduleInfo.FLAG_SUBPACKAGE_ID);
		int task_type = cxt.getJobDetail().getJobDataMap().getInt(ScheduleInfo.FLAG_TASK_TYPE);
		ScheduleInfo scheduleInfo = new ScheduleInfo(subpackage_id,task_type);
		ScheduleManager.addScheduler(scheduleInfo);
	}

}
