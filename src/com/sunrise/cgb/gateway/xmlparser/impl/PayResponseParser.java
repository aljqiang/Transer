package com.sunrise.cgb.gateway.xmlparser.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.sunrise.cgb.gateway.xmlparser.ProtoContentParser;
import com.sunrise.foundation.utils.StringUtil;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class PayResponseParser extends ProtoContentParser {

	private Stack<String> stack = new Stack<String>();
	
	private String errorCode;
	private String errorMsg;
	private String totalRecordCount;
	private String totalParseredCount;
	private String statField1Amount;
	private String statField2Amount;
	
	public PayResponseParser(){
		this.errorCode = "";
		this.errorMsg = "";
		this.totalParseredCount = "";
		this.totalRecordCount = "";
		this.statField1Amount = "";
		this.statField2Amount = "";
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		stack.add(attributes.getValue(0));
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		stack.pop();
		
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
    	String value = new String(ch,start,length);
    	if(StringUtil.isNull(value))
    		return;
    	String curr = stack.peek();
    	if(curr.toUpperCase().equals("errorCode".toUpperCase()))
    		this.setErrorCode(value);
    	else if(curr.toUpperCase().equals("errorMsg".toUpperCase()))
    		this.setErrorMsg(value);
    	else if(curr.toUpperCase().equals("totalParseredCount".toUpperCase()))
    		this.setTotalParseredCount(value);
    	else if(curr.toUpperCase().equals("totalRecordCount".toUpperCase()))
    		this.setTotalRecordCount(value);
    	else if(curr.toUpperCase().equals("statField1Amount".toUpperCase()))
    		this.setStatField1Amount(value);
    	else if(curr.toUpperCase().equals("statField2Amount".toUpperCase()))
    		this.setStatField2Amount(value);
	}

	@Override
	public Map<String, Object> toBussinessParams() {
		Map<String,Object> bp = new HashMap<String,Object>();
		bp.put("errorCode", errorCode);
		bp.put("errorMsg", errorMsg);
		bp.put("totalRecordCount", totalRecordCount);
		bp.put("totalParseredCount", totalParseredCount);
		bp.put("statField1Amount", statField1Amount);
		bp.put("statField2Amount", statField2Amount);
		return bp;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public String getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(String totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}

	public String getTotalParseredCount() {
		return totalParseredCount;
	}

	public void setTotalParseredCount(String totalParseredCount) {
		this.totalParseredCount = totalParseredCount;
	}

	public String getStatField1Amount() {
		return statField1Amount;
	}

	public void setStatField1Amount(String statField1Amount) {
		this.statField1Amount = statField1Amount;
	}

	public String getStatField2Amount() {
		return statField2Amount;
	}

	public void setStatField2Amount(String statField2Amount) {
		this.statField2Amount = statField2Amount;
	}

	@Override
	public ProtoContentParser build() {
		return new PayResponseParser();
	}
}
