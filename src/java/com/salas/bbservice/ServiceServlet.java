// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002, 2003, 2004 by R. Pito Salas
//
// This program is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software Foundation;
// either version 2 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
// without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with this program;
// if not, write to the Free Software Foundation, Inc., 59 Temple Place,
// Suite 330, Boston, MA 02111-1307 USA
//
// Contact: R. Pito Salas
// mailto:pitosalas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: ServiceServlet.java,v 1.7 2007/07/04 09:39:08 alg Exp $
//

package com.salas.bbservice;

import com.salas.bbservice.ping.PingHandler;
import com.salas.bbservice.plans.PlansHandler;
import com.salas.bbservice.service.account.AccountHandler;
import com.salas.bbservice.service.connect.ConnectHandler;
import com.salas.bbservice.service.forum.ForumHandler;
import com.salas.bbservice.service.meta.MetaHandler;
import com.salas.bbservice.service.reports.ReportsHandler;
import com.salas.bbservice.service.sync.SynchronizationHandler;
import com.salas.bbservice.service.tags.TagsHandler;
import com.salas.bbservice.service.updates.UpdatesHandler;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service servlet.
 */
public class ServiceServlet extends XmlRpcServlet
{
    private static final Logger LOG = Logger.getLogger(ServiceServlet.class.getName());
    
    static
    {
        // Setting HTTP user-agent
        System.setProperty("http.agent", "BlogBridge Service (http://www.blogbridge.com/)");

        // setup of logging
        Logger informaLogger = Logger.getLogger("de.nava.informa");
        informaLogger.setLevel(Level.WARNING);

        // disable SSL sertificates
        disableSSLCertificates();

        // Set proper networking timeouts
        System.setProperty("sun.net.client.defaultReadTimeout", "30000");
        System.setProperty("sun.net.client.defaultConnectTimeout", "20000");
    }

    private static void disableSSLCertificates()
    {
        // Create a trust manager that does not validate certificate chains
       TrustManager[] trustAllCerts = new TrustManager[]
       {
           new X509TrustManager()
           {
               public java.security.cert.X509Certificate[] getAcceptedIssuers()
               {
                   return null;
               }

               public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                   String authType)
               {
               }

               public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                   String authType)
               {
               }
           }
       };

       // Install the all-trusting trust manager
       try
       {
           SSLContext sc = SSLContext.getInstance("SSL");
           sc.init(null, trustAllCerts, new java.security.SecureRandom());
           HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
       } catch (Exception e)
       {
           if (LOG.isLoggable(Level.WARNING))
           {
               LOG.log(Level.WARNING, "Probably SSL certificates validation wasn't disabled.", e);
           }
       }
    }

    @Override
    protected XmlRpcServletServer newXmlRpcServer(ServletConfig servletConfig)
            throws XmlRpcException
    {
        return new XmlRpcServletServer()
        {
            @Override
            protected XmlRpcHttpRequestConfigImpl newConfig(HttpServletRequest httpServletRequest)
            {
                return new HandlerConfig(httpServletRequest.getRemoteAddr());
            }
        };
    }

    /**
     * Creates and returns the handler mapping.
     *
     * @return mapping table.
     *
     * @throws XmlRpcException in case of any mapping exception.
     */
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException
    {
        PropertyHandlerMapping mapping = new PropertyHandlerMapping();

        RequestProcessorFactoryFactory factory =
            new RequestProcessorFactoryFactory.RequestSpecificProcessorFactoryFactory()
            {
                @Override
                protected Object getRequestProcessor(Class aClass, XmlRpcRequest xmlRpcRequest)
                        throws XmlRpcException
                {
                    Object processor = super.getRequestProcessor(aClass, xmlRpcRequest);
                    if (processor instanceof IInitializableHandler)
                    {
                        ((IInitializableHandler)processor).init((HandlerConfig)xmlRpcRequest.getConfig());
                    }
                    return processor;
                }
            };
        mapping.setRequestProcessorFactoryFactory(factory);

        mapping.addHandler("accounts", AccountHandler.class);
        mapping.addHandler("sync", SynchronizationHandler.class);
        mapping.addHandler("ping", PingHandler.class);
        mapping.addHandler("meta", MetaHandler.class);
        mapping.addHandler("reports", ReportsHandler.class);
        mapping.addHandler("tags", TagsHandler.class);
        mapping.addHandler("updates", UpdatesHandler.class);
        mapping.addHandler("forum", ForumHandler.class);
        mapping.addHandler("plans", PlansHandler.class);
        mapping.addHandler("connect", ConnectHandler.class);

        return mapping;
    }
}
