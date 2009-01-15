<%@ page import="com.salas.bbservice.stats.StatsProcessor"%>
<%@ page import="com.salas.bbservice.domain.ReadingListInfo"%>
<%@ page import="com.salas.bbservice.utils.StringUtils"%>
<%@ page import="com.salas.bbservice.persistence.IStatsDao"%>
<%@ page import="com.salas.bbservice.persistence.DaoConfig"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Statistics - Reading Lists</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/paging.css"/>
    <link rel="Stylesheet" type="text/css" href="../styles/commands.css"/>
  </head>

  <!--[if lte IE 6]>
  <style type="text/css">
    body { behavior:url("../scripts/csshover.htc"); }
  </style>
  <![endif]-->

  <body>
    <%
      StatsProcessor sp = new StatsProcessor();

      int max = StringUtils.toInt(request.getParameter("max"), 25);
      int offset = StringUtils.toInt(request.getParameter("offset"), 0);

      if ("del".equals(request.getParameter("action")))
      {
         int userId = StringUtils.toInt(request.getParameter("userid"), -1);
         String title = request.getParameter("title");

         if (userId != -1 && !StringUtils.isEmpty(title))
         {
            IStatsDao statsDao = (IStatsDao)DaoConfig.getDaoManager().getDao(IStatsDao.class);
            statsDao.clearReadingListStats(userId, title);
         }
      }

      boolean accessStats = "1".equals(request.getParameter("access"));
      ReadingListInfo[] lists = sp.getReadingListsInfo(accessStats, offset, max);
      int total = sp.getReadingListsCount(accessStats);

      String tbStats = "<table>";
      tbStats += "<thead><td colspan=\"6\">Top Reading Lists</td></thead>";
      tbStats += "<tr><td>List</td><td>Author</td><td>Feeds</td><td>Total / Unique Visits</td><td>Last 2 weeks circulation</td><td>Commands</td></tr>";
      for (int i = 0; i < lists.length; i++)
      {
        ReadingListInfo list = lists[i];
        int[] circ = list.lastWeekCirculation;
        int[] normCirc = ReadingListInfo.normalize(circ, 10);
        boolean active = list.feeds > 0 && list.active;

        String escapedTitle = StringUtils.encodeForURL(list.title);
        String url = "http://www.blogbridge.com/rl/" + list.userId + "/" + escapedTitle + ".opml";

        tbStats += "<tr>";
        tbStats += "<td>";
        if (active) tbStats += "<a href=\"" + url + "\">";
        tbStats += list.title;
        if (active) tbStats += "</a>";
        tbStats += "</td>";
        tbStats += "<td><a href=\"mailto:" + list.userEmail + "\">" + list.userFullName.replaceAll("\\s", "&nbsp;") + "</td>";
        tbStats += "<td>" + list.feeds + "</td>";
        tbStats += "<td>" + list.totalVisits + " / " + list.uniqueVisits + "</td>";
        tbStats += "<td>";
        for (int j = 0; j < normCirc.length; j++)
        {
          int val = normCirc[j];
          tbStats += "<img src=\"../images/dot-blue.gif\" width='7' height='" + val + "' title='" + circ[j] + " visit(s)'>";
        }
        tbStats += "&nbsp;" + circ[circ.length - 1];
        tbStats += "</td>";
        tbStats += "<td><a href=\"readinglists.jsp?action=del&access=" + (accessStats ? "1" : "0") + "&offset=" + offset + "&max=" + max + "&userid=" + list.userId + "&title=" + escapedTitle + "\"><img src=\"../images/delete.gif\" border=\"0\"></a></td>";
        tbStats += "</tr>";
      }
      tbStats += "</table>";
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
          <a href='readinglists.jsp?offset=<%= offset %>&max=<%= max %>&access=0'>All</a>&nbsp;&nbsp;|&nbsp;&nbsp;
          <a href='readinglists.jsp?offset=<%= offset %>&max=<%= max %>&access=1'>Accessed</a>
        </div>
        <%= tbStats %>
      </div>

      <div class="data" id="paging">
        <ul>
            <%
            for (int i = 0; i < total; i += max)
            {
              out.print("<li>");
              out.print("<a href='readinglists.jsp?offset=" + i + "&max=" + max + "&access=" +
                  (accessStats ? "1" : "0") + "'>" + i + "</a>");
              out.print("</li>");
            }
            %>
        </ul>&nbsp;
      </div>
    </div>
  </body>
</html>