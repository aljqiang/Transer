package com.sunrise.cgb.gateway.connector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.IConnectorRequest;
import com.sunrise.cgb.gateway.IConnectorResponse;
import com.sunrise.cgb.gateway.ISoapConnectorRequest;
import com.sunrise.cgb.gateway.ISoapConnectorResponse;
import com.sunrise.cgb.gateway.ProtoFactory;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:SOAP传输通讯
 */
public class GateWayConnector extends TransConnectorImpl{

    private static Log log = LogFactory.getLog(GateWayConnector.class);
	
	private String codec;
	
	public GateWayConnector(){
		super(ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_ip, "10.2.104.167"),
				 ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_port, 8113),
				 ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_con_timeout, 10000));
		this.codec = ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_codec,"GBK");		
	}

	/**
	 * 发送柜面网关请求
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@Override
	public IConnectorResponse sendRequest(IConnectorRequest request) throws Exception{
		//创建SOCKET
		initConnector();		
		//创建IO对象
		BufferedReader reader = new BufferedReader(new InputStreamReader(gateway.getInputStream(),codec));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(gateway.getOutputStream(),codec));
		//发送请求
		String requestContent = ((ISoapConnectorRequest) request).toXml();
		
		log.debug(requestContent);
		writer.write("Content-Type: text/xml; charset="+codec);
		writer.write("\r\n");
		writer.write("Content-Length: "+requestContent.getBytes().length);
		writer.write("\r\n\r\n");
		writer.write(requestContent);
		writer.flush();
				
		//读取输入流
		log.debug("开始等待报文回复.");
		boolean startRead = false;
		StringBuilder buffer = new StringBuilder();
		for(String temp = reader.readLine();true;temp = reader.readLine())
		{
			log.debug(temp);
			if(temp == null || temp.trim().contains("</soapenv:Envelope>")){
				//文本协议结束
				buffer.append(temp.trim().substring(0,temp.lastIndexOf(">")+1));
				break;
			}else if(temp.trim().contains("<?xml")){
				startRead = true;
				buffer.append(temp.trim().substring(temp.indexOf("<")));
				buffer.append("\r\n");
			}else if(startRead){
				buffer.append(temp);
				buffer.append("\r\n");
			}
		}
		
		log.debug("获取返回报文:\r\n"+buffer.toString());
		
		//创建回复对象
		IConnectorResponse rep = ProtoFactory.buildResponse(request.getType());
		((ISoapConnectorResponse)rep).read(buffer.toString());
		return rep;
		
	}
}
