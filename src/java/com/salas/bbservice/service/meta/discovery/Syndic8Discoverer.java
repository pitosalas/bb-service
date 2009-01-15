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
// $Id: Syndic8Discoverer.java,v 1.1.1.1 2006/10/23 13:55:46 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.utils.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.StringTokenizer;

/**
 * Syndic8 discoverer implementation.
 */
public class Syndic8Discoverer implements IBlogDiscoverer
{
    private static final String SERVICE_URL         = "http://www.syndic8.com/xmlrpc.php";
    private static final String FUNCTION_FIND_FEEDS = "syndic8.FindFeeds";
    private static final String FUNCTION_FIND_SITES = "syndic8.FindSites";
    private static final String FUNCTION_GET_INFO   = "syndic8.GetFeedInfo";

    private static final String[] UNSIGNIFICANT_DOMAIN_PARTS = new String[]{ "co" };

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
        return FIELD_DATA_URL | FIELD_HTML_URL | FIELD_TITLE | FIELD_AUTHOR | FIELD_DESCRIPTION;
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

        try
        {
            Vector ids = findFeedIDs(client, url);
            Hashtable dataPacket = findMatchingFeed(client, ids, url);

            if (dataPacket != null) discoveredFields = copyData(dataPacket, source);
        } catch (XmlRpcException e)
        {
            throw new IOException("Problem with XML RPC packet.");
        } catch (Exception e)
        {
            throw new IOException("Problem with service or communication.");
        }

        return discoveredFields;
    }

    /**
     * Queries Syndic8 service for ID's of feeds having given URL somewhere in info fields.
     *
     * @param aClient   XML-RPC client to use.
     * @param url       URL to look for.
     *
     * @return list of ID's or NULL.
     *
     * @throws XmlRpcException  in case of XML-RPC specific error.
     * @throws IOException      in case of communication problems.
     */
    Vector findFeedIDs(XmlRpcClient aClient, URL url)
        throws XmlRpcException, IOException
    {
        Vector args = new Vector();
        args.add(url.toString());

        // Find ID's of feeds by data URL.
        Vector ids = (Vector)aClient.execute(FUNCTION_FIND_FEEDS, args);

        // If ID's were not found then try find by Site URL.
        if (ids == null || ids.size() == 0)
        {
            ids = (Vector)aClient.execute(FUNCTION_FIND_SITES, args);
        }

        return ids;
    }

    /**
     * Looks for the feed which is most close to what we need.
     *
     * @param aClient   XML-RPC client to use.
     * @param aFeedIDs  ID's of feeds or NULL if none.
     * @param aUrl      original URL we were using for discovery.
     *
     * @return data packet with feed info fields or NULL if nothing could be found.
     *
     * @throws XmlRpcException  in case of XML-RPC specific error.
     * @throws IOException      in case of communication problems.
     */
    private Hashtable findMatchingFeed(XmlRpcClient aClient, Vector aFeedIDs, URL aUrl)
        throws XmlRpcException, IOException
    {
        Hashtable dataPacket = null;

        if (aFeedIDs != null && aFeedIDs.size() > 0)
        {
            for (int i = 0; dataPacket == null && i < aFeedIDs.size(); i++)
            {
                Integer feedId = (Integer)aFeedIDs.get(i);
                Hashtable feedInfo = getFeedById(aClient, feedId);
                if (isMatchingFeed(feedInfo, aUrl)) dataPacket = feedInfo;
            }
        }

        return dataPacket;
    }

    /**
     * Queries service for info fields of feed pointed out by ID.
     *
     * @param aClient   XML-RPC client to use.
     * @param aFeedId   ID of the feed to load.
     *
     * @return data packet with feed info.
     *
     * @throws XmlRpcException  in case of XML-RPC specific error.
     * @throws IOException      in case of communication problems.
     */
    Hashtable getFeedById(XmlRpcClient aClient, Integer aFeedId)
        throws XmlRpcException, IOException
    {
        Vector args = new Vector();
        args.add(aFeedId);
        return (Hashtable)aClient.execute(FUNCTION_GET_INFO, args);
    }

    /**
     * Tells if the feed with given information is OK for the original URL.
     *
     * @param aFeedInfo feed info discovered.
     * @param aUrl      original URL we were using for discovery.
     *
     * @return TRUE if this info can be used as result of discovery and further watching can be
     *         stoped.
     */
    static boolean isMatchingFeed(Hashtable aFeedInfo, URL aUrl)
    {
        boolean ok = false;

        if (aFeedInfo != null && aUrl != null)
        {
            String host = aUrl.getHost().toLowerCase();

            String dataUrl = (String)aFeedInfo.get("dataurl");
            boolean dataUrlFound = dataUrl != null;
            boolean dataUrlOk = false;
            try
            {
                if (dataUrlFound)
                {
                    URL dUrl = new URL(dataUrl);
                    String dUrlHost = dUrl.getHost().toLowerCase();
                    dataUrlOk = domainsAreMatching(dUrlHost, host);
                }
            } catch (MalformedURLException e)
            {
                // Data URL is invalid -- remove it from data
                aFeedInfo.remove("dataurl");
                dataUrlFound = false;
            }

            String siteUrl = (String)aFeedInfo.get("siteurl");
            boolean siteUrlFound = siteUrl != null;
            boolean siteUrlOk = false;
            try
            {
                if (siteUrlFound)
                {
                    URL sUrl = new URL(siteUrl);
                    String sUrlHost = sUrl.getHost().toLowerCase();
                    siteUrlOk = domainsAreMatching(sUrlHost, host);
                }
            } catch (MalformedURLException e)
            {
                // Site URL is invalid -- remove it from data
                aFeedInfo.remove("siteurl");
                siteUrlFound = false;
            }

            ok = (siteUrlFound && siteUrlOk && ((dataUrlFound && dataUrlOk) || !dataUrlFound)) ||
                 (dataUrlFound && dataUrlOk && ((siteUrlFound && siteUrlOk) || !siteUrlFound));
        }

        return ok;
    }

    /**
     * Tests the domains of two hosts to be matching. It's allowed to not to match completely, but
     * only last significant parts. It means that "www.noizeramp.com" will match
     * "blog.noizeramp.com" and "noizeramp.com", but not "com" or "salas.com". Also, "bells.co.uk"
     * will not match "whistles.co.uk".
     *
     * @param firstHostName     first host name.
     * @param secondHostName    second host name.
     *
     * @return TRUE if match.
     */
    static boolean domainsAreMatching(String firstHostName, String secondHostName)
    {
        if (firstHostName == null || secondHostName == null) return false;

        String[] first = breakHostName(firstHostName);
        String[] second = breakHostName(secondHostName);

        int tailFirst = first.length - 1;
        int tailLast = second.length - 1;
        int matched = 0;
        boolean finished = false;

        while ((tailLast > -1) && (tailFirst > -1) && !finished)
        {
            String firstTok = first[tailFirst--];
            String secondTok = second[tailLast--];

            finished = firstTok == null || secondTok == null ||
                !firstTok.equalsIgnoreCase(secondTok);

            if (!finished) matched++;
        }

        boolean matching = false;
        if (matched > 2)
        {
            matching = true;
        } else if (matched == 2)
        {
            String src = second[second.length - 2];
            matching = isSignificantPreLastDomainPart(src);
        }

        return matching;
    }

    /**
     * Finds out if the part of domain name before the last is significant
     * (not "co" in "something.co.uk" or something similar).
     *
     * @param domainPart    pre-last domain part.
     *
     * @return TRUE if significant.
     */
    static boolean isSignificantPreLastDomainPart(String domainPart)
    {
        boolean found = false;
        int i = 0;
        while (i < UNSIGNIFICANT_DOMAIN_PARTS.length &&
            !(found = UNSIGNIFICANT_DOMAIN_PARTS[i].equalsIgnoreCase(domainPart)))
        {
            i++;
        }

        return !found;
    }

    /**
     * Breaks host name into pieces using "." as delimiter.
     *
     * @param host host name.
     *
     * @return array of pieces.
     */
    static String[] breakHostName(String host)
    {
        if (host == null) return null;
        
        StringTokenizer tok = new StringTokenizer(host, ".");
        int tokens = tok.countTokens();

        String[] tokList = new String[tokens];
        for (int i = 0; i < tokens; i++) tokList[i] = tok.nextToken();

        return tokList;
    }

    /**
     * Copies data from data packet to blog object.
     *
     * @param dataPacket    data packet.
     * @param blog          blog.
     *
     * @return OR'ed discovered fields.
     */
    static int copyData(Hashtable dataPacket, Blog blog)
    {
        int aDiscoveredFields = 0;

        // Data URL
        String dataUrl = (String)dataPacket.get("dataurl");
        if (StringUtils.isEmpty(blog.getDataUrl()) && !StringUtils.isEmpty(dataUrl))
        {
            blog.setDataUrl(dataUrl);
            aDiscoveredFields |= FIELD_DATA_URL;
        }

        // Site URL
        String htmlUrl = (String)dataPacket.get("siteurl");
        if (StringUtils.isEmpty(blog.getHtmlUrl()) && !StringUtils.isEmpty(htmlUrl))
        {
            blog.setHtmlUrl(htmlUrl);
            aDiscoveredFields |= FIELD_HTML_URL;
        }

        // Title
        String title = (String)dataPacket.get("sitename");
        if (StringUtils.isEmpty(blog.getTitle()) && !StringUtils.isEmpty(title))
        {
            blog.setTitle(title);
            aDiscoveredFields |= FIELD_TITLE;
        }

        // Author
        String author = (String)dataPacket.get("creator");
        if (author == null) author = (String)dataPacket.get("editor");
        if (author == null) author = (String)dataPacket.get("webmaster");
        if (StringUtils.isEmpty(blog.getAuthor()) && !StringUtils.isEmpty(author))
        {
            blog.setAuthor(author);
            aDiscoveredFields |= FIELD_AUTHOR;
        }

        // Description
        String description = (String)dataPacket.get("description");
        if (StringUtils.isEmpty(blog.getDescription()) && !StringUtils.isEmpty(description))
        {
            blog.setDescription(description);
            aDiscoveredFields |= FIELD_DESCRIPTION;
        }

        return aDiscoveredFields;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "Syndic8Discoverer";
    }
}
