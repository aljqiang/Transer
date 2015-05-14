package com.sunrise.cgb.schedule.impl;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.JobBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;

import com.sunrise.cgb.schedule.IScheduleEngine;
import com.sunrise.cgb.schedule.ScheduleInfo;
import com.sunrise.cgb.schedule.ScheduleManager;
import com.sunrise.cgb.schedule.ScheduleResult;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class QuartzScheduleEngine implements IScheduleEngine {

	private static Log log = LogFactory.getLog(QuartzScheduleEngine.class);

	/**
	 * Quartz调度器
	 */
    private static SchedulerFactory sf = new StdSchedulerFactory();

	@Override
	public ScheduleResult lauchTask(ScheduleInfo info){
        try{
        	String key = info.getSubpackage_id()+"";
        	Scheduler scheduler = sf.getScheduler();
//        	JobDetail details = new JobDetail(key,Scheduler.DEFAULT_GROUP,BaseJob.class);
//        	CronTrigger trigger = new CronTrigger(key,null,info.getTimeExp());
        	JobDetail details = JobBuilder.newJob(BaseJob.class).withIdentity(key, Scheduler.DEFAULT_GROUP).build();

        	TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
        	 				     .withIdentity(key)
        	 				     .withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(info.getTimeExp())));
        	CronTrigger trigger = triggerBuilder.build();

			//Trigger trigger = new SimpleTrigger(key,Scheduler.DEFAULT_GROUP,info.getStartTime(),null, 0,0L);
			details.getJobDataMap().put(ScheduleInfo.FLAG_SUBPACKAGE_ID,info.getSubpackage_id());
			details.getJobDataMap().put(ScheduleInfo.FLAG_TASK_TYPE, info.getTask_type());
			//加载计划任务进Quartz调度器
			scheduler.scheduleJob(details,trigger);
			if(!scheduler.isShutdown())
				scheduler.start();

            // 以任务类型记录调度任务信息
            QuartzScheduleInfoMap.addQS(info.getSubpackage_id(),info.getTask_type());
            log.debug("已存储subpackage_id为["+info.getSubpackage_id()+"]调度任务");

			log.debug("添加["+info.getSubpackage_id()+"]拆分包计划到调度器");
        } catch (RuntimeException e){
        	log.error("添加调度计划出错:"+e.getMessage());
        } catch (SchedulerException e) {
			log.error("创建调度器出错:",e);
			return new ScheduleResult("创建调度器出错:"+e.getMessage());
		} catch (ParseException e) {
			log.error("解析表达式出错",e);
			return new ScheduleResult("解析表达式出错:"+e.getMessage());
		}

        return new ScheduleResult();
	}

	@Override
	public ScheduleResult cancelTaskPlan(ScheduleInfo info) {
    	//检测Quartz的调度池是否存在该任务
    	Scheduler scheduler = null;
		try {
			scheduler = sf.getScheduler();
		} catch (SchedulerException e) {
			log.error("获取Quartz调度器失败.",e);
			return new ScheduleResult("获取Quartz调度器失败:"+e.getMessage());
		}
    	ScheduleResult result = new ScheduleResult();
    	//先查执行任务池是否存在对应任务,是则中断线程返回取消任务成功
    	if(ScheduleManager.interruptRunningTask(info))
    		return result;
		//Quartz标识
    	String key = info.getSubpackage_id()+"";
    	//取消Quartz内调度线程
    	try {
			scheduler.pauseJob(new JobKey(key, Scheduler.DEFAULT_GROUP));
		} catch (SchedulerException e) {
			log.error("停止标识为:["+key+"]的QuartzJob失败.",e);
			result.setCause("停止标识为:["+key+"]的QuartzJob失败:"+e.getMessage());
		}
    	try {
			scheduler.pauseTrigger(new TriggerKey(key,null));
		} catch (SchedulerException e) {
			log.error("停止标识为:["+key+"]的QuartzTrigger失败.",e);
			result.setCause("停止标识为:["+key+"]的QuartzTrigger失败:"+e.getMessage());
		}
    	try {
			scheduler.deleteJob(new JobKey(key,Scheduler.DEFAULT_GROUP));
		} catch (SchedulerException e) {
			log.error("删除标识为:["+key+"]的QuatzJob失败");
			result.setCause("删除标识为:["+key+"]的QuatzJob失败:"+e.getMessage());
		}
    	//输出信息
    	if(result.isSucc())
    		log.info("删除标识为:["+key+"]的任务计划成功");

        // 移除定时调度任务任务
        QuartzScheduleInfoMap.removeQS(info.getSubpackage_id());

    	//返回结果
    	return result;
	}

	@Override
	public boolean isTaskPlanExsit(ScheduleInfo info) throws Exception {
    	Scheduler scheduler = null;
		try {
			scheduler = sf.getScheduler();
		} catch (SchedulerException e) {
			log.error("获取Quartz调度器失败.",e);
			throw new Exception("判断是否已存在此任务计划时,获取Quartz调度器失败:"+e.getMessage());
		}
		if(null == scheduler.getTrigger(new TriggerKey(info.getSubpackage_id()+"", null)))
			return false;
		else
			return true;
	}

    /**
     * 关闭所有定时任务
     */
    @Override
    public boolean shutdownJobs() throws Exception {
        boolean result;

        try {
            Scheduler sched = sf.getScheduler();
            if(!sched.isShutdown()) {
                sched.shutdown();
            }
           result=true;
            QuartzScheduleInfoMap.removeAllQS();
           log.debug("############关闭所有定时任务############");
        } catch (Exception e) {
            log.error("关闭所有定时任务失败:" + e);
            throw new Exception("关闭所有定时任务失败:"+e.getMessage());
        }

        return result;
    }
}
