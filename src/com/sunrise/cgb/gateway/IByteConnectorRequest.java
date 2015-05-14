package com.sunrise.cgb.gateway;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface IByteConnectorRequest {
	/**
	 * 转换为定长报文
	 * @return
	 */
	public byte[] toBytes() throws Exception;
}
