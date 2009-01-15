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
// $Id: RSSSupport.java,v 1.1.1.1 2006/10/23 13:55:51 alg Exp $
//

package com.salas.bbservice.syndication;

import com.salas.bbservice.stats.Statistics;
import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.exporters.RSS_2_0_Exporter;
import de.nava.informa.impl.basic.ChannelBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Generates feed of different types.
 */
public class RSSSupport
{
    public static String generateFeed(String aType)
    {
        StringWriter feedStringWriter = new StringWriter();

        try
        {
            RSS_2_0_Exporter exporter = new RSS_2_0_Exporter(feedStringWriter, "UTF-8");
            ChannelIF feed = generateFeedObject(aType);
            if (feed != null) exporter.write(feed);
        } catch (IOException e)
        {
            // Error writing feed to string
        }

        return feedStringWriter.toString();
    }

    private static ChannelIF generateFeedObject(String aType)
    {
        ChannelBuilder builder = new ChannelBuilder();
        ChannelIF feed = null;

        if (aType == null || aType.equalsIgnoreCase("discoveryStats"))
        {
            feed = createDiscoverStatsFeed(builder);
        }

        return feed;
    }

    private static ChannelIF createDiscoverStatsFeed(ChannelBuilder aBuilder)
    {
        ChannelIF feed = aBuilder.createChannel("BBService - Discovery Stats");

        feed.setDescription("Discovery service statistics");
        setCommonFeedProperties(feed);

        long numberOfQueries = Statistics.getMetaQueries();
        long numberOfDBHits = Statistics.getMetaDatabaseHits();
        long numberOfNew = Statistics.getMetaNewDiscoveries();
        long numberOfSuggestions = Statistics.getMetaSuggestionDiscoveries();
        long avgSecondsPerDiscovery = (long)(Statistics.getAverageDiscoveryTimePerBlog() / 1000);

        long blogsInDatabase = Statistics.getBlogsCount();
        long blogsIncomplete = Statistics.getIncompleteBlogsCount();
        long linksInDatabase = Statistics.getBlogsLinksCount();
        long linksToBadBlogs = Statistics.getBadBlogLinksCount();

        String messageFormat = "<table>" +
            "<tr><td>Number of queries</td><td>{0}</td></td>" +
            "<tr><td>DB Hits</td><td>{1}</td></td>" +
            "<tr><td>New Discoveries</td><td>{2}</td></td>" +
            "<tr><td>Suggestions</td><td>{3}</td></td>" +
            "<tr><td>Avg. Seconds Per Discovery</td><td>{4}</td></td>" +
            "<tr><td>Blogs</td><td>{5}</td></td>" +
            "<tr><td>Blogs Incomplete</td><td>{6}</td></td>" +
            "<tr><td>Links</td><td>{7}</td></td>" +
            "<tr><td>Links To Bad Blogs</td><td>{8}</td></td>" +
            "</table>";

        String message = MessageFormat.format(messageFormat, new Object[]
        {
            new Long(numberOfQueries),
            new Long(numberOfDBHits),
            new Long(numberOfNew),
            new Long(numberOfSuggestions),
            new Long(avgSecondsPerDiscovery),
            new Long(blogsInDatabase),
            new Long(blogsIncomplete),
            new Long(linksInDatabase),
            new Long(linksToBadBlogs)
        });

        ItemIF item = aBuilder.createItem(feed, "Uptime: " + Statistics.getUptimeString(),
            message, null);

        item.setDate(new Date());

        return feed;
    }

    private static void setCommonFeedProperties(ChannelIF aFeed)
    {
        aFeed.setGenerator("BBService");
        aFeed.setLanguage("en_US");
    }
}
