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
// $Id: FeedFieldsDiscoverer.java,v 1.2 2006/10/31 12:48:32 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.utils.StringUtils;
import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.util.Map;

/**
 * Discovers Title, Author, Description fields right from the feed.
 */
public class FeedFieldsDiscoverer extends AbstractDiscoverer
{
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
        return FIELD_AUTHOR | FIELD_TITLE | FIELD_DESCRIPTION | FIELD_HTML_URL;
    }

    /**
     * Makes a try to discover the URL and update information in the source blog.
     *
     * @param source source blog.
     * @param url    url to discover.
     *
     * @return fields actually discovered.
     *
     * @throws java.io.IOException in case of communication error.
     */
    public int discover(Blog source, URL url) throws IOException
    {
        int discoveredFields = 0;

        // This discoverer ignores ingoing URL, but uses the data URL from the blog source
        // instead.
        String dataUrl = source.getDataUrl();
        if (dataUrl != null)
        {
            try
            {
                url = new URL(dataUrl);

                ChannelIF channel = FeedParser.parse(new ChannelBuilder(),
                    new BufferedReader(new InputStreamReader(urlInputStream(url))));
                discoveredFields = copyData(channel, source);
            } catch (ParseException e)
            {
                CompositeDiscoverer.toLog("    !!! Feed Parsing Error: " + e);
            } catch (Exception e)
            {
                CompositeDiscoverer.toLog("    !!! Feed Info Error: " + e);
            }
        }

        return discoveredFields;
    }

    /**
     * Copies data from parsed channel into the blog object.
     *
     * @param aChannel  channel to copy data from.
     * @param aBlog     blog to put data into.
     *
     * @return OR'ed discovered fields.
     */
    static int copyData(ChannelIF aChannel, Blog aBlog)
    {
        int discoveredFields = 0;

        if (aChannel == null) return 0;

        // Channel title
        if (aChannel.getTitle() != null && aBlog.getTitle() == null)
        {
            aBlog.setTitle(aChannel.getTitle());
            discoveredFields |= FIELD_TITLE;
        }

        // Channel creator -- author
        if (aChannel.getCreator() != null && aBlog.getAuthor() == null)
        {
            aBlog.setAuthor(aChannel.getCreator());
            discoveredFields |= FIELD_AUTHOR;
        }

        // Channel description
        if (aChannel.getDescription() != null && aBlog.getDescription() == null)
        {
            aBlog.setDescription(aChannel.getDescription());
            discoveredFields |= FIELD_DESCRIPTION;
        }

        // Channel site
        if (aChannel.getSite() != null && StringUtils.isEmpty(aBlog.getHtmlUrl()))
        {
            aBlog.setHtmlUrl(aChannel.getSite().toString());
            discoveredFields |= FIELD_HTML_URL;
        }

        return discoveredFields;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "FeedFieldsDiscoverer";
    }
}
