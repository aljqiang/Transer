package com.sunrise.cgb.http;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.gateway.xmlparser.ProtoParser.ProtoContentParseResult;
import com.sunrise.cgb.gateway.xmlparser.ProtoParser;
import com.sunrise.cgb.queue.QueryTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:接收柜面网关连接接口
 */
public class CGBGateWayServlet extends HttpServlet {

    private static Log log = LogFactory.getLog(CGBGateWayServlet.class);
	
	@Override
	public void doGet(HttpServletRequest req,HttpServletResponse rep) throws ServletException,IOException{
		this.doPost(req, rep);
	}
	
	@Override
	public void doPost(HttpServletRequest req,HttpServletResponse rep) throws ServletException,IOException{
		//传呼中心请求接收处理
		String contentStr = getReqContentText(req);
		//解析字符串到实体类
		ProtoContentParseResult result = null;
		try {
			result = ProtoParser.parse(contentStr);
		} catch (SAXException e) {
			log.error("解析柜面网关请求出错:",e);
		} catch (ParserConfigurationException e) {
			log.error("解析柜面网关请求时,配置出错:",e);
		}
		if(result == null || !result.isSuccess()){
			return;		
		}
		//初始化查询任务值对象,设置输出流以及查询类型,查询参数
		QueryTaskEntity qTask = new QueryTaskEntity();
		qTask.setOutput(rep.getOutputStream());
		qTask.setType(result.protoContent().getHeader_trade_code());
		qTask.addQueryParams(result.protoContent().toQueryParam());
		//添加到查询任务队列
		TransHelper.transer().taskQueue().queryTaskQueue().add2WaitingQueue(qTask);
		//锁定任务,阻塞返回线程
		synchronized(qTask){
			try {
				qTask.wait(60*1000*2);
			} catch (InterruptedException e) {
				log.info("查询任务线程阻塞被中断.",e);
			}
		}
	}
	/**
	 * 读取请求报文
	 * @param req
	 * @return
	 * @throws IOException
	 */
	private String getReqContentText(HttpServletRequest req) throws IOException{
		byte[] bytes = new byte[req.getContentLength()];
		req.getInputStream().read(bytes);
		return new String(bytes);
	}
}
