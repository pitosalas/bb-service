<%@ page import="com.salas.bbservice.persistence.DaoConfig"%>
<%@ page import="com.salas.bbservice.persistence.IVersionsDao"%>
<%@ page import="java.util.List"%>
<%@ page import="com.salas.bbservice.domain.Version"%>
<%@ page import="com.salas.bbservice.utils.StringUtils"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Versions</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/paging.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/commands.css"/>
  </head>

  <style>
    .production { font-weight: bold; color: #f00; }
  </style>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

<%
  IVersionsDao dao = (IVersionsDao)DaoConfig.getDao(IVersionsDao.class);

  int deleteId = (int)StringUtils.parseLong(request.getParameter("delete"), -1);
  if (deleteId != -1)
  {
    dao.removeVersion(deleteId);
  }

  List versions = dao.getLatestVersions(25);
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
        <a href="versions-edit.jsp">Add Version</a>
      </div>

      <div class="data">
        <table class="datatable">
          <thead>
            <td>Operations</td>
            <td>Version</td>
            <td>Release Date</td>
          </thead>
          <tbody>
<%
  DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
  for (int i = 0; i < versions.size(); i++)
  {
    Version version = (Version)versions.get(i);
    out.println("<tr" + (version.isProduction() ? " class='production'>" : ">"));
    out.print("<td><a href='versions-edit.jsp?id=" + version.getId() + "'>Edit</a> <a href='versions.jsp?delete=" + version.getId() + "'>Delete</a></td>");
    out.print("<td>" + version.getVersion() + "</td>");
    out.print("<td>" + dateFormat.format(new Date(version.getReleaseTime())) + "</td>");
    out.println("</tr>");
  }
%>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  </body>
</html>