<%@ page import="com.salas.bbservice.stats.StatsTable,
                 com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.stats.StatsOutputHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Miscellaneous</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
  </head>
  <style>
    .data td+td { width: 70px; }
  </style>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <body>
    <%
      StatsProcessor sp = new StatsProcessor();

      String tblMisc = StatsOutputHelper.generateStatsTable(
          sp.getMiscStats(), "miscStats", "Miscellaneous statistics",
          false, false, false);

      String tblOsUsage = StatsOutputHelper.generateStatsTable(
          sp.getOsUsageStats(), "osUsage", "OS usage statistics",
          true, false, false);

      String tblJavaVersionUsage = StatsOutputHelper.generateStatsTable(
          sp.getJavaVersionUsageStats(), "javaVersionUsage", "Java version statistics",
          true, false, false);
    %>
    <div id="page">
      <div id="header">
        <div id="left"></div>
        <div id="right"></div>
        <h1><span>BlogBridge</span></h1>
        <h2><span>Administrative Console</span></h2>
      </div>

      <%@ include file="menu.html"%>

      <div id="content">
        <%= tblMisc %>
        <%= tblOsUsage %>
        <%= tblJavaVersionUsage %>
      </div>
    </div>
  </body>
</html>