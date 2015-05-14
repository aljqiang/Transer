package com.sunrise.cgb.core;

import java.util.HashMap;
import java.util.Map;

import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.core.strategy.*;
import com.sunrise.cgb.gateway.strategy.NatpTransStrategy;
import com.sunrise.cgb.gateway.strategy.SimpleTransStrategy;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class TransHelper {

    private static final Map<Integer, ITransStrategy> transStrategy = new HashMap<Integer,ITransStrategy>();

    private static final Map<Integer,IQueueScheduleStrategy> scheduleStrategy = new HashMap<Integer,IQueueScheduleStrategy>();

    private static CGBTranser transer;
    /**
     * 设置通讯主程序
     * @param transer
     */
    public static void setTranser(CGBTranser transer){
        TransHelper.transer =transer;
    }
    /**
     * 获取主程序
     * @return
     */
    public static CGBTranser transer(){
        return transer;
    }

    static{
        if(transStrategy.size() == 0 || scheduleStrategy.size() == 0)
            TransHelper.init();
    }
    /**
     * 初始化
     */
    public static void init(){
        TransHelper.registerTransStrategy(ProgramConfig.TASK_TYPE_PAY, new NatpTransStrategy(ProgramConfig.PAY));
        TransHelper.registerTransStrategy(ProgramConfig.TASK_TYPE_POINT, new SimpleTransStrategy(ProgramConfig.TASK_TYPE_POINT));   // 新增积分任务文件发送策略
        TransHelper.registerTransStrategy(ProgramConfig.TASK_TYPE_PAY, new SimpleTransStrategy(ProgramConfig.TASK_TYPE_PAY));
        TransHelper.registerTransStrategy(ProgramConfig.TASK_TYPE_EDMS, new SimpleTransStrategy(ProgramConfig.TASK_TYPE_EDMS));
        TransHelper.registerTransStrategy(ProgramConfig.TASK_TYPE_SMSG, new SimpleTransStrategy(ProgramConfig.TASK_TYPE_SMSG));

        TransHelper.registerScheduleStrategy(ProgramConfig.TASK_TYPE_POINT, new PointQueueScheduleStrategy());  // 新增积分任务任务调度策略
        TransHelper.registerScheduleStrategy(ProgramConfig.TASK_TYPE_PAY, new PayQueueScheduleStrategy());
        TransHelper.registerScheduleStrategy(ProgramConfig.TASK_TYPE_CC, new CcQueueScheduleStrategy());
        TransHelper.registerScheduleStrategy(ProgramConfig.TASK_TYPE_EDMS, new EdmsQueueScheduleStrategy());
        TransHelper.registerScheduleStrategy(ProgramConfig.TASK_TYPE_SMSG, new SmsgQueueScheduleStrategy());
    }
    /**
     * 添加通讯策略
     * @param taskType
     * @param strategy
     */
    public static void registerTransStrategy(Integer taskType,ITransStrategy strategy){
        transStrategy.put(taskType, strategy);
    }
    /**
     * 添加任务队列调度策略
     * @param taskType
     * @param strategy
     */
    public static void registerScheduleStrategy(Integer taskType,IQueueScheduleStrategy strategy){
        scheduleStrategy.put(taskType,strategy);
    }
    /**
     * 获取通讯策略
     * @param taskType
     * @return
     */
    public static ITransStrategy getTransStrategy(int taskType){
        return TransHelper.transStrategy.get(taskType);
    }
    /**
     * 获取任务队列调度策略
     * @param taskType
     * @return
     */
    public static IQueueScheduleStrategy getScheduleStrategy(int taskType){
        return TransHelper.scheduleStrategy.get(taskType);
    }
    /**
     * 检测是否合法的任务类型
     * @param taskType
     * @return
     */
    public static boolean checkTaskType(int taskTypeId){
        for(Integer taskType:ProgramConfig.TASK_TYPE)
        {
            if(taskTypeId == taskType)
                return true;
        }
        return false;
    }
}
