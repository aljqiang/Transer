<%@ page language="java" import="com.sunrise.framework.core.ApplicationManager" pageEncoding="UTF-8" %>
<%@ page import="com.sunrise.cgb.schedule.impl.QuartzScheduleEngine" %>
<%@ page import="com.sunrise.cgb.schedule.ScheduleInfo" %>
<html>
<head>
</head>
<body>

<%
    QuartzScheduleEngine qse = new QuartzScheduleEngine();

    ScheduleInfo DatafileCleanTask = new ScheduleInfo(-999,0,"0 0 22 * * ? *");
%>
<h1 align="center">清理任务调度作业已经成功停止! <%qse.cancelTaskPlan(DatafileCleanTask);%></h1>
<h1 align="center"><a href="jobs_info.jsp" style="text-decoration: none;">【返回】</a></h1>
</body>
</html>