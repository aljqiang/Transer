package com.sunrise.cgb.schedule;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface IScheduleEngine {
    /**
     * 添加调度计划
     * @param info
     * @throws Exception
     */
    public ScheduleResult lauchTask(ScheduleInfo info);
    /**
     * 取消调度计划
     * @param info
     * @return
     */
    public ScheduleResult cancelTaskPlan(ScheduleInfo info);
    /**
     * 判断任务计划是否存在
     * @param info
     * @return
     */
    public boolean isTaskPlanExsit(ScheduleInfo info) throws Exception;

    /**
     * 关闭所有定时任务
     * @return
     */
    public boolean shutdownJobs() throws Exception;
}
