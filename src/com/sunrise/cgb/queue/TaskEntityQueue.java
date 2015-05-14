package com.sunrise.cgb.queue;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class TaskEntityQueue {

	private static TaskEntityQueue taskQueue;
	
	private final BlockQueue<SendTaskEntity> smsgTaskQueue = new BlockQueue<SendTaskEntity>();
	
	private final BlockQueue<SendTaskEntity> edmsTaskQueue = new BlockQueue<SendTaskEntity>();

	private final BlockQueue<SendTaskEntity> respTaskQueue = new BlockQueue<SendTaskEntity>();
	
	private final BlockQueue<QueryTaskEntity> queryTaskQueue = new BlockQueue<QueryTaskEntity>();
	
	private final BlockQueue<SendTaskEntity> payTaskQueue = new BlockQueue<SendTaskEntity>();

    // 新增积分系统发送任务队列容器
	private final BlockQueue<SendTaskEntity> pointTaskQueue = new BlockQueue<SendTaskEntity>();

	private TaskEntityQueue(){
		
	}
	/**
	 * 获取任务队列管理容器
	 * @return
	 */
	public static synchronized TaskEntityQueue queue(){
		if(null == taskQueue)
			taskQueue = new TaskEntityQueue();
		return taskQueue;
	}
	/**
	 * 获取短信任务队列
	 * @return
	 */
	public BlockQueue<SendTaskEntity> smsgTaskQueue(){
		return this.smsgTaskQueue;
	}
	/**
	 * 获取电邮任务队列
	 * @return
	 */
	public BlockQueue<SendTaskEntity> edmsTaskQueue(){
		return this.edmsTaskQueue;
	}
	/**
	 * 获取反馈任务队列
	 * @return
	 */
	public BlockQueue<SendTaskEntity> respTaskQueue(){
		return this.respTaskQueue;
	}
	/**
	 * 获取查询任务队列
	 * @return
	 */
	public BlockQueue<QueryTaskEntity> queryTaskQueue(){
		return this.queryTaskQueue;
	}
	/**
	 * 获取调帐任务队列
	 * @return
	 */
	public BlockQueue<SendTaskEntity> payTaskQueue(){
		return this.payTaskQueue;
	}

    /**
     * 获取积分任务队列
     */
    public BlockQueue<SendTaskEntity> pointTaskQueue(){
        return  this.pointTaskQueue;
    }
}
