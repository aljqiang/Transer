package com.sunrise.cgb.core.strategy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.core.IQueueScheduleStrategy;
import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.queue.AbstractTaskEntity;
import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class SmsgQueueScheduleStrategy implements IQueueScheduleStrategy {

	private Log log = LogFactory.getLog(SmsgQueueScheduleStrategy.class);
	
	@Override
	public void addTaskEntity(AbstractTaskEntity entity) {
		TransHelper.transer()
						 .taskQueue()
						 .smsgTaskQueue()
						 .add2WaitingQueue((SendTaskEntity) entity);
	}

	@Override
	public QueueScheduleResult canSend() {
		QueueScheduleResult result = new QueueScheduleResult();
		//从队列获取任务
		if(TransHelper.transer().checkSmsgLoad()){
			SendTaskEntity task = TransHelper.transer()
														  		.taskQueue()
														  		.smsgTaskQueue()
														  		.popTaskEntity();
			result.setStatus((task!=null));
			if(!result.canSend()) 
				TransHelper.transer().decreaseSmsgCnt();
			else
				result.setTaskEntity(task);
		}
		return result;
	}

}
