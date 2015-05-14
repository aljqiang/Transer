package com.sunrise.cgb.gateway.connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.gateway.ITransConnector;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:通用通讯处理器
 */
public abstract class TransConnectorImpl implements ITransConnector {

	private static Log log = LogFactory.getLog(TransConnectorImpl.class);
	
	protected Socket gateway;
	private String gateway_addr;
	private int gateway_port;
	private int con_timeout;
	/**
	 * 通用通讯处理器
	 * @param gateway_addr
	 * @param gateway_port
	 */
	public TransConnectorImpl(String gateway_addr,int gateway_port){
		this.gateway_addr = gateway_addr;
		this.gateway_port = gateway_port;
		this.con_timeout = -1;
	}
	
	/**
	 * 通用通讯处理器
	 * @param gateway_addr
	 * @param gateway_port
	 */
	public TransConnectorImpl(String gateway_addr,int gateway_port,int timeout){
		this.gateway_addr = gateway_addr;
		this.gateway_port = gateway_port;
		this.con_timeout = timeout;
	}
	
	/**
	 * 设置连接等待超时时间
	 * @param timeout
	 */
	public void setConnectTimeout(int timeout){
		this.con_timeout = timeout;
	}
	
	/**
	 * 初始化连接
	 * @throws Exception
	 */
	protected void initConnector() throws Exception {
		//若已经创建则不创建
		if(null != gateway)
			return;
		//若未创建则重新初始化
		try {
			gateway = new Socket();
			if(-1 == con_timeout)
				gateway.connect(new InetSocketAddress(gateway_addr,gateway_port));
			else
				gateway.connect(new InetSocketAddress(gateway_addr,gateway_port),con_timeout);
		} catch (UnknownHostException e) {
			log.error("向"+gateway_addr+"建立连接出错,找不到该地址.");
			throw new Exception(e);
		} catch (IOException e) {
			log.error("建立柜面网关出现IO异常",e);
			throw new Exception(e);
		}
	}

	@Override
	public void release() {
		try {
			if(null == gateway)
				log.error("柜面网关通讯对象还没有建立socket连接，无法释放资源.");
			else
				gateway.close();
		} catch (Exception e) {
			log.error("销毁柜面网关连接出错.",e);
		} finally{
			gateway = null;
		}
	}

	/**
	 * 设置新连接
	 * @param connection
	 */
	public void setGatewayConnection(Socket connection){
		this.gateway = connection;
	}
}
