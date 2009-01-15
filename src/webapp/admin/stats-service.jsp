<%@ page import="com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.stats.StatsOutputHelper,
                 com.salas.bbservice.utils.StringUtils"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Service</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/commands.css"/>
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
    <%
      boolean localeStats = request.getParameter("locale") != null;
      int topReadLimit = StringUtils.toInt(request.getParameter("read"), 0);
      int topRatedLimit = StringUtils.toInt(request.getParameter("rated"), 0);

      StatsProcessor sp = new StatsProcessor();

      // Service statistics
      String tblLocaleStats = !localeStats ? "" : StatsOutputHelper.generateStatsTable(
          sp.getLocaleStats(), "localeStats", "Users' locale statistics",
          true, false, true);

      String tblTopReadFeeds = topReadLimit <= 0 ? "" : StatsOutputHelper.generateStatsTable(
          sp.getTopReadFeeds(topReadLimit), "top10read", "Top " + topReadLimit +
          " most read feeds", true, false, false);

      String tblTopRatedFeeds = topRatedLimit <= 0 ? "" : StatsOutputHelper.generateStatsTable(
          sp.getTopRatedFeeds(topRatedLimit, 0.05), "top10rated", "Top " + topRatedLimit +
          " most rated feeds", false, false, false);
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

        <div id="commands">
          <a href="stats-service-cdl.jsp?<%= request.getQueryString() %>">Excel</a>
        </div>

        <%= tblTopReadFeeds %>
        <%= tblLocaleStats %>
        <%= tblTopRatedFeeds %>
      </div>
    </div>
  </body>
</html>