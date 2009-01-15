<%@ page import="java.util.Properties,
                 com.salas.bbservice.utils.StringUtils,
                 com.salas.bbservice.persistence.DaoConfig,
                 com.salas.bbservice.persistence.IUserDao,
                 com.salas.bbservice.domain.User,
                 javax.mail.Session,
                 java.io.IOException,
                 com.salas.bbservice.service.mail.MailServiceException,
                 com.salas.bbservice.service.mail.MailService"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Post Annoucement - Step 3</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
  </head>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

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

          IUserDao userDao = ((IUserDao)DaoConfig.getDao(IUserDao.class));
            User[] users = userDao.select(Integer.parseInt(group), n == null ? -1 : Integer.parseInt(n));

          Session aSession;
          try
          {
            Properties props = MailService.getMailProperties();
            props.put("mail.host", smtpServer);
            props.put("mail.user", smtpUser);

            aSession = Session.getInstance(props);
          } catch (IOException e)
          {
            aSession = null;
          }
        %>

        <h1>
          <% if (aSession == null) out.print("Failed to initialize mail session."); else
            out.print("Sending..."); %></h1>

        <%
          out.flush();

          if (aSession != null)
          {
            for (int i = 0; i < users.length; i++)
            {
              User user = users[i];
              String email = user.getEmail();

              out.print(email + "... ");
              out.flush();

              try
              {
                MailService.getInstance().sendMessage(aSession, from, reAddress, email,
                    subject, text);
                out.println("OK<br>");
              } catch (MailServiceException e)
              {
                out.println("<span style=\"color:#f00\"><b>Failed: " + e.getMessage() + "</b></span><br><br>");
              }
            }
          }
        %>
        <h1>Sending finished ...</h1>
      </div>
    </div>
  </body>
</html>