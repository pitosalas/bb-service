<%@ page import="java.util.Properties,
                 com.salas.bbservice.service.meta.MetaService,
                 com.salas.bbservice.utils.ApplicationProperties,
                 com.salas.bbservice.utils.Configuration,
                 java.util.Map"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Service configuration</title>
    <link rel="Stylesheet" type="text/css" href="../styles/global.css"/>
  </head>

  <style>
    /* Specific */
    #discoverers ul { margin: 0; padding: 0; }
    #discoverers li { margin: 0; padding: 0; list-style: none; }
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
      <%
        // database
        Properties dbProps = new Properties();
        ClassLoader classLoader = this.getClass().getClassLoader();
        dbProps.load(classLoader.getResourceAsStream("database.properties"));

        // discovery
        Map[] discoverers = Configuration.getDiscoverers();

        // mail
        Properties mailProps = new Properties();
        mailProps.load(classLoader.getResourceAsStream("mail.properties"));
      %>
      <div id="content">
        <table>
          <thead>
            <td colspan="2">Database</td>
          </thead>
          <tr>
            <td>Driver</td>
            <td><%= dbProps.getProperty("driver") %></td>
          </tr>
          <tr>
            <td>URL</td>
            <td><%= dbProps.getProperty("url") %></td>
          </tr>
          <tr>
            <td>Username</td>
            <td><%= dbProps.getProperty("username") %></td>
          </tr>
        </table>

        <table>
          <thead>
            <td colspan="2">Discovery</td>
          </thead>
          <tr>
            <td>Number of running threads</td>
            <td><%= Configuration.getDiscoveryThreads() %></td>
          </tr>
          <tr id="discoverers">
            <td>Discoverers</td>
            <td>
              <ul>
                <%
                  for (int i = 0; i < discoverers.length; i++)
                  {
                    out.println("<li>" + discoverers[i] + "</li>");
                  }
                %>
              </ul>
            </td>
          </tr>
          <tr>
            <td>Cleanup period</td>
            <td><%= Configuration.getBlogCleanupPeriod() %> minutes</td>
          </tr>
          <tr>
            <td>Blog life span</td>
            <td><%= Configuration.getBlogLifespan() %> minutes</td>
          </tr>
          <tr>
            <td>Minimum blogs to preserve</td>
            <td><%= Configuration.getMinBlogsInDatabase() %></td>
          </tr>
          <tr>
            <td>Incomplete blog update period</td>
            <td><%= Configuration.getIncompleteUpdatePeriod() / 1000 %> seconds</td>
          </tr>
          <tr>
            <td>Incomplete blog check period</td>
            <td><%= Configuration.getIncompleteCheckPeriod() / 1000 %> seconds</td>
          </tr>
          <tr>
            <td>Blog update period</td>
            <td><%= Configuration.getScheduledUpdatePeriod() / 1000 %> seconds</td>
          </tr>
          <tr>
            <td>Blog update check period</td>
            <td><%= Configuration.getScheduledCheckPeriod() / 1000 %> seconds</td>
          </tr>
        </table>

        <table>
          <thead>
            <td colspan="2">Mail</td>
          </thead>
          <tr>
            <td>Protocol</td>
            <td><%= mailProps.get("mail.transport.protocol") %></td>
          </tr>
          <tr>
            <td>Host</td>
            <td><%= mailProps.get("mail.host") %></td>
          </tr>
          <tr>
            <td>User</td>
            <td><%= mailProps.get("mail.user") %></td>
          </tr>
          <tr>
            <td>From</td>
            <td><%= mailProps.get("mail.from") %></td>
          </tr>
        </table>

        <table>
          <thead>
            <td colspan="2">Deployment</td>
          </thead>
          <tr>
            <td>Path</td>
            <td><%= Configuration.getDeployPath() %></td>
          </tr>
          <tr>
            <td>URL</td>
            <td><%= Configuration.getDeployURL() %></td>
          </tr>
        </table>
      </div>
    </div>
  </body>
</html>