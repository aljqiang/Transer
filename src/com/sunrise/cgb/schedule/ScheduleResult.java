package com.sunrise.cgb.schedule;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class ScheduleResult {

	private boolean succ;
	private String cause;
	
	public ScheduleResult(){
		this.succ = true;
		this.cause = "";
	}
	
	public ScheduleResult(String cause){
		this.succ = false;
		this.cause = cause;
	}

	public boolean isSucc() {
		return succ;
	}
	
	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.succ = false;
		this.cause += cause + "\r\n";
	}
}
