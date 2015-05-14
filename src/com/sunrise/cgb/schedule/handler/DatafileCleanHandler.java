package com.sunrise.cgb.schedule.handler;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.common.util.FileWalker;
import com.sunrise.cgb.common.util.FileWalker.ICatchHandler;
import com.sunrise.cgb.common.util.FileWalker.IScanChecker;
import com.sunrise.cgb.schedule.ITaskHandler;
import com.sunrise.cgb.schedule.ScheduleInfo;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class DatafileCleanHandler implements ITaskHandler {
	
	private static Log log = LogFactory.getLog(DatafileCleanHandler.class);
	
	private List<String> dataFileDirs;	
	
	public DatafileCleanHandler(){

		String ufs_edms_dir = ProgramConfig.EDMS.getConfig(ConstantKey.ufs_send_dir, "e:/UFS/CMM/save/CMM/") + 
							  			ProgramConfig.EDMS.getConfig(ConstantKey.ufs_task_seq, "90001") + "/";
		
		String ufs_smsp_dir = ProgramConfig.SMSG.getConfig(ConstantKey.ufs_send_dir, "e:/UFS/CMM/save/CMM/") + 
		  		  						ProgramConfig.SMSG.getConfig(ConstantKey.ufs_task_seq, "90001") + "/";

        // 返签账额不需要定时清理
        /*String ufs_pay_dir = ProgramConfig.PAY.getConfig(ConstantKey.ufs_send_dir, "e:/UFS/CMM/save/CMM/") +
                ProgramConfig.PAY.getConfig(ConstantKey.ufs_task_seq, "90001") + "/";*/
		
		this.dataFileDirs = new ArrayList<String>();
		this.dataFileDirs.add(ufs_edms_dir);
		this.dataFileDirs.add(ufs_smsp_dir);
//		this.dataFileDirs.add(ufs_pay_dir);
	}

	@Override
	public Integer getTaskType() {
		return 0;
	}

	@Override
	public void handle(ScheduleInfo info) {
		
		for(String dataFileDir:dataFileDirs)
		{
			FileWalker dataFilesWalker = new FileWalker(dataFileDir,
																			  new UfsDatafilesScaner(dataFileDir),
																			  new UfsDatafilesCatchHandler());
			dataFilesWalker.walkAndScan();
		}
	}
	
	/**
	 * UFS文件特征搜索器
	 * @author 翻书侠
	 *
	 */
	private static class UfsDatafilesScaner implements IScanChecker{
		
		private String baseDir;
		private int currDate;
		private DateFormat fmt;
		
		public UfsDatafilesScaner(String baseDir){
			try{
				this.baseDir = baseDir;
				this.fmt = new SimpleDateFormat("yyyyMMdd");
				this.currDate = Integer.parseInt(fmt.format(new Date()));
			}catch(Exception ex){
				log.error("过期ufs数据文件初始化当前日期出错.",ex);
			}
		}
		
		@Override
		public boolean checkFile(File aimFile) {
			//不识别文件名
			return false;
		}

		@Override
		public boolean checkFolder(File aimFolder) {
			try{
				int dateMark = Integer.parseInt(aimFolder.getName());
				if(currDate>dateMark)
					return true;
			}catch(Exception ex){
				log.error("判断文件:"+aimFolder.getName()+"是否冗余文件出错.",ex);
			}
			return false;
		}
		
	}
	
	/**
	 * UFS数据文件处理器
	 * @author 翻书侠
	 *
	 */
	private static class UfsDatafilesCatchHandler implements ICatchHandler{
		@Override
		public void fileOperation(File aimFile) {
			aimFile.delete();			
		}

		@Override
		public void folderOperation(File folder) {
			folder.delete();
		}
		
	}
}
