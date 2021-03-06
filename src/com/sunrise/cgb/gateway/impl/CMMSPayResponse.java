package com.sunrise.cgb.gateway.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.IConnectorResponse;
import com.sunrise.cgb.gateway.ISoapConnectorResponse;
import com.sunrise.cgb.gateway.xmlparser.ProtoParser;
import com.sunrise.cgb.gateway.xmlparser.ProtoParser.ProtoContentParseResult;
import com.sunrise.cgb.gateway.xmlparser.impl.PayResponseParser;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CMMSPayResponse extends ConnectorResponseImpl implements ISoapConnectorResponse {

	private Log log = LogFactory.getLog(CMMSPayResponse.class);
	
	@Override
	public int getType() {
		return ProgramConfig.IO_TYPE_UPLOAD_PAY;
	}

	@Override
	public void read(String xml) throws Exception {
		//解析报文体
		ProtoContentParseResult parseResult = null;
		try {
			parseResult = ProtoParser.parse(xml);
		} catch (Exception e) {
			log.error("解析柜面网关转发调帐文服的反馈XML出错.",e);
			setResult(IConnectorResponse.RESULT_FAIL);
			setCode("response xml:"+xml+" stack:"+e.getMessage());
			return;
		}
		//抽取有用信息
		if(parseResult.isSuccess()){
			PayResponseParser content = (PayResponseParser)parseResult.protoContent().getBody();
			if("0000".equals(content.getErrorCode()))
				setResult(IConnectorResponse.RESULT_SUCCESS);
			else{
				setResult(IConnectorResponse.RESULT_FAIL);
				setCode(content.getErrorCode()+":"+content.getErrorMsg());
			}
		}else{
			setResult(IConnectorResponse.RESULT_FAIL);
			setCode(parseResult.getFault()+",response xml:"+xml);
		}
	}

}
