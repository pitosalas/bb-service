<%@ page import="com.salas.bbservice.persistence.IClientErrorDao,
                 com.salas.bbservice.persistence.DaoConfig,
                 com.salas.bbservice.domain.ClientError,
                 com.salas.bbservice.domain.User,
                 java.util.Date,
                 java.text.DateFormat,
                 com.salas.bbservice.persistence.IFeedbackMessageDao,
                 com.salas.bbservice.domain.FeedbackMessage"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Client Feedback Messages</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/paging.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/commands.css"/>
  </head>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <style type="text/css">
    td { width: 40px; }
    td+td { width: 100px }
    td+td+td { width: 550px }

    tr td { text-align: center }
    tr td+td+td { text-align: left }

    td a { text-decoration: none; }
  </style>

  <%
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    IFeedbackMessageDao dao = (IFeedbackMessageDao)DaoConfig.getDao(IFeedbackMessageDao.class);

    int offset = 0;
    int limit = 20;
    int cutLimit = 50;

    String offsetS = request.getParameter("offset");
    String limitS = request.getParameter("limit");

    try
    {
      offset = Integer.parseInt(offsetS);
    } catch (RuntimeException e)
    {
    }

    try
    {
      limit = Integer.parseInt(limitS);
    } catch (RuntimeException e)
    {
    }

    FeedbackMessage[] messages = dao.select(offset, limit);
    int total = dao.getMessagesCount();
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

        <div id="commands">
          <a href="client-feedbacks-clear.jsp"><img border="0" src="../images/icons/delete.gif"> Delete all</a>
        </div>

        <div class="data">
          <table class="datatable">
            <thead>
              <td>#</td>
              <td>Date</td>
              <td>Message</td>
            </thead>
            <%
              for (int i = 0; i < messages.length; i++)
              {
                FeedbackMessage message = messages[i];
                Date date = new Date(message.getTime());

                String textCut = message.getMessage();
                int length = textCut.indexOf('\n');
                if (length == -1 || length > cutLimit) length = cutLimit;
                if (length < textCut.length()) textCut = textCut.substring(0, length) + "...";

                out.print("<tr>");
                out.print("<td>" + (offset + i) + "</td>");
                out.print("<td align='center'>" + dateFormat.format(date) + "</td>");
                out.print("<td><a href='client-feedback-details.jsp?offset=" + offset +
                    "&limit=" + limit +
                    "&id=" + message.getId() + "'>" + textCut + "</a></td>");
                out.print("</tr>");
              }
            %>
          </table>
        </div>

        <div class="data" id="paging">
          <ul>
            <%
              for (int i = 0; i < total; i += limit)
              {
                out.print("<li>");
                out.print("<a href='client-feedbacks.jsp?offset=" + i + "&limit=" + limit + "'>" + i + "</a>");
                out.print("</li>");
              }
            %>
          </ul>&nbsp;
        </div>
      </div>
    </div>
  </body>
</html>