<%
  String type = (String)request.getParameter("type");

  String feed = com.salas.bbservice.syndication.RSSSupport.generateFeed(type);

  out.println(feed);
%>