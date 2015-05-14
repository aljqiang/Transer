package com.sunrise.cgb.queue;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public abstract class AbstractTaskEntity {

	public static final int HANDLE_TYPE_QUERY = 1;
	
	public static final int HANDLE_TYPE_NORMAL = 2;
	/**
	 * 获取任务实处理类型
	 * @return
	 */
	public abstract int getTaskHandleType();
}
