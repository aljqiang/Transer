<%@ page language="java" import="com.sunrise.framework.core.ApplicationManager" pageEncoding="UTF-8" %>
<%@ page import="com.sunrise.cgb.schedule.handler.DatafileCleanHandler" %>
<%@ page import="com.sunrise.cgb.schedule.ScheduleManager" %>
<%@ page import="com.sunrise.cgb.schedule.ScheduleInfo" %>
<html>
<head>
</head>
<body>

<%
    ScheduleManager.registerTaskHandler(new DatafileCleanHandler());
    ScheduleManager.engine().lauchTask(new ScheduleInfo(-999,0,"0 0 22 * * ? *"));
%>

<h1 align="center">suc</h1>
</body>
</html>