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
// $Id: CompositeDiscoverer.java,v 1.2 2008/06/04 06:04:43 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.stats.Statistics;
import com.salas.bbservice.utils.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Composite discoverer organizes complex discovery process by sequential
 * calling of registered discoverers. It analyzes their fields reports and
 * decides whether the call to some discoverer is necessary or redundant.
 */
class CompositeDiscoverer implements IBlogDiscoverer
{
    private static final DateFormat DATE_FORMAT =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    private int                 compositeFields;
    private IBlogDiscoverer[]   discoverers;

    /**
     * Creates composite discoverer.
     */
    public CompositeDiscoverer()
    {
        this(new IBlogDiscoverer[0]);
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
     * Creates composite discoverer.
     *
     * @param aDiscoverers list of sub-discoverers.
     */
    public CompositeDiscoverer(IBlogDiscoverer[] aDiscoverers)
    {
        compositeFields = 0;

        discoverers = aDiscoverers;
        for (int i = 0; i < aDiscoverers.length; i++)
        {
            compositeFields |= aDiscoverers[i].getProvidedFields();
        }
    }

    /**
     * Returns the OR'ed list of fields provided by this discoverer.
     *
     * @return list of fields.
     */
    public int getProvidedFields()
    {
        return compositeFields;
    }

    /**
     * Makes a try to discover the URL and update information in the source blog.
     *
     * @param source source blog.
     * @param url    url to discover.
     *
     * @return fields actually discovered.
     *
     * @throws IOException in case when discovery was completely impossible because of
     *                     IOExceptions of underlying discoverers.
     */
    public int discover(Blog source, URL url) throws IOException
    {
        if (url == null) return 0;
        
        int resolved = discover0(source, url);

        String htmlUrl = source.getHtmlUrl();

        // If we have URL of the site and it isn't matching URL we are using to discover
        // the Feed then we make another try to discover it using site URL. We reset
        // inlinks in UNDISCOVERED state to allow them to be filled with information
        // from this new discovery.
        if (!StringUtils.isEmpty(htmlUrl))
        {
            if (!htmlUrl.trim().equalsIgnoreCase(url.toString()))
            {
                source.setInboundLinks(Blog.UNDISCOVERED);
                resolved = discover0(source, new URL(htmlUrl));
            }
        } else if (source.getDataUrl() != null)
        {
            // If there's no site URL then set inlinks to UNKNOWN
            source.setInboundLinks(Blog.UNKNOWN);
            resolved |= FIELD_INBOUND_LINKS;
        }

        return resolved;
    }

    /**
     * Makes a try to discover the URL and update information in the source blog.
     *
     * @param source source blog.
     * @param url    url to discover.
     *
     * @return fields actually discovered.
     *
     * @throws IOException in case when discovery was completely impossible because of
     *                     IOExceptions of underlying discoverers.
     */
    private int discover0(Blog source, URL url) throws IOException
    {
        if (source == null) return 0;

        int resolvedFields = detectResolvedFields(source);

//        toLog("    --> discover " + url + " resolved fields (" + resolvedFields + ")");

        int exceptions = 0;
        int failedFields = 0;
        for (int i = 0; i < discoverers.length; i++)
        {
            IBlogDiscoverer discoverer = discoverers[i];
            if (resolvedFields < (resolvedFields | discoverer.getProvidedFields()))
            {
                int fields = -1;
                Exception ex = null;
                try
                {
                    fields = discoverer.discover(source, url);
                    resolvedFields |= fields;
                } catch (Exception e)
                {
                    ex = e;

                    if (isMandatoryDiscoverer(discoverer))
                    {
                        exceptions++;
                        failedFields |= discoverer.getProvidedFields();
                    }

                    if (e instanceof RuntimeException) e.printStackTrace();
                } finally
                {
                    Statistics.registerMetaDiscovererCall(discoverer, ex == null);
                    String msg;
                    if (ex != null)
                    {
                        String exMessage = ex.getMessage();
                        msg = "ERR: " + (exMessage == null ? ex.toString() : exMessage);
                    } else
                    {
                        msg = Integer.toString(fields);
                    }

//                    toLog("      " + discoverer + " [" + msg + "]");

                    if (ex != null && discoverer instanceof HtmlDiscoverer) ex.printStackTrace();
                }
            }
        }

//        toLog("    <-- finished: resolved = " + resolvedFields + ", failed = " + failedFields);

        // If all discoverers failed due to IOException then we likely have communication
        // problem and not problem with Blog
        if (exceptions > 0 && exceptions == discoverers.length) throw new IOException();

        // If not all failures covered by successful resolutions then we mark blog as incompletely
        // discovered.
        source.setIncompleteDiscovery((failedFields | resolvedFields) != resolvedFields);

        return resolvedFields;
    }

    /**
     * Returns TRUE if discoverer is mandatory and errors should be taken in
     * account.
     *
     * @param aDiscoverer discoverer.
     *
     * @return TRUE if mandatory and errors should be counted.
     */
    private boolean isMandatoryDiscoverer(IBlogDiscoverer aDiscoverer)
    {
        return !(aDiscoverer instanceof HtmlDiscoverer) &&
               !(aDiscoverer instanceof DirectDiscoverer) &&
               !(aDiscoverer instanceof FeedFieldsDiscoverer);
    }

    /**
     * Returns OR'ed fields which are already resolved according to values of blog fields.
     *
     * @param aBlog blog to test.
     *
     * @return OR'ed resolved fields.
     */
    private int detectResolvedFields(Blog aBlog)
    {
        int resolvedFields = 0;

        if (aBlog != null)
        {
            if (!StringUtils.isEmpty(aBlog.getDataUrl())) resolvedFields |= FIELD_DATA_URL;
            if (!StringUtils.isEmpty(aBlog.getHtmlUrl())) resolvedFields |= FIELD_HTML_URL;
            if (aBlog.getInboundLinks() >= 0) resolvedFields |= FIELD_INBOUND_LINKS;
            if (!StringUtils.isEmpty(aBlog.getTitle())) resolvedFields |= FIELD_TITLE;
            if (!StringUtils.isEmpty(aBlog.getAuthor())) resolvedFields |= FIELD_AUTHOR;
            if (!StringUtils.isEmpty(aBlog.getDescription())) resolvedFields |= FIELD_DESCRIPTION;
        }

        return resolvedFields;
    }

    public static void toLog(String str)
    {
        System.out.println(DATE_FORMAT.format(new Date()) +
            " [" + Thread.currentThread().getName() + "] " + str);
    }
}
