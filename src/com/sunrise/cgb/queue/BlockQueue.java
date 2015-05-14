package com.sunrise.cgb.queue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class BlockQueue<T> {
	
	private final List<T> queue = new ArrayList<T>();
	
	public BlockQueue(){
		
	}
	
	/**
	 * 添加处理完毕的任务
	 * @param entity
	 */
	public void add2WaitingQueue(T entity){
		synchronized(this.queue){
			this.queue.add(entity);
			this.queue.notifyAll();
		}
	}
	
	/**
	 * 获取队列元素
	 * @return
	 */
	public T popTaskEntity(){
		synchronized(this.queue){
			T entity;
			if(0 == this.queue.size())
				entity = null;
			else
			    entity = this.queue.remove(0);
			this.queue.notifyAll();
			return entity;
		}
	}
}
