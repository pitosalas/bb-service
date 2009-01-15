<%@ page import="com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.stats.StatsOutputHelper,
                 com.salas.bbservice.stats.StatsTable"%><%@ page import="com.salas.bbservice.utils.StringUtils"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Keywords</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/paging.css"/>
  </head>
  <style type="text/css">

    div.data {
      width: 600px;
    }
  </style>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <body>
    <%
      int max = StringUtils.toInt(request.getParameter("max"), 30);

      StatsTable chart = StatsProcessor.getKeywordsChart(max);
      String output = StatsOutputHelper.generateStatsTable(chart, "keywords",
          "Most Popular Keywords", true, false, false);
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
        <%= output %>
    </div>
  </body>
</html>