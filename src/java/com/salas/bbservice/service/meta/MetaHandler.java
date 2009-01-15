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
// $Id: MetaHandler.java,v 1.5 2008/10/27 07:39:05 alg Exp $
//

package com.salas.bbservice.service.meta;

import com.salas.bbservice.HandlerConfig;
import com.salas.bbservice.IInitializableHandler;
import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.service.account.AccountHandler;
import com.salas.bbservice.utils.ApplicationProperties;
import com.salas.bbservice.utils.Configuration;
import com.salas.bbservice.utils.Constants;
import com.salas.bbservice.utils.StringUtils;

import java.util.Hashtable;
import java.util.regex.Pattern;

/**
 * Handles requests to Meta module.
 */
public final class MetaHandler implements IInitializableHandler
{
    private static final Blog BAD_BLOG = new Blog(null, null, null, null, null, -1, null, null, 2, -1);

    private HandlerConfig config;

    /**
     * Sets the configuration of the caller.
     *
     * @param config caller.
     */
    public void init(HandlerConfig config)
    {
        this.config = config;
    }

    /**
     * Returns blog information by any of it's URL's. If URL is unknown then
     * it will be scheduled for discovery.
     *
     * @param url   URL to check.
     *
     * @return blog information.
     */
    public Hashtable getBlogByUrl(String url)
    {
        return getBlogByUrlAny(url, false);
    }

    /**
     * Returns blog information by any of it's URL's. If URL is unknown then
     * it will be scheduled for discovery. All of the strings except URL's are
     * encoded in UTF-8 byte arrays.
     *
     * @param url   URL to check.
     *
     * @return blog information.
     */
    public Hashtable getBlogByUrlInUtf8(String url)
    {
        return getBlogByUrlAny(url, true);
    }

    /**
     * Returns blog information by any of it's URL's. If URL is unknown then
     * it will be scheduled for discovery. All of the strings except URL's can be
     * encoded in UTF-8 byte arrays if <code>inUtf8</code> is set to TRUE.
     *
     * @param url       URL to check.
     * @param inUtf8    TRUE to return strings in UTF-8.
     *
     * @return blog information.
     */
    public Hashtable getBlogByUrlAny(String url, boolean inUtf8)
    {
        final Hashtable response;
        final MetaService service = MetaService.getInstance();

        Blog blog = isIgnoring(url) ? badBlog() : service.getBlogByUrl(url, getClientId());

        if (blog == null)
        {
            // No blog registered by this HTML URL.
            // Schedule resolution and return 'wait' packet.
            response = MetaResponse.createWaitResponse();
        } else
        {
            // Blog found.
            Hashtable communityFields = service.getCommunityFields(blog.getId());
            response = MetaResponse.createBlogResponse(blog, communityFields, inUtf8);
        }

        return response;
    }

    /**
     * Returns TRUE if we are ignoring this URL.
     *
     * @param url URL.
     *
     * @return TRUE if ignoring.
     */
    private boolean isIgnoring(String url)
    {
        Pattern p = Configuration.getIgnorePattern();
        return !(p == null || url == null) && p.matcher(url).matches();
    }

    /**
     * Returns a bad blog.
     *
     * @return bad blog.
     */
    private synchronized Blog badBlog()
    {
        return BAD_BLOG;
    }

    /**
     * Returns some client ID.
     *
     * @return client ID.
     */
    private int getClientId()
    {
        return config == null || config.getClientIP() == null ? -1 : config.getClientIP().hashCode();
    }

    /**
     * Called by anyone willing to associate arbitrary URL with the feed URL. If feed URL is unknown
     * it will be resolved prior to the association.
     *
     * @param url       arbitrary URL.
     * @param feedUrl   feed URL.
     *
     * @return empty string.
     */
    public String suggestFeedUrl(String url, String feedUrl)
    {
        final MetaService service = MetaService.getInstance();
        service.discoverAndAssociate(url, feedUrl, getClientId());

        return Constants.EMPTY_STRING;
    }

    /**
     * Sets the values of community overridable fields for the Blog.
     *
     * @param sessionId ID of the session.
     * @param feedUrl   Feed URL of the Blog record.
     * @param fields    fields to set.
     *
     * @return empty string in successful case or error message.
     */
    public String setCommunityFields(int sessionId, String feedUrl, Hashtable fields)
    {
        String result;

        User u = AccountHandler.getUserBySessionId(sessionId);
        if (u == null)
        {
            result = "Session ID is incorrect.";
        } else
        {
            if (!u.isActivated())
            {
                result = "Service account is not activated.";
            } else
            {
                result = MetaService.getInstance().setCommunityFields(feedUrl, fields, getClientId());
            }
        }

        return result == null ? Constants.EMPTY_STRING : result; 
    }

    /**
     * Gets the values of community overridable fields for the Blog.
     *
     * @param feedUrl   Feed URL of the Blog record.
     *
     * @return fields.
     */
    public Hashtable getCommunityFields(String feedUrl)
    {
        return MetaService.getInstance().getCommunityFields(feedUrl);
    }

    /**
     * Returns the map with OPML URLs.
     *
     * @return URLs.
     */
    public Hashtable getOPMLURLs()
    {
        Hashtable<String, byte[]> result = new Hashtable<String, byte[] >(2);

        result.put(ApplicationProperties.OPML_STARTING_POINTS_URL,
            StringUtils.toUTF8(ApplicationProperties.get(ApplicationProperties.OPML_STARTING_POINTS_URL, "")));
        result.put(ApplicationProperties.OPML_EXPERTS_URL,
            StringUtils.toUTF8(ApplicationProperties.get(ApplicationProperties.OPML_EXPERTS_URL, "")));

        return result;
    }
}
