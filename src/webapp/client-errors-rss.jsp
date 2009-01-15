<%@ page import="com.salas.bbservice.persistence.IClientErrorDao,
                 com.salas.bbservice.persistence.DaoConfig,
                 com.salas.bbservice.domain.ClientError,
                 com.salas.bbservice.domain.User,
                 java.util.Date,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<rss version="2.0">
<channel>
	<title>BB Service - Errors</title>
<%--	<link><%= request.getServletPath() %></link> --%>
	<description>User Errors</description>
	<language>en_US</language>

  <%
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    IClientErrorDao dao = (IClientErrorDao)DaoConfig.getDao(IClientErrorDao.class);

    int offset = 0;
    int limit = 20;

    String limitS = request.getParameter("limit");

    try
    {
      limit = Integer.parseInt(limitS);
    } catch (RuntimeException e)
    {
    }

    ClientError[] errors = dao.select(offset, limit);
    int total = dao.getErrorsCount();
  %>

  <%
    for (int i = 0; i < errors.length; i++)
    {
      ClientError error = errors[i];
      Date date = new Date(error.getTime());

      String details = error.getDetails();
      details = details.replaceAll("&", "&amp;");
      details = details.replaceAll("<", "&lt;");
      details = details.replaceAll(">", "&gt;");
      details = details.replaceAll("'", "&apos;");
      details = details.replaceAll("\"", "&quot;");
      details = details.replaceAll("\n", "<br>");

      out.println("<item>");
      out.println("  <title><![CDATA[[" + error.getVersion() + "] <" + dateFormat.format(date) +
          "> " + error.getMessage() + "]]></title>");
      out.println("  <description><![CDATA[" + details + "]]></description>");
      out.println("  <pubDate>" + dateFormat.format(date) + "</pubDate>");
      out.println("</item>");
    }
  %>

</channel>
</rss>
