package com.sunrise.cgb.schedule;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface ITaskHandler {
	/**
	 * 处理任务类型
	 * @return
	 */
	public Integer getTaskType();
	/**
	 * 处理逻辑
	 * @param info
	 */
	public void handle(ScheduleInfo info);
}
