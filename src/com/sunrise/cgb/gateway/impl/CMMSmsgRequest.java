package com.sunrise.cgb.gateway.impl;

import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.CGBConnectorUtil;
import com.sunrise.cgb.gateway.ISoapConnectorRequest;
import com.sunrise.cgb.gateway.CGBConnectorUtil.CGBSeqNum;
import com.sunrise.cgb.queue.SendTaskEntity;
import com.sunrise.foundation.utils.StringUtil;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CMMSmsgRequest extends ConnectorRequestImpl implements ISoapConnectorRequest {

	@Override
	public int getType() {
		return ProgramConfig.IO_TYPE_SMSG;
	}

	@Override
	public void read(SendTaskEntity entity) {
		
		setParam(ConstantKey.header_version_code,ProgramConfig.SMSG.getConfig(ConstantKey.header_version_code,1));
		setParam(ConstantKey.header_ct, ProgramConfig.SMSG.getConfig(ConstantKey.header_ct, 0));
		setParam(ConstantKey.header_encrypt, ProgramConfig.SMSG.getConfig(ConstantKey.header_encrypt, 0));
		setParam(ConstantKey.header_send_sid, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_send_sid, "test"));
		setParam(ConstantKey.header_trade_code, ProgramConfig.SMSG.getConfig(ConstantKey.header_trade_code, "CMM003"));
		setParam(ConstantKey.header_trans, ProgramConfig.SMSG.getConfig(ConstantKey.header_trans, 500001));
		setParam(ConstantKey.header_server_code, ProgramConfig.SMSG.getConfig(ConstantKey.server_code,""));
		
		CGBSeqNum seqNum = CGBConnectorUtil.genGatewaySeqNum();
		setParam(ConstantKey.header_seq_num, seqNum.getSeqNum());
		setParam(ConstantKey.header_seq_time, seqNum.getSeqTime());
		setParam(ConstantKey.header_seq_date, seqNum.getSeqDate());
		//设置流水号
		entity.setSeqNum(seqNum.getSeqNum());
		
		setParam("TransCode", ProgramConfig.SMSG.getConfig(ConstantKey.header_trade_code, "CMM003"));
		setParam("SENDID", ProgramConfig.GATEWAY.getConfig(ConstantKey.header_send_sid, "test"));
		setParam("SENDDATE", seqNum.getSeqDate());
		setParam("SENDSN", seqNum.getSeqNum());
		setParam("CHANNELCODE",ProgramConfig.SMSG.getConfig(ConstantKey.body_chanel_code, "test"));
		setParam("FILENAME", entity.getfileName());
		setParam("Branch", ProgramConfig.SMSG.getConfig(ConstantKey.body_branch, "test"));
		setParam("SubBranch", ProgramConfig.SMSG.getConfig(ConstantKey.body_subbranch, "test"));
		//params.put("OperatorId", ProgramConfig.SMSG.getConfig(ConstantKey.body_oper, "test"));
		String OperatorId = StringUtil.isNull(entity.getRespCode())?
											ProgramConfig.SMSG.getConfig(ConstantKey.body_oper, "999999002"):entity.getRespCode();
		//2013-09-22 徐云西说可能跟踪号码会浮动并且超过最大长度，需要截取长度 修改人：李嘉伟
		if(OperatorId.length() > 4){
			OperatorId = OperatorId.substring(0,OperatorId.length()-4);
			if(OperatorId.length()>9)
				OperatorId = OperatorId.substring(1);
		}
		setParam("OperatorId", OperatorId);

	}

	@Override
	public String toXml() throws Exception{
		return CGBConnectorUtil.genSoapRequestProto(getType(), getParams());
	}
}
