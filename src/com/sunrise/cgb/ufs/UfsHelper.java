package com.sunrise.cgb.ufs;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sunrise.cgb.ufs.impl.PayTaskInfoCreator;
import com.sunrise.cgb.ufs.impl.PointTaskInfoCreator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ml.bdbm.client.FTSRequest;
import com.ml.bdbm.client.FTSResponse;
import com.ml.bdbm.client.FTSTaskClient;
import com.ml.bdbm.client.TransferFile;
import com.sunrise.cgb.common.ConstantKey;
import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.SendFileResult;
import com.sunrise.cgb.ufs.impl.EdmsTaskInfoCreator;
import com.sunrise.cgb.ufs.impl.SmsgTaskInfoCreator;
import com.sunrise.foundation.file.FileUtil;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class UfsHelper {

    private static Log log = LogFactory.getLog(UfsHelper.class);

    public final static int MAX_SEQ_NUM = 999999;  // 流水号最大值(6位)

//    public static Integer seqNum = 0;  // 任务类型流水号

    public final static DecimalFormat numFmt = new DecimalFormat("000000");  // 流水号格式

    public static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyyMMdd");

    /**
     * 发送文件参数构造器
     */
    private static final Map<Integer, ICreateTaskInfo> taskInfoCreator = new HashMap<Integer, ICreateTaskInfo>();

    /**
     * 发送数据文件到EDMS系统
     *
     * @param info
     * @return
     */
    public static SendFileResult sendEdmsDataFile(UfsTaskInfo info) {
        String ip = ProgramConfig.EDMS.getConfig(ConstantKey.ufs_ip, "");
        int port = ProgramConfig.EDMS.getConfig(ConstantKey.ufs_port, 32792);
        String taskId = ProgramConfig.EDMS.getConfig(ConstantKey.ufs_task_id, "P8VEH7fK");
        int timeout = ProgramConfig.EDMS.getConfig(ConstantKey.ufs_timeout, 99999);
        return UfsHelper.sendDataFile(taskId, ip, port, timeout, info);
    }

    /**
     * 发送数据文件到SMSG系统
     *
     * @param info
     * @return
     */
    public static SendFileResult sendSmsgDataFile(UfsTaskInfo info) {
        String ip = ProgramConfig.SMSG.getConfig(ConstantKey.ufs_ip, "");
        int port = ProgramConfig.SMSG.getConfig(ConstantKey.ufs_port, 32792);
        String taskId = ProgramConfig.SMSG.getConfig(ConstantKey.ufs_task_id, "P8VEH7fK");
        int timeout = ProgramConfig.SMSG.getConfig(ConstantKey.ufs_timeout, 99999);
        return UfsHelper.sendDataFile(taskId, ip, port, timeout, info);
    }

    /**
     * 发送数据文件到PAY系统
     *
     * @param info
     * @return
     */
    public static SendFileResult sendPayDataFile(UfsTaskInfo info) {
        String ip = ProgramConfig.PAY.getConfig(ConstantKey.ufs_ip, "");
        int port = ProgramConfig.PAY.getConfig(ConstantKey.ufs_port, 32792);
        String taskId = ProgramConfig.PAY.getConfig(ConstantKey.ufs_task_id, "PBWK3WUB");
        int timeout = ProgramConfig.PAY.getConfig(ConstantKey.ufs_timeout, 99999);

        return UfsHelper.paySendDataFile(taskId, ip, port, timeout, info);
    }

    /**
     * 发送数据文件到POINT系统
     */
    public static SendFileResult sendPointDataFile(UfsTaskInfo info) {
        String ip = ProgramConfig.POINT.getConfig(ConstantKey.ufs_ip, "");
        int port = ProgramConfig.POINT.getConfig(ConstantKey.ufs_port, 32792);
        String taskId = ProgramConfig.POINT.getConfig(ConstantKey.ufs_task_id, "PBWK3WUB");
        int timeout = ProgramConfig.POINT.getConfig(ConstantKey.ufs_timeout, 99999);

        return UfsHelper.pointSendDataFile(taskId, ip, port, timeout, info);
    }

    /**
     * 发送数据文件到指定渠道系统
     *
     * @param taskId
     * @param aimIp
     * @param transPort
     * @param timeout
     * @param info
     * @return
     */
    public static SendFileResult sendDataFile(String taskId, String aimIp, int transPort, int timeout, UfsTaskInfo info) {
        SendFileResult result = new SendFileResult();
        FTSTaskClient client = new FTSTaskClient(aimIp, transPort);

        TransferFile file = new TransferFile(info.getSrcFile(), info.getSrcDir(), info.getAimFile(), info.getAimDir());
        FTSRequest request = new FTSRequest(taskId, new TransferFile[]{file});
        FTSResponse resp = null;
        try {
            resp = client.callTask(request, timeout);
        } catch (Exception e) {
            result.setCode("runtimeErr");
            result.setMsg("调用callTask时出错:" + e.getMessage());
            result.setSuccess(false);
            return result;
        }
        result.setCode(resp.getErrorCode());
        result.setMsg(resp.getErrorMessage());
        result.setId(resp.getMessageId());
        if (result.getCode().equals("0000")) // 0000代码发送成功
            result.setSuccess(true);
        else
            result.setSuccess(false);

        return result;
    }

    /**
     * 发送数据文件到调账系统
     *
     * @param taskId
     * @param aimIp
     * @param transPort
     * @param timeout
     * @param info
     * @return
     */
    public static SendFileResult paySendDataFile(String taskId, String aimIp, int transPort, int timeout, UfsTaskInfo info) {
        SendFileResult result = new SendFileResult();
        FTSTaskClient client = new FTSTaskClient(aimIp, transPort);

        String dateStr = dateFmt.format(new Date());
        String srcDir = ProgramConfig.PAY.getConfig("ufs_send_dir", "e:/UFS/CMM/save/CMM/") + dateStr + "/";

        String fileName = "GCPXCMSP.END";

        List<TransferFile> list = new ArrayList<TransferFile>();

        list.add(new TransferFile(info.getSrcFile(), info.getSrcDir(), info.getAimFile(), info.getAimDir()));
        list.add(new TransferFile(fileName, srcDir, fileName, info.getAimDir()));

        log.debug("====================================.END文件为" + srcDir + fileName);

        FTSRequest request = new FTSRequest(taskId, list.toArray(new TransferFile[list.size()]));
        FTSResponse resp = null;
        try {
            resp = client.callTask(request, timeout);
        } catch (Exception e) {
            result.setCode("runtimeErr");
            result.setMsg("调用callTask时出错:" + e.getMessage());
            result.setSuccess(false);
            return result;
        }
        result.setCode(resp.getErrorCode());
        result.setMsg(resp.getErrorMessage());
        result.setId(resp.getMessageId());
        if (result.getCode().equals("0000"))
            result.setSuccess(true);
        else {
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 发送数据文件到积分系统
     */
    public static SendFileResult pointSendDataFile(String taskId, String aimIp, int transPort, int timeout, UfsTaskInfo info) {
        SendFileResult result = new SendFileResult();
        FTSTaskClient client = new FTSTaskClient(aimIp, transPort);

        List<TransferFile> list = new ArrayList<TransferFile>();

        // 获取文件个数，并实例化同等个数的实例对象
        for (int i = 0; i < info.getSrcFiles().length; i++) {

//            log.info("=======================第"+i+"个文件名为："+info.getSrcFiles()[i]);

            list.add(new TransferFile(info.getSrcFiles()[i], info.getSrcDir(), info.getAimFiles()[i], info.getAimDir()));
        }

        FTSRequest request = new FTSRequest(taskId, list.toArray(new TransferFile[list.size()]));
        FTSResponse resp = null;
        try {
            resp = client.callTask(request, timeout);
        } catch (Exception e) {
            result.setCode("runtimeErr");
            result.setMsg("调用callTask时出错:" + e.getMessage());
            result.setSuccess(false);
            return result;
        }
        result.setCode(resp.getErrorCode());
        result.setMsg(resp.getErrorMessage());
        result.setId(resp.getMessageId());
        if (result.getCode().equals("0000")) // 0000代码发送成功
            result.setSuccess(true);
        else
            result.setSuccess(false);

        return result;
    }

    /**
     * 创建UFS需要的文件路径和文件目录
     *
     * @param dataFilePath
     * @return
     */
    public static UfsTaskInfo createTaskInfo(int taskType, String dataFilePath) {

        //获取发送文件参数
        UfsTaskInfo taskInfo = UfsHelper.taskInfoCreator.get(taskType).createTaskInfo();

        // 积分类型发送多个文件逻辑
        if (taskType == ProgramConfig.TASK_TYPE_POINT) {

            int seqNum;  // 任务类型流水号

            int fileCount = new File(taskInfo.getSrcDir()).listFiles().length;

            if (fileCount > 0) {
                seqNum = UfsHelper.fileReader(taskInfo.getSrcDir()).length + 1;
            } else {
                seqNum = 1;
            }

            String[] str = UfsHelper.fileReader(dataFilePath);

            String[] fileName = new String[str.length];

            for (int i = 0; i < str.length; i++) {

                File dataFile = new File(str[i]);
                File destFile = new File(taskInfo.getSrcDir() + taskInfo.getSrcFile() + "_" + numFmt.format(seqNum) + ".txt");
                fileName[i] = taskInfo.getSrcFile() + "_" + numFmt.format(seqNum) + ".txt";

                log.debug("开始将数据文件:" + dataFile.getAbsolutePath() + "转移到目录" + destFile.getAbsolutePath());
                //dataFile.renameTo(destFile);
                boolean result = FileUtil.copy(dataFile, destFile);
                if (!result) {
                    log.debug("转移数据文件:" + dataFile.getAbsolutePath() + "失败");
                }

                seqNum++;
            }

            taskInfo.setSrcFiles(fileName);
            taskInfo.setAimFiles(fileName);
        } else if (taskType == ProgramConfig.TASK_TYPE_PAY) {
            File dataFile = new File(dataFilePath);
            File destFile = new File(taskInfo.getSrcDir() + taskInfo.getSrcFile());
            log.debug("开始将数据文件:" + dataFile.getAbsolutePath() + "转移到目录" + destFile.getAbsolutePath());

            boolean result = FileUtil.copy(dataFile, destFile);
            try {
                String dateStr = dateFmt.format(new Date());
                String srcDir = ProgramConfig.PAY.getConfig("ufs_send_dir", "e:/UFS/CMM/save/CMM/") + "/" + dateStr + "/";

                File markerFile = new File(srcDir, "GCPXCMSP.END");
                if (!markerFile.exists())
                    markerFile.createNewFile();
            } catch (IOException ex) {
                log.debug("============================调账文件生成.END文件失败" + ex.getMessage());
            }

            if (!result) {
                log.debug("转移数据文件:" + dataFile.getAbsolutePath() + "失败");
            }
        } else {
            //复制移动文件到发送目录
            //20130625 文件路径改为直接获取 File dataFile = new File(ProgramConfig.GATEWAY.getConfig(ConstantKey.datafile_root, "")+"/"+dataFilePath);
            File dataFile = new File(dataFilePath);
            File destFile = new File(taskInfo.getSrcDir() + taskInfo.getSrcFile());
            log.debug("开始将数据文件:" + dataFile.getAbsolutePath() + "转移到目录" + destFile.getAbsolutePath());
            //dataFile.renameTo(destFile);
            boolean result = FileUtil.copy(dataFile, destFile);
            if (!result) {
                log.debug("转移数据文件:" + dataFile.getAbsolutePath() + "失败");
            }
        }

        return taskInfo;
    }

    /**
     * 遍历文件目录
     */
    private static String[] fileReader(String pathInput) {
        File file = new File(pathInput);
        File[] files = file.listFiles();

        String[] filepathList = null;

        try {
            int k = 0;
            String filePath = "";

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
//                    log.info("=============================文件路径：\n" + files[i].getPath().replace("\\","/"));
                    if (k != 0) {
                        filePath += ",";
                    }

                    filePath += files[i].getPath().replace("\\", "/");
                    k++;

                } else if (files[i].isDirectory()) {
                    log.error("===================找不到文件====================");
                }
            }

            filepathList = filePath.split(",");

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return filepathList;
    }

    /**
     * 注册文件参数创建处理器
     *
     * @param taskType
     * @param creator
     */
    public static void registerTaskInfoCreator(int taskType, ICreateTaskInfo creator) {
        UfsHelper.taskInfoCreator.put(taskType, creator);
    }

    /**
     * 初始化固定的文件参数创建器
     */
    public static void init() {
        UfsHelper.registerTaskInfoCreator(ProgramConfig.TASK_TYPE_EDMS, new EdmsTaskInfoCreator());
        UfsHelper.registerTaskInfoCreator(ProgramConfig.TASK_TYPE_SMSG, new SmsgTaskInfoCreator());
        UfsHelper.registerTaskInfoCreator(ProgramConfig.TASK_TYPE_PAY, new PayTaskInfoCreator());
        UfsHelper.registerTaskInfoCreator(ProgramConfig.TASK_TYPE_POINT, new PointTaskInfoCreator());
    }
}
