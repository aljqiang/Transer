package com.sunrise.cgb.core;

import com.sunrise.cgb.gateway.SendFileResult;
import com.sunrise.cgb.gateway.SendProtoResult;
import com.sunrise.cgb.gateway.SendTaskWorker;
import com.sunrise.cgb.gateway.SendTaskWorker.OverTimeChecker;
import com.sunrise.cgb.gateway.SendTaskWorker.Resender;
import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface ITransStrategy {

	/**
	 * 发送数据文件
	 * @param entity
	 * @return
	 */
	public SendFileResult sendDatafile(SendTaskEntity entity);
	
	/**
	 * 发送报文协议
	 * @param entity
	 * @param worker
	 * @param checker
	 * @return
	 */
	public SendProtoResult sendProto(SendTaskEntity entity,SendTaskWorker worker,Resender resender,OverTimeChecker checker);

	/**
	 * 传输结束后的释放资源
	 */
	public void release();
	
	/**
	 * 完成任务处理逻辑
	 * @throws Exception
	 */
	public void complete(SendTaskEntity entity) throws Exception;
}
