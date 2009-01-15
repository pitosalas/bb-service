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
// mailto:pito_salas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: BlogstreetDiscoverer.java,v 1.1.1.1 2006/10/23 13:55:44 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.Map;

/**
 * Blogstreet discoverer implementation.
 */
public class BlogstreetDiscoverer implements IBlogDiscoverer
{
    private static final String SERVICE_URL = "http://www.blogstreet.com/xrbin/xmlrpc.cgi";
    private static final String FUNCTION = "blogstreet.getRSS";

    private static final XmlRpcClient client;

    static {
        XmlRpcClient cl = null;

        try
        {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(SERVICE_URL));
            cl = new XmlRpcClient();
            cl.setConfig(config);
        } catch (MalformedURLException e)
        {
            // Problem with service URL
        } finally
        {
            client = cl;
        }
    }
    /**
     * Sets the properties for discoverer.
     *
     * @param properties properties.
     */
    public void setProperties(Map properties)
    {
    }

    /**
     * Returns the OR'ed list of fields provided by this discoverer.
     *
     * @return list of fields.
     */
    public int getProvidedFields()
    {
        return FIELD_DATA_URL;
    }

    /**
     * Makes a try to discover the URL and update information in the source blog.
     *
     * @param source source blog.
     * @param url    url to discover.
     *
     * @return fields actually discovered.
     *
     * @throws IOException in case of problems with communications.
     */
    public int discover(Blog source, URL url) throws IOException
    {
        if (client == null) return 0;
        int discoveredFields = 0;

        // Prepare arguments
        Vector args = new Vector();
        args.add(url.toString());

        // Call service. Result is single string with URL or error message.
        String res = null;

        try
        {
            res = (String)client.execute(FUNCTION, args);
        } catch (XmlRpcException e)
        {
            throw new IOException("Problem with XML RPC packet framework.");
        }

        if (res != null && res.startsWith("http://"))
        {
            source.setDataUrl(res.trim());
            discoveredFields = FIELD_DATA_URL;
        }

        return discoveredFields;
    }
}
