package com.sunrise.cgb.ufs.impl;

import java.io.File;
import java.util.Date;

import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.ufs.ICreateTaskInfo;
import com.sunrise.cgb.ufs.UfsTaskInfo;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class EdmsTaskInfoCreator extends ICreateTaskInfo {

	@Override
	public int getTaskType() {
		return ProgramConfig.TASK_TYPE_EDMS;
	}
	
	public EdmsTaskInfoCreator() {
		String dir = ProgramConfig.EDMS.getConfig(ConstantKey.ufs_send_dir, "e:/UFS/CMM/save/CMM/") + 
		  		  		  ProgramConfig.EDMS.getConfig(ConstantKey.ufs_task_seq, "90001") + "/";
		File dirFolder = new File(dir);
		if(!dirFolder.exists())
			dirFolder.mkdirs();
	}

	@Override
	public UfsTaskInfo createTaskInfo() {
		UfsTaskInfo info = new UfsTaskInfo();
		//创建当前日期标识
		String dateStr = this.dateFmt.format(new Date());
		//构造源系统发送路径
		String srcDir = ProgramConfig.EDMS.getConfig(ConstantKey.ufs_send_dir, "e:/UFS/CMM/save/CMM/") + 
							  ProgramConfig.EDMS.getConfig(ConstantKey.ufs_task_seq, "90001") + "/" + 
							  dateStr + "/";
		//判断是否需要创建发送目录
		synchronized(this.dateFmt){
			File srcDirFolder = new File(srcDir);
			if(!srcDirFolder.exists())
				srcDirFolder.mkdirs();
			this.dateFmt.notifyAll();
		}
		info.setSrcDir(srcDir);
		//构造目标系统接收路径
		String aimDir = ProgramConfig.EDMS.getConfig(ConstantKey.ufs_save_dir, "e:/UFS/CMM/save/CMM/") + 
								ProgramConfig.EDMS.getConfig(ConstantKey.ufs_task_seq, "90001") + "/" +
								dateStr + "/";
		info.setAimDir(aimDir);
		//构造源系统发送文件名
		synchronized(this.numFmt){
			if(this.seqNum >= this.MAX_SEQ_NUM)
				this.seqNum = 0;
			this.seqNum++;			

			String srcFileName = ProgramConfig.EDMS.getConfig(ConstantKey.ufs_file_name, "CMM_EDMS_") + 
											ProgramConfig.EDMS.getConfig(ConstantKey.ufs_task_seq, 90001) + "_" +
											dateStr + "_" + numFmt.format(this.seqNum) + ".zip";
			info.setSrcFile(srcFileName);
			info.setAimFile(srcFileName);
			
			this.numFmt.notifyAll();
		}
		return info;
	}

}
