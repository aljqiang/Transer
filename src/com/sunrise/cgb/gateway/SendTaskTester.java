package com.sunrise.cgb.gateway;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.queue.SendTaskEntity;
import com.sunrise.foundation.utils.StringUtil;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class SendTaskTester extends SendTaskWorker {

	public static Log log = LogFactory.getLog(SendTaskTester.class);
	
	private String errorMsg;
	
	private boolean result;
	
	private SendTaskEntity entityRef;
	
	public SendTaskTester(SendTaskEntity entity) {
		super(entity);
		//初始化返回结果参数
		this.errorMsg = "";
		this.result = true;
		this.entityRef = entity;
		//记录发送信息
		log.debug("开始计划发送测试任务,任务类型为:"+entity.getTaskType());
	}

	/**
	 * 释放资源
	 */
	@Override
	public void release(){
		
	}
	
	/**
	 * 处理连接返回值
	 * @param rep
	 */
	@Override
	public void handleProcess(IConnectorResponse rep){
		if(rep.getResult() == IConnectorResponse.RESULT_FAIL){
			this.result = false;
			this.errorMsg = rep.getErrorCode();
		}
	}
	/**
	 * 连接i失败处理
	 */
	@Override
	public void exceptionProcess(String errCode,String cause){
		this.errorMsg = errCode+",cause:"+cause;
		this.result = false;
	}
	
	/**
	 * 获取是否成功
	 * @return
	 */
	public boolean isSuccess(){
		return this.result;
	}
	
	/**
	 * 获取流水号
	 * @return
	 */
	public String getSeqnum(){
		return (StringUtil.isNull(this.entityRef.getSeqNum()))?"在ufs系统发送数据文件到目标时已经出错.无流水号.":this.entityRef.getSeqNum();
	}
	
	/**
	 * 获取错误信息
	 * @return
	 */
	public String getErrmsg(){
		return this.errorMsg;
	}
}
