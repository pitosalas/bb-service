<%@ page import="com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.stats.StatsOutputHelper,
                 com.salas.bbservice.domain.User,
                 java.text.DateFormat"%>
<%@ page import="com.salas.bbservice.utils.StringUtils"%>
<%@ page import="com.salas.bbservice.domain.dao.ValueCount"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Users</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/commands.css"/>
  </head>
  <style type="text/css">

    div.data {
      width: 600px;
    }

    #trends { width: 300; }
  </style>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <body>
    <%
      DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

      int max = StringUtils.toInt(request.getParameter("max"), 0);
      int days = StringUtils.toInt(request.getParameter("days"), 7);

      User[] users = StatsProcessor.getRecentUsers(max);
      ValueCount[] regs = StatsProcessor.getRecentRegistrationTrends(days);
    %>
    <div id="page">
      <div id="header">
        <div id="left"></div>
        <div id="right"></div>
        <h1><span>BlogBridge</span></h1>
        <h2><span>Administrative Console</span></h2>
      </div>

      <%@ include file="menu.html"%>

      <div id="content">

        <div id="tips">
          Parameters:<br>
          &nbsp;&nbsp;days=X (default 7) - number of days to display<br>
          &nbsp;&nbsp;max=N (default 0 -- all from today) - number of user records to display
        </div>

        <div class="data">
          <table class="datatable" id="trends">
            <thead>
              <td>Date</td>
              <td>Registrations</td>
            </thead>
            <%
              for (int i = 0; i < regs.length; i++)
              {
                ValueCount reg = regs[i];
                out.print("<tr>");
                out.print("<td>" + reg.getValue() + "</td>");
                out.print("<td>" + reg.getCount() + "</td>");
                out.print("</tr>");
              }
            %>
          </table>

          <table class="datatable">
            <thead>
              <td>#</td>
              <td>Full name</td>
              <td>E-mail</td>
              <td>Activated</td>
              <td>Notification</td>
              <td>Date</td>
            </thead>
            <%
              for (int i = 0; i < users.length; i++)
              {
                User user = users[i];
                out.print("<tr>");
                out.print("<td>" + i + "</td>");
                out.print("<td align='left'>" + user.getFullName().replaceAll("\\s", "&nbsp;") + "</td>");
                out.print("<td align='left'>" + user.getEmail() + "</td>");
                out.print("<td>" + (user.isActivated() ? "Yes" : "No") + "</td>");
                out.print("<td>" + (user.isNotifyOnUpdates() ? "Yes" : "No") + "</td>");
                out.print("<td>" + dateFormat.format(user.getRegistrationDate()) + "</td>");
                out.print("</tr>");
              }
            %>
          </table>
        </div>
      </div>
    </div>
  </body>
</html>