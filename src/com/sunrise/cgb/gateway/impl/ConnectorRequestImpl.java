package com.sunrise.cgb.gateway.impl;

import java.util.HashMap;
import java.util.Map;

import com.sunrise.cgb.gateway.IConnectorRequest;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public abstract class ConnectorRequestImpl implements IConnectorRequest{

	private Map<String, Object> params = new HashMap<String, Object>();
	/**
	 * 设置输出参数
	 * @param key
	 * @param value
	 */
	protected void setParam(String key,Object value){
		this.params.put(key, value);
	}
	/**
	 * 获取输出参数
	 * @return
	 */
	protected Map<String,Object> getParams(){
		return this.params;
	}
}
