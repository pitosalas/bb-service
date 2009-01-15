<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.salas.bbservice.domain.VersionChange"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.ParseException"%>
<%@ page import="com.salas.bbservice.utils.StringUtils"%>
<%@ page import="java.util.List"%>
<%@ page import="com.salas.bbservice.persistence.DaoConfig"%>
<%@ page import="com.salas.bbservice.persistence.IVersionsDao"%>
<%@ page import="com.salas.bbservice.domain.Version"%>
<%
  String error = null;

  // Do update
  int versionId = (int)StringUtils.parseLong(request.getParameter("id"), -1);
  String vversion = request.getParameter("version");
  boolean production = "1".equals(request.getParameter("production"));
  long releaseTime = -1;
  String releaseDate = request.getParameter("releaseDate");
  try
  {
    releaseTime = new SimpleDateFormat("MM/dd/yy").parse(releaseDate).getTime();
  } catch (ParseException e)
  {
    error = "Date format is incorrect.";
  }

  if (!StringUtils.isEmpty(vversion) && releaseTime != -1)
  {
    // Version data is valid.
    // Now collect all changes information.
    List changes = new ArrayList();
    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements())
    {
      String name = (String)parameterNames.nextElement();
      if (name.startsWith("type-"))
      {
        String number = name.substring(5);
        int type = (int)StringUtils.parseLong(request.getParameter(name), -1);
        String details = request.getParameter("details-" + number);

        if (type != -1 && !StringUtils.isEmpty(details))
        {
          // Found non-empty change details record
          changes.add(new VersionChange(-1, type, details));
        }
      }
    }

    // List of changes ready now replace old version with new
    IVersionsDao dao = (IVersionsDao)DaoConfig.getDao(IVersionsDao.class);
    if (versionId != -1) dao.removeVersion(versionId);

    Version versionO = new Version(vversion, releaseTime, production);
    dao.addVersion(versionO);

    for (int i = 0; i < changes.size(); i++)
    {
      VersionChange versionChange = (VersionChange)changes.get(i);
      versionChange.setVersionId(versionO.getId());
      dao.addVersionChange(versionChange);
    }

  } else
  {
    error = "Invalid data specified: versionId=" + versionId +
        ", version=" + vversion +
        ", releaseTime=" + releaseTime;
  }
%>

<% if (error == null) { %>
<jsp:include page="versions.jsp"/>
<% } else out.println(error); %>