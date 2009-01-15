<%@ page import="com.salas.bbservice.stats.Statistics"
    contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Service</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
  </head>
  <style>
    td+td { width: 70px; }
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
              <td>Parameter</td>
              <td>Value</td>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Sync Stores</td>
              <td><%= Statistics.getSyncStoreCount() %></td>
            </tr>
            <tr>
              <td>Sync Restores</td>
              <td><%= Statistics.getSyncRestoreCount() %></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </body>
</html>