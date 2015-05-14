package com.sunrise.cgb.core.strategy;

import com.sunrise.cgb.core.IQueueScheduleStrategy;
import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.queue.AbstractTaskEntity;
import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class PayQueueScheduleStrategy implements IQueueScheduleStrategy {

	@Override
	public void addTaskEntity(AbstractTaskEntity entity) {
		TransHelper.transer()
						 .taskQueue()
						 .payTaskQueue()
						 .add2WaitingQueue((SendTaskEntity) entity);
	}

	@Override
	public QueueScheduleResult canSend() {
		QueueScheduleResult result = new QueueScheduleResult();
		//从队列获取任务
		if(TransHelper.transer().checkPayLoad()){
			SendTaskEntity task = TransHelper.transer()
														  		.taskQueue()
														  		.payTaskQueue()
														  		.popTaskEntity();
			result.setStatus((task!=null));
			if(!result.canSend()) 
				TransHelper.transer().decreasePayCnt();
			else
				result.setTaskEntity(task);
		}
		return result;
	}

}
