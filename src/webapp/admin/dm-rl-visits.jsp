<%@ page import="com.salas.bbservice.domain.ReadingListInfo,
                 com.salas.bbservice.persistence.DaoConfig" %>
<%@ page import="com.salas.bbservice.persistence.IDataMiningDao" %>
<%@ page import="com.salas.bbservice.utils.StringUtils" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Data Mining - Most Visited RLs</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
</head>
<style type="text/css">
    td+td {
        width: 150px;
    }
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

    <%@ include file="menu.html" %>

    <div id="content">
        <div id="tips">
          Parameters:<br>
          &nbsp;&nbsp;max=N (default 50) - number of reading lists to display
        </div>

        <table>
            <thead>
                <tr>
                    <td colspan="4">Top Visited Lists</td>
                </tr>
            </thead>
            <tr>
                <td>List</td>
                <td>Author</td>
                <td>Last&nbsp;Sync&nbsp;Time</td>
                <td>Total / Unique Visits</td>
            </tr>
            <%
                int max = StringUtils.toInt(request.getParameter("max"), 50);

                IDataMiningDao dm = (IDataMiningDao)DaoConfig.getDao(IDataMiningDao.class);
                ReadingListInfo[] lists = dm.getTopVisitedReadingLists(max);

                MessageFormat fmt = new MessageFormat(
                    "<tr><td>{0}</td><td><a href=\"mailto:{1}\">{2}</a></td><td>{3}</td><td>{4} / {5}</td></tr>");
                for (ReadingListInfo list : lists)
                {
                    boolean active = list.active;
                    String escapedTitle = StringUtils.encodeForURL(list.title);
                    String url = "http://www.blogbridge.com/rl/" + list.userId + "/" + escapedTitle + ".opml";

                    String paramTitle = "";
                    if (active) paramTitle += "<a href=\"" + url + "\">";
                    paramTitle += list.title;
                    if (active) paramTitle += "</a>";

                    out.println(fmt.format(new Object[] {
                        paramTitle,
                        list.userEmail,
                        list.userFullName.replaceAll("\\s", "&nbsp;"),
                        list.lastSyncTime,
                        list.totalVisits,
                        list.uniqueVisits
                    }));
                }
            %>
        </table>
    </div>
</div>
</body>
</html>