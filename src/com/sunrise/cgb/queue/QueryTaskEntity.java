package com.sunrise.cgb.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class QueryTaskEntity extends AbstractTaskEntity{

	private String type;
	
	private ServletOutputStream out;
	
	private Map<String,Object> queryParams = new HashMap<String,Object>();
	
	/**
	 * 获取查询类型
	 * @return
	 */
	public String getType() {
		return type;
	}
	/**
	 * 设置查询类型
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 获取查询参数
	 * @return
	 */
	public Map<String, Object> getQueryParams() {
		return queryParams;
	}
	/**
	 * 添加查询参数
	 * @param key
	 * @param value
	 */
	public void setQueryParams(String key,String value) {
		this.queryParams.put(key, value);
	}
	
	/**
	 * 添加查询参数
	 * @param params
	 */
	public void addQueryParams(Map<String,Object> params){
		this.queryParams.putAll(params);
	}
		
	/**
	 * 设置返回端输出流
	 * @param out
	 */
	public void setOutput(ServletOutputStream out){
		this.out = out;
	}
	
	/**
	 * 获取返回输出流
	 * @return
	 */
	public ServletOutputStream getOutput(){
		return this.out;
	}
	
	@Override
	public int getTaskHandleType() {
		return super.HANDLE_TYPE_QUERY;
	}
}
