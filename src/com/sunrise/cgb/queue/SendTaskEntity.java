package com.sunrise.cgb.queue;

import java.text.DecimalFormat;
import java.util.Date;

import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.IConnectorResponse;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class SendTaskEntity extends AbstractTaskEntity {

    private String srcDir;//源WEB系统的文件路径
    private long taskId;//日志ID
    private String fileName;//UFS传送文件名
    private int taskType;//任务类型
    private long packetId;//拆分包ID
    private Date planSendTime;//计划发送时间
    private Date sendTime;//实际发送时间
    private Date endTime;//实际结束时间
    private int resendTime;//重发次数
    private int result;//发送结果
    private String errCode;//错误代码
    private String errCause;//错误描述
    private String respCode;//活动跟中代码
    private String seqNum;//柜面网关通讯流水号

    private static final DecimalFormat fileRecordNumFmt = new DecimalFormat("0000000000");
    private long fileRecordNum;//电邮需要用到的记录数

    public SendTaskEntity() {
        this.fileName = "";
        this.errCause = "";
        this.respCode = "";
        this.errCode = "";
        this.seqNum = "";
    }

    @Override
    public String toString() {
        String msg = "\r\n========================\r\n";

        String taskType=null;

        if(this.taskType==ProgramConfig.TASK_TYPE_SMSG){
            taskType="SMSG";
        }else if(this.taskType==ProgramConfig.TASK_TYPE_EDMS){
            taskType="EDMS";
        }else if(this.taskType==ProgramConfig.TASK_TYPE_PAY){
            taskType="PAY";
        }else{
            taskType="POINT";
        }

        msg += "传输任务类型:" +taskType + "\r\n";
        msg += "执行结果:" + ((this.result == IConnectorResponse.RESULT_SUCCESS) ? "成功" : "失败") + "\r\n";
        msg += "错误描述:" + this.errCode + "," + this.errCause + "\r\n";
        msg += "拆分包ID:" + this.packetId + "\r\n";
        msg += "源路径:" + this.packetId + "\r\n";
        msg += "发送路径:" + this.fileName + "\r\n";
        msg += "========================\r\n";

        return msg;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getfileName() {
        return fileName;
    }

    public void setfileName(String ufsFileName) {
        this.fileName = ufsFileName;
    }

    public String getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(String srcDir) {
        this.srcDir = srcDir;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public long getPacketId() {
        return packetId;
    }

    public void setPacketId(long packetId) {
        this.packetId = packetId;
    }

    public Date getPlanSendTime() {
        return planSendTime;
    }

    public void setPlanSendTime(Date planSendTime) {
        this.planSendTime = planSendTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getResendTime() {
        return resendTime;
    }

    public void setResendTime(int resendTime) {
        this.resendTime = resendTime;
    }


    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrCause() {
        return errCause;
    }

    public void setErrCause(String errCause) {
        this.errCause = errCause;
    }

    public String getFileRecordNumStr() {
        return fileRecordNumFmt.format(fileRecordNum);
    }

    public long getFileRecordNum() {
        return fileRecordNum;
    }

    public void setFileRecordNum(long fileRecordNum) {
        this.fileRecordNum = fileRecordNum;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }

    @Override
    public int getTaskHandleType() {
        return super.HANDLE_TYPE_NORMAL;
    }
}
