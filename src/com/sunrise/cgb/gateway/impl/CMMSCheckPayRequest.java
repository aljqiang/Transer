package com.sunrise.cgb.gateway.impl;

import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.CGBConnectorUtil;
import com.sunrise.cgb.gateway.ISoapConnectorRequest;
import com.sunrise.cgb.gateway.CGBConnectorUtil.CGBSeqNum;
import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CMMSCheckPayRequest extends NatpTransRequest implements ISoapConnectorRequest{
	
	public CMMSCheckPayRequest(){
		super();
	}
	
	@Override
	public int getType() {
		return ProgramConfig.IO_TYPE_CHECK_UPLOAD_PAY;
	}

	@Override
	public void read(SendTaskEntity entity) {
		//设置报文头
		setParam(ConstantKey.header_version_code,ProgramConfig.GATEWAY.getConfig(ConstantKey.header_version_code,1));
		setParam(ConstantKey.header_ct, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_ct, 0));
		setParam(ConstantKey.header_encrypt, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_encrypt, 0));
		setParam(ConstantKey.header_send_sid, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_send_sid, "CMMS"));
		setParam(ConstantKey.header_trade_code, ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_tradeCode_check, ""));
		setParam(ConstantKey.header_trans, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_trans, 500001));
		setParam(ConstantKey.header_server_code, ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_code,""));
		//设置流水号
		CGBSeqNum seqNum = CGBConnectorUtil.genGatewaySeqNum();
		setParam(ConstantKey.header_seq_num, seqNum.getSeqNum());
		setParam(ConstantKey.header_seq_time, seqNum.getSeqTime());
		setParam(ConstantKey.header_seq_date, seqNum.getSeqDate());
		entity.setSeqNum(seqNum.getSeqNum());
		//设置报文体
		setParam("templateCodeName",ProgramConfig.GATEWAY.getConfig(ConstantKey.template_code_name_check, ""));
		setParam("G_TRANSCODE",ProgramConfig.GATEWAY.getConfig(ConstantKey.g_transcode_check, ""));
		setParam("orSenderId", ProgramConfig.PAY.getConfig(ConstantKey.pay_or_sender_id, ""));
		setParam("orSenderSN", (this.orSenderSN == null)?seqNum.getSeqNum():this.orSenderSN.getSeqNum());
		setParam("orSenderDate", (this.orSenderSN == null)?seqNum.getSeqDate():this.orSenderSN.getSeqDate());
		
		setParam("direction", ProgramConfig.PAY.getConfig(ConstantKey.pay_direction, "1"));
		setParam("utId", ProgramConfig.PAY.getConfig(ConstantKey.pay_utID, "9999999999"));
		setParam("hostId", ProgramConfig.GATEWAY.getConfig(ConstantKey.pay_hostId, ""));
		setParam("libOrPathName", CGBConnectorUtil.fillRightString(
				ProgramConfig.PAY.getConfig(ConstantKey.pay_libOrPathName, ""),63,' '));
		setParam("docName", CGBConnectorUtil.fillRightString(
				ProgramConfig.PAY.getConfig(ConstantKey.pay_docName, ""),15,' '));
		setParam("reserved1", "          ");
	}

	@Override
	public String toXml() throws Exception {
		return CGBConnectorUtil.genSoapRequestProto(getType(), getParams());
	}
	

}
