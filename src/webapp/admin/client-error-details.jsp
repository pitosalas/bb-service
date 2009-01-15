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
    <link rel="Stylesheet" type="text/css" href="../styles/commands.css"/>
  </head>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <style type="text/css">
    td { width: 100px; }
    td+td { width: 700px }
  </style>

  <%
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    IClientErrorDao dao = (IClientErrorDao)DaoConfig.getDao(IClientErrorDao.class);

    ClientError error = null;
    int id = -1;
    try
    {
      id = Integer.parseInt(request.getParameter("id"));
      error = dao.findById(id);
    } catch (NumberFormatException e)
    {
      // Id has invalid format
    }

    String offset = request.getParameter("offset");
    String limit = request.getParameter("limit");

    String coordinates = null;
    if (offset != null && limit != null)
    {
      coordinates = "?offset=" + offset + "&limit=" + limit;
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

        <div id="commands">
          <a href="client-errors.jsp<%= (coordinates != null) ? coordinates : "" %>"><img border="0" src="../images/icons/back.gif"> Back</a>
        </div>

        <div class="data">
          <%
            if (error == null)
            {
              out.println("<h1>Record not found!</h1>");
            } else
            {
              String details = error.getDetails();
              details = details.replaceAll("&", "&amp;");
              details = details.replaceAll("<", "&lt;");
              details = details.replaceAll(">", "&gt;");
              details = details.replaceAll("'", "&apos;");
              details = details.replaceAll("\"", "&quot;");
          %>
            <table class="datatable">
              <tr>
                <td class="rowheader">Date</td>
                <td><%= dateFormat.format(new Date(error.getTime()))%></td>
              </tr>
              <tr>
                <td class="rowheader">Version</td>
                <td><%= error.getVersion()%></td>
              </tr>
              <tr>
                <td class="rowheader">Message</td>
                <td><%= error.getMessage()%></td>
              </tr>
              <tr>
                <td colspan="2"><pre><%= details %></pre></td>
              </tr>
            </table>
          <%
            }
          %>
        </div>
      </div>
    </div>
  </body>
</html>