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
public class SmsgResponseParser extends ProtoContentParser {

	private Stack<String> stack = new Stack<String>();
	
	private String TransCode = "";
	private String SENDID = "";
	private String SENDDATE = "";
	private String SENDSN = "";
	private String FILENAME = "";
	private String CHANNELCODE = "";
	private String Branch = "";
	private String SubBranch = "";
	private String OperatorId = "";
	
	private String RESPCODE = "";
	private String RESPMSG = "";

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
    	if(curr.toUpperCase().equals("TransCode".toUpperCase()))
    		this.setTransCode(value);
    	else if(curr.toUpperCase().equals("SENDID".toUpperCase()))
    		this.setSENDID(value);
        else if(curr.toUpperCase().equals("SENDDATE".toUpperCase()))
        	this.setSENDDATE(value);
        else if(curr.toUpperCase().equals("SENDSN".toUpperCase()))
        	this.setSENDSN(value);
        else if(curr.toUpperCase().equals("FILENAME".toUpperCase()))
        	this.setFILENAME(value);
        else if(curr.toUpperCase().equals("CHANNELCODE".toUpperCase()))
        	this.setCHANNELCODE(value);
        else if(curr.toUpperCase().equals("Branch".toUpperCase()))
        	this.setBranch(value);
        else if(curr.toUpperCase().equals("SubBranch".toUpperCase()))
        	this.setSubBranch(value);
        else if(curr.toUpperCase().equals("OperatorId".toUpperCase()))
        	this.setOperatorId(value);
        else if(curr.toUpperCase().equals("RESPCODE".toUpperCase()))
        	this.setRESPCODE(value);
        else if(curr.toUpperCase().equals("RESPMSG".toUpperCase()))
        	this.setRESPMSG(value);
	}

	@Override
	public Map<String, Object> toBussinessParams() {
		Map<String,Object> bp = new HashMap<String,Object>();
		bp.put("TransCode", TransCode);
		bp.put("SENDID", SENDID);
		bp.put("SENDDATE", SENDDATE);
		bp.put("SENDSN", SENDSN);
		bp.put("CHANNELCODE",CHANNELCODE);
		bp.put("FILENAME",FILENAME);
		bp.put("Branch", Branch);
		bp.put("SubBranch", SubBranch);
		bp.put("OperatorId", OperatorId);
		bp.put("RESPCODE", RESPCODE);
		bp.put("RESPMSG", RESPMSG);
		return bp;
	}

	public String getTransCode() {
		return TransCode;
	}

	public void setTransCode(String transCode) {
		TransCode = transCode;
	}

	public String getSENDID() {
		return SENDID;
	}

	public void setSENDID(String sENDID) {
		SENDID = sENDID;
	}

	public String getSENDDATE() {
		return SENDDATE;
	}

	public void setSENDDATE(String sENDDATE) {
		SENDDATE = sENDDATE;
	}

	public String getSENDSN() {
		return SENDSN;
	}

	public void setSENDSN(String sENDSN) {
		SENDSN = sENDSN;
	}

	public String getFILENAME() {
		return FILENAME;
	}

	public void setFILENAME(String fILENAME) {
		FILENAME = fILENAME;
	}

	public String getCHANNELCODE() {
		return CHANNELCODE;
	}

	public void setCHANNELCODE(String cHANNELCODE) {
		CHANNELCODE = cHANNELCODE;
	}

	public String getBranch() {
		return Branch;
	}

	public void setBranch(String branch) {
		Branch = branch;
	}

	public String getSubBranch() {
		return SubBranch;
	}

	public void setSubBranch(String subBranch) {
		SubBranch = subBranch;
	}

	public String getOperatorId() {
		return OperatorId;
	}

	public void setOperatorId(String operatorId) {
		OperatorId = operatorId;
	}

	public String getRESPCODE() {
		return RESPCODE;
	}

	public void setRESPCODE(String rESPCODE) {
		RESPCODE = rESPCODE;
	}

	public String getRESPMSG() {
		return RESPMSG;
	}

	public void setRESPMSG(String rESPMSG) {
		RESPMSG = rESPMSG;
	}

	@Override
	public ProtoContentParser build() {
		return new SmsgResponseParser();
	}

}
