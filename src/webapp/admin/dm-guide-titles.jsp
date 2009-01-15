<%@ page import="com.salas.bbservice.domain.dao.ValueCount,
                 com.salas.bbservice.persistence.DaoConfig,
                 com.salas.bbservice.persistence.IDataMiningDao" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="com.salas.bbservice.utils.StringUtils"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Data Mining - Guide Titles</title>
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
            &nbsp;&nbsp;max=N (default 50) - number of guide titles to display
        </div>

        <table>
            <thead>
                <tr>
                    <td colspan="2">Guide Names</td>
                </tr>
            </thead>
            <tr>
                <td>Name</td>
                <td>Count</td>
            </tr>
            <%
                int max = StringUtils.toInt(request.getParameter("max"), 50);

                IDataMiningDao dm = (IDataMiningDao)DaoConfig.getDao(IDataMiningDao.class);
                ValueCount[] topGuideNames = dm.getTopGuideNames(max);

                MessageFormat fmt = new MessageFormat("<tr><td>{0}</td><td>{1}</td></tr>");
                for (int i = 0; i < topGuideNames.length; i++)
                {
                    ValueCount gn = topGuideNames[i];
                    out.println(fmt.format(new Object[] {
                        gn.getValue(),
                        new Integer(gn.getCount()) }));
                }
            %>
        </table>
    </div>
</div>
</body>
</html>