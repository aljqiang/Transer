package com.sunrise.cgb.gateway;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface ISoapConnectorResponse{


	/**
	 * 读取返回结果
	 * @param entity
	 */
	public void read(String xml) throws Exception;
	
}
