package com.sunrise.cgb.common;

import com.sunrise.foundation.utils.ResourceConfig;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:程序配置环境
 */
public class ProgramConfig {
	/**
	 * 通信服务器配置
	 */
	public static final ResourceConfig GATEWAY = ResourceConfig.getConfig("gateway.properties");
	/**
	 * 短信平台系统通信配置
	 */
	public static final ResourceConfig SMSG = ResourceConfig.getConfig("gateway.smsg.properties");
	/**
	 * 互联网邮件系统通信配置
	 */
	public static final ResourceConfig EDMS = ResourceConfig.getConfig("gateway.edms.properties");
	/**
	 * 传呼中心通信配置
	 */
	public static final ResourceConfig CALLCENTER = ResourceConfig.getConfig("gateway.callcenter.properties");
	/**
	 * 核心系统通讯配置
	 */
	public static final ResourceConfig PAY = ResourceConfig.getConfig("gateway.pay.ufs.properties");

    /**
     * 积分系统通讯配置
     */
    public static final ResourceConfig POINT=ResourceConfig.getConfig("gateway.point.ufs.properties");

	/**
	 * 渠道下发系统任务类型
	 */	
	public static final Integer TASK_TYPE_SMSG = 0;
	
	public static final Integer TASK_TYPE_EDMS = 2;
	
	public static final Integer TASK_TYPE_CC = 3;
	
	public static final Integer TASK_TYPE_PAY = 5;

    public static final Integer TASK_TYPE_POINT = 8;    // 新增积分系统任务类型
	
	public static final Integer[] TASK_TYPE = {TASK_TYPE_SMSG,TASK_TYPE_EDMS,TASK_TYPE_CC,TASK_TYPE_PAY,TASK_TYPE_POINT};
	
	/**
	 * 通讯应答类型
	 */
	public static final Integer IO_TYPE_SMSG = 0;
	
	public static final Integer IO_TYPE_EDMS = 2;
	
	public static final Integer IO_TYPE_CC = 3;
	
	public static final Integer IO_TYPE_UPLOAD_PAY = 5;
	
	public static final Integer IO_TYPE_CHECK_UPLOAD_PAY = 6;

    public static final Integer IO_TYPE_UPLOAD_POINT= 8;
	
	/**
	 * 发送异常代码
	 */
	public static final String SEND_ERR_FILE = "数据文件传输失败";
	
	public static final String SEND_ERR_OVERTIME = "发送超时";
	
	public static final String SEND_ERR_EXCEPTION = "外围系统执行错误";
	
	public static final String SEND_ERR_OVERRESEND = "重发过多";
	
	public static final String CONNECT_ERR_EXCEPTION = "柜面网关通讯错误";
	
	public static final String HOST_TRANS_ERR = "主机通讯出错";
	/**
	 * 任务状态
	 */
	public static final Integer TASK_STATE_SENDING = 0;
	
	public static final Integer TASK_STATE_SENDED = 1;
	
	public static final Integer TASK_STATE_RESULT = 2;
	/**
	 * 测试用数据文件路径(基于Webroot)
	 */
	public static final String EDMS_TEST_FILE = "/test/10460.zip";
	
	public static final String SMSG_TEST_FILE = "/test/eg.txt";
	
	public static final String PAY_TEST_FILE = "/test/GDBT01TP_20130819010101.txt";
}
