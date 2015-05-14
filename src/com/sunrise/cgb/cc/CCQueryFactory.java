package com.sunrise.cgb.cc;

import java.util.HashMap;
import java.util.Map;

import com.sunrise.cgb.cc.impl.ActivityQueryHandler;
import com.sunrise.cgb.cc.impl.RewardQueryHandler;
import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.CGBConnectorUtil;


/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CCQueryFactory {

	private static final Map<String,ICCQueryHandler> pattern = new HashMap<String, ICCQueryHandler>();
	
	private CCQueryFactory() {
	
	}
	/**
	 * 添加报文体解析实体
	 * @param tradeCode
	 * @param rcb
	 */
	public static void registerQueryHandler(String tradeCode,ICCQueryHandler rcb){
		CGBConnectorUtil.loadProtoPattern(rcb.responseBody());
		pattern.put(tradeCode, rcb);
	}
	/**
	 * 获取报文体解析实体
	 * @param tradeCode
	 * @return
	 */
	public static ICCQueryHandler buildQueryHandler(String tradeCode){
		return pattern.get(tradeCode);
	}
	
	/**
	 * 初始化查询执行处理器
	 */
	public static void init(){
		CCQueryFactory.registerQueryHandler(
				ProgramConfig.CALLCENTER.getConfig(ConstantKey.header_cc_qact_trade_code, ""), 
				new ActivityQueryHandler());
		CCQueryFactory.registerQueryHandler(
				ProgramConfig.CALLCENTER.getConfig(ConstantKey.header_cc_qreward_trade_code, ""),
				new RewardQueryHandler());
	}
}
