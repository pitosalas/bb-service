<%@ page import="com.salas.bbservice.stats.Statistics,
                 java.util.Map,
                 java.util.Set"%><%@ page import="com.salas.bbservice.service.meta.MetaService"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Discovery</title>
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
            <td colspan="2">Metrics</td>
          </thead>
          <tr>
            <td>Number of queries</td>
            <td><%= Statistics.getMetaQueries() %></td>
          </tr>
          <tr>
            <td>Discovery queue size</td>
            <td><%= MetaService.getInstance().getDiscoveryManager().getDiscoveryQueueSize() %></td>
          </tr>
          <tr>
            <td>Number of database hits</td>
            <td><%= Statistics.getMetaDatabaseHits() %></td>
          </tr>
          <tr>
            <td>Number of new discovery requests</td>
            <td><%= Statistics.getMetaNewDiscoveries() %></td>
          </tr>
          <tr>
            <td>Number of suggestions</td>
            <td><%= Statistics.getMetaSuggestionDiscoveries() %></td>
          </tr>
          <tr>
            <td>Average discovery time per Blog</td>
            <td><%= Statistics.getAverageDiscoveryTimePerBlog() / 1000.0 %> seconds</td>
          </tr>
        </table>

        <table>
          <thead>
            <td colspan="2">Discoverers: Calls and Failures</td>
          </thead>
          <%
            Map calls = Statistics.getMetaDiscoverersCalls();
            Map errors = Statistics.getMataDicoverersErrors();
            Object[] discoverers = calls.keySet().toArray();
            for (int i = 0; i < discoverers.length; i++)
            {
              Object discoverer = discoverers[i];
              Integer callsCntI = (Integer)calls.get(discoverer);
              Integer errorsCntI = (Integer)errors.get(discoverer);
              int callsCnt = callsCntI == null ? 0 : callsCntI.intValue();
              int errorsCnt = errorsCntI == null ? 0 : errorsCntI.intValue();
              out.println("<tr>");
              out.println("<td>" + discoverer + "</td>");
              out.println("<td>" + callsCnt + " calls / " + errorsCnt + " errors</td>");
              out.println("</tr>");
            }
            
            if (discoverers.length == 0)
            {
              out.println("<tr><td>Discoverers</td><td>Not executed yet</td></tr>");
            }
          %>
        </table>

        <table>
          <thead>
            <td colspan="2">Database</td>
          </thead>
          <tr>
            <td>Blogs in database</td>
            <td><%= Statistics.getBlogsCount() %> (<%= Statistics.getIncompleteBlogsCount() %> incomplete)</td>
          </tr>
          <tr>
            <td>Links in database</td>
            <td><%= Statistics.getBlogsLinksCount() %> (<%= Statistics.getBadBlogLinksCount() %> to bad blogs)</td>
          </tr>
        </table>
      </div>
    </div>
  </body>
</html>