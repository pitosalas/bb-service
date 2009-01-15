<%@ page import="com.salas.bbservice.stats.StatsProcessor"%>
<%@ page import="com.salas.bbservice.stats.StatsTable"%>
<%@ page import="com.salas.bbservice.stats.StatsOutputHelper"%>
<%@ page import="com.salas.bbservice.utils.StringUtils"%>
<%@ page import="com.salas.bbservice.domain.dao.ValueCountDate" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="com.salas.bbservice.persistence.IStatsDao" %>
<%@ page import="java.util.List" %>
<%@ page import="com.salas.bbservice.persistence.DaoConfig" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Statistics - Top Synchronizers</title>
  <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
</head>
<style type="text/css">
  .data td+td { width: 70px; }
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

    <div id="tips">
        Parameters:<br>
        &nbsp;&nbsp;max=N (default 50) - number of users to display
    </div>

    <div id="content">
        <table class="top-people">
            <thead>
                <tr>
                    <td colspan="3">Top Synchronizers</td>
                </tr>
            </thead>
            <tr>
                <td class="nam">User</td>
                <td class="tim">Last&nbsp;Sync&nbsp;Time</td>
                <td class="val">Synchronizations</td>
            </tr>
            <%
                int max = StringUtils.toInt(request.getParameter("max"), 50);

                IStatsDao sm = (IStatsDao)DaoConfig.getDao(IStatsDao.class);
                List<ValueCountDate> us = sm.getTopSynchronizers(max);

                MessageFormat fmt = new MessageFormat("<tr><td>{0}</td><td>{1}</td><td>{2}</td></tr>");
                for (ValueCountDate u : us)
                {
                    out.println(fmt.format(new Object[] { u.getValue(), u.getDate(), u.getCount() }));
                }
            %>
        </table>
    </div>
  </div>
</body>
</html>