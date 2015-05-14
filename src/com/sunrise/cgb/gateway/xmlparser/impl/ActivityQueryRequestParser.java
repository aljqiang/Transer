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
public class ActivityQueryRequestParser extends ProtoContentParser {

	private Stack<String> stack = new Stack<String>();
	
	private String CM_Party_ID = "";
	private String CM_BANK_ID = "";
	private String OFFER_END_DATE_START = "";
	private String OFFER_END_DATE_END = "";
	private String NextPageId = "1";
	
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
    	if(curr.toUpperCase().equals("CUST_ID".toUpperCase()))
    		this.setCM_Party_ID(value);
    	else if(curr.toUpperCase().equals("BANK_ID".toUpperCase()))
    		this.setCM_BANK_ID(value);
    	else if(curr.toUpperCase().equals("OFFER_END_DATE_START".toUpperCase()))
    		this.setOFFER_END_DATE_START(value);
    	else if(curr.toUpperCase().equals("OFFER_END_DATE_END".toUpperCase()))
    		this.setOFFER_END_DATE_END(value);
    	else if(curr.toUpperCase().equals("NextPageId".toUpperCase()))
    		this.setNextPageId(value);
	}

	@Override
	public Map<String, Object> toBussinessParams() {
		Map<String,Object> bp = new HashMap<String,Object>();
		bp.put("CUST_ID", CM_Party_ID);
		bp.put("BANK_ID", CM_BANK_ID);
		bp.put("OFFER_END_DATE_START", OFFER_END_DATE_START);
		bp.put("OFFER_END_DATE_END", OFFER_END_DATE_END);
		bp.put("NextPageId", NextPageId);
		return bp;
	}

	public String getCM_Party_ID() {
		return CM_Party_ID;
	}

	public void setCM_Party_ID(String cM_Party_ID) {
		CM_Party_ID = cM_Party_ID;
	}

	public String getCM_BANK_ID() {
		return CM_BANK_ID;
	}

	public void setCM_BANK_ID(String cM_BANK_ID) {
		CM_BANK_ID = cM_BANK_ID;
	}

	public String getOFFER_END_DATE_START() {
		return OFFER_END_DATE_START;
	}

	public void setOFFER_END_DATE_START(String oFFER_END_DATE_START) {
		OFFER_END_DATE_START = oFFER_END_DATE_START;
	}

	public String getOFFER_END_DATE_END() {
		return OFFER_END_DATE_END;
	}

	public void setOFFER_END_DATE_END(String oFFER_END_DATE_END) {
		OFFER_END_DATE_END = oFFER_END_DATE_END;
	}

	public String getNextPageId() {
		return NextPageId;
	}

	public void setNextPageId(String nextPageId) {
		NextPageId = nextPageId;
	}

	@Override
	public ProtoContentParser build() {
		return new ActivityQueryRequestParser();
	}

}
