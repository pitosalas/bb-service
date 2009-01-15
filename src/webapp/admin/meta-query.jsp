<%@ page import="com.salas.bbservice.service.meta.MetaHandler,
                 java.util.Hashtable"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  Hashtable result = null;
  String[] statuses;
  Integer statusI;
  String status = null;

  String url = request.getParameter("url");
  if (url != null && url.trim().length() > 0)
  {
    MetaHandler meta = new MetaHandler();
    result = meta.getBlogByUrl(url);

    statuses = new String[] { "VALID", "PROCESSING", "INVALID" };
    statusI = (Integer)result.get("code");
    status = statusI == null ? "Unknown" : statuses[statusI.intValue()];
  }
%>
<html>
  <head>
    <title>Meta-data query</title>
<%--
    <link rel="Stylesheet" type="text/css" title="Default" href="style.css"/>
--%>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
  </head>

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

      <div id="content">
        <div id="form">
          <form action="meta-query.jsp" method="get">
            URL:<input id="url" type="text" name="url" <% if (url != null) { out.print("value=\"" + url + "\""); } %>/>
            <input type="submit" value="Lookup"/>
          </form>
        </div>
<% if (result != null) { %>
        <div id="data">
          <table>
            <thead>
              <td>Parameter</td>
              <td>Value</td>
            </thead>
            <tr>
              <td>Status</td>
              <td><%= status %></td>
            </tr>
            <tr>
              <td>Title</td>
              <td><%= result.get("title") == null ? "" : result.get("title") %></td>
            </tr>
            <tr>
              <td>Author</td>
              <td><%= result.get("author") == null ? "" : result.get("author") %></td>
            </tr>
            <tr>
              <td>Description</td>
              <td><%= result.get("description") == null ? "" : result.get("description") %></td>
            </tr>
            <tr>
              <td>HTML URL</td>
              <td><%= result.get("htmlUrl") == null ? "" : result.get("htmlUrl") %></td>
            </tr>
            <tr>
              <td>Data URL</td>
              <td><%= result.get("dataUrl") == null ? "" : result.get("dataUrl") %></td>
            </tr>
            <tr>
              <td>Inbound Links</td>
              <td><%= result.get("inboundLinks") == null ? "" : result.get("inboundLinks") %></td>
            </tr>
            <tr>
              <td>Category</td>
              <td><%= result.get("category") == null ? "" : result.get("category") %></td>
            </tr>
            <tr>
              <td>Location</td>
              <td><%= result.get("location") == null ? "" : result.get("location") %></td>
            </tr>
          </table>
        </div>
<% } %>
      </div>
    </div>
  </body>
</html>