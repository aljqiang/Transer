package com.sunrise.cgb.gateway.xmlparser;

import java.util.HashMap;
import java.util.Map;

import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.xmlparser.impl.CheckPayResponseParser;
import com.sunrise.cgb.gateway.xmlparser.impl.EdmsResponseParser;
import com.sunrise.cgb.gateway.xmlparser.impl.ActivityQueryRequestParser;
import com.sunrise.cgb.gateway.xmlparser.impl.PayResponseParser;
import com.sunrise.cgb.gateway.xmlparser.impl.RewardQueryRequestParser;
import com.sunrise.cgb.gateway.xmlparser.impl.SmsgResponseParser;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public abstract class ProtoParserFactory {

	private static final Map<String,ProtoContentParser> pattern = new HashMap<String, ProtoContentParser>();
	
	private ProtoParserFactory() {
	
	}
	/**
	 * 添加报文体解析实体
	 * @param tradeCode
	 * @param rcb
	 */
	public static void registerProtoParser(String tradeCode,ProtoContentParser rcb){
		pattern.put(tradeCode, rcb);
	}
	/**
	 * 获取报文体解析实体
	 * @param tradeCode
	 * @return
	 */
	public static ProtoContentParser buildProtoParser(String tradeCode){
		try{
			return pattern.get(tradeCode).build();
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 初始化报文解析工厂
	 */
	public static void init(){
		//注册呼叫中心查询报文解析
		ProtoParserFactory.registerProtoParser(
				ProgramConfig.CALLCENTER.getConfig(ConstantKey.header_cc_qact_trade_code,""),
				new ActivityQueryRequestParser());
		ProtoParserFactory.registerProtoParser(
				ProgramConfig.CALLCENTER.getConfig(ConstantKey.header_cc_qreward_trade_code,""),
				new RewardQueryRequestParser());
		//注册电邮系统报文解析
		ProtoParserFactory.registerProtoParser(
				ProgramConfig.EDMS.getConfig(ConstantKey.header_trade_code,""),
				new EdmsResponseParser());
		//注册短信平台系统报文解析
		ProtoParserFactory.registerProtoParser(
				ProgramConfig.SMSG.getConfig(ConstantKey.header_trade_code, ""),
				new SmsgResponseParser());
		//注册主机调帐报文解析
		ProtoParserFactory.registerProtoParser(
				ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_tradeCode_upload, ""),
				new PayResponseParser());
		//注册主机检查调帐报文解析
		ProtoParserFactory.registerProtoParser(
				ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_tradeCode_check, ""), 
				new CheckPayResponseParser());
	}
}
