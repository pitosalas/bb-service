<%@ page import="com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.stats.StatsOutputHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Monthly</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
<%--    <link rel="Stylesheet" type="text/css" href="../styles/stats.css"/>--%>
  </head>
  <style>
    .data td { width: 70px; vertical-align: middle; text-align: center; }
  </style>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <body>
    <%
      StatsProcessor sp = new StatsProcessor();

      String startDate = request.getParameter("start");
      String endDate = request.getParameter("end");

      String tbMonthlyStats = StatsOutputHelper.generateStatsTable(
          sp.getMonthlyStats(startDate, endDate), "monthlyStats",
          "Monthly Statistics", true, false, false);
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
        <div id="tips">
          Usage: ?start=YYYYmm&amp;end=YYYYmm<br>
          Example: ?start=200501&amp;end=200504
        </div>
        <%= tbMonthlyStats %>
      </div>
    </div>
  </body>
</html>