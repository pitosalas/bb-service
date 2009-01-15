<%@ page import="com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.domain.User,
                 java.text.DateFormat,
                 com.salas.bbservice.utils.StringUtils"%>
<%@ page contentType="application/vnd.ms-excel;charset=UTF-8" language="java" %>
<%
  DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

  int offset = 0;
  int max = Integer.MAX_VALUE;
  User[] users = StatsProcessor.getUsers(offset, max, null);

  for (int i = 0; i < users.length; i++)
  {
    User user = users[i];
    out.print("\"" + StringUtils.escapeForCDL(user.getFullName()) + "\",");
    out.print("\"" + StringUtils.escapeForCDL(user.getEmail()) + "\",");
    out.print("\"" + user.isActivated() + "\",");
    out.print("\"" + user.isNotifyOnUpdates() + "\",");
    out.println("\"" + dateFormat.format(user.getRegistrationDate()) + "\"");
  }
%>