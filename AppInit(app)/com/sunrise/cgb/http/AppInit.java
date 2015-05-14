package com.sunrise.cgb.http;

import java.sql.ResultSet;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.cc.CCQueryFactory;
import com.sunrise.cgb.core.CGBTranser;
import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.gateway.ProtoFactory;
import com.sunrise.cgb.gateway.xmlparser.ProtoParserFactory;
import com.sunrise.cgb.queue.SendTaskEntity;
import com.sunrise.cgb.schedule.ScheduleInfo;
import com.sunrise.cgb.schedule.ScheduleManager;
import com.sunrise.cgb.schedule.handler.DatafileCleanHandler;
import com.sunrise.cgb.ufs.UfsHelper;
import com.sunrise.foundation.dbutil.ResultSetHandler;
import com.sunrise.rdcp.engine.RDCPInit;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class AppInit extends RDCPInit {

    private static Log log = LogFactory.getLog(RDCPInit.class);
    
	@Override
	public void init(FilterConfig config) throws ServletException {
		//初始化柜面网关通讯程序
		CGBTranser transer = CGBTranser.create();
		//设置到工具类
		TransHelper.setTranser(transer);
		//初始化协议对象
		ProtoFactory.init();
		//添加交易码关联报文体解析器
		ProtoParserFactory.init();
		//初始化UFS文件发送参数处理器
		UfsHelper.init();
		//查询处理器
		CCQueryFactory.init();
		
		//加载未完成任务到任务队列
		transer.getLogger().queryTaskFailure(new ResultSetHandler<Object>() {
			
			@Override
			public Object handle(ResultSet rs) throws Exception {
				long cnt = 0;
				while(rs.next())
				{
					cnt++;
					SendTaskEntity entity = new SendTaskEntity();
					entity.setPacketId(rs.getLong("subpackage_id"));
					entity.setPlanSendTime(rs.getDate("plan_send_time"));
					entity.setSrcDir(rs.getString("src_dir"));
					entity.setTaskId(rs.getLong("id"));
					entity.setTaskType(rs.getInt("task_type"));
					entity.setRespCode(rs.getString("respcode"));
					entity.setFileRecordNum(rs.getLong("record_num"));
					TransHelper.getScheduleStrategy(entity.getTaskType()).addTaskEntity(entity);
				}
				log.info("本次启动共加载["+cnt+"]个待发送任务.");
				return null;
			}
		});
		//加载已完成任务到完成队列
		transer.getLogger().queryRespFailure(new ResultSetHandler<Object>() {

			@Override
			public Object handle(ResultSet rs) throws Exception {
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
				log.info("本次启动共加载["+cnt+"]个未反馈CMMS的已发送任务.");
				return null;
			}
		});
		//设置http连接为多核模式
		HttpConnection.setRunningMode(HttpConnection.RUNNING_MODE_MULTI);
		//初始化通讯程序工作线程
		transer.runSmsgSchedule();
		transer.runEdmsSchedule();
		transer.runRespQueueSchedule();
		transer.runPaySchedule();
		transer.runPointSchedule();
		transer.runCcSchedule();

        // 关闭所有定时任务
        try {
            ScheduleManager.engine().shutdownJobs();
        } catch (Exception e) {
            e.printStackTrace();
        }

		//初始化定时清理作业
		ScheduleManager.registerTaskHandler(new DatafileCleanHandler());
		ScheduleManager.engine().lauchTask(new ScheduleInfo(-999,0,"0 0 22 * * ? *"));
		//调用后续初始化
        super.init(config);
	}
	
}
