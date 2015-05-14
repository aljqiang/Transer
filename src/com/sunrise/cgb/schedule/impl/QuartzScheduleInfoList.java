package com.sunrise.cgb.schedule.impl;

import com.sunrise.cgb.schedule.ScheduleInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 调度任务存储类
 * User: Larry
 * Date: 2015-02-05
 * Time: 15:29
 * Version: 1.0
 */

public class QuartzScheduleInfoList {
    private static Log log = LogFactory.getLog(QuartzScheduleInfoList.class);

    /**
     * 调度任务存储数组
     */
    private static final List<ScheduleInfo> quartzScheduleInfoList = new ArrayList<ScheduleInfo>();


    /**
     * 添加调度任务
     */
    public static void addQS(ScheduleInfo info) {
        synchronized (quartzScheduleInfoList) {
            QuartzScheduleInfoList.quartzScheduleInfoList.add(info);
        }
    }

    /**
     * 移除调度任务
     */
    public static void removeQS(ScheduleInfo info) {
        QuartzScheduleInfoList.quartzScheduleInfoList.remove(info);
    }

    /**
     * 获取调度任务
     */
    public static int getAllQSBySID(long subpackage_id) {
        int cnt = 0;
        synchronized (quartzScheduleInfoList) {
            for (ScheduleInfo scheduleInfo : quartzScheduleInfoList) {
                if (scheduleInfo.getSubpackage_id() == subpackage_id) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    /**
     * 获取调度任务
     */
    public static int getAllQSByTID(int type_id) {
        int cnt = 0;
        synchronized (quartzScheduleInfoList) {
            for (ScheduleInfo scheduleInfo : quartzScheduleInfoList) {
                if (scheduleInfo.getTask_type() == type_id) {
                    cnt++;
                }
            }
    }
        return cnt;
    }

    /**
     * 清理调度任务
     */
    public static void removeAllQS() {
        synchronized (quartzScheduleInfoList) {
            QuartzScheduleInfoList.quartzScheduleInfoList.clear();
        }
    }
}
