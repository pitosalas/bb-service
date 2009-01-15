<%@ page import="java.util.Properties,
                 com.salas.bbservice.utils.StringUtils,
                 com.salas.bbservice.persistence.DaoConfig,
                 com.salas.bbservice.persistence.IUserDao,
                 com.salas.bbservice.domain.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Post Annoucement - Step 2</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
  </head>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <style type="text/css">
    table { width: 100%; }
    td { width: 10%; }
    td+td { width: 90%; }
    td { border: 0; }

    input, textarea { border: 1px solid #ccc; padding: 2px; }

    input[name="subject"], input[name="reAddress"], input[name="from"] { width: 100%; }
    input[name="smtpServer"], input[name="smtpUser"], select[name="group"] { width: 200px; }
    #text { border: 1px solid #ccc; background: #fff; padding: 2px; width: 100%; }

    #usersListToggle { cursor: pointer; width: 9px; height: 9px; }
    #usersList { background: #fff; border: 1px solid #ccc; padding: 2px; width: 100%; }
  </style>

  <script language="JavaScript">

  visible = true;

  function toggleUsersList() {
    div = document.getElementById("usersList");
    img = document.getElementById("usersListToggle");

    div.style.display = (visible ? "none" : "");
    img.src = (visible ? "../images/plus.png" : "../images/minus.png");
    visible = !visible;
  }

  </script>

  <body onLoad="toggleUsersList()">

    <div id="page">
      <div id="header">
        <div id="left"></div>
        <div id="right"></div>
        <h1><span>BlogBridge</span></h1>
        <h2><span>Administrative Console</span></h2>
      </div>

      <%@ include file="menu.html"%>

      <div id="content">

        <%
          String smtpServer = request.getParameter("smtpServer");
          String smtpUser = request.getParameter("smtpUser");
          String from = request.getParameter("from");
          String text = request.getParameter("text");
          String reAddress = request.getParameter("reAddress");
          String group = request.getParameter("group");
          String subject = request.getParameter("subject");
          String n = request.getParameter("n");

          if (group == null) group = "0";
          if (text == null) text = "";
          if (reAddress == null) reAddress = "";
          if (subject == null) subject = "";

          // mail
          ClassLoader classLoader = this.getClass().getClassLoader();
          Properties mailProps = new Properties();
          mailProps.load(classLoader.getResourceAsStream("mail.properties"));
          if (smtpServer == null) smtpServer = mailProps.getProperty("mail.host");
          if (smtpUser == null) smtpUser = mailProps.getProperty("mail.user");
          if (from == null) from = mailProps.getProperty("mail.from");

          String[] groups = IUserDao.USER_GROUPS;

          IUserDao userDao = ((IUserDao)DaoConfig.getDao(IUserDao.class));
          User[] users = userDao.select(Integer.parseInt(group), n == null ? -1 : Integer.parseInt(n));

          StringBuffer buf = new StringBuffer();
          for (int i = 0; i < users.length; i++)
          {
            User user = users[i];

            String fullName = user.getFullName();
            String email = user.getEmail();

            if (fullName != null && fullName.trim().length() > 0)
            {
              fullName = fullName.replaceAll("[;<>&]", "_");
              buf.append(fullName).append(" &lt;").append(email).append("&gt;; ");
            } else
            {
              buf.append(email).append("; ");
            }
          }

          String usersList = buf.toString();
        %>

        <form action="announcement-step3.jsp" method="post">
          <div style="display:none">
            <input type="hidden" name="smtpServer" value="<%= smtpServer %>">
            <input type="hidden" name="smtpUser" value="<%= smtpUser %>">
            <input type="hidden" name="group" value="<%= group %>">
            <input type="hidden" name="n" value="<%= n == null ? "" : n %>">
            <textarea name="from"><%= from %></textarea>
            <textarea name="reAddress"><%= reAddress %></textarea>
            <textarea name="subject"><%= subject %></textarea>
            <textarea name="text"><%= text %></textarea>
          </div>

          <table>
            <tr>
              <td>SMTP&nbsp;Server:</td>
              <td><%= smtpServer %></td>
            </tr>
            <tr>
              <td>Username:</td>
              <td><%= smtpUser %></td>
            </tr>
            <tr>
              <td>From:</td>
              <td><%= StringUtils.escape(from) %></td>
            </tr>
            <tr>
              <td>To:</td>
              <td>
                <img id="usersListToggle" src="../images/plus.png" onClick="javascript:toggleUsersList()">&nbsp;
                <%= groups[Integer.parseInt(group)]%>
                <div id="usersList"><%= usersList %></div>
              </td>
            </tr>
            <tr>
              <td>Reply&nbsp;To:</td>
              <td><%= StringUtils.escape(reAddress) %></td>
            </tr>
            <tr>
              <td>Subject:</td>
              <td><%= StringUtils.escape(subject) %></td>
            </tr>
            <tr>
              <td colspan="2"><div id="text"><%= text.replaceAll("\\n", "<br>") %></div></td>
            </tr>
            <tr>
              <td colspan="2"><input type="submit" value="Send"></td>
            </tr>
          </table>
        </form>
      </div>
    </div>
  </body>
</html>