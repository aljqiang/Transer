package com.sunrise.cgb.core;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.exception.SendTaskException;
import com.sunrise.cgb.queue.SendTaskEntity;
import com.sunrise.foundation.dbutil.DBException;
import com.sunrise.foundation.dbutil.QueryRunner;
import com.sunrise.foundation.dbutil.ResultSetHandler;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class LoggerDao {
	
	private static Log log = LogFactory.getLog(LoggerDao.class);
	
	/**
	 * 添加发送任务
	 * @param entity
	 */
	public void addTaskRecord(SendTaskEntity entity){
		try {
			Long taskId = QueryRunner.queryResultSet(
					"select CD_IO_SENDTASK_LOG_SEQ.nextval from SYSIBM.dual", 
					QueryRunner.LONG_RESULT_HANDLER).longValue();
			entity.setTaskId(taskId);
			QueryRunner.update("insert into CD_IO_SENDTASK_LOG (ID,TASK_TYPE,SUBPACKAGE_ID," +
																					"PLAN_SEND_TIME,SRC_DIR,STATUS," +
																					"RECORD_NUM,RESPCODE) values " +
																				   "(?,?,?," +
																				   "CURRENT TIMESTAMP,?,?," +
																				   "?,?)",
										    new Object[]{entity.getTaskId(),entity.getTaskType(),entity.getPacketId(),
																entity.getSrcDir(),ProgramConfig.TASK_STATE_SENDING,
																entity.getFileRecordNum(),entity.getRespCode()});
		} catch (DBException e) {
			log.error(e.getMessage(),new SendTaskException(entity, "添加发送任务记录时出错."));			
		}
	}
	
	/**
	 * 更新发送任务结果
	 * @param entity
	 */
	public void updateTaskRecord(SendTaskEntity entity){
		try{
			//修辑错误描述
			if(entity.getErrCause().length()>100)
				entity.setErrCause(entity.getErrCause().substring(0,100));
			//执行SQL
			QueryRunner.update("update CD_IO_SENDTASK_LOG set send_time = ?,end_time = ?,status = ?," +
										   "result = ?,err_code = ?,err_cause = ?,seqnum = ? where id = ?",
											new Object[]{new Timestamp(entity.getSendTime().getTime()),new Timestamp(entity.getEndTime().getTime()),
																ProgramConfig.TASK_STATE_SENDED,
																entity.getResult(),entity.getErrCode(),entity.getErrCause(),entity.getSeqNum(),entity.getTaskId()});
		}catch(DBException e){
			log.error(e.getMessage(),new SendTaskException(entity,"修改发送任务记录时出错."));
		}
	}
	
	/**
	 * 更新发送状态
	 * @param entity
	 */
	public void updateTaskResped(SendTaskEntity entity){
		try{
			QueryRunner.update("update CD_IO_SENDTASK_LOG set status = ? where id = ?",
											new Object[]{ProgramConfig.TASK_STATE_RESULT,entity.getTaskId()});
		}catch(DBException e){
			log.error(e.getMessage(),new SendTaskException(entity,"修改返回CMMS状态出错."));
		}
	}

	/**
	 * 查询发送返回结果到业务系统失败的记录
	 * @param <T>
	 * @param proc
	 */
	public <T> T queryRespFailure(ResultSetHandler<T> proc){
		try{
			return QueryRunner.queryResultSet("select * from CD_IO_SENDTASK_LOG where status = ?", 
																  new Object[]{ProgramConfig.TASK_STATE_SENDED},
																  proc);
		}catch(DBException e){
			log.error("加载未反馈给CMMS记录出错.",e);
		}
		return null;
	}
	
	/**
	 * 查询没有发出的任务记录
	 * @param proc
	 */
	public void queryTaskFailure(ResultSetHandler<Object> proc){
		try{
			QueryRunner.queryResultSet("select * from CD_IO_SENDTASK_LOG where status = ?",
													   new Object[]{ProgramConfig.TASK_STATE_SENDING},
													   proc);
		}catch(DBException e){
			log.error("加载未发送任务记录出错.",e);
		}
	}
}
