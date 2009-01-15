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
// $Id: TechnoratiDiscoverer.java,v 1.6 2008/10/27 07:06:15 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.service.meta.discovery.technorati.*;
import com.salas.bbservice.stats.Statistics;
import com.salas.bbservice.utils.StringUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Discoverer uses Technorati services to resolve XML URL.
 */
public class TechnoratiDiscoverer implements IBlogDiscoverer
{
    private static final Pattern CHECK_PATTERN = Pattern.compile("^[0-9a-fA-F]{32}$");

    // Technorati service client
    private TechnoratiServiceClient clientTechnorati = null;

    /**
     * Creates new discoverer.
     */
    public TechnoratiDiscoverer()
    {
    }

    /**
     * Sets the properties for discoverer.
     *
     * @param properties properties.
     */
    public void setProperties(Map properties)
    {
        String key = (String)properties.get("key");

        if (key != null && CHECK_PATTERN.matcher(key.trim()).find())
        {
            clientTechnorati = new TechnoratiServiceClient(key);
        } else
        {
            CompositeDiscoverer.toLog(this.toString() + " 'key' is not specified.");
        }
    }

    /**
     * Returns the OR'ed list of fields provided by this discoverer.
     *
     * @return list of fields.
     */
    public int getProvidedFields()
    {
        return clientTechnorati == null ? 0 : FIELD_DATA_URL | FIELD_HTML_URL | FIELD_INBOUND_LINKS;
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
        int discoveredFields = 0;

        boolean failed = true;
        try
        {
            final TechnoratiResponse response = clientTechnorati.getResponse(url.toExternalForm());

            if (response instanceof TechnoratiResult)
            {
                TechnoratiResult tresult = (TechnoratiResult)response;
                discoveredFields = parseTechnoratiResult(tresult, source);
                failed = false;
            } else
            {
                TechnoratiError error = (TechnoratiError)response;
                if (error.isTemporary())
                {
                    throw new IOException("Technorati error: " + error.getMessage());
                } else discoveredFields = 0;
            }
        } catch (TechnoratiServiceException e)
        {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                // Timeout happens only when Technorati knows nothing about the URL
                // to my best knowledge.
                discoveredFields = 0;
            } else throw new IOException("Problem with service or communication: " + e.getMessage());
        } finally
        {
            Statistics.registerMetaDiscovererCall(this, !failed);
        }

        return discoveredFields;
    }

    /**
     * Parses Technorati result block and returns own result block if recognizes necessary URL's.
     *
     * @param technoratiResult original result from Technorati.
     * @param source            blog record to fill.
     *
     * @return own result or <code>null</code> if nothing useful found.
     */
    int parseTechnoratiResult(final TechnoratiResult technoratiResult, Blog source)
    {
        int discoveredFields = 0;
        final TechnoratiResultWeblog[] blogs = technoratiResult.getWebLogs();
        if (blogs.length > 0)
        {
            final TechnoratiResultWeblog blog = blogs[0];

            // resolve data url
            if (source.getDataUrl() == null)
            {
                discoveredFields |= setDataUrl(blog, source);
            }

            // resolve html url
            final String url = blog.getUrl();
            if (StringUtils.isEmpty(source.getHtmlUrl()) && !StringUtils.isEmpty(url))
            {
                source.setHtmlUrl(url);
                discoveredFields |= FIELD_HTML_URL;
            }

            // resolve the rank if available
            if (blog.getRank() != null)
            {
                source.setRank(blog.getRank().intValue());
            }
        }

        // mark rank as UNKNOWN if the blog wasn't discovered
        if (source.getRank() == Blog.UNDISCOVERED)
        {
            source.setRank(Blog.UNKNOWN);
        }

        // resolve inbound links
        Integer inboundLinks = technoratiResult.getInboundLinks();
        int il = inboundLinks == null ? Blog.UNKNOWN : inboundLinks.intValue();
        source.setInboundLinks(il);
        discoveredFields |= FIELD_INBOUND_LINKS;

        return discoveredFields;
    }

    private int setDataUrl(final TechnoratiResultWeblog aBlog, Blog source)
    {
        final String rssUrl = aBlog.getRssUrl();
        final String atomUrl = aBlog.getAtomUrl();

        // Check any of acceptable URL's
        String url = null;
        if (!StringUtils.isEmpty(rssUrl))
        {
            url = rssUrl;
        } else if (!StringUtils.isEmpty(atomUrl))
        {
            url = atomUrl;
        }

        boolean discovered = false;
        if (url != null)
        {
            source.setDataUrl(url);
            discovered = true;
        }

        return discovered ? FIELD_DATA_URL : 0;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "TechnoratiDiscoverer";
    }
}
