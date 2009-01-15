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
// $Id: TestSyndic8Discoverer.java,v 1.1.1.1 2006/10/23 13:56:02 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import junit.framework.TestCase;

import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;

/**
 * @see Syndic8Discoverer
 */
public class TestSyndic8Discoverer extends TestCase
{
    /**
     * @see Syndic8Discoverer#copyData
     */
    public void testCopyData()
    {
        Hashtable dataPacket = new Hashtable();
        Blog blog;
        String value;

        dataPacket.clear();
        blog = new Blog();
        value = "data";
        dataPacket.put("dataurl", value);
        assertEquals(IBlogDiscoverer.FIELD_DATA_URL, Syndic8Discoverer.copyData(dataPacket, blog));
        assertEquals(value, blog.getDataUrl());

        dataPacket.clear();
        blog = new Blog();
        value = "site";
        dataPacket.put("siteurl", value);
        assertEquals(IBlogDiscoverer.FIELD_HTML_URL, Syndic8Discoverer.copyData(dataPacket, blog));
        assertEquals(value, blog.getHtmlUrl());

        dataPacket.clear();
        blog = new Blog();
        value = "title";
        dataPacket.put("sitename", value);
        assertEquals(IBlogDiscoverer.FIELD_TITLE, Syndic8Discoverer.copyData(dataPacket, blog));
        assertEquals(value, blog.getTitle());

        dataPacket.clear();
        blog = new Blog();
        dataPacket.put("creator", "creator");
        dataPacket.put("editor", "editor");
        dataPacket.put("webmaster", "webmaster");
        assertEquals(IBlogDiscoverer.FIELD_AUTHOR, Syndic8Discoverer.copyData(dataPacket, blog));
        assertEquals("creator", blog.getAuthor());

        dataPacket.clear();
        blog = new Blog();
        dataPacket.put("editor", "editor");
        dataPacket.put("webmaster", "webmaster");
        assertEquals(IBlogDiscoverer.FIELD_AUTHOR, Syndic8Discoverer.copyData(dataPacket, blog));
        assertEquals("editor", blog.getAuthor());

        dataPacket.clear();
        blog = new Blog();
        dataPacket.put("webmaster", "webmaster");
        assertEquals(IBlogDiscoverer.FIELD_AUTHOR, Syndic8Discoverer.copyData(dataPacket, blog));
        assertEquals("webmaster", blog.getAuthor());

        dataPacket.clear();
        blog = new Blog();
        value = "descr";
        dataPacket.put("description", value);
        assertEquals(IBlogDiscoverer.FIELD_DESCRIPTION, Syndic8Discoverer.copyData(dataPacket, blog));
        assertEquals(value, blog.getDescription());

        dataPacket.clear();
        blog = new Blog();
        dataPacket.put("creator", "creator");
        dataPacket.put("editor", "editor");
        dataPacket.put("webmaster", "webmaster");
        dataPacket.put("sitename", "sitename");
        dataPacket.put("siteurl", "siteurl");
        dataPacket.put("dataurl", "dataurl");
        dataPacket.put("description", "descr");
        int discovered = IBlogDiscoverer.FIELD_AUTHOR |
            IBlogDiscoverer.FIELD_DATA_URL |
            IBlogDiscoverer.FIELD_DESCRIPTION |
            IBlogDiscoverer.FIELD_HTML_URL |
            IBlogDiscoverer.FIELD_TITLE;

        assertEquals(discovered, Syndic8Discoverer.copyData(dataPacket, blog));
    }

    /**
     * Tests how feed info matcher works.
     *
     * @throws Exception in error case.
     */
    public void testIsMatchingFeed() throws Exception
    {
        Hashtable feedInfo = prepareFeedInfo(null, null);
        URL shortUrl = new URL("http://noizeramp.com/");
        URL longUrl = new URL("http://blog.noizeramp.com/");
        URL shortUrl2 = new URL("http://www.salas.com/");

        // No URL's
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, null));
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, shortUrl));
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, longUrl));

        // Data URL is returned
        feedInfo = prepareFeedInfo("http://blog.noizeramp.com/index.xml", null);
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, null));
        assertTrue(Syndic8Discoverer.isMatchingFeed(feedInfo, shortUrl));
        assertTrue(Syndic8Discoverer.isMatchingFeed(feedInfo, longUrl));
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, shortUrl2));

        // Site URL is returned
        feedInfo = prepareFeedInfo(null, "http://blog.noizeramp.com/");
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, null));
        assertTrue(Syndic8Discoverer.isMatchingFeed(feedInfo, shortUrl));
        assertTrue(Syndic8Discoverer.isMatchingFeed(feedInfo, longUrl));
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, shortUrl2));

        // Site URL is OK, but Data URL points to somewhere else
        feedInfo = prepareFeedInfo("http://www.salas.com/index.rdf", "http://blog.noizeramp.com/");
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, null));
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, shortUrl));
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, longUrl));
        assertFalse(Syndic8Discoverer.isMatchingFeed(feedInfo, shortUrl2));

        // Both URL's returned, but site URL is damaged
        feedInfo = prepareFeedInfo("http://www.salas.com/index.rdf", "hpts://www.salas.com/");
        assertTrue(Syndic8Discoverer.isMatchingFeed(feedInfo, shortUrl2));
        assertNull(feedInfo.get("siteurl"));

        // Both URL's returned, but data URL is damaged
        feedInfo = prepareFeedInfo("hasa://www.salas.com/index.rdf", "http://www.salas.com/");
        assertTrue(Syndic8Discoverer.isMatchingFeed(feedInfo, shortUrl2));
        assertNull(feedInfo.get("dataurl"));
    }

    /**
     * Tests selective discovery.
     *
     * @throws Exception in error case.
     */
    public void testSelectiveDiscovery() throws Exception
    {
        URL url1 = new URL("http://www.salas.com/");
        URL url2 = new URL("http://noizeramp.com/");
        URL url3 = new URL("http://nba.com/");

        Hashtable[] feedInfos = new Hashtable[]
        {
            prepareFeedInfo("http://blog.noizeramp.com/index.xml", "http://www.salas.com/"),
            prepareFeedInfo("http://blog.noizeramp.com/index.xml", "http://blog.noizeramp.com/"),
            prepareFeedInfo("http://www.nba.com/news.xml", null)
        };
        TstSyndic8Discoverer discoverer = new TstSyndic8Discoverer(feedInfos);

        // There's no site/data pair where both feeds were pointing to the same site
        // or only one of them present -- fail
        assertEquals(0, discoverer.discover(new Blog(), url1));

        // Second item in list is win
        assertEquals(IBlogDiscoverer.FIELD_DATA_URL | IBlogDiscoverer.FIELD_HTML_URL,
            discoverer.discover(new Blog(), url2));

        // There's record with data URL only and it's a win
        assertEquals(IBlogDiscoverer.FIELD_DATA_URL, discoverer.discover(new Blog(), url3));
    }

    /**
     * Tests how the domain names match is detected.
     */
    public void testDomainsAreMatching()
    {
        // Nulls handling
        assertFalse(Syndic8Discoverer.domainsAreMatching(null, null));
        assertFalse(Syndic8Discoverer.domainsAreMatching("a", null));
        assertFalse(Syndic8Discoverer.domainsAreMatching(null, "a"));

        // Matches
        assertTrue(Syndic8Discoverer.domainsAreMatching("noizeramp.com", "noizeramp.com"));
        assertTrue(Syndic8Discoverer.domainsAreMatching("blog.noizeramp.com", "noizeramp.com"));
        assertTrue(Syndic8Discoverer.domainsAreMatching("blog.noizeramp.com", "www.noizeramp.com"));
        assertTrue(Syndic8Discoverer.domainsAreMatching("noizeramp.com", "www.noizeramp.com"));

        // Non-matches
        assertFalse(Syndic8Discoverer.domainsAreMatching("noizeramp.com", "com"));
        assertFalse(Syndic8Discoverer.domainsAreMatching("noizeramp.com", "salas.com"));
        assertFalse(Syndic8Discoverer.domainsAreMatching("bells.co.uk", "whistles.co.uk"));
    }

    /**
     * Test the significance detection.
     */
    public void testIsSignificantDomainPart()
    {
        assertTrue(Syndic8Discoverer.isSignificantPreLastDomainPart("noizeramp"));
        assertFalse(Syndic8Discoverer.isSignificantPreLastDomainPart("co"));
    }

    /**
     * Test the breaking of host name into pieces.
     */
    public void testBreakHostName()
    {
        assertNull(Syndic8Discoverer.breakHostName(null));

        assertEquals(0, Syndic8Discoverer.breakHostName("").length);

        assertEquals(1, Syndic8Discoverer.breakHostName("com").length);
        assertEquals("com", Syndic8Discoverer.breakHostName("com")[0]);

        assertEquals(2, Syndic8Discoverer.breakHostName("noizeramp.com").length);
        assertEquals("noizeramp", Syndic8Discoverer.breakHostName("noizeramp.com")[0]);
        assertEquals("com", Syndic8Discoverer.breakHostName("noizeramp.com")[1]);
    }

    private static Hashtable prepareFeedInfo(String dataUrl, String siteUrl)
    {
        Hashtable feedInfo = new Hashtable();
        if (dataUrl != null) feedInfo.put("dataurl", dataUrl);
        if (siteUrl != null) feedInfo.put("siteurl", siteUrl);

        return feedInfo;
    }

    /** Simple syndic8 discoverer using predefined set of feeds. */
    private static class TstSyndic8Discoverer extends Syndic8Discoverer
    {
        private Hashtable[] feedInfos;
        private Vector      ids;

        public TstSyndic8Discoverer(Hashtable[] aFeedInfos)
        {
            feedInfos = aFeedInfos;

            ids = new Vector(aFeedInfos.length);
            for (int i = 0; i < aFeedInfos.length; i++)
            {
                ids.add(new Integer(i));
            }
        }

        // Returns fake ID's
        Vector findFeedIDs(XmlRpcClient aClient, URL url)
            throws XmlRpcException, IOException
        {
            return ids;
        }

        // Returns pre-defined feed info.
        Hashtable getFeedById(XmlRpcClient aClient, Integer aFeedId)
        {
            int index = aFeedId.intValue();
            return feedInfos[index];
        }
    }
}
