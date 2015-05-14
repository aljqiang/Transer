package com.sunrise.cgb.gateway.strategy;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.agree.eai.file.natp.NatpFileClient;

import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.core.ITransStrategy;
import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.gateway.IConnectorRequest;
import com.sunrise.cgb.gateway.IConnectorResponse;
import com.sunrise.cgb.gateway.ITransConnector;
import com.sunrise.cgb.gateway.ProtoFactory;
import com.sunrise.cgb.gateway.SendFileResult;
import com.sunrise.cgb.gateway.SendProtoResult;
import com.sunrise.cgb.gateway.SendTaskWorker;
import com.sunrise.cgb.gateway.CGBConnectorUtil.CGBSeqNum;
import com.sunrise.cgb.gateway.SendTaskWorker.OverTimeChecker;
import com.sunrise.cgb.gateway.SendTaskWorker.Resender;
import com.sunrise.cgb.gateway.connector.GateWayConnector;
import com.sunrise.cgb.gateway.impl.NatpTransRequest;
import com.sunrise.cgb.http.HttpConnection;
import com.sunrise.cgb.queue.SendTaskEntity;
import com.sunrise.foundation.utils.ResourceConfig;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class NatpTransStrategy implements ITransStrategy {

	private Log log = LogFactory.getLog(NatpTransStrategy.class);
	
	private String fs_ip;
	private int fs_port;
	private int fs_timeout;
	private int fs_con_timeout;
	
	private String hostName;
	private String recvUri;
	private ResourceConfig mainConfig;
	/**
	 * 初始化通讯参数
	 */
	public NatpTransStrategy(ResourceConfig mainConfig){
		this.fs_ip = ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_ip, "");
		this.fs_port = ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_port, 65506);
		this.fs_timeout = ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_timeout,1800)*1000;
		this.fs_con_timeout = ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_con_timeout, 600)*1000;
		this.hostName = ProgramConfig.GATEWAY.getConfig(ConstantKey.header_send_sid, "CMMS");
		this.recvUri = ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_pay_recvuri, "");
		this.mainConfig = mainConfig;
	}
	
	@Override
	public SendFileResult sendDatafile(SendTaskEntity entity) {
		//初始化发送结果
		SendFileResult sendResult = new SendFileResult();
		sendResult.setType("NATP");
		//记录日志
		log.info("开始使用赞同文服上传主机文件.拆分包ID:"+entity.getPacketId()+",文件发送路径:"+entity.getSrcDir());
		//创建连接文服对象
		NatpFileClient client = new NatpFileClient(fs_ip,fs_port);
		try {
			//超时设置
		    client.setConnectTimeout(fs_con_timeout*1000);
		    client.setSoTimeout(fs_timeout*1000);
		    //初始化传输参数, 创建到NATP文件服务器的连接
		    String remoteFileName = genRemoteFileName(entity);
		    client.init(hostName, 
		    			 entity.getSrcDir(), 
		    			 remoteFileName, 
		    			 NatpFileClient.ACTION_UPLOAD_FILE); 
		    //开始发送
		    client.run();
		    //日志记录
		    log.info("上传文件到赞同文服完成.拆分包id:"+entity.getPacketId()+",文件在文服路径:"+remoteFileName);
		    //发送完成
		    sendResult.setSuccess(true);
		}catch(Exception ex){
			sendResult.setSuccess(false);
			sendResult.setCode("none");
			sendResult.setMsg(ex.getMessage());
			ex.printStackTrace();
		} finally {
			//关闭资源
			client.destroy(); 
		}
		return sendResult;
	}
	
	/**
	 * 生成文服上传路径文件名
	 * @param sourceName
	 * @return
	 */
	private String genRemoteFileName(SendTaskEntity entity){
		//获取文件名
		String fileName = entity.getSrcDir().substring(entity.getSrcDir().lastIndexOf("/")+1);
		entity.setfileName(fileName);
		//生成文服路径
//		String remoteFileName = ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_coremachine_root, "") + 
//				  mainConfig.getConfig(ConstantKey.pay_business_system, "") + "/" +
//				  mainConfig.getConfig(ConstantKey.pay_channl_id, "") + "/" +
//				  mainConfig.getConfig(ConstantKey.pay_channl_detail, "") + "/" +
//				  ProgramConfig.GATEWAY.getConfig(ConstantKey.fs_tradeCode_upload, "") + "/" + fileName;
//“文件传输系统>虚拟柜员号>真实文件名”
		String remoteFileName = mainConfig.getConfig(ConstantKey.pay_fileCategory, "CMMS") + ">"+
											  mainConfig.getConfig(ConstantKey.pay_utID, "9999999999") + ">" + fileName;
	    //日志记录
	    log.info("准备上传文件到赞同文服完成.拆分包id:"+entity.getPacketId()+",文件在文服路径:"+remoteFileName);
	    //返回生成文服路径
	    return remoteFileName;
	}

	@Override
	public void release() {
		TransHelper.transer().decreasePayCnt();
		TransHelper.transer().showPayWorkerCnt();		
	}

	@Override
	public SendProtoResult sendProto(
			SendTaskEntity entity,SendTaskWorker worker, Resender resender, OverTimeChecker checker) {
		//创建发送报文结果
		SendProtoResult result = new SendProtoResult();
		//创建通讯处理器
		ITransConnector natpConnector = new GateWayConnector();
		//发送上传报文请求
		IConnectorRequest alarmRequest = null;
		try{
			//创建请求实例
			alarmRequest = ProtoFactory.buildRequest(ProgramConfig.IO_TYPE_UPLOAD_PAY);
			//填充报文发送对象
			alarmRequest.read(entity);
		}catch(Exception ex){
			String message = "创建提醒文服上传文件请求出错.";
			log.error(message,ex);
			throw new RuntimeException(message);
		}
		//进行通讯
		IConnectorResponse alarmResponse = null;
		try{
			alarmResponse = natpConnector.sendRequest(alarmRequest);
		}catch(Exception ex){
			//关闭超时检测
			checker.interrupt();
			//日志记录
			String errMsg = "发送通知文服上传文件到主机失败,与柜面网关通讯错误,流水号:"+entity.getSeqNum()+" cause:"+ex.getMessage();
			log.error(errMsg,ex);
			//判断是否需要重发
			if(resender.isOvertimes()){
				//异常处理
				worker.exceptionProcess(ProgramConfig.CONNECT_ERR_EXCEPTION,errMsg);
				//中断发送循环
				result.setBreak();
			}
			//中断后续操作
			return result;
		}finally{
			//释放资源
			natpConnector.release();
		}
		//提醒文服传输失败
		if(alarmResponse.getResult() == IConnectorResponse.RESULT_FAIL){
			//中断超时检测
			checker.interrupt();
			//是否超过重发上限
			if(resender.isOvertimes()){
				//异常处理
				worker.exceptionProcess(ProgramConfig.SEND_ERR_EXCEPTION, "柜面网关:"+alarmResponse.getErrorCode());
				//结束发送循环
				result.setBreak();
			}
			return result;
		}
		//创建查询文服传输情况参数
		IConnectorRequest checkRequest = null;
		IConnectorResponse checkResponse = null;
		CGBSeqNum orSenderSN = ((NatpTransRequest)alarmRequest).getSeqnum();
		try{
			//提醒文服传输文件成功,定时发送查询是否成功发送到主机网关
			while(true){
				try{
					log.info("再次发送查询文服上传到主机情况.");
					checkRequest = ProtoFactory.buildRequest(ProgramConfig.IO_TYPE_CHECK_UPLOAD_PAY);
					((NatpTransRequest)checkRequest).setSeqnum(orSenderSN);
					checkRequest.read(entity);
					checkResponse = natpConnector.sendRequest(checkRequest);
					if(checkResponse.getResult() == IConnectorResponse.RESULT_SUCCESS){
						worker.handleProcess(checkResponse);
						result.setSuccess();
						result.setBreak();
						return result;
					}else if(checkResponse.getResult() == IConnectorResponse.RESULT_FAIL){
						//判断是否需要重发
						if(resender.isOvertimes()){
							//异常处理
							worker.exceptionProcess(ProgramConfig.HOST_TRANS_ERR, checkResponse.getErrorCode());
							//中断发送循环
							result.setBreak();
						}
						//中断后续操作
						return result;
					}
					//每5分钟发送一次查询报文
					try {
						//Thread.sleep(300000);
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						result.setBreak();
						return result;
					}
				}catch(Exception ex){
					log.error("检测文服传输情况通讯失败.",ex);
					//建立连接失败,每10秒再发一次连接请求
					try{
						Thread.sleep(10000);
					}catch(InterruptedException e){
						result.setBreak();
						return result;						
					}
				}finally{
					natpConnector.release();
				}
			}
		}finally{
			//关闭超时检测
			checker.interrupt();	
		}
	}

	@Override
	public void complete(SendTaskEntity entity) throws Exception {
		//发送参数
		Map<String,String> params = new HashMap<String,String>();
		params.put("result", entity.getResult()+"");
		params.put("packageId", entity.getPacketId()+"");
		//访问反馈地址
		HttpConnection.setOvertime(50,600);
		HttpConnection.send(recvUri, params);
		//更新任务发送执行结果状态
		TransHelper.transer().getLogger().updateTaskResped(entity);
		//记录反馈任务信息
		String msg = "\r\n==========发送完成信息==============\r\n";
		msg += "传输任务类型:调帐\r\n";
		msg += "执行结果:"+((entity.getResult()==IConnectorResponse.RESULT_SUCCESS)?"成功":"失败")+"\r\n";
		msg += "错误描述:"+entity.getErrCode()+","+entity.getErrCause()+"\r\n";
		msg += "拆分包ID:"+entity.getPacketId()+"\r\n";
		msg += "源路径:"+entity.getSrcDir()+"\r\n";
		msg += "发送调帐文件路径:" + entity.getfileName() + "\r\n";		
		msg += "==============================\r\n";
		log.info(msg);		
	}

}
