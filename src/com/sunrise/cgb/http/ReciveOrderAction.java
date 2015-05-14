package com.sunrise.cgb.http;

import java.sql.ResultSet;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.gateway.SendTaskTester;
import com.sunrise.cgb.queue.SendTaskEntity;
import com.sunrise.foundation.dbutil.ResultSetHandler;
import com.sunrise.framework.struts2.BaseAction;
import com.sunrise.framework.struts2.Header;
import com.sunrise.framework.struts2.JsonObject;
import com.sunrise.framework.core.Constants;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:接收发送任务接口
 */
public class ReciveOrderAction extends BaseAction {

    private static Log log = LogFactory.getLog(ReciveOrderAction.class);
    
    private int orderType;
    private long packageId;
    private String dataFilePath;
    
    private String respCode;
    private long recordNum;
    
	@Override
	protected Object defaultAction() throws Exception {
		return null;
	}
	
	/**
	 * 添加发送任务到发送队列
	 * @return
	 */
	public JsonObject sendTask(){
		//检测参数是否合法
		if(!TransHelper.checkTaskType(orderType))
			return new JsonObject(new Header(0,"success"),"error");
		//封装发送任务实体类
		SendTaskEntity entity = new SendTaskEntity();
		entity.setPlanSendTime(new Date());
		entity.setTaskType(this.getOrderType());
		entity.setPacketId(this.getPackageId());
		entity.setSrcDir(this.getDataFilePath());
		entity.setFileRecordNum(this.getRecordNum());
		entity.setRespCode(this.getRespCode());
		if(entity.getPacketId() == -3L){
			//判断packageId是否为-3，是则为调试用的发送任务，直接发送 2013-08-13 李嘉伟
			//更改发送的数据文件
			if(this.getOrderType()== ProgramConfig.TASK_TYPE_EDMS)
				entity.setSrcDir(Constants.getWebRoot()+ProgramConfig.EDMS_TEST_FILE);
			else if(this.getOrderType() == ProgramConfig.TASK_TYPE_PAY)
				entity.setSrcDir(Constants.getWebRoot()+ProgramConfig.PAY_TEST_FILE);
			else
				entity.setSrcDir(Constants.getWebRoot()+ProgramConfig.SMSG_TEST_FILE);
			//构建测试发送器
			SendTaskTester tester = new SendTaskTester(entity);
			tester.setOverTime(3600);
			tester.setResender(10, 10000);
			tester.run();
			if(tester.isSuccess())
				return new JsonObject(new Header(0,"success"),"发送成功,流水号:"+tester.getSeqnum()+"数据文件名称:"+entity.getfileName());
			else
				return new JsonObject(new Header(0,"success"),"发送失败,流水号:"+tester.getSeqnum()+";错误信息:"+tester.getErrmsg());
		}else{
			//加入日志表
			TransHelper.transer().getLogger().addTaskRecord(entity);
			//加入任务队列
			TransHelper.getScheduleStrategy(orderType).addTaskEntity(entity);
			//写入日志
			log.debug("添加任务(packageId:"+packageId+" taskType:"+this.orderType+" srcDir:"+this.dataFilePath+")完成.");
		}
		return new JsonObject(new Header(0,"success"),"");
	}
	
	/**
	 * 显示网络程序信息
	 * @return
	 */
	public JsonObject showInfo(){
		String msg = "";
		msg += "CallCenter Query Running:"+TransHelper.transer().showCcWorkerCnt()+" ;";
		msg += "Edms task Running:"+TransHelper.transer().showEdmsWorkerCnt()+" ;";
		msg += "Pay task Running:"+TransHelper.transer().showPayWorkerCnt()+" ;";
		msg += "Point task Running:"+TransHelper.transer().showPointWorkerCnt()+" ;";
		msg += "Smsg task Running:"+TransHelper.transer().showSmsgWorkerCnt()+" ";
		return new JsonObject(new Header(0,"success"),msg);
	}
	
	/**
	 * 手动反馈完成任务结果到业务系统
	 * @return
	 */
	public JsonObject startRevise(){
		//System.exit(0);
//		String srcDir = "hello.txt";
//		log.debug("开始调用UFS API发送拆分包文件目录为:"+srcDir);
//		SendFileResult ufsResult = null;
//		UfsTaskInfo info = UfsHelper.createTaskInfo(ProgramConfig.TASK_TYPE_TEST, srcDir);
//		UfsHelper.sendDataFile(ProgramConfig.TEST.getConfig(ConstantKey.ufs_task_id, ""),
//									       ProgramConfig.TEST.getConfig(ConstantKey.ufs_ip, ""),
//									       ProgramConfig.TEST.getConfig(ConstantKey.ufs_port, 9000),
//									       ProgramConfig.TEST.getConfig(ConstantKey.ufs_timeout, 400),
//									       info);
//		log.debug("调用UFS API发送拆分包文件目录为:"+srcDir+"完成!!");
		//加载已完成任务到完成队列
		String resultInfo = TransHelper.transer()
													.getLogger()
													.queryRespFailure(new ResultSetHandler<String>() {

			@Override
			public String handle(ResultSet rs) throws Exception {
				long cnt = 0;
				while(rs.next())
				{
					cnt++;
					SendTaskEntity entity = new SendTaskEntity();
					entity.setSendTime(rs.getDate("send_time"));
					entity.setEndTime(rs.getDate("end_time"));
					entity.setErrCause(rs.getString("err_cause"));
					entity.setErrCode(rs.getString("err_code"));
					entity.setPacketId(rs.getLong("subpackage_id"));
					entity.setPlanSendTime(rs.getDate("plan_send_time"));
					entity.setSrcDir(rs.getString("src_dir"));
					entity.setTaskId(rs.getLong("id"));
					entity.setTaskType(rs.getInt("task_type"));
					entity.setResult(rs.getInt("result"));
					TransHelper.transer().taskQueue().respTaskQueue().add2WaitingQueue(entity);
				}
				String resultInfo = "本次启动共加载["+cnt+"]个未反馈CMMS的已发送任务.";
				log.info(resultInfo);
				return resultInfo;
			}
		});
		return new JsonObject(new Header(0,"success"),resultInfo);
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	public long getPackageId() {
		return packageId;
	}

	public void setPackageId(long packageId) {
		this.packageId = packageId;
	}

	public long getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(long recordNum) {
		this.recordNum = recordNum;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	
}
