package com.sunrise.cgb.gateway.xmlparser;

import java.util.HashMap;
import java.util.Map;

import com.sunrise.cgb.common.ConstantKey;
/**
 * 报文实体
 * @author 翻书侠
 *
 */
public class ProtoContentEntity{

	private String header_version_code;
	private String header_trans;
	private String header_trade_code;
	private String header_server_code;
	private String header_seq_time;
	private String header_seq_num;
	private String header_seq_date;
	private String header_send_sid;
	private String header_encrypt;
	private String header_ct;
	
	private String header_error_code;
	private String header_error_msg;
	
	private ProtoContentParser body;

	public ProtoContentEntity() {		
		
	}

	public String getHeader_version_code() {
		return header_version_code;
	}

	public void setHeader_version_code(String header_version_code) {
		this.header_version_code = header_version_code;
	}

	public String getHeader_trans() {
		return header_trans;
	}

	public void setHeader_trans(String header_trans) {
		this.header_trans = header_trans;
	}

	public String getHeader_trade_code() {
		return header_trade_code;
	}

	public void setHeader_trade_code(String header_trade_code) {
		this.header_trade_code = header_trade_code;
	}

	public String getHeader_server_code() {
		return header_server_code;
	}

	public void setHeader_server_code(String header_server_code) {
		this.header_server_code = header_server_code;
	}

	public String getHeader_seq_time() {
		return header_seq_time;
	}

	public void setHeader_seq_time(String header_seq_time) {
		this.header_seq_time = header_seq_time;
	}

	public String getHeader_seq_num() {
		return header_seq_num;
	}

	public void setHeader_seq_num(String header_seq_num) {
		this.header_seq_num = header_seq_num;
	}

	public String getHeader_seq_date() {
		return header_seq_date;
	}

	public void setHeader_seq_date(String header_seq_date) {
		this.header_seq_date = header_seq_date;
	}

	public String getHeader_send_sid() {
		return header_send_sid;
	}

	public void setHeader_send_sid(String header_send_sid) {
		this.header_send_sid = header_send_sid;
	}

	public String getHeader_encrypt() {
		return header_encrypt;
	}

	public void setHeader_encrypt(String header_encrypt) {
		this.header_encrypt = header_encrypt;
	}

	public String getHeader_ct() {
		return header_ct;
	}

	public void setHeader_ct(String header_ct) {
		this.header_ct = header_ct;
	}

	public ProtoContentParser getBody() {
		return body;
	}

	public void setBody(ProtoContentParser body) {
		this.body = body;
	}
	
	public String getHeader_error_code() {
		return header_error_code;
	}

	public void setHeader_error_code(String header_error_code) {
		this.header_error_code = header_error_code;
	}

	public String getHeader_error_msg() {
		return header_error_msg;
	}

	public void setHeader_error_msg(String header_error_msg) {
		this.header_error_msg = header_error_msg;
	}

	/**
	 * 输出到查询参数集
	 * @return
	 */
	public Map<String,Object> toQueryParam(){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put(ConstantKey.header_ct, header_ct);
		params.put(ConstantKey.header_encrypt, header_encrypt);
		params.put(ConstantKey.header_send_sid, header_send_sid);
		params.put(ConstantKey.header_seq_date, header_seq_date);
		params.put(ConstantKey.header_seq_num, header_seq_num);
		params.put(ConstantKey.header_seq_time, header_seq_time);
		params.put(ConstantKey.header_server_code, header_server_code);
		params.put(ConstantKey.header_trade_code, header_trade_code);
		params.put(ConstantKey.header_trans, header_trans);
		params.put(ConstantKey.header_version_code, header_version_code);
		params.putAll(this.body.toBussinessParams());
		return params;
	}
}