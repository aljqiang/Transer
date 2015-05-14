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
public class CheckPayResponseParser extends ProtoContentParser {

	private Stack<String> stack = new Stack<String>();
	
	private String errorCode;
	private String errorMsg;
	private String retCount;
	private String orSenderId;
	private String orSenderSN;
	private String orSenderDate;
	private String ooSenderId;
	private String ooSenderSN;
	private String ooSenderDate;
	private String fileTransStatus;
	private String statusDesc;
	private String transMode;
	private String reserved1;
	
	public CheckPayResponseParser(){
		this.errorCode = "";
		this.errorMsg = "";
		this.retCount = "";
		this.orSenderId = "";
		this.orSenderSN = "";
		this.orSenderDate = "";
		this.ooSenderId = "";
		this.ooSenderSN = "";
		this.ooSenderDate = "";
		this.fileTransStatus = "";
		this.statusDesc = "";
		this.transMode = "";
		this.reserved1 = "";
	}

	@Override
	public Map<String, Object> toBussinessParams() {
		Map<String,Object> bp = new HashMap<String,Object>();
		bp.put("errorCode", errorCode);
		bp.put("errorMsg", errorMsg);
		bp.put("retCount", retCount);
		bp.put("orSenderId", orSenderId);
		bp.put("orSenderSN", orSenderSN);
		bp.put("orSenderDate", orSenderDate);
		bp.put("ooSenderId", ooSenderId);
		bp.put("ooSenderSN", ooSenderSN);
		bp.put("ooSenderDate", ooSenderDate);
		bp.put("fileTransStatus", fileTransStatus);
		bp.put("statusDesc", statusDesc);
		bp.put("transMode", transMode);
		bp.put("reserved1", reserved1);
		return bp;
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
    	else if(curr.toUpperCase().equals("retCount".toUpperCase()))
    		this.setRetCount(value);
    	else if(curr.toUpperCase().equals("orSenderId".toUpperCase()))
    		this.setOrSenderId(value);
    	else if(curr.toUpperCase().equals("orSenderSN".toUpperCase()))
    		this.setOrSenderSN(value);
    	else if(curr.toUpperCase().equals("orSenderDate".toUpperCase()))
    		this.setOrSenderDate(value);
    	else if(curr.toUpperCase().equals("ooSenderId".toUpperCase()))
    		this.setOoSenderId(value);
    	else if(curr.toUpperCase().equals("ooSenderSN".toUpperCase()))
    		this.setOoSenderSN(value);
    	else if(curr.toUpperCase().equals("ooSenderDate".toUpperCase()))
    		this.setOoSenderDate(value);
    	else if(curr.toUpperCase().equals("fileTransStatus".toUpperCase()))
    		this.setFileTransStatus(value);
    	else if(curr.toUpperCase().equals("statusDesc".toUpperCase()))
    		this.setStatusDesc(value);
    	else if(curr.toUpperCase().equals("transMode".toUpperCase()))
    		this.setTransMode(value);
    	else if(curr.toUpperCase().equals("reserved1".toUpperCase()))
    		this.setReserved1(value);
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

	public String getRetCount() {
		return retCount;
	}

	public void setRetCount(String retCount) {
		this.retCount = retCount;
	}

	public String getOrSenderId() {
		return orSenderId;
	}

	public void setOrSenderId(String orSenderId) {
		this.orSenderId = orSenderId;
	}

	public String getOrSenderSN() {
		return orSenderSN;
	}

	public void setOrSenderSN(String orSenderSN) {
		this.orSenderSN = orSenderSN;
	}

	public String getOrSenderDate() {
		return orSenderDate;
	}

	public void setOrSenderDate(String orSenderDate) {
		this.orSenderDate = orSenderDate;
	}

	public String getOoSenderId() {
		return ooSenderId;
	}

	public void setOoSenderId(String ooSenderId) {
		this.ooSenderId = ooSenderId;
	}

	public String getOoSenderSN() {
		return ooSenderSN;
	}

	public void setOoSenderSN(String ooSenderSN) {
		this.ooSenderSN = ooSenderSN;
	}

	public String getOoSenderDate() {
		return ooSenderDate;
	}

	public void setOoSenderDate(String ooSenderDate) {
		this.ooSenderDate = ooSenderDate;
	}

	public String getFileTransStatus() {
		return fileTransStatus;
	}

	public void setFileTransStatus(String fileTransStatus) {
		this.fileTransStatus = fileTransStatus;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getTransMode() {
		return transMode;
	}

	public void setTransMode(String transMode) {
		this.transMode = transMode;
	}

	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	@Override
	public ProtoContentParser build() {
		return new CheckPayResponseParser();
	}
}