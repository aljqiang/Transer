package com.sunrise.cgb.gateway.impl;

import com.sunrise.cgb.gateway.IConnectorResponse;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public abstract class ConnectorResponseImpl implements IConnectorResponse{
	/**
	 * 错误编码
	 */
	private String code="";
	/**
	 * 返回下游系统执行结果
	 */
	private int result;

	@Override
	public int getResult() {		
		return result;
	}

	@Override
	public String getErrorCode() {		
		return code;
	}
	/**
	 * 设置错误编码
	 * @param code
	 */
	protected void setCode(String code) {
		this.code = code;
	}
	/**
	 * 设置执行结果
	 * @param result
	 */
	protected void setResult(int result) {
		this.result = result;
	}

}
