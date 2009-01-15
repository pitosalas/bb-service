<%@ page import="com.salas.bbservice.domain.dao.LabelCountPercentage,
                 com.salas.bbservice.persistence.DaoConfig"%>
<%@ page import="com.salas.bbservice.persistence.IDataMiningDao"%>
<%@ page import="java.text.MessageFormat"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Data Mining - Guides of Users</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
  </head>
  <style type="text/css">
    td+td { width: 150px; }
  </style>

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
        <table>
          <thead>
            <tr>
              <td colspan="3">Guides of Users</td>
            </tr>
          </thead>
            <tr>
                <td>Guides No.</td>
                <td>Users</td>
                <td>Percentage</td>
            </tr>
          <%
              IDataMiningDao dm = (IDataMiningDao)DaoConfig.getDao(IDataMiningDao.class);
              LabelCountPercentage[] data = dm.getUsersToGuides();

              MessageFormat fmt = new MessageFormat("<tr><td>{0}</td><td>{1}</td><td>{2}</td></tr>");
              for (int i = 0; i < data.length; i++)
              {
                  LabelCountPercentage lcp = data[i];
                  out.println(fmt.format(new Object[] {
                      lcp.getLabel(),
                      new Integer(lcp.getCount()),
                      new Double(lcp.getPercentage()) }));
              }
          %>
        </table>
      </div>
    </div>
  </body>
</html>