<%@ page import="com.salas.bbservice.persistence.IUserDao,
                 com.salas.bbservice.persistence.DaoConfig,
                 com.salas.bbservice.domain.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Subscribers</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
  </head>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <%
    boolean releaseNotificationsOnly = request.getParameter("releaseNotifications") != null;

    IUserDao userDao = (IUserDao)DaoConfig.getDaoManager().getDao(IUserDao.class);

    User[] users = userDao.select(releaseNotificationsOnly);
    
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
  %>

  <body>
    <div id="page">
      <div id="header">
        <div id="left"></div>
        <div id="right"></div>
        <h1><span>BlogBridge</span></h1>
        <h2><span>Administrative Console</span></h2>
      </div>

      <%@ include file="menu.html"%>

      <div id="content">
        <h1>Subscribers:</h1>
        <div id="emails"><%= buf.toString() %></div>
      </div>
    </div>
  </body>
</html>