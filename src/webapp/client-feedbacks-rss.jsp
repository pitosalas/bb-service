<%@ page import="com.salas.bbservice.persistence.IClientErrorDao,
                 com.salas.bbservice.persistence.DaoConfig,
                 java.util.Date,
                 java.text.DateFormat,
                 com.salas.bbservice.persistence.IFeedbackMessageDao,
                 com.salas.bbservice.domain.FeedbackMessage,
                 java.text.SimpleDateFormat"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<rss version="2.0">
<channel>
	<title>BB Service - Feedbacks</title>
<%--	<link><%= request.getServletPath() %></link>--%>
	<description>User Feedback</description>
	<language>en_US</language>

  <%
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    IFeedbackMessageDao dao = (IFeedbackMessageDao)DaoConfig.getDao(IFeedbackMessageDao.class);

    int offset = 0;
    int limit = 20;
    int cutLimit = 50;

    String limitS = request.getParameter("limit");

    try
    {
      limit = Integer.parseInt(limitS);
    } catch (RuntimeException e)
    {
    }

    FeedbackMessage[] messages = dao.select(offset, limit);

    for (int i = 0; i < messages.length; i++)
    {
      FeedbackMessage message = messages[i];
      Date date = new Date(message.getTime());

      String title = message.getMessage();
      int length = title.indexOf('\n');
      if (length == -1 || length > cutLimit) length = cutLimit;
      if (length < title.length()) title = title.substring(0, length) + "...";

      String description = message.getMessage();
      description = description.replaceAll("\n", "<br>");

      out.println("<item>");
      out.println("  <title><![CDATA[" + title + "]]></title>");
      out.println("  <description><![CDATA[" + description + "]]></description>");
      out.println("  <pubDate>" + dateFormat.format(date) + "</pubDate>");
      out.println("</item>");
    }
  %>

</channel>
</rss>
