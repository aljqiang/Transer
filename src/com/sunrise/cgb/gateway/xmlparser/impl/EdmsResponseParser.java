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
public class EdmsResponseParser extends ProtoContentParser{

	private Stack<String> stack = new Stack<String>();
	
	private String taskName = "";
	private String mark = "";
	private String SendBeginDate = "";
	private String FileCount = "";
	private String SendEndDate = "";
	private String RecordNum = "";
	private String FileName = "";
	
	private String RespCode = "";
	private String RespMsg = "";

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
    	if(curr.toUpperCase().equals("taskName".toUpperCase()))
    		this.setTaskName(value);
    	else if(curr.toUpperCase().equals("mark".toUpperCase()))
    		this.setMark(value);
        else if(curr.toUpperCase().equals("SendBeginDate".toUpperCase()))
        	this.setSendBeginDate(value);
        else if(curr.toUpperCase().equals("FileCount".toUpperCase()))
        	this.setFileCount(value);
        else if(curr.toUpperCase().equals("SendEndDate".toUpperCase()))
        	this.setSendEndDate(value);
        else if(curr.toUpperCase().equals("RecordNum".toUpperCase()))
        	this.setRecordNum(value);
        else if(curr.toUpperCase().equals("FileName".toUpperCase()))
        	this.setFileName(value);
        else if(curr.toUpperCase().equals("RespCode".toUpperCase()))
        	this.setRespCode(value);
        else if(curr.toUpperCase().equals("RespMsg".toUpperCase()))
        	this.setRespMsg(value);
	}

	@Override
	public Map<String, Object> toBussinessParams() {
		Map<String,Object> bp = new HashMap<String,Object>();
		bp.put("taskName", taskName);
		bp.put("mark", mark);
		bp.put("FileCount", FileCount);
		bp.put("SendBeginDate", SendBeginDate);
		bp.put("SendEndDate", SendEndDate);
		bp.put("RecordNum", RecordNum);
		bp.put("FileName", FileName);
		bp.put("RespCode", RespCode);
		bp.put("RespMsg", RespMsg);
		return bp;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getSendBeginDate() {
		return SendBeginDate;
	}

	public void setSendBeginDate(String sendBeginDate) {
		SendBeginDate = sendBeginDate;
	}

	public String getFileCount() {
		return FileCount;
	}

	public void setFileCount(String fileCount) {
		FileCount = fileCount;
	}

	public String getSendEndDate() {
		return SendEndDate;
	}

	public void setSendEndDate(String sendEndDate) {
		SendEndDate = sendEndDate;
	}

	public String getRecordNum() {
		return RecordNum;
	}

	public void setRecordNum(String recordNum) {
		RecordNum = recordNum;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getRespCode() {
		return RespCode;
	}

	public void setRespCode(String respCode) {
		RespCode = respCode;
	}

	public String getRespMsg() {
		return RespMsg;
	}

	public void setRespMsg(String respMsg) {
		RespMsg = respMsg;
	}

	@Override
	public ProtoContentParser build() {
		return new EdmsResponseParser();
	}

}
