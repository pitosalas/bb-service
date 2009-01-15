<%@ page import="com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.stats.StatsOutputHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Installations and Runs</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
<%--    <link rel="Stylesheet" type="text/css" href="../styles/stats.css"/>--%>
  </head>
  <style>
    .data td { width: 70px; }
  </style>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <body>
    <%
      StatsProcessor sp = new StatsProcessor();

      boolean first = true, second = true, third = true;

      String tblInstPerVersion = first ?
        StatsOutputHelper.generateStatsTable(
          sp.getInstallationsPerVersion(), "instPerVersion", "Installations per version",
          true, true, true) : "";

      String tblRunsPerVersion = second ? StatsOutputHelper.generateStatsTable(
          sp.getRunsPerVersion(), "runsPerVersion", "Runs per version",
          true, true, true) : "";

      String tblInstPerVersionDistr = third ? StatsOutputHelper.generateStatsTable(
          sp.getInstallationsPerVersionDistribution(), "instPerVersionDist",
          "Installations per version distribution",
          true, false, false) : "";
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
        <%= tblInstPerVersion %>
        <%= tblRunsPerVersion %>
        <%= tblInstPerVersionDistr %>
      </div>
    </div>
  </body>
</html>