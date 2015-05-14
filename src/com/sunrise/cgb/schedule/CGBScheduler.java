package com.sunrise.cgb.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CGBScheduler extends Thread {
	
	private static Log log = LogFactory.getLog(CGBScheduler.class);
	/**
	 * 调度任务参数
	 */
	private ScheduleInfo info;
	/**
	 * 调度结束状态
	 */
	private boolean dead;
	/**
	 * 调度监控线程
	 */
	private CGBScheduleMonitor monitor;
	/**
	 * 任务调度监控线程构造函数
	 * @param monitor
	 * @param info
	 */
	protected CGBScheduler(CGBScheduleMonitor monitor,ScheduleInfo info){
		this.info = info;
		this.monitor = monitor;
		this.dead = false;
		this.setName("CGBScheduler(\"tasktype:"+info.getTask_type()+"\",\"subpackage_id:"+info.getSubpackage_id()+"\")");
	}
	
	/**
	 * 返回调度器参数
	 * @return
	 */
	protected ScheduleInfo getInfo(){
		return this.info;
	}
	
	/**
	 * 查看结束状态
	 * @return
	 */
	protected boolean isDead(){
		return this.dead;
	}
	
	@Override
	public void run(){
		try{
			log.error("开始执行拆分包ID为:"+info.getSubpackage_id()+",类型为:"+info.getTask_type()+"任务..");
			ITaskHandler handler = ScheduleManager.getTaskHandler(info.getTask_type());
			handler.handle(info);
			log.error("结束执行拆分包ID为:"+info.getSubpackage_id()+",类型为:"+info.getTask_type()+"任务..");
		}finally{
			//更改结束状态
			this.dead = true;
			//通知监控器当前任务已经完成
			this.monitor.notifyMonitor(getInfo());
		}
	}
}
