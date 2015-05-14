package com.sunrise.cgb.exception;

import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class SendTaskException extends Exception {
	
	public SendTaskEntity e;
	public String cause;
	public SendTaskException(SendTaskEntity e,String cause){
		this.e = e;
		this.cause = cause;
	}
	
	@Override 
	public String getMessage(){
		return this.toString();
	}
	
	@Override
	public String toString(){
		String msg = "\r\n############################\r\n";
		msg += "传输任务信息:"+"\r\n";
		msg += "传输数据文件:" + e.getSrcDir() + "\r\n";
		msg += "传输任务类型:" + e.getTaskType() + "\r\n";
		msg += "传输发送时间:" + e.getSendTime() + "\r\n";
		msg += "柜面网关传输任务出错:\r\n"+this.cause + "\r\n";
		msg += "############################\r\n";
		return msg;
	}
	
}
