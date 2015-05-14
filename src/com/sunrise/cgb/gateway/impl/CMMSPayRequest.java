package com.sunrise.cgb.gateway.impl;

import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.CGBConnectorUtil;
import com.sunrise.cgb.gateway.ISoapConnectorRequest;
import com.sunrise.cgb.queue.SendTaskEntity;
import com.sunrise.foundation.utils.StringUtil;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CMMSPayRequest extends NatpTransRequest implements ISoapConnectorRequest {
	/**
	 * 调帐文件上传请求
	 */
	public CMMSPayRequest(){
		super();
	}
	
	@Override
	public int getType() {
		return ProgramConfig.IO_TYPE_UPLOAD_PAY;
	}

	@Override
	public void read(SendTaskEntity entity) {
		//设置报文头
		setParam(ConstantKey.header_version_code,ProgramConfig.GATEWAY.getConfig(ConstantKey.header_version_code,1));
		setParam(ConstantKey.header_ct, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_ct, 0));
		setParam(ConstantKey.header_encrypt, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_encrypt, 0));
		setParam(ConstantKey.header_send_sid, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_send_sid, "CMMS"));
		setParam(ConstantKey.header_trade_code, ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_tradeCode_upload, ""));
		setParam(ConstantKey.header_trans, ProgramConfig.GATEWAY.getConfig(ConstantKey.header_trans, 500001));
		setParam(ConstantKey.header_server_code, ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_code,""));
		//设置流水号
		genSeqnum();
		setParam(ConstantKey.header_seq_num, orSenderSN.getSeqNum());
		setParam(ConstantKey.header_seq_time, orSenderSN.getSeqTime());
		setParam(ConstantKey.header_seq_date, orSenderSN.getSeqDate());
		entity.setSeqNum(orSenderSN.getSeqNum());
		//设置报文体
		setParam("templateCodeName",ProgramConfig.GATEWAY.getConfig(ConstantKey.template_code_name_upload, ""));
		setParam("G_TRANSCODE",ProgramConfig.GATEWAY.getConfig(ConstantKey.g_transcode_upload, ""));
		setParam("hostId", ProgramConfig.PAY.getConfig(ConstantKey.pay_hostId, "000001"));
		setParam("libOrPathName", CGBConnectorUtil.fillRightString(
				ProgramConfig.PAY.getConfig(ConstantKey.pay_libOrPathName, "000001"),64,' '));
		setParam("docName", CGBConnectorUtil.fillRightString(
				ProgramConfig.PAY.getConfig(ConstantKey.pay_docName, ""),15,' '));
		setParam("memberName", CGBConnectorUtil.fillRightString(
				ProgramConfig.PAY.getConfig(ConstantKey.pay_memberName,""),10,' '));
		setParam("orSenderId", ProgramConfig.PAY.getConfig(ConstantKey.pay_or_sender_id, ""));
		setParam("orSenderSN", orSenderSN.getSeqNum());
		setParam("orSenderDate", orSenderSN.getSeqDate());
		setParam("orSenderTime", orSenderSN.getSeqTime());
		setParam("ooSenderId", ProgramConfig.PAY.getConfig(ConstantKey.pay_oo_sender_id, ""));
		setParam("ooSenderSN", orSenderSN.getSeqNum());
		setParam("ooSenderDate", orSenderSN.getSeqDate());
		setParam("ooSenderTime", orSenderSN.getSeqTime());
		setParam("fileCategory", ProgramConfig.PAY.getConfig(ConstantKey.pay_fileCategory, ""));
		setParam("terminalNO", ProgramConfig.PAY.getConfig(ConstantKey.pay_ternno, "          "));
		setParam("utID", ProgramConfig.PAY.getConfig(ConstantKey.pay_utID, ""));
		setParam("priority",ProgramConfig.PAY.getConfig(ConstantKey.pay_priority, "  "));
		setParam("srcFileName", entity.getfileName());
		
		String srcFileDes = ProgramConfig.PAY.getConfig(ConstantKey.body_srcFileDes, "市场营销管理系统批量入账文件");		
		setParam("srcFileDes", StringUtil.changeEncoding(srcFileDes, "ISO-8859-1", "UTF-8"));
		setParam("synchStatus", orSenderSN.getSeqNum());
		setParam("reserved1", "");
		setParam("reserved2", "");
	}

	@Override
	public String toXml() throws Exception {
		return CGBConnectorUtil.genSoapRequestProto(getType(), getParams());
	}
}
