<%@ page import="com.salas.bbservice.stats.StatsTable,
                 com.salas.bbservice.stats.StatsProcessor,
                 com.salas.bbservice.stats.StatsOutputHelper,
                 com.salas.bbservice.utils.StringUtils"
 contentType="application/vnd.ms-excel;charset=UTF-8" language="java" %><%

  response.setHeader("Content-Disposition", "inline;filename=service-stats.csv");

  boolean localeStats = request.getParameter("locale") != null;
  int topReadLimit = StringUtils.toInt(request.getParameter("read"), 0);
  int topRatedLimit = StringUtils.toInt(request.getParameter("rated"), 0);

  StatsProcessor sp = new StatsProcessor();

  String csvLocales;
  if (localeStats)
  {
    StatsTable tblLocaleStats = sp.getLocaleStats();
    csvLocales = StatsOutputHelper.generateCSV(tblLocaleStats, "Locales", true, true);
  } else csvLocales = "";

  String csvTopReadChannels;
  if (topReadLimit > 0)
  {
    StatsTable tblTopReadChannels = sp.getTopReadFeeds(topReadLimit);
    csvTopReadChannels = StatsOutputHelper.generateCSV(tblTopReadChannels,
            "Top " + topReadLimit + " Read Channels", true, false);
  } else csvTopReadChannels = "";

  String csvTopRatedChannels;
  if (topRatedLimit > 0)
  {
    StatsTable tblTopRatedChannels = sp.getTopRatedFeeds(topRatedLimit, 0.05);
    csvTopRatedChannels = StatsOutputHelper.generateCSV(tblTopRatedChannels,
            "Top " + topReadLimit + " Rated Channels", true, false);
  } else csvTopRatedChannels = "";

  out.print(csvLocales);
  out.print(csvTopReadChannels);
  out.print(csvTopRatedChannels);
  if (true) return;
%>
