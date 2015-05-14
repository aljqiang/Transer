package com.sunrise.cgb.gateway;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class SendProtoResult {

	private boolean result;
	private boolean outbreak;
	
	public SendProtoResult(){
		this.result = false;
		this.outbreak = false;
	}
	
	public void setBreak(){
		this.outbreak = true;
	}
	
	public boolean isBreak(){
		return this.outbreak;
	}
	
	public void setSuccess(){
		this.result = true;
	}
	
	public boolean isSuccess(){
		return this.result;
	}
}
