package com.sunrise.cgb.gateway.xmlparser;

import java.util.Map;

import org.xml.sax.SAXException;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public abstract class ProtoContentParser {

	public abstract void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException;
	
	public abstract void endElement(String uri, String localName, String qName) throws SAXException;
	
	public abstract void characters(char[] ch, int start, int length) throws SAXException;
	
	public abstract Map<String,Object> toBussinessParams();
	
	public abstract ProtoContentParser build();
}
