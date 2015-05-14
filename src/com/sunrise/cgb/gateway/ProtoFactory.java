package com.sunrise.cgb.gateway;

import java.util.HashMap;
import java.util.Map;

import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.impl.CMMSCheckPayRequest;
import com.sunrise.cgb.gateway.impl.CMMSCheckPayResponse;
import com.sunrise.cgb.gateway.impl.CMMSEdmsRequest;
import com.sunrise.cgb.gateway.impl.CMMSEdmsResponse;
import com.sunrise.cgb.gateway.impl.CMMSPayRequest;
import com.sunrise.cgb.gateway.impl.CMMSPayResponse;
import com.sunrise.cgb.gateway.impl.CMMSmsgRequest;
import com.sunrise.cgb.gateway.impl.CMMSmsgResponse;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:IoRequestResponseFactory 创建请求回复对象工厂
 */
public abstract class ProtoFactory {

	/**
	 * 通讯对象构造者
	 * @author 翻书侠
	 *
	 */
	public static interface IProtoBuilder{
		/**
		 * 创建发送请求
		 * @return
		 */
		public IConnectorRequest buildConnectorRequest();
		/**
		 * 创建接收回应
		 * @return
		 */
		public IConnectorResponse buildConnectorResponse();
	}
	
	/**
	 * 各种类型的通讯对象构造器
	 */
	private static final Map<Integer,IProtoBuilder> protoBuilder = new HashMap<Integer,IProtoBuilder>();
	
	/**
	 * 初始化工厂对象列表,通信模板,xml解析器
	 */
	public static void init(){
		//加载电邮系统通讯SOAP报文模板
		CGBConnectorUtil.registerSoapRequestPattern(ProgramConfig.IO_TYPE_EDMS, "protocol/SoapRequestEdms.ftl");
		//加载短信系统通讯SOAP报文模板
		CGBConnectorUtil.registerSoapRequestPattern(ProgramConfig.IO_TYPE_SMSG, "protocol/SoapRequestSmsp.ftl");
		//加载卡核心系统调帐SOAP报文模板
		CGBConnectorUtil.registerSoapRequestPattern(ProgramConfig.IO_TYPE_UPLOAD_PAY, "protocol/SoapRequestPay.ftl");
		//加载卡核心检测是否收到调帐SOAP模板
		CGBConnectorUtil.registerSoapRequestPattern(ProgramConfig.IO_TYPE_CHECK_UPLOAD_PAY, "protocol/SoapRequestCheckPay.ftl");
		//注册电邮通讯的构造器
		ProtoFactory.registerProtoBuilder(ProgramConfig.IO_TYPE_EDMS,
				new IProtoBuilder(){
					@Override
					public IConnectorRequest buildConnectorRequest() {
						return new CMMSEdmsRequest();
					}
					@Override
					public IConnectorResponse buildConnectorResponse() {
						return new CMMSEdmsResponse();
					}
		});
		//注册短信通讯的构造器
		ProtoFactory.registerProtoBuilder(ProgramConfig.IO_TYPE_SMSG, 
				new IProtoBuilder(){
					@Override
					public IConnectorRequest buildConnectorRequest() {
						return new CMMSmsgRequest();
					}
					@Override
					public IConnectorResponse buildConnectorResponse() {
						return new CMMSmsgResponse();
					}
		});
		//注册核心通讯的构造器
		ProtoFactory.registerProtoBuilder(ProgramConfig.IO_TYPE_UPLOAD_PAY,
				new IProtoBuilder(){
					@Override
					public IConnectorRequest buildConnectorRequest() {
						return new CMMSPayRequest();
					}
					@Override
					public IConnectorResponse buildConnectorResponse() {
						return new CMMSPayResponse();
					}
		});
		ProtoFactory.registerProtoBuilder(ProgramConfig.IO_TYPE_CHECK_UPLOAD_PAY,
				new IProtoBuilder(){
					@Override
					public IConnectorRequest buildConnectorRequest() {
						return new CMMSCheckPayRequest();
					}
					@Override
					public IConnectorResponse buildConnectorResponse() {
						return new CMMSCheckPayResponse();
					}			
		});
	}	
	
	private ProtoFactory(){
		
	}
	
	/**
	 * 注册新的通讯对象构造者
	 * @param builder
	 */
	public static void registerProtoBuilder(Integer taskType,IProtoBuilder builder){
		ProtoFactory.protoBuilder.put(taskType, builder);
	}
	
	/**
	 * 创建回复对象
	 * @param type
	 * @param info
	 * @return
	 */
	public static IConnectorResponse buildResponse(Integer ioType){
		IProtoBuilder builder = protoBuilder.get(ioType);
		if(null != builder)
			return builder.buildConnectorResponse();
		else
			return null;
	}
	/**
	 * 创建请求对象
	 * @param type
	 * @param info
	 * @return
	 */
	public static IConnectorRequest buildRequest(Integer ioType){
		IProtoBuilder builder = protoBuilder.get(ioType);
		if(null != builder)
			return builder.buildConnectorRequest();
		else
			return null;
	}
}
