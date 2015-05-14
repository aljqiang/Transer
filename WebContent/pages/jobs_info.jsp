<%@ page language="java" import="com.sunrise.framework.core.ApplicationManager" pageEncoding="UTF-8" %>
<%@ page import="com.sunrise.cgb.core.CGBTranser" %>
<%@ page import="com.sunrise.cgb.schedule.impl.QuartzScheduleInfoMap" %>
<html>
<head>
</head>
<body>

<%
    CGBTranser transer = CGBTranser.create();

    int T_DC_Cnt =QuartzScheduleInfoMap.getAllQSByTID(0);

    int SMSG_Cnt=transer.showSmsgWorkerCnt();
    int EDMS_Cnt=transer.showEdmsWorkerCnt();
    int PAY_Cnt=transer.showPayWorkerCnt();
    int POINT_Cnt=transer.showPointWorkerCnt();
%>

<h1 align="center">当前系统还有[<font color="red"><%=T_DC_Cnt%></font>]个定时清理作业调度任务.!
    <a href="jobs_cancel.jsp" style="text-decoration: none;">【停止调度作业】</a></h1>

<h1 align="center">当前系统还有[<font color="red"><%=SMSG_Cnt%></font>]个短信发送任务正在执行.!</h1>
<h1 align="center">当前系统还有[<font color="red"><%=EDMS_Cnt%></font>]个电邮发送任务正在执行.!</h1>
<h1 align="center">当前系统还有[<font color="red"><%=PAY_Cnt%></font>]个调账发送任务正在执行.!</h1>
<h1 align="center">当前系统还有[<font color="red"><%=POINT_Cnt%></font>]个积分发送任务正在执行.!</h1>
</body>
</html>