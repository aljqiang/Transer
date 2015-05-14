package com.sunrise.cgb.gateway;

import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface IConnectorRequest {
	/**
	 * 对应任务类型
	 * @return
	 */
	public int getType();
	/**
	 * 填充请求属性
	 * @param entity
	 */
	public void read(SendTaskEntity entity);

}
