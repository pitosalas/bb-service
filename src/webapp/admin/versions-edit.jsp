<%@ page import="com.salas.bbservice.domain.VersionChange"%>
<%@ page import="com.salas.bbservice.utils.StringUtils"%>
<%@ page import="com.salas.bbservice.persistence.IVersionsDao"%>
<%@ page import="com.salas.bbservice.persistence.DaoConfig"%>
<%@ page import="com.salas.bbservice.domain.Version"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Version Edit</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/paging.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/commands.css"/>
  </head>

  <style>
    .ops { clear: both; float: left; margin-right: 10px; }
    .type { float: left; margin-right: 10px; }
    .details { width: 500px; }
  </style>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

<%
  int versionId = (int)StringUtils.parseLong(request.getParameter("id"), -1);
  DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

  String version = "";
  boolean isProduction = false;
  String releaseDate = dateFormat.format(new Date());

  List changesList = null;
  if (versionId != -1)
  {
    IVersionsDao dao = (IVersionsDao)DaoConfig.getDao(IVersionsDao.class);
    Version versionSource = dao.findVersion(versionId);

    if (versionSource != null)
    {
      version = versionSource.getVersion();
      isProduction = versionSource.isProduction();
      releaseDate = dateFormat.format(new Date(versionSource.getReleaseTime()));

      changesList = dao.listChangesOfVersion(versionSource.getId());
    }
  }
%>

  <script type="text/javascript" src="../scripts/versions-changes.js"></script>
  <script type="text/javascript">
    function onSave() {
      document.forms[0].submit();
    }

    function initChanges() {
<%
  if (changesList != null)
  {
    for (int i = 0; i < changesList.size(); i++)
    {
      VersionChange change = (VersionChange) changesList.get(i);
      out.println("addChange(" + change.getType() + ",\"" +
        StringUtils.escapeForCDL(change.getDetails()) + "\");");
    }
  }
%>
    }
  </script>

  <body onload="initChanges()">
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
          <a href="#" onclick="onSave(); return false;">Save</a>
          <a href="versions.jsp">Cancel</a>
        </div>

        <form action="versions-update.jsp" method="post">
          <div class="data">
            <input type="hidden" name="id" value="<%= versionId %>"/>
            <table>
              <tr>
                <td>Version:</td>
                <td><input type="text" maxlength="6" name="version" value="<%= version %>"/></td>
              </tr>
              <tr>
                <td><label for="production">Production:</label></td>
                <td><input type="checkbox" id="production" name="production" value="1" <%= isProduction ? "checked" : "" %> /></td>
              </tr>
              <tr>
                <td>Release Date:</td>
                <td><input type="text" name="releaseDate" maxlength="8" value="<%= releaseDate %>" /></td>
              </tr>
            </table>
          </div>

          <div id="commands">
            Changes in version: <a href="#" onClick="addChange(); return false;">Add Change</a>
          </div>
<%

  int type = 0;
  String details = "Some details";

%>
          <div id="changes"></div>
        </form>
      </div>
    </div>
  </body>
</html>