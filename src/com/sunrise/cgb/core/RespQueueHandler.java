package com.sunrise.cgb.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.exception.SendTaskException;
import com.sunrise.cgb.queue.BlockQueue;
import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class RespQueueHandler extends Thread {

	private static Log log = LogFactory.getLog(RespQueueHandler.class);
	//轮询队列时间
	private int loop_delay;
	//重发处理器
	private Resender resender;
	/**
	 * 反馈任务队列处理器
	 * @param uri
	 * @param loop_delay
	 */
	public RespQueueHandler(int loop_delay){
		this.loop_delay = loop_delay;
		this.resender = new Resender();
	}
	
	@Override
	public void run(){
		//启动重发队列线程
		Thread resendThread = new Thread(resender);
		resendThread.setName("FailureTaskQueue-Resender");
		resendThread.setDaemon(true);
		resendThread.start();
		//轮训队列
		while(true)
		{
			//获取待反馈任务实例
			SendTaskEntity entity = TransHelper.transer()
																.taskQueue()
																.respTaskQueue()
																.popTaskEntity();
			if(null == entity){
				try {
					sleep(loop_delay);
					continue;
				} catch (InterruptedException e) {
					log.error("轮询完成任务队列时被中断.",e);
				}
			}
			//发送返回CMMS处理更改表状态		
			try {
				TransHelper
					.getTransStrategy(entity.getTaskType())
						.complete(entity);
			} catch (Exception e) {
				//记录到日志
				log.error("反馈任务执行结果到地址失败.",new SendTaskException(entity, "发送任务结果失败"));
				//添加到重发队列
				this.resender
					  .failureTaskQueue
					  .add2WaitingQueue(entity);
			}
		}
	}
	/**
	 * 反馈失败重发处理器
	 * @author 翻书侠
	 *
	 */
	private static class Resender implements Runnable{

		//反馈失败任务待重发队列
		private final BlockQueue<SendTaskEntity> failureTaskQueue = new BlockQueue<SendTaskEntity>();
		//重发时间间隔
		private int resend_delay;
		
		/**
		 * 初始化
		 * @param handler
		 */
		public Resender() {
			this.setDelay(10*1000);
		}
		
		/**
		 * 设置重发间隔
		 * @param delay
		 */
		public void setDelay(int delay){
			this.resend_delay = delay;
		}
		
		@Override
		public void run() {
			while(true){
				//获取任务
				SendTaskEntity entity = failureTaskQueue.popTaskEntity();
				if(null != entity){
				//重发任务
					boolean isLoop = true;
					while(isLoop)
					{
						try{
							TransHelper
								.getTransStrategy(entity.getTaskType())
									.complete(entity);
							isLoop = false;
						}catch(Exception ex){
							try {
								Thread.sleep(resend_delay);
							} catch (InterruptedException e) {
								log.error("重发反馈失败任务休眠被中断.");
							}
						}						
					}
				}
				//每10秒轮询一次
				try {
					Thread.sleep(resend_delay);
				} catch (InterruptedException e) {
					log.error("反馈失败队列轮询被中断.",e);
				}
			}			
		}
	}
}
