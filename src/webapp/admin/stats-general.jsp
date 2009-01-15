<%@ page import="com.salas.bbservice.stats.Statistics"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - General</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
  </head>
  <style>
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
            <td colspan="2">General</td>
          </thead>
          <tr>
            <td>Uptime</td>
            <td><%= Statistics.getUptimeString() %></td>
          </tr>
        </table>
      </div>
    </div>
  </body>
</html>