package com.sunrise.cgb.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CGBScheduleMonitor extends Thread {

	private static Log log = LogFactory.getLog(CGBScheduleMonitor.class);
	/**
	 * 正在运行线程数量
	 */
	private Integer running_cnt = 0;
	/**
	 * 最大运行线程量
	 */
	private static int MAX_RUNNING_SCHEDULER = 15;
	/**
	 * 监控调度线程构造函数
	 */
	protected CGBScheduleMonitor(){
		this.setName("CGBScheduleMonitor");
	}
	
	/**
	 * 通知监控器当前执行任务量
	 */
	protected void notifyMonitor(ScheduleInfo info){
		synchronized(running_cnt){
			if(running_cnt >= 0)
				running_cnt--;
			ScheduleManager.removeRunningTask(info);
		}
	}
	
	@Override
	public void run(){
		while(true)
		{
			//初始化参数
			CGBScheduler scheduler;
			boolean canStart = false;
			boolean canSleep = true;
			//判断是否运行任务线程大于最大执行量
			synchronized(running_cnt){
				if(running_cnt<=MAX_RUNNING_SCHEDULER){
					canStart = true;
					canSleep = false;
				}
			}			
			//从调度队列获取执行任务
			if(canStart){
				scheduler = ScheduleManager.popScheduler();
				if(null == scheduler)
					canSleep = true;
				if(!canSleep)
					//添加到任务运行池
					ScheduleManager.addRunningPool(scheduler);
			}
			//判断是否休眠
			if(canSleep)
				try{
					sleep(1000);
				}catch(InterruptedException e){
					log.error("调度监控线程被中断.",e);
				}			
		}
	}
}
