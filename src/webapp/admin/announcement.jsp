<%@ page import="java.util.Properties" %>
<%@ page import="com.salas.bbservice.utils.StringUtils" %>
<%@ page import="com.salas.bbservice.persistence.IUserDao"%>
<%@ page import="java.text.MessageFormat"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Post Annoucement</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
</head>

<!--[if lte IE 6]>
<style type="text/css">
  body { behavior:url("../scripts/csshover.htc"); }
</style>
<![endif]-->

<style type="text/css">
    table {
        width: 100%;
    }

    td {
        border: 0;
        width: 100%;
    }

    td.c1 {
        width: 10%;
    }

    td.c2 {
        width: 90%;
    }

    input, textarea {
        border: 1px solid #ccc;
        padding: 2px;
    }

    #subject, #reAddress, #from {
        width: 100%;
    }

    #smtpServer, #smtpUser, #group {
        width: 200px;
    }

    #text {
        width: 100%;
        height: 300px;
    }
</style>

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

    <%--
    plain text only to start is ok;
    allow me to set return address;
    allow me to see the list of people sent to before actually sending;
    if large number then do an "are you sure";
    send one at a time;
    allow me to set smtp server;

    (15:20:59) spyromus: 1) we already have a big list of subscribers,
    do you really need "are you sure" thing or you wish to select whom you will send the letter?
    (15:22:07) R P Salas: 1) no i don't think i need individidual selection,
    but I do need "subscribed for updates" vs. "all activated" vs. "all registered"
    (15:22:42) R P Salas: the reason for the are you sure is to avoid due to "bugs"
    that the new command sends to the wrong list. just an extra safety valve because the
    action is so significant

    (15:21:21) spyromus: 2) about smtp server -- I thought we will be using F7?
    (15:22:59) R P Salas: 2) sometimes f7 smtp doesn't work but yes, that's the default

    (15:21:39) spyromus: 3) what is "send one at a time"?
    (15:23:15) R P Salas: 3) i am just copying what you said " It will take all subscribers'
    emails and send the letter to them one-by-one (or through BCC)"
    --%>

    <%
        String smtpServer = request.getParameter("smtpServer");
        String smtpUser = request.getParameter("smtpUser");
        String from = request.getParameter("from");
        String text = request.getParameter("text");
        String reAddress = request.getParameter("reAddress");
        String group = request.getParameter("group");
        String subject = request.getParameter("subject");
        String n = request.getParameter("n");

        if (group == null) group = "0";
        if (text == null) text = "";
        if (reAddress == null) reAddress = "";
        if (subject == null) subject = "";
        if (n == null) n = "";

        // mail
        ClassLoader classLoader = this.getClass().getClassLoader();
        Properties mailProps = new Properties();
        mailProps.load(classLoader.getResourceAsStream("mail.properties"));
        if (smtpServer == null) smtpServer = mailProps.getProperty("mail.host");
        if (smtpUser == null) smtpUser = mailProps.getProperty("mail.user");
        if (from == null) from = mailProps.getProperty("mail.from");

        String[] groups = IUserDao.USER_GROUPS;
    %>

    <form action="announcement-step2.jsp" method="post">
        <table>
            <tr>
                <td class="c1">SMTP&nbsp;Server:</td>
                <td class="c2"><input type="text" id="smtpServer" name="smtpServer" value="<%= smtpServer %>"></td>
            </tr>
            <tr>
                <td class="c1">Username:</td>
                <td class="c2"><input type="text" id="smtpUser" name="smtpUser" value="<%= smtpUser %>"></td>
            </tr>
            <tr>
                <td class="c1">From:</td>
                <td class="c2"><input type="text" id="from" name="from" value="<%= StringUtils.escape(from) %>"></td>
            </tr>
            <tr>
                <td class="c1">To:</td>
                <td class="c2">
                    <select id="group" name="group">
                        <%
                            MessageFormat fmt = new MessageFormat("<option value=\"{0}\" {1}>{2}</option>");
                            for (int i = 0; i < groups.length; i++)
                            {
                                String grp = groups[i];
                                String id = Integer.toString(i);
                                String sel = group.equals(id) ? "selected" : "";

                                out.println(fmt.format(new Object[] { id, sel, grp }));
                            }
                        %>
                    </select>
                    &nbsp;
                    N:&nbsp;<input type="text" id="n" name="n" value="<%= n %>">
                </td>
            </tr>
            <tr>
                <td class="c1">Reply&nbsp;To:</td>
                <td class="c2"><input id="reAddress" type="text" name="reAddress" value="<%= reAddress %>"></td>
            </tr>
            <tr>
                <td class="c1">Subject:</td>
                <td class="c2"><input type="text" id="subject" name="subject" value="<%= subject %>"></td>
            </tr>
            <tr>
                <td colspan="2"><textarea id="text" name="text"><%= text %></textarea></td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Send"></td>
            </tr>
        </table>
    </form>
</div>
</div>
</body>
</html>