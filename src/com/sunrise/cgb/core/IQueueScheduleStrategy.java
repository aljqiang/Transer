package com.sunrise.cgb.core;

import com.sunrise.cgb.queue.AbstractTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface IQueueScheduleStrategy {

	public void addTaskEntity(AbstractTaskEntity entity);
	
	public QueueScheduleResult canSend();
	
	public class QueueScheduleResult{
		
		private boolean status;
		
		private AbstractTaskEntity taskEntity;

		public QueueScheduleResult(){
			this.status = false;
			this.taskEntity = null;
		}
		
		public boolean canSend() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		public AbstractTaskEntity getTaskEntity() {
			return taskEntity;
		}

		public void setTaskEntity(AbstractTaskEntity taskEntity) {
			this.taskEntity = taskEntity;
		}
		
	}
}
