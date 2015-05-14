package com.sunrise.cgb.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 李嘉伟
 * @version 1.0 2013-05-24
 */
public class HttpConnection {

	private static Log log = LogFactory.getLog(HttpConnection.class);
	
	public final static int RUNNING_MODE_MULTI = 1;
	
	public final static int RUNNING_MODE_SINGLE = 0;
	/**
	 * 运行模式
	 */
	private static int RUNNING_MODE;
	/**
	 * 单核模式的连接超时上限(50秒)
	 */
	private static int connectOt = 50000;
	/**
	 * 单核模式的等待回复上限(50秒)
	 */
	private static int waitOt = 50000;
	/**
	 * 重发上限(30次)
	 */
	private static int resendTimes = 30;
	/**
	 * 重发等待时间(20秒)
	 */
	private static int resendWait = 20000;
	/**
	 * 多核模式的超时限制
	 */
	private final static ThreadLocal<OvertimeSetting> localOtSetting = new ThreadLocal<OvertimeSetting>();
	/**
	 * 超时限制MODEL
	 * @author 翻书侠
	 *
	 */
	private static class OvertimeSetting{
		private int connectOt;
		private int waitOt;
		public int getConnectOt() {
			return connectOt;
		}
		public void setConnectOt(int connectOt) {
			this.connectOt = connectOt;
		}
		public int getWaitOt() {
			return waitOt;
		}
		public void setWaitOt(int waitOt) {
			this.waitOt = waitOt;
		}
	}
	
	/**
	 * 初始化
	 */
	static{
		HttpConnection.RUNNING_MODE = HttpConnection.RUNNING_MODE_SINGLE;
	}
	
	/**
	 * 更改运行模式
	 * @param mode
	 */
	public static void setRunningMode(int mode){
		HttpConnection.RUNNING_MODE = mode;
	}
	
	/**
	 * 设置当前超时配置
	 * @param connectOtSecond
	 * @param waitOtSecond
	 */
	public static void setOvertime(int connectOtSecond,int waitOtSecond){
		OvertimeSetting ot = new OvertimeSetting();
		ot.setConnectOt(connectOtSecond*1000);
		ot.setWaitOt(waitOtSecond*1000);
		HttpConnection.localOtSetting.set(ot);
	}
	
	/**
	 * 设置连接超时上限
	 * @param t
	 */
	public static void setConnectOvertime(int second){
		HttpConnection.connectOt = second*1000;
	}
	
	/**
	 * 设置等待回复超时上限
	 * @param second
	 */
	public static void setResponseWaitingOvertime(int second){
		HttpConnection.waitOt = second*1000;
	}
	
    /**
     * 直建对HTTP请求
     * @param uri
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpResponse send(String uri, Map<String, String> params) throws Exception{
        String param = "";
        for(String key:params.keySet()){
            param += key+"="+params.get(key)+"&";
        }
        return send(uri,param,null);
    }
    
    /**
     * 制造请求参数
     * @param params
     * @return
     */
    public static String makeRequestParam(Map<String,String> params){
        String param = "";
        for(String key:params.keySet()){
            param += key+"="+params.get(key)+"&";
        }
        return param;
    }
    
    /**
     * HTTP请求
     * @param uri
     * @param content
     * @param decode
     * @return
     * @throws Exception
     */
    public static HttpResponse send(String uri, String content,String decode) throws Exception{
    	//打印访问日志
    	log.info("开始访问地址:["+uri+"].");
    	//创建响应对象
        HttpResponse resp = new HttpResponse();
        //初始化参数
        HttpURLConnection connection = null;
        //循环重发
        for(int resendCnt = 0;resendCnt<resendTimes;resendCnt++)
        {
	        try{
	            //创建连接对象
	        	URL url = new URL(uri);
	        	connection = (HttpURLConnection)url.openConnection();
	        	
	            //判断运行模式设置超时
	        	OvertimeSetting ot = HttpConnection.localOtSetting.get();
	            if(HttpConnection.RUNNING_MODE == HttpConnection.RUNNING_MODE_SINGLE ||
	               null == ot){
	            	connection.setReadTimeout(HttpConnection.waitOt);
	            	connection.setConnectTimeout(HttpConnection.connectOt);
	            }else{
	               	connection.setReadTimeout(ot.getWaitOt());
	               	connection.setConnectTimeout(ot.getConnectOt());
	            }
	            //设置参数
	            connection.setDoInput(true);
	            connection.setDoOutput(true);
	            connection.setRequestMethod("POST");
	            //发出请求
	            try{
		            OutputStream requestOut = connection.getOutputStream();
		            BufferedWriter out = new BufferedWriter(
		            		new OutputStreamWriter(requestOut,(decode == null)?"UTF-8":decode));
		            out.write(content);
		            out.flush();
	            }catch(Exception ex){
		        	throw ex;
		        }finally{
		        	try{
		        		//释放请求io资源
		        		connection.getOutputStream().close();
		        	}catch(Exception ex){
		        		log.error("释放请求io资源失败.",ex);
		        	}
		        }
	            //读取返回信息
                resp.read(connection.getInputStream());        	
	        	//判断是否访问成功
                if(resp.isSuccess())
                	return resp;
                
	        }catch(Exception ex){
	        	log.error("连接对象访问地址:["+uri+"]出错.",ex);
        		//重构响应对象
        		resp.init();
	        	//设置错误信息
	        	resp.setErrMessage(ex.getMessage());
	        }finally{
	        	try{
		            //销毁连接对象
	        		if(null != connection)
	        			connection.disconnect();
	        	}catch(Exception ex){
	        		log.error("释放连接对象出错.",ex);
	        	}
	        }
	        //重发休眠
	        try {
				Thread.sleep(HttpConnection.resendWait);
			} catch (InterruptedException e) {
				log.error("Http连接请求重发休眠被中断.",e);
			}
        }        
        throw new Exception("访问地址:["+uri+"]出错:"+resp.getErrMessage());
    }
}
