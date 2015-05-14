package com.sunrise.cgb.gateway.xmlparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sunrise.foundation.utils.StringUtil;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class ProtoParser {

	public static Log log = LogFactory.getLog(ProtoParser.class);
	/**
	 * 解析获取报文实体对象
	 * @param text
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static ProtoContentParseResult parse(String text) throws SAXException, IOException, ParserConfigurationException{
		ByteArrayInputStream dataIn = new ByteArrayInputStream(text.getBytes());
		ProtoContentParseHandler handler = new ProtoContentParseHandler();
        SAXParserFactory.newInstance().newSAXParser().parse(dataIn,handler);
        dataIn.close();
        return handler.result();
	}
	
	/**
	 * 解析读取项目资源文件到报文实体对象
	 * @param resourse
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static ProtoContentParseResult parseResource(String resourse) throws SAXException, IOException, ParserConfigurationException{
		ProtoContentParseHandler handler = new ProtoContentParseHandler();
		SAXParserFactory.newInstance().newSAXParser().parse(
				ProtoParser.class.getClassLoader().getResourceAsStream(resourse), handler);
		return handler.result();
	}

	/**
	 * XML报文解析器
	 * @author 翻书侠
	 *
	 */
	private static class ProtoContentParseHandler extends DefaultHandler{
		private ProtoContentEntity contentEntity;
		private String faulstString;
		private Stack<String> stack = new Stack<String>();
		
		public ProtoContentParseHandler(){
			this.contentEntity = new ProtoContentEntity();
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
	    	stack.push(qName);			
	    	if(qName.toUpperCase().equals("gateway:field".toUpperCase())){
	    		if(null != contentEntity.getBody())
	    			contentEntity.getBody().startElement(uri, localName, qName, attributes);
	    		else{
	    			ProtoContentParser body = ProtoParserFactory.buildProtoParser(contentEntity.getHeader_trade_code());
	    			body.startElement(uri, localName, qName, attributes);
	    			contentEntity.setBody(body);
	    		}
	    	}	    		
		};
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
	    	stack.pop();			
	    	if(qName.toUpperCase().equals("gateway:field".toUpperCase()))
	    			contentEntity.getBody().endElement(uri,localName,qName);
		};
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
	    	String value = new String(ch,start,length);
	    	if(StringUtil.isNull(value))
	    		return;
	    	String curr = stack.peek();
	    	/** 错误信息截取 **/
	    	if(curr.toUpperCase().equals("faultstring".toUpperCase())){
				this.faulstString = value;
	    		return;
			}
	    	/** 通用报文头设置 **/
	    	if(curr.toUpperCase().equals("gateway:versionNo".toUpperCase()))
				contentEntity.setHeader_version_code(value);
	    	else if(curr.toUpperCase().equals("gateway:toEncrypt".toUpperCase()))
	    		contentEntity.setHeader_encrypt(value);
		    else if(curr.toUpperCase().equals("gateway:commCode".toUpperCase()))
		    	contentEntity.setHeader_trans(value);
			else if(curr.toUpperCase().equals("gateway:commType".toUpperCase()))
				contentEntity.setHeader_ct(value);
			else if(curr.toUpperCase().equals("gateway:receiverId".toUpperCase()))
				contentEntity.setHeader_server_code(value);
			else if(curr.toUpperCase().equals("gateway:senderId".toUpperCase()))
				contentEntity.setHeader_send_sid(value);
			else if(curr.toUpperCase().equals("gateway:senderSN".toUpperCase()))
				contentEntity.setHeader_seq_num(value);
			else if(curr.toUpperCase().equals("gateway:senderDate".toUpperCase()))
				contentEntity.setHeader_seq_date(value);
			else if(curr.toUpperCase().equals("gateway:senderTime".toUpperCase()))
				contentEntity.setHeader_seq_time(value);
			else if(curr.toUpperCase().equals("gateway:tradeCode".toUpperCase()))
				contentEntity.setHeader_trade_code(value);
			else if(curr.toUpperCase().equals("gateway:gwErrorCode".toUpperCase()))
				contentEntity.setHeader_error_code(value);
			else if(curr.toUpperCase().equals("gateway:gwErrorMessage".toUpperCase()))
				contentEntity.setHeader_error_msg(value);
	    	/** 报文体设置 **/
			else if(curr.toUpperCase().equals("gateway:field".toUpperCase()))
	    			contentEntity.getBody().characters(ch, start, length);
		};
		
		/**
		 * 返回解析对象
		 * @return
		 */
		public ProtoContentParseResult result(){
			ProtoContentParseResult result = new ProtoContentParseResult(contentEntity);
			result.setFault(faulstString);
			return result;
		}
	}
	
	/**
	 * 解析结果
	 * @author 翻书侠
	 *
	 */
	public static class ProtoContentParseResult{
		private ProtoContentEntity entity;
		private String fault;
		
		ProtoContentParseResult(ProtoContentEntity entity){
			if(null != entity && !StringUtil.isNull(entity.getHeader_version_code()))
					this.entity = entity;
			else if(null != entity)
					this.fault = entity.getHeader_error_msg();
		}
		
		void setFault(String fault){
			this.fault = fault;
		}
		
		public String getFault(){
			return this.fault;
		}
		
		public ProtoContentEntity protoContent(){
			return this.entity;
		}
		
		public boolean isSuccess(){
			if(null == entity)
				return false;
			else
				return true;
		}
		
	}
}
