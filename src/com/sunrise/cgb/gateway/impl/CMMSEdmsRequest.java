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
public class CMMSEdmsRequest extends ConnectorRequestImpl implements ISoapConnectorRequest{

	@Override
	public int getType() {
		return ProgramConfig.IO_TYPE_EDMS;
	}

	@Override
	public void read(SendTaskEntity entity) {
		

		setParam(ConstantKey.header_version_code,ProgramConfig.EDMS.getConfig(ConstantKey.header_version_code,1));
		setParam(ConstantKey.header_ct, ProgramConfig.EDMS.getConfig(ConstantKey.header_ct, 0));
		setParam(ConstantKey.header_encrypt, ProgramConfig.EDMS.getConfig(ConstantKey.header_encrypt, 0));
		setParam(ConstantKey.header_send_sid, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_send_sid, "test"));
		setParam(ConstantKey.header_trade_code, ProgramConfig.EDMS.getConfig(ConstantKey.header_trade_code, "CMM003"));
		setParam(ConstantKey.header_trans, ProgramConfig.EDMS.getConfig(ConstantKey.header_trans, 500001));
		setParam(ConstantKey.header_server_code, ProgramConfig.EDMS.getConfig(ConstantKey.server_code,""));
		
		CGBSeqNum seqNum = CGBConnectorUtil.genGatewaySeqNum();
		setParam(ConstantKey.header_seq_num, seqNum.getSeqNum());
		setParam(ConstantKey.header_seq_time, seqNum.getSeqTime());
		setParam(ConstantKey.header_seq_date, seqNum.getSeqDate());
		//设置流水号
		entity.setSeqNum(seqNum.getSeqNum());
		
		String taskName = ProgramConfig.EDMS.getConfig(ConstantKey.body_taskName, "营销系统源任务0628");
		taskName = StringUtil.changeEncoding(taskName, "ISO-8859-1", "UTF-8");
		setParam("taskName", taskName);
		setParam("mark", seqNum.getSeqTime());
		setParam("FileCount", "0000000001");
		setParam("SendBeginDate", seqNum.getSeqDate());
		setParam("SendEndDate", seqNum.getSeqDate());
		setParam("RecordNum", entity.getFileRecordNumStr());
		setParam("FileName", entity.getfileName());
	}

	@Override
	public String toXml() throws Exception{
		return CGBConnectorUtil.genSoapRequestProto(getType(), getParams());
	}
}
