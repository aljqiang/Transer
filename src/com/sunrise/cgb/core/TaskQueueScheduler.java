package com.sunrise.cgb.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.core.IQueueScheduleStrategy.QueueScheduleResult;
import com.sunrise.cgb.gateway.QueryTaskWorker;
import com.sunrise.cgb.gateway.SendTaskWorker;
import com.sunrise.cgb.queue.AbstractTaskEntity;
import com.sunrise.cgb.queue.QueryTaskEntity;
import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class TaskQueueScheduler extends Thread {

	private static Log log = LogFactory.getLog(TaskQueueScheduler.class);
	//轮询频率(秒)
	private double send_loop_delay;
	//任务类型
	private int task_type;
	//超时配置	
	private int ot;
	//重发配置
	private int resendTime;
	//重发间隔
	private int resendDelay;
	/**
	 * 任务队列调度器
	 * @param send_loop_delay
	 * @param task_type
	 * @param ot
	 * @param resendTime
	 */
	public TaskQueueScheduler(double send_loop_delay,int task_type,int ot,int resendTime,int resendDelay){
		this.send_loop_delay = send_loop_delay;
		this.task_type = task_type;
		this.ot = ot;
		this.resendTime = resendTime;
		this.resendDelay = resendDelay;
	}
	
	@Override
	public void run(){
		while(true){
			QueueScheduleResult result = TransHelper.getScheduleStrategy(task_type).canSend();
			//判断发送还是等待
			if(result.canSend()){
				AbstractTaskEntity task = result.getTaskEntity();
				if(task.getTaskHandleType() == AbstractTaskEntity.HANDLE_TYPE_NORMAL){
					SendTaskWorker sendTask = new SendTaskWorker((SendTaskEntity)task);
					sendTask.setOverTime(this.ot);
					sendTask.setResender(this.resendTime, this.resendDelay);
					sendTask.start();
				}else
					new QueryTaskWorker((QueryTaskEntity)task).start();
				
				continue;
			}
			//轮询休眠
			try {
				Thread.sleep(((int)(send_loop_delay*1000)));
			} catch (InterruptedException e) {
				log.error("轮询发送任务队列被中断.",e);
				throw new RuntimeException();
			}
		}
	}
}
