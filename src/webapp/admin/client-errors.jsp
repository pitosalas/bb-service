<%@ page import="com.salas.bbservice.persistence.IClientErrorDao,
                 com.salas.bbservice.persistence.DaoConfig,
                 com.salas.bbservice.domain.ClientError,
                 com.salas.bbservice.domain.User,
                 java.util.Date,
                 java.text.DateFormat"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Client Errors</title>
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
    td+td+td { width: 50px }
    td+td+td+td { width: 550px }

    tr td { text-align: center }
    tr td+td+td+td { text-align: left }

    td a { text-decoration: none; }
  </style>

  <%
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    IClientErrorDao dao = (IClientErrorDao)DaoConfig.getDao(IClientErrorDao.class);

    int offset = 0;
    int limit = 20;

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

    ClientError[] errors = dao.select(offset, limit);
    int total = dao.getErrorsCount();
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
          <a href="client-errors-clear.jsp"><img border="0" src="../images/icons/delete.gif"> Delete all</a>
        </div>

        <div class="data">
          <table class="datatable">
            <thead>
              <td>#</td>
              <td>Date</td>
              <td>Version</td>
              <td>Message</td>
            </thead>
            <%
              for (int i = 0; i < errors.length; i++)
              {
                ClientError error = errors[i];
                Date date = new Date(error.getTime());

                out.print("<tr>");
                out.print("<td>" + (offset + i) + "</td>");
                out.print("<td align='center'>" + dateFormat.format(date) + "</td>");
                out.print("<td align='center'>" + error.getVersion() + "</td>");
                out.print("<td><a href='client-error-details.jsp?offset=" + offset +
                    "&limit=" + limit +
                    "&id=" + error.getId() + "'>" + error.getMessage() + "</a></td>");
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
                out.print("<a href='client-errors.jsp?offset=" + i + "&limit=" + limit + "'>" + i + "</a>");
                out.print("</li>");
              }
            %>
          </ul>&nbsp;
        </div>
      </div>
    </div>
  </body>
</html>