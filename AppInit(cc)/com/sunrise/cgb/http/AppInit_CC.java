package com.sunrise.cgb.http;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.cc.CCQueryFactory;
import com.sunrise.cgb.core.CGBTranser;
import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.gateway.xmlparser.ProtoParserFactory;
import com.sunrise.rdcp.engine.RDCPInit;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class AppInit_CC extends RDCPInit {

    private static Log log = LogFactory.getLog(RDCPInit.class);
    
	@Override
	public void init(FilterConfig config) throws ServletException {
		//初始化柜面网关通讯程序
		CGBTranser transer = CGBTranser.create();
		//设置到工具类
		TransHelper.setTranser(transer);
		//查询处理器
		CCQueryFactory.init();
		//添加交易码关联报文体解析器
		ProtoParserFactory.init();
		//初始化通讯程序工作线程
		transer.runCcSchedule();
		//调用后续初始化
        super.init(config);
	}
	
}
