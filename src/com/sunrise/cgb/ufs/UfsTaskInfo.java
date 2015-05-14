package com.sunrise.cgb.ufs;
/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class UfsTaskInfo {

	private String srcFile;
	private String srcDir;
	private String aimFile;
	private String aimDir;

    private String[] srcFiles;  // 此数组用于存放积分多个文件
    private String[] aimFiles;  // 此数组用于存放积分多个文件

	public String getSrcFile() {
		return srcFile;
	}
	public void setSrcFile(String srcFile) {
		this.srcFile = srcFile;
	}
	public String getSrcDir() {
		return srcDir;
	}
	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}
	public String getAimFile() {
		return aimFile;
	}
	public void setAimFile(String aimFile) {
		this.aimFile = aimFile;
	}
	public String getAimDir() {
		return aimDir;
	}
	public void setAimDir(String aimDir) {
		this.aimDir = aimDir;
	}

    public String[] getSrcFiles() {
        return srcFiles;
    }

    public void setSrcFiles(String[] srcFiles) {
        this.srcFiles = srcFiles;
    }

    public String[] getAimFiles() {
        return aimFiles;
    }

    public void setAimFiles(String[] aimFiles) {
        this.aimFiles = aimFiles;
    }
}
