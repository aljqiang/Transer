package com.sunrise.cgb.schedule.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 调度任务存储类
 * User: Larry
 * Date: 2015-02-06
 * Time: 14:55
 * Version: 1.0
 */

public class QuartzScheduleInfoMap {
    private static final Log log= LogFactory.getLog(QuartzScheduleInfoMap.class);

    /**
     * 调度任务存储集合
     */
    private static final Map<Long,Integer> map=new HashMap<Long,Integer>();

    /**
     * 添加调度任务
     */
    public static void addQS(long subpackage_id,int task_type){
        synchronized(map){
            QuartzScheduleInfoMap.map.put(subpackage_id,task_type);
        }
    }

    /**
     * 移除调度任务
     */
    public static void removeQS(long subpackage_id){
        synchronized(map){
            QuartzScheduleInfoMap.map.remove(subpackage_id);
        }
    }

    /**
     * 获取调度任务
     */
    public static int getAllQSByTID(int task_type) {
        int cnt=0;

        synchronized(map){
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry=(Map.Entry)it.next();

                if(task_type == entry.getValue()){
                    cnt++;
                }
            }
        }

        return cnt;
    }

    /**
     * 清理调度任务
     */
    public static void removeAllQS(){
        synchronized(map){
            QuartzScheduleInfoMap.map.clear();
        }
    }
}
