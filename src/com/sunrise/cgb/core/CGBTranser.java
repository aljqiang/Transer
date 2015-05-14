package com.sunrise.cgb.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.queue.TaskEntityQueue;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CGBTranser {

	private static Log log = LogFactory.getLog(CGBTranser.class);
	
	private static CGBTranser transer;
	/**
	 * 发送任务队列
	 */
	private TaskEntityQueue taskQueue;
	
	/**
	 * 短信系统工作线程记录量
	 */
	private Integer smsgWorkerCnt = 0;
	private Object smsgWorkerCntLock = new Object();
	/**
	 * 短信系统工作线程最大限制
	 */
	private int SMSG_WORKER_LOAD;
	/**
	 * 电邮系统工作线程记录量
	 */
	private Integer edmsWorkerCnt = 0;
	private Object edmsWorkerCntLock = new Object();
	/**
	 * 电邮系统工作线程最大限制
	 */
	private int EDMS_WORKER_LOAD;
	/**
	 * 传呼中心工作线程记录两
	 */
	private Integer ccWorkerCnt = 0;
	private Object ccWorkerCntLock = new Object();
	/**
	 * 传呼中心工作线程最大限制
	 */
	private int CC_WORKER_LOAD;
	/**
	 * 调帐传输工作线程记录量
	 */
	private Integer payWorkerCnt = 0;
	private Object payWorkerCntLock = new Object();

    /**
     * 积分系统传输工作线程记录量
     */
    private Integer pointWorkerCnt=0;
    private Object pointWorkerCntLock = new Object();

	/**
     * 调帐传输工作线程最大限制
     */
    private int PAY_WORKER_LOAD;

    /**
     * 积分系统传输工作线程最大限制
     */
    private int POINT_WORKER_LOAD;

	/**
	 * 发送任务日志记录DAO
	 */
	private LoggerDao logger;
	/**
	 * 单例构造函数.
	 */
	private CGBTranser(){
		//初始化发送任务队列
		this.taskQueue = TaskEntityQueue.queue();
		//初始化各任务类型处理任务线程最大限度
		this.SMSG_WORKER_LOAD = ProgramConfig.SMSG.getConfig(ConstantKey.send_worker_thread,5);
		this.EDMS_WORKER_LOAD = ProgramConfig.EDMS.getConfig(ConstantKey.send_worker_thread,5);
		this.CC_WORKER_LOAD = ProgramConfig.CALLCENTER.getConfig(ConstantKey.send_worker_thread,20);
		this.PAY_WORKER_LOAD = ProgramConfig.PAY.getConfig(ConstantKey.send_worker_thread, 5);
		this.POINT_WORKER_LOAD = ProgramConfig.POINT.getConfig(ConstantKey.send_worker_thread, 5);
		//初始化Dao对象
		logger = new LoggerDao();
	}
	/**
	 * 创建发送任务调度器
	 * @return
	 */
	public static synchronized CGBTranser create(){
		if(null == transer)
			transer = new CGBTranser();
		return transer;
	}
	
	/**
	 * 获取发送日志记录操作
	 * @return
	 */
	public LoggerDao getLogger(){
		return transer.logger;
	}
	
	/**
	 * 获取任务队列
	 * @return
	 */
	public TaskEntityQueue taskQueue(){
		return transer.taskQueue;
	}
	
	/**
	 * 开始轮询短信任务队列
	 */
	public void runSmsgSchedule(){
		log.debug(" 开始轮询短信任务队列");
		TaskQueueScheduler smsgScheduler = new TaskQueueScheduler(
										ProgramConfig.SMSG.getConfig(ConstantKey.send_loop_delay,3),
										ProgramConfig.TASK_TYPE_SMSG, 
										ProgramConfig.SMSG.getConfig(ConstantKey.send_ot, 60*5),
										ProgramConfig.SMSG.getConfig(ConstantKey.resend_times, 5),
										ProgramConfig.SMSG.getConfig(ConstantKey.resend_wait_time, 10000));
		smsgScheduler.setDaemon(true);
		smsgScheduler.setName("SmsgQueueScheduler");
		smsgScheduler.start();
	}
	
	/**
	 * 改动当前电邮系统工作线程数记录
	 */
	public void decreaseSmsgCnt(){
		synchronized(transer.smsgWorkerCntLock){
			if(transer.smsgWorkerCnt > 0)
				transer.smsgWorkerCnt--;
			transer.smsgWorkerCntLock.notifyAll();
		}
	}
	
	/**
	 * 判断邮件平台负载情况
	 * @return
	 */
	public boolean checkSmsgLoad(){
		synchronized(transer.smsgWorkerCntLock){
			boolean result;
			if(transer.smsgWorkerCnt+1>transer.SMSG_WORKER_LOAD){
				result = false;
			}else{
				result = true;
				transer.smsgWorkerCnt++;
			}
			//log.debug("当前系统有["+(((transer.smsgWorkerCnt-1)<=0)?0:(transer.smsgWorkerCnt-1))+"]个短信发送任务正在执行.");
			transer.smsgWorkerCntLock.notifyAll();
			return result;
		}
	}
	
	/**
	 * 开始轮询调帐任务队列
	 */
	public void runPaySchedule(){
		log.debug("开始轮询调帐任务队列");
		TaskQueueScheduler payQueueScheduler = new TaskQueueScheduler(
				ProgramConfig.PAY.getConfig(ConstantKey.send_loop_delay, 3),
				ProgramConfig.TASK_TYPE_PAY,
				ProgramConfig.PAY.getConfig(ConstantKey.send_ot, 60*5),
				ProgramConfig.PAY.getConfig(ConstantKey.resend_times, 5),
				ProgramConfig.PAY.getConfig(ConstantKey.resend_wait_time, 10000));
		payQueueScheduler.setDaemon(true);
		payQueueScheduler.setName("PayQueueScheduler");
		payQueueScheduler.start();
	}

	/**
	 * 改动当前入账系统工作线程数记录
	 */
	public void decreasePayCnt(){
		synchronized(transer.payWorkerCntLock){
			if(transer.payWorkerCnt > 0)
				transer.payWorkerCnt--;
			transer.payWorkerCntLock.notifyAll();
		}
	}

    /**
     * 判断入账系统负载情况
     * @return
     */
    public boolean checkPayLoad(){
        synchronized(transer.payWorkerCntLock){
            boolean result;
            if(transer.payWorkerCnt+1>transer.PAY_WORKER_LOAD){
                result = false;
            }else{
                result = true;
                transer.payWorkerCnt++;
            }
            //log.debug("当前系统有["+(((transer.smsgWorkerCnt-1)<=0)?0:(transer.smsgWorkerCnt-1))+"]个短信发送任务正在执行.");
            transer.payWorkerCntLock.notifyAll();
            return result;
        }
    }

    /**
     * 开始轮询积分任务队列
     */
    public void runPointSchedule(){
        log.debug("开始轮询积分任务队列");
        TaskQueueScheduler pointQueueScheduler = new TaskQueueScheduler(
                ProgramConfig.POINT.getConfig(ConstantKey.send_loop_delay, 3),
                ProgramConfig.TASK_TYPE_POINT,
                ProgramConfig.POINT.getConfig(ConstantKey.send_ot, 60*5),
                ProgramConfig.POINT.getConfig(ConstantKey.resend_times, 5),
                ProgramConfig.POINT.getConfig(ConstantKey.resend_wait_time, 10000));
        pointQueueScheduler.setDaemon(true);
        pointQueueScheduler.setName("PointQueueScheduler");
        pointQueueScheduler.start();
    }

    /**
     * 改变当前积分系统工作线程数记录
     */
    public void decreasePointCnt(){
        synchronized(transer.pointWorkerCntLock){
            if(transer.pointWorkerCnt > 0)
                transer.pointWorkerCnt--;
            transer.pointWorkerCntLock.notifyAll();
        }
    }

    /**
     * 判断积分系统负载情况
     */
    public boolean checkPointLoad(){
        synchronized(transer.pointWorkerCntLock){
            boolean result;
            if(transer.pointWorkerCnt+1>transer.POINT_WORKER_LOAD){
                result = false;
            }else{
                result = true;
                transer.pointWorkerCnt++;
            }
            //log.debug("当前系统有["+(((transer.smsgWorkerCnt-1)<=0)?0:(transer.smsgWorkerCnt-1))+"]个短信发送任务正在执行.");
            transer.pointWorkerCntLock.notifyAll();
            return result;
        }
    }
	
	/**
	 * 开始轮询电邮任务队列
	 */
	public void runEdmsSchedule(){
		log.debug("开启轮询电邮任务队列");
		TaskQueueScheduler edmsQueueScheduler = new TaskQueueScheduler(
				ProgramConfig.EDMS.getConfig(ConstantKey.send_loop_delay,3),
				ProgramConfig.TASK_TYPE_EDMS, 
				ProgramConfig.EDMS.getConfig(ConstantKey.send_ot, 60*5),
				ProgramConfig.EDMS.getConfig(ConstantKey.resend_times, 5),
				ProgramConfig.EDMS.getConfig(ConstantKey.resend_wait_time, 10000));
		edmsQueueScheduler.setDaemon(true);
		edmsQueueScheduler.setName("EdmsQueueScheduler");
		edmsQueueScheduler.start();
	}
	
	/**
	 * 改动当前电邮发送任务工作线程记录 
	 */
	public void decreaseEdmsCnt(){
		synchronized(transer.edmsWorkerCntLock){
			if(transer.edmsWorkerCnt > 0)
				transer.edmsWorkerCnt--;
			transer.edmsWorkerCntLock.notifyAll();
		}
	}
	
	/**
	 * 检测电邮系统负载情况
	 * @return
	 */
	public boolean checkEdmsLoad(){
		synchronized(transer.edmsWorkerCntLock){
			boolean result;
			if(transer.edmsWorkerCnt+1>transer.EDMS_WORKER_LOAD){
				result = false;
			}else{
				result = true;
				transer.edmsWorkerCnt++;
			}
			//log.debug("当前系统有["+(((transer.edmsWorkerCnt-1)<=0)?0:(transer.edmsWorkerCnt-1))+"]个电邮发送任务正在执行.");
			transer.edmsWorkerCntLock.notifyAll();
			return result;
		}
	}
	
	/**
	 * 开始轮询已经传呼中心查询任务
	 */
	public void runCcSchedule(){
		log.debug("开启轮询传呼中心查询任务");
		TaskQueueScheduler ccScheduler = new TaskQueueScheduler(
										ProgramConfig.CALLCENTER.getConfig(ConstantKey.send_loop_delay,0.3),
										ProgramConfig.TASK_TYPE_CC, 
										ProgramConfig.CALLCENTER.getConfig(ConstantKey.send_ot, 122),
										ProgramConfig.CALLCENTER.getConfig(ConstantKey.resend_times, 5),
										ProgramConfig.CALLCENTER.getConfig(ConstantKey.resend_wait_time, 10000));
		ccScheduler.setDaemon(true);
		ccScheduler.setName("CallCenterQueueScheduler");
		ccScheduler.start();
	}
	
	/**
	 * 改动当前系统传呼中心查询线程记录
	 */
	public void decreaseCcLoad(){
		synchronized(transer.ccWorkerCntLock){
			if(transer.ccWorkerCnt > 0)
				transer.ccWorkerCnt--;
			transer.ccWorkerCntLock.notifyAll();
		}
	}
	
	/**
	 * 检测传呼查询任务负载
	 * @return
	 */
	public boolean checkCcLoad(){
		synchronized(transer.ccWorkerCntLock){
			boolean result;
			if(transer.ccWorkerCnt+1>transer.CC_WORKER_LOAD){
				result = false;
			}else{
				result = true;
				transer.ccWorkerCnt++;
			}
			//log.debug("当前系统有["+(((transer.ccWorkerCnt-1)<=0)?0:(transer.ccWorkerCnt-1))+"]个cc查询任务正在执行.");
			transer.ccWorkerCntLock.notifyAll();
			return result;
		}
	}
	
	/**
	 * 开始轮询已经处理结束的任务
	 */
	public void runRespQueueSchedule(){
		RespQueueHandler handler = new RespQueueHandler(
													ProgramConfig.GATEWAY.getConfig(ConstantKey.gateway_queue_loop_delay, 300));
		handler.setDaemon(true);
		handler.setName("EndedTaskQueueHandler");
		handler.start();
	}
	/**
	 * 显示当前cc查询情况
	 */
	public int showCcWorkerCnt(){
		int cntResult = 0;
		synchronized(transer.ccWorkerCntLock){
			cntResult = (((transer.ccWorkerCnt-1)<0)?0:(transer.ccWorkerCnt-1));
			log.debug("当前系统还有["+cntResult+"]个cc查询任务正在执行.");
		}
		return cntResult;
	}
	/**
	 * 显示当前短信执行情况
	 */
	public int showSmsgWorkerCnt(){
		int cntResult = 0;
		synchronized(transer.smsgWorkerCntLock){
			cntResult = (((transer.smsgWorkerCnt-1)<0)?0:(transer.smsgWorkerCnt-1));
			log.debug("当前系统还有["+cntResult+"]个短信发送任务正在执行.");
		}
		return cntResult;
	}
	/**
	 * 显示当前电邮执行情况.
	 */
	public int showEdmsWorkerCnt(){
		int cntResult = 0;
		synchronized(transer.edmsWorkerCntLock){
			cntResult = (((transer.edmsWorkerCnt-1)<0)?0:(transer.edmsWorkerCnt-1));
			log.debug("当前系统还有["+cntResult+"]个电邮发送任务正在执行.");
		}
		return cntResult;
	}
	/**
	 * 显示当前调帐任务执行情况
	 * @return
	 */
	public int showPayWorkerCnt(){
		int cntResult = 0;
		synchronized(transer.payWorkerCntLock){
			cntResult = (((transer.payWorkerCnt-1)<0)?0:(transer.payWorkerCnt-1));
			log.debug("当前系统还有["+cntResult+"]个调帐任务正在执行.");
		}
		return cntResult;
	}

    /**
     * 显示当前积分任务执行情况
     */
    public int showPointWorkerCnt(){
        int cntResult = 0;
        synchronized(transer.pointWorkerCntLock){
            cntResult=(((transer.pointWorkerCnt-1)<0)?0:(transer.pointWorkerCnt-1));
            log.debug("当前系统还有["+cntResult+"]个积分任务正在执行.");
        }
        return cntResult;
    }
}
