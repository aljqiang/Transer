package com.sunrise.cgb.common.util;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:文件操作
 */
public class FileWalker {

	private static Log log = LogFactory.getLog(FileWalker.class);
	/**
	 * 特征扫描接口
	 * @author 翻书侠
	 *
	 */
	public static interface IScanChecker{
		/**
		 * 文件是否满足特征
		 * @param aimFile
		 * @return
		 */
		public boolean checkFile(File aimFile);
		/**
		 * 文件夹是否满足特征
		 * @param aimFolder
		 * @return
		 */
		public boolean checkFolder(File aimFolder);
	}

	/**
	 * 捕捉处理器
	 * @author 翻书侠
	 *
	 */
	public static interface ICatchHandler{
		/**
		 * 文件处理
		 * @param aimFile
		 */
		public void fileOperation(File aimFile);
		/**
		 * 文件夹处理
		 * @param folder
		 */
		public void folderOperation(File folder);
	}
	
	//扫描基础目录
	private String baseFilePath;
	//扫描目标特征辨别器
	private IScanChecker scaner;
	//扫描到目标处理器
	private ICatchHandler handler;
	
	/**
	 * 无参构造函数
	 */
	public FileWalker(){
		this.baseFilePath = "";
		this.scaner = null;
		this.handler = null;
	}
	
	/**
	 * 初始化基本目录扫描的构造函数
	 * @param baseFilePath
	 * @param scaner
	 * @param handler
	 */
	public FileWalker(String baseFilePath,IScanChecker scaner,ICatchHandler handler){
		this.baseFilePath = baseFilePath;
		this.scaner = scaner;
		this.handler = handler;
	}
	
	public void setBaseFilePath(String baseFilePath) {
		this.baseFilePath = baseFilePath;
	}

	public void setScaner(IScanChecker scaner) {
		this.scaner = scaner;
	}

	public void setHandler(ICatchHandler handler) {
		this.handler = handler;
	}

	/**
	 * 开始扫描初始的数据文件实例
	 */
	public void walkAndScan(){
		File baseFile = new File(baseFilePath);
		log.info("=====开始扫描指定文件实例,路径:"+baseFilePath);
		if(!baseFile.exists()){
			log.info("目标不存在,退出扫描.");
			return;
		}
		scanFileInstance(baseFile);
		log.info("=====结束扫描指定文件实例,路径:"+baseFilePath);		
	}
	
	/**
	 * 扫描指定文件实例
	 * @param file
	 */
	public void scanFileInstance(File instance){
		if(instance.isDirectory())
			if(scaner.checkFolder(instance))
				listAimFolder(instance);
			else
				for(File childFile:instance.listFiles())
				{
					scanFileInstance(childFile);
				}	
		else if(scaner.checkFile(instance))
			handler.fileOperation(instance);
	}
	
	/**
	 * 遍历指定目录
	 * @param file
	 */
	public void listAimFolder(File folder){
		for(File childFile:folder.listFiles())
		{
			if(childFile.isDirectory())
				listAimFolder(childFile);
			else
				handler.fileOperation(childFile);
		}	
		handler.folderOperation(folder);
	}
}
