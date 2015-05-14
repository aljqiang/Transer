package com.sunrise.cgb.exception;

import com.sunrise.cgb.queue.QueryTaskEntity;
import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class QueryTaskException extends Exception {
	
	private QueryTaskEntity e;
	private String cause;	
	private String errCode;
	private String errMsg;	
	
	public QueryTaskException(QueryTaskEntity e,String cause,String errCode,String errMsg){
		this.e = e;
		this.cause = cause;
		this.errCode = errCode;
		this.errMsg = errMsg;
	}
	
	@Override 
	public String getMessage(){
		return this.toString();
	}
	
	@Override
	public String toString(){
		String msg = "\r\n############################\r\n";
		msg += "传输任务类型"+e.getType()+"\r\n";
		msg += "柜面网关查询任务出错:\r\n"+this.cause + "\r\n";
		msg += "############################\r\n";
		return msg;
	}
	
	public String getErrCode(){
		return this.errCode;
	}
	
	public String getErrMsg(){
		return this.errMsg;
	}
}
