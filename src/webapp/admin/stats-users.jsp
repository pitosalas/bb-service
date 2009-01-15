<%@ page import="com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.domain.User,
                 java.text.DateFormat"%>
<%@ page import="com.salas.bbservice.utils.StringUtils"%>
<%@ page import="com.salas.bbservice.persistence.DaoConfig"%>
<%@ page import="com.salas.bbservice.persistence.IUserDao"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Users</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/paging.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/commands.css"/>
  </head>
  <style type="text/css">

    div.data {
      width: 600px;
    }
  </style>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <script type="text/javascript">
  function deleteUser(id) { submitCommand("delete", id, "Are you sure to delete this user?"); }
  function deactivateUser(id) { submitCommand("deactivate", id); return false; }
  function activateUser(id) { submitCommand("activate", id); }

  function submitCommand(cmd, id, confirmation) {
    if (!confirmation || confirm(confirmation)) {
      cmdForm.cmd.value = cmd;
      cmdForm.id.value = id;
      cmdForm.submit();
    }
  }
  </script>

  <body>
    <%
      DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

      String error = null;
      String command = request.getParameter("cmd");
      if (command != null)
      {
        int userId = StringUtils.toInt(request.getParameter("id"), -1);

        if (userId != -1)
        {
          IUserDao userDao = (IUserDao)DaoConfig.getDao(IUserDao.class);
          User user = userDao.findById(userId);
          if ("activate".equalsIgnoreCase(command))
          {
            user.setActivated(true);
            userDao.update(user);
          } else if ("deactivate".equalsIgnoreCase(command))
          {
            user.setActivated(false);
            userDao.update(user);
          } else if ("delete".equals(command))
          {
            try
            {
              userDao.delete(user);
            } catch (Exception e)
            {
                error = "Failed to delete user: " + e.getMessage();
            }
          }
        }
      }

      int max = StringUtils.toInt(request.getParameter("max"), 25);
      int offset = StringUtils.toInt(request.getParameter("offset"), 0); 
      String pattern = request.getParameter("pattern");

      User[] users = StatsProcessor.getUsers(offset, max, pattern);
      int total = StatsProcessor.getUsersCount(pattern);
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

        <div id="commands">
          <form action="stats-users.jsp" method="get">
            <input type="hidden" name="max" value="<%= max %>"/>
            <input type="hidden" name="offset" value="<%= offset %>"/>
            <a href="stats-users-cdl.jsp">Excel</a>&nbsp;&nbsp;&nbsp;&nbsp;
            Search (Name or Email): <input type="text" name="pattern" width="25" <%= (pattern != null ? "value='" + pattern + "'" : "") %>/>
          </form>
        </div>

        <%
          if (error != null) out.println("<div id=\"tips\">" + error + "</div>");
        %>

        <div class="data">
          <table class="datatable">
            <thead>
              <td>#</td>
              <td>Full name</td>
              <td>E-mail</td>
              <td>Activated</td>
              <td>Notification</td>
              <td>Date</td>
              <td>Operation</td>
            </thead>
            <%
              for (int i = 0; i < users.length; i++)
              {
                User user = users[i];

                String cmd = user.isActivated() ? "deactivateUser" : "activateUser";
                String cmdImg = user.isActivated() ? "disable" : "enable";
                long id = user.getId();

                out.print("<tr>");
                out.print("<td>" + (offset + i) + "</td>");
                out.print("<td align='left'>" + user.getFullName().replaceAll("\\s", "&nbsp;") + "</td>");
                out.print("<td align='left'>" + user.getEmail() + "</td>");
                out.print("<td>" + (user.isActivated() ? "Yes" : "No") + "</td>");
                out.print("<td>" + (user.isNotifyOnUpdates() ? "Yes" : "No") + "</td>");
                out.print("<td>" + dateFormat.format(user.getRegistrationDate()) + "</td>");
                out.print("<td>");
                out.print("<a href='#' onclick='" + cmd + "(" + id + ")'><img border='0' src='../images/" + cmdImg + ".gif'></a>&nbsp;");
//                out.print("<a href='#' onclick='deleteUser(" + id + ")'>");
                out.print("<img border='0' src='../images/delete.gif'>");
//                out.print("</a>");
                out.print("</td>");
                out.print("</tr>");
              }
            %>
          </table>
        </div>

        <div class="data" id="paging">
          <ul>
            <%
              for (int i = 0; i < total; i += max)
              {
                out.print("<li>");
                out.print("<a href='stats-users.jsp?offset=" + i + "&max=" + max +
                    (pattern != null ? "&pattern=" + pattern : "") + "'>" + i + "</a>");
                out.print("</li>");
              }
            %>
          </ul>&nbsp;
        </div>
      </div>
    </div>

    <form name="cmdForm" action="stats-users.jsp" method="get">
       <input name="cmd" value="" type="hidden">
       <input name="id" value="" type="hidden">
       <input name="max" value="<%= max %>" type="hidden">
       <input name="offset" value="<%= offset %>" type="hidden">
      <%
        if (pattern != null) out.println("<input name=\"pattern\" value=\"" + pattern + "\" type=\"hidden\">");
      %>

     </form>

  </body>
</html>