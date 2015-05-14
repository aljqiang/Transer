package com.sunrise.cgb.gateway.strategy;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import com.sunrise.cgb.gateway.SendTaskWorker.OverTimeChecker;
import com.sunrise.cgb.gateway.SendTaskWorker.Resender;
import com.sunrise.cgb.gateway.connector.GateWayConnector;
import com.sunrise.cgb.http.HttpConnection;
import com.sunrise.cgb.queue.SendTaskEntity;
import com.sunrise.cgb.ufs.UfsHelper;
import com.sunrise.cgb.ufs.UfsTaskInfo;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:简单传输策略
 */
public class SimpleTransStrategy implements ITransStrategy {
	
	private static Log log = LogFactory.getLog(SimpleTransStrategy.class);

	private int taskType;
	private String recvUri;
	
	public SimpleTransStrategy(int taskType){
		this.taskType = taskType;
		this.recvUri = ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_recvuri, "");
	}
	
	@Override
	public SendFileResult sendDatafile(SendTaskEntity entity) {
		//初始化发送结果
		SendFileResult sendResult = null;
		
		//发送文件到UFS文件服务器
		log.debug("开始调用UFS API发送拆分包ID为:"+entity.getPacketId()+",文件目录为:"+entity.getSrcDir());
		UfsTaskInfo ufsTaskInfo = UfsHelper.createTaskInfo(entity.getTaskType(), entity.getSrcDir());

		//根据任务类型进行UFS API发送,以及设置后续协议需要参数
		if(entity.getTaskType() == ProgramConfig.TASK_TYPE_SMSG){
			sendResult = UfsHelper.sendSmsgDataFile(ufsTaskInfo);			
			entity.setfileName(ufsTaskInfo.getAimFile());
		}else if(entity.getTaskType() == ProgramConfig.TASK_TYPE_PAY){
            sendResult = UfsHelper.sendPayDataFile(ufsTaskInfo);
            entity.setfileName(ufsTaskInfo.getAimFile());
        }else if(entity.getTaskType() == ProgramConfig.TASK_TYPE_POINT){
            sendResult = UfsHelper.sendPointDataFile(ufsTaskInfo);
            entity.setfileName(ufsTaskInfo.getAimFile());
        }else if(entity.getTaskType() == ProgramConfig.TASK_TYPE_EDMS){
			sendResult = UfsHelper.sendEdmsDataFile(ufsTaskInfo);
			entity.setfileName(ufsTaskInfo.getAimDir()+ufsTaskInfo.getAimFile());
		}	
		
		//设置传送方式
		sendResult.setType("UFS传输");	
		//返回结果
		return sendResult;
	}

	@Override
	public void release() {
		if(taskType == ProgramConfig.TASK_TYPE_EDMS){
			TransHelper.transer().decreaseEdmsCnt();
			TransHelper.transer().showEdmsWorkerCnt();
		}else if(taskType == ProgramConfig.TASK_TYPE_SMSG){
			TransHelper.transer().decreaseSmsgCnt();
			TransHelper.transer().showSmsgWorkerCnt();
		}else if(taskType == ProgramConfig.TASK_TYPE_PAY){
            TransHelper.transer().decreasePayCnt();
            TransHelper.transer().showPayWorkerCnt();
        }else if(taskType == ProgramConfig.TASK_TYPE_POINT){
            TransHelper.transer().decreasePointCnt();
            TransHelper.transer().showPointWorkerCnt();
        }
	}

	/**
	 * 根据任务类型创建请求对象
	 * @return
	 */
	private IConnectorRequest buildRequest(){
		if(ProgramConfig.TASK_TYPE_EDMS == taskType)
			return ProtoFactory.buildRequest(ProgramConfig.IO_TYPE_EDMS);
		else if(ProgramConfig.TASK_TYPE_SMSG == taskType)
			return ProtoFactory.buildRequest(ProgramConfig.IO_TYPE_SMSG);
		return null;
	}
	
	@Override
	public SendProtoResult sendProto(
			SendTaskEntity entity,SendTaskWorker worker, Resender resender,OverTimeChecker checker) {
		//创建发送报文结果
		SendProtoResult result = new SendProtoResult();
		//初始化通讯连接处理器
		ITransConnector connector = new GateWayConnector();
		//创建请求对象
		IConnectorRequest request = null;
		try{
			//根据任务类型创建请求对象
			request = buildRequest();	
			//填充请求对象内容
			request.read(entity);
		}catch(Exception e){
			String errMsg = "初始化发送报文对象出错,任务类型:["+taskType+"]";
			log.error("初始化发送报文对象出错,任务类型:["+taskType+"]",e);
			//抛出异常
			throw new RuntimeException(errMsg);
		}
		//同步等待发送完成
		IConnectorResponse response = null;
		try{		
			response= connector.sendRequest(request);
		}catch(Exception e){
			//日志记录
			String errMsg = "与柜面网关通讯失败,流水号:"+entity.getSeqNum()+" cause:"+e.getMessage();
			log.error(errMsg,e);
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
			//中断超时检测
			checker.interrupt();
			//释放资源
			connector.release();
		}
		//柜面网关通讯成功,判断下游系统是否接收成功
		if(response.getResult() == IConnectorResponse.RESULT_SUCCESS){
			//成功处理
			worker.handleProcess(response);
			//结束发送循环
			result.setSuccess();
			result.setBreak();
		}else	if(resender.isOvertimes()){
			//下游系统接收失败,判断是否超过重发上限
			//失败处理
			worker.exceptionProcess(ProgramConfig.SEND_ERR_EXCEPTION, "柜面网关:"+response.getErrorCode());
			//结束发送循环
			result.setBreak();
		}
		return result;
	}

	@Override
	public void complete(SendTaskEntity entity) throws Exception {
		//发送参数(EDMS/SMSG)
		Map<String,String> params = new HashMap<String,String>();
		params.put("SUBPACKAGE_ID", ""+entity.getPacketId());
		params.put("SENT_STATUS", ""+entity.getResult());
		String sentNote = URLEncoder.encode(entity.getErrCode()+";"+entity.getErrCause(),"UTF-8");
		params.put("SENT_NOTE", sentNote);

        // 发送参数(PAY)
        params.put("result", entity.getResult()+"");
        params.put("packageId", entity.getPacketId()+"");

		//访问反馈地址
		HttpConnection.setOvertime(50, 50);
//		HttpConnection.send(recvUri, params);

        if(entity.getTaskType() == ProgramConfig.TASK_TYPE_PAY){
            recvUri=ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_pay_recvuri, "");
        }else if(entity.getTaskType() == ProgramConfig.TASK_TYPE_POINT){
            recvUri=ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_point_recvuri, "");
        }else{
            recvUri=ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_recvuri, "");
        }

        HttpConnection.send(recvUri, params);

		//更新任务发送执行结果状态
		TransHelper.transer().getLogger().updateTaskResped(entity);
		//记录反馈任务信息

        /*String task_type="";
        if(entity.getTaskType()==ProgramConfig.TASK_TYPE_SMSG){
            task_type="SMSG";
        }else if(entity.getTaskType()==ProgramConfig.TASK_TYPE_EDMS){
            task_type="EDMS";
        }else{
            task_type="PAY";
        }*/

		String msg = "\r\n==========发送完成信息==============\r\n";

        String taskType=null;

        if(this.taskType==ProgramConfig.TASK_TYPE_SMSG){
            taskType="SMSG";
        }else if(this.taskType==ProgramConfig.TASK_TYPE_EDMS){
            taskType="EDMS";
        }else if(this.taskType==ProgramConfig.TASK_TYPE_PAY){
            taskType="PAY";
        }else{
            taskType="POINT";
        }

		msg += "传输任务类型:"+taskType+"\r\n";
		msg += "执行结果:"+((entity.getResult()==IConnectorResponse.RESULT_SUCCESS)?"成功":"失败")+"\r\n";
		msg += "错误描述:"+entity.getErrCode()+","+entity.getErrCause()+"\r\n";
		msg += "拆分包ID:"+entity.getPacketId()+"\r\n";
		msg += "源路径:"+entity.getSrcDir()+"\r\n";
		msg += "发送UFS路径:" + entity.getfileName() + "\r\n";		
		msg += "==============================\r\n";
		log.info(msg);
	}
}
