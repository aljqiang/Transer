package com.sunrise.cgb.core.strategy;

import com.sunrise.cgb.core.IQueueScheduleStrategy;
import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.core.IQueueScheduleStrategy.QueueScheduleResult;
import com.sunrise.cgb.queue.AbstractTaskEntity;
import com.sunrise.cgb.queue.QueryTaskEntity;
import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CcQueueScheduleStrategy implements IQueueScheduleStrategy {

	@Override
	public void addTaskEntity(AbstractTaskEntity entity) {
		TransHelper.transer()
						 .taskQueue()
						 .queryTaskQueue()
						 .add2WaitingQueue((QueryTaskEntity) entity);
	}

	@Override
	public QueueScheduleResult canSend() {
		QueueScheduleResult result = new QueueScheduleResult();
		//从队列获取任务
		if(TransHelper.transer().checkCcLoad()){
			QueryTaskEntity task = TransHelper.transer()
														  		.taskQueue()
														  		.queryTaskQueue()
														  		.popTaskEntity();
			result.setStatus((task!=null));
			if(!result.canSend()) 
				TransHelper.transer().decreaseCcLoad();
			else
				result.setTaskEntity(task);
		}
		return result;
	}

}
