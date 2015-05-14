package com.sunrise.cgb.core.strategy;

import com.sunrise.cgb.core.IQueueScheduleStrategy;
import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.queue.AbstractTaskEntity;
import com.sunrise.cgb.queue.SendTaskEntity;

/**
 * 积分任务调度策略
 * User: Larry
 * Date: 2014-10-28
 * Time: 10:03
 * Version: 1.0
 */

public class PointQueueScheduleStrategy implements IQueueScheduleStrategy{

    @Override
    public void addTaskEntity(AbstractTaskEntity entity) {
        TransHelper.transer()
                .taskQueue()
                .pointTaskQueue()
                .add2WaitingQueue((SendTaskEntity) entity);
    }

    @Override
    public QueueScheduleResult canSend() {
        QueueScheduleResult result = new QueueScheduleResult();
        //从队列获取任务
        if(TransHelper.transer().checkPointLoad()){
            SendTaskEntity task = TransHelper.transer()
                    .taskQueue()
                    .pointTaskQueue()
                    .popTaskEntity();
            result.setStatus((task!=null));
            if(!result.canSend())
                TransHelper.transer().decreasePointCnt();
            else
                result.setTaskEntity(task);
        }
        return result;
    }
}
