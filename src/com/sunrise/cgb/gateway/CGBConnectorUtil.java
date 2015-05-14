package com.sunrise.cgb.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.framework.freemarker.FreeMarkerUtil;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public abstract class CGBConnectorUtil {

	private static Log log = LogFactory.getLog(CGBConnectorUtil.class);
	/**
	 * 柜面网关8位序列号
	 */
	private static final DecimalFormat eightNumFmt = new DecimalFormat("00000000");
	/**
	 * 年月日时分秒格式化
	 */
	private static final SimpleDateFormat detailDateFmt = new SimpleDateFormat("yyyyMMddhhmmss");
	/**
	 * 柜面网关序列号
	 */
	private static Integer gateWaySeqNum = 0;
	/**
	 * 柜面网关序列号最大值
	 */
	private static final int MAX_GATEWAY_SEQ_LEN = 99999999;
	/**
	 * 通信模板
	 */
	private static final Map<String,String> protoPattern = new HashMap<String,String>();
	/**
	 * 各任务类型的通信模块资源名
	 */
	private static final Map<Integer,String> requestProtoName = new HashMap<Integer,String>();
	/**
	 * 初始化
	 */
	static {
		CGBConnectorUtil.loadProtoPattern("protocol/ProtoHeaderSoap.ftl");
		CGBConnectorUtil.loadProtoPattern("protocol/SoapRespErrorQuery.ftl");
	}
	
	private CGBConnectorUtil(){
		
	}
	/**
	 * 生成22位柜面网关通用报文头的流水号
	 * @return
	 */
	public static CGBSeqNum genGatewaySeqNum(){
		synchronized(gateWaySeqNum){
			if(gateWaySeqNum>CGBConnectorUtil.MAX_GATEWAY_SEQ_LEN)
				gateWaySeqNum = 0;
			
			gateWaySeqNum++;
			
			return new CGBSeqNum(detailDateFmt.format(new Date()),eightNumFmt.format(gateWaySeqNum));
		}
	}
	
	/**
	 * 流水号对象
	 * @author 翻书侠
	 *
	 */
	public static class CGBSeqNum{
		private String seqNum;
		private String seqDate;
		private String seqTime;
		/**
		 * 序列对象构造函数
		 */
		public CGBSeqNum(String datetime,String seqNum){
			this.seqDate = datetime.substring(0,8);
			this.seqTime = datetime.substring(8);
			this.seqNum = datetime+seqNum;
		}
		public String getSeqNum() {
			return seqNum;
		}
		public String getSeqDate() {
			return seqDate;
		}
		public String getSeqTime() {
			return seqTime;
		}
	}
	
	/**
	 * 设置通信模板
	 * @param resourceName
	 */
	public static void loadProtoPattern(String resourceName){
		log.info("加载通信请求模板:"+resourceName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				CGBConnectorUtil.class.getClassLoader().getResourceAsStream(resourceName)));
		StringBuffer buffer = new StringBuffer();
		String content;
		try {
			content = reader.readLine();
			while(null != content)
			{
				buffer.append(content.trim() + "\r\n");
				content = reader.readLine();
			}
			protoPattern.put(resourceName, buffer.toString());
		} catch (IOException e) {
			log.error("加载协议通信模板:"+resourceName+"出错.",e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				log.error("关闭协议通信模板:"+resourceName+"加载资源出错.",e);
			}
		}
	}
	
	/**
	 * 加载对应任务类型通信模板
	 * @param ioType
	 * @param sourceName
	 */
	public static void registerSoapRequestPattern(Integer ioType,String sourceName){
		requestProtoName.put(ioType, sourceName);
		loadProtoPattern(sourceName);
	}
	
	/**
	 * 生成对应任务类型通信报文
	 * @param taskType
	 * @param params
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public static String genSoapRequestProto(Integer ioType,Map<String,Object> params) throws Exception{
		String resource = requestProtoName.get(ioType);
		return genSoapProto(params, resource);
	}
	
	/**
	 * 输出对应任务类型通信报文
	 * @param taskType
	 * @param params
	 * @param out
	 * @throws Exception
	 */
	public static void writeSoapRequestProto(Integer ioType,Map<String,Object> params,OutputStream out) throws Exception{
		String resource = requestProtoName.get(ioType);
		writeSoapProto(params,resource,out);
	}
	
	/**
	 * 根据协议文档生成报文
	 * @param params
	 * @param patternName
	 * @return
	 */
	public static String genSoapProto(Map<String,Object> params,String patternName) throws Exception{
		try {
			return FreeMarkerUtil.process(protoPattern.get(patternName),params);
		} catch (Exception e) {
			log.error("生成["+patternName+"]协议报文出错.", e);
			throw new Exception("生成协议报文出错.");
		}
	}
	
	/**
	 * 直接输出数据到输出流
	 * @param params
	 * @param patternName
	 * @param out
	 */
	public static void writeSoapProto(Map<String,Object> params,String patternName,OutputStream out) throws Exception{
		try {
			out.write(genSoapProto(params, patternName).getBytes());
		} catch (Exception e) {
			log.debug("输出报文到输出流时出错.",e);
			throw new Exception("输出报文到输出流时出错.");
		}
	}
	
	/**
	 * 输出报文头到输出流
	 * @param params
	 * @param out
	 */
	public static void writeSOAPHeader(Map<String,Object> params,OutputStream out) throws Exception{
		try {
			writeSoapProto(params, "protocol/ProtoHeaderSoap.ftl", out);
		} catch (IOException e) {
			log.debug("输出报文头到输出流时出错.",e);
			throw new Exception("输出报文头到输出流时出错.");
		}
	}
	
	/**
	 * 输出查询错误报文体到输出流
	 * @param params
	 * @param out
	 */
	public static void writeSOAPQueryErr(Map<String,Object> params,OutputStream out) throws Exception{
		try{
			writeSoapProto(params, "protocol/SoapRespErrorQuery.ftl", out);
			out.flush();
		}catch(IOException e){
			log.debug("输出查询报文错误码到输出流出错.",e);
			throw new Exception("输出查询报文错误码到输出流出错.");
		}
	}
		
	/**
	 * 自动向右补符号
	 * @param text
	 * @param length
	 * @return
	 */
	public static String fillRightString(String text,int length,char symbol){
		char[] content = new char[length];
		char[] textChar = text.toCharArray();
		for(int pos = 0;pos<length;pos++)
		{
			if(pos<textChar.length)
				content[pos] = textChar[pos];
			else
				content[pos] = symbol;
		}
		return new String(content);
	}
	/**
	 * 自动填充左符号
	 * @param text
	 * @param length
	 * @param symbol
	 * @return
	 */
	public static String fillLeftString(String text,int length,char symbol){
		char[] content = new char[length];
		char[] textChar = text.toCharArray();
		if(content.length >= textChar.length){
			for(int mark = content.length - textChar.length,pos = 0;pos<length;pos++)
			{
				if(pos<mark)
					content[pos] = symbol;
				else
					content[pos] = textChar[pos-mark];
			}
		}else{
			for(int pos = length-1;pos>0;pos--)
				content[pos] = textChar[pos];
		}
		return new String(content);	
	}
	/**
	 * 测试入口
	 * @param args
	 */
	public static void main(String[] args){
		CGBSeqNum seqNum = CGBConnectorUtil.genGatewaySeqNum();
		System.out.println(seqNum.getSeqDate());
		System.out.println(seqNum.getSeqTime());
		System.out.println(seqNum.getSeqNum());
	}
}
