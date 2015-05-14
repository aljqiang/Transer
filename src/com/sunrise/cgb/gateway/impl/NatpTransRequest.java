package com.sunrise.cgb.gateway.impl;

import com.sunrise.cgb.gateway.CGBConnectorUtil;
import com.sunrise.cgb.gateway.CGBConnectorUtil.CGBSeqNum;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public abstract class NatpTransRequest extends ConnectorRequestImpl {

	protected CGBSeqNum orSenderSN;
	
	public NatpTransRequest(){
		this.orSenderSN = null;
	}
	/**
	 * 设置使用流水号
	 * @param seqNum
	 */
	public void setSeqnum(CGBSeqNum seqNum){
		this.orSenderSN = seqNum;
	}
	
	/**
	 * 生成流水号
	 */
	public void genSeqnum(){
		this.orSenderSN = CGBConnectorUtil.genGatewaySeqNum();
	}
	
	/**
	 * 获取使用流水号
	 * @return
	 */
	public CGBSeqNum getSeqnum(){
		return this.orSenderSN;
	}
}
