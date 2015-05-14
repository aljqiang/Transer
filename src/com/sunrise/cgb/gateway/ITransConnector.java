package com.sunrise.cgb.gateway;

import java.io.IOException;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface ITransConnector {
	/**
	 * 发送报文
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public IConnectorResponse sendRequest(IConnectorRequest request) throws Exception;
	/**
	 * 释放资源
	 */
	public void release();
}
