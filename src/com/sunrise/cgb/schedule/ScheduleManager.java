package com.sunrise.cgb.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sunrise.cgb.schedule.impl.QuartzScheduleEngine;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class ScheduleManager {
	
    private static Log log = LogFactory.getLog(ScheduleManager.class);
	/**
	 * 调度器引擎
	 */
    private static IScheduleEngine engine = new QuartzScheduleEngine();
    /**
     * 调度任务处理器
     */
    private static final Map<Integer,ITaskHandler> taskHandlers = new HashMap<Integer,ITaskHandler>();
    /**
     * 等待任务队列
     */
    private static final List<CGBScheduler> taskQueue = new ArrayList<CGBScheduler>();
    /**
     * 正在执行任务池
     */
    private static final Map<Long,CGBScheduler> runningTaskPool = new HashMap<Long,CGBScheduler>();
    /**
     * 任务调度监控线程
     */
    private static final CGBScheduleMonitor monitor = new CGBScheduleMonitor();
    /**
     * 初始化调度管理器
     */
    static{
    	monitor.start();
    }
    
    /**
     * 返回调度引擎
     * @return
     */
    public static IScheduleEngine engine(){
    	return engine;
    }
    
    /**
     * 添加等待执行任务
     * @param scheduler
     */
    public static void addScheduler(ScheduleInfo info){
    	synchronized(taskQueue){
    		ScheduleManager.taskQueue.add(new CGBScheduler(monitor,info));
    	}
    }
    
    /**
     * 获取最早等待执行任务
     * @return
     */
    protected static CGBScheduler popScheduler(){
    	CGBScheduler scheduler = null;
    	synchronized(taskQueue){
    		if(taskQueue.size()!=0)
    			scheduler = taskQueue.remove(0);
    	}
    	return scheduler;
    }
    
    /**
     * 执行任务池是否存在相同任务
     * @param info
     * @return
     */
    public static boolean isRunningPoolExist(ScheduleInfo info){
    	boolean result = false;
    	synchronized(runningTaskPool){
    		result = runningTaskPool.containsKey(info.getSubpackage_id());
    	}
    	return result;
    }
    
    /**
     * 添加到任务执行池
     * @param scheduler
     */
    protected static void addRunningPool(CGBScheduler scheduler){
    	synchronized(runningTaskPool){
    		runningTaskPool.put(scheduler.getInfo().getSubpackage_id(),scheduler);
    		scheduler.start();
    	}
    }
    
    /**
     * 移除任务执行池任务
     * @param info
     */
    protected static void removeRunningTask(ScheduleInfo info){
    	synchronized(runningTaskPool){
    		if(runningTaskPool.containsKey(info.getSubpackage_id()))
    			runningTaskPool.remove(info.getSubpackage_id());
    	}
    }
    
    /**
     * 中断执行池任务
     * @param info
     */
    public static boolean interruptRunningTask(ScheduleInfo info){
    	boolean result = false;
    	synchronized(runningTaskPool){
    		if(runningTaskPool.containsKey(info.getSubpackage_id())){
    			runningTaskPool.get(info.getSubpackage_id()).interrupt();
    			result = true;
    		}
    	}
    	return result;
    }
    
    /**
     * 添加任务处理器
     * @param handler
     */
    public static void registerTaskHandler(ITaskHandler handler){
    	taskHandlers.put(handler.getTaskType(), handler);
    }
    
    /**
     * 根据任务类型获取任务处理器
     * @param task_type
     */
    protected static ITaskHandler getTaskHandler(int task_type){
    	return taskHandlers.get(task_type);
    }
}
