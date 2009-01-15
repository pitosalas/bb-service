<%@ page import="com.salas.bbservice.tools.ThreadDumpBean"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Map"%>

<HTML>
<BODY>
<h2>Thread Summary:</h2>
<table cellpadding="5" cellspacing="5">
    <tr>

        <th>Thread</th>
        <th>State</th>
        <th>Priority</th>
        <th>Daemon</th>

    </tr>
    <%
        ThreadDumpBean b = new ThreadDumpBean();
        Collection<Thread> t = b.getThreads();
        for (Thread th : t)
        {
            out.println("<tr>");
            out.println("<td><a href='#" + th.getId() + "'>" + th.getName() + "</a></td>");
            out.println("<td>" + th.getState() + "</td>");
            out.println("<td>" + th.getPriority() + "</td>");
            out.println("<td>" + th.isDaemon() + "</td>");
            out.println("</tr>");
        }
    %>
</table>

<h2>Stack Trace of JVM:</h2>
<%
    Map<Thread, StackTraceElement[]> trs = b.getTraces();
    for (Map.Entry en : trs.entrySet())
    {
        Thread th = (Thread)en.getKey();
        StackTraceElement[] st = (StackTraceElement[])en.getValue();

        out.println("<h4><a name='" + th.getId() + "'>" + th.toString() + "</a></h4>");
        out.println("<pre>");
        for (StackTraceElement s : st)
        {
            out.println("        at " + s);
        }
        out.println("</pre>");
    }
%>
</BODY>
</HTML>