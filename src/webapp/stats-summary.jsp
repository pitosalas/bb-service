<%@ page import="com.salas.bbservice.stats.StatsTable,
                 com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.stats.StatsOutputHelper,
                 com.salas.bbservice.stats.Statistics,
                 java.util.List,
                 java.util.ArrayList,
                 java.util.StringTokenizer,
                 com.salas.bbservice.utils.StringUtils"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Summary</title>
    <style type="text/css">
      table { border-collapse: collapse; font: 10px Verdana, Tahoma, Arial, sans-serif; margin-bottom: 2px; }
      thead { background-color: #777; color: #FFF; }
      td { padding-left: 5px; width: 70px; border: 1px solid #777; }
    </style>
  </head>
  <body>
    <%
      // read the list of versions to display
      String fixedVersionsS = request.getParameter("versions");
      String[] fixedVersions;
      if (fixedVersionsS != null)
      {
        fixedVersions = StringUtils.split(fixedVersionsS, " ");
      } else
      {
        fixedVersions = new String[0];
      }

      // read number of most recent versions to display
      String recentS = request.getParameter("recent");
      int recent = 2;
      try
      {
        if (recentS != null) recent = Integer.parseInt(recentS.trim());
      } catch (NumberFormatException e)
      {
          // Invalid format of recent count
      }

      StatsProcessor sp = new StatsProcessor();

      String tblInstallations = StatsOutputHelper.generateStatsTable(
          sp.getLatestVersionsInstallsStats(recent, fixedVersions), "installs", "Installations",
          true, true, false);

      String tblRuns = StatsOutputHelper.generateStatsTable(
          sp.getLatestVersionsRunsStats(recent, fixedVersions), "runs", "Runs",
          true, true, false);

      String tblRegistrations = StatsOutputHelper.generateStatsTable(
          sp.getUserRegistrations(), "registrations", "User Registrations",
          true, true, false);

    %>

    <%= tblInstallations %>
    <%= tblRuns %>
    <%= tblRegistrations %>

    <table>
      <thead>
        <td colspan="2">Feed Stats</td>
      </thead>
      <tr>
        <td>How many</td>
        <td><%= Statistics.getBlogsCount() %></td>
      </tr>
      <tr>
        <td>Incomplete</td>
        <td><%= Statistics.getIncompleteBlogsCount() %></td>
      </tr>
    </table>
  </body>
</html>