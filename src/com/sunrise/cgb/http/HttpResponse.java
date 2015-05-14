package com.sunrise.cgb.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSON;
import com.sunrise.framework.struts2.JsonObject;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class HttpResponse {

	private String respContent;
	private String bodyMessage;
	private boolean result;
	private String errMessage;
	
	protected HttpResponse(){
		init();
	}
	
	/**
	 * 初始化响应对象
	 */
	protected void init(){
		this.respContent = "";
		this.bodyMessage = "";
		this.errMessage = "";
		this.result = false;	
	}
	
	/**
	 * 读取HTTP返回数据
	 * @param is
	 */
	public void read(InputStream is) throws Exception{
		try {
			//建立输入流
			InputStreamReader isr = new InputStreamReader(is,"utf-8");
			//读取输入流
	        BufferedReader br = new BufferedReader(isr);
	        String temp = br.readLine();
	        while(null != temp)
	        {
	        	this.respContent += temp;
	        	temp = br.readLine();
	        }
	        //赋值
	        JsonObject respJson = JSON.parseObject(this.respContent, JsonObject.class);
	        this.result = true;
	        try{
	        	this.bodyMessage = (String) respJson.getBody();
	        }catch(Exception e){
	        	
	        }
		} catch (Exception e) {
			this.result = false;
			throw e;
		} finally {
			try{
		        is.close();				
			}catch(Exception ex){
				throw ex;
			}
		}
	}
	/**
	 * 获取返回内容
	 * @return
	 */
	public String getBodyMessage(){
		return bodyMessage;
	}
	/**
	 * 获取返回结果内容
	 * @return
	 */
	public String getRespContent(){
		return respContent;
	}
	/**
	 * 根据返回的HEADER确定是否成功
	 * @return
	 */
	public boolean isSuccess(){
		return result;
	}

	public String getErrMessage() {
		return errMessage.equals("")?"重发失败":errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}
}
