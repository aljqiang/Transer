package com.sunrise.cgb.gateway;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface IByteConnectorResponse{
	/**
	 * 解析反馈报文
	 * @param content
	 */
	public void read(byte[] content) throws Exception;
}
