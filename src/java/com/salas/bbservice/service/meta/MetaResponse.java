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
// $Id: MetaResponse.java,v 1.1.1.1 2006/10/23 13:55:44 alg Exp $
//

package com.salas.bbservice.service.meta;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.utils.StringUtils;

import java.util.Hashtable;

/**
 * Response from meta-module.
 */
final class MetaResponse extends Hashtable
{
    private static final String KEY_STATUS_CODE         = "code";
    private static final String KEY_HTML_URL            = "htmlUrl";
    private static final String KEY_DATA_URL            = "dataUrl";
    private static final String KEY_INBOUND_LINKS       = "inboundLinks";
    private static final String KEY_CATEGORY            = "category";
    private static final String KEY_LOCATION            = "location";
    private static final String KEY_TITLE               = "title";
    private static final String KEY_AUTHOR              = "author";
    private static final String KEY_DESCRIPTION         = "description";
    private static final String KEY_COMMUNITY_FIELDS    = "communityFields";
    private static final String KEY_RANK                = "rank";

    /**
     * Hidden constructor.
     */
    private MetaResponse()
    {
    }

    /**
     * Creates Wait-packet.
     *
     * @return wait packet.
     */
    public static MetaResponse createWaitResponse()
    {
        MetaResponse response = new MetaResponse();

        response.setStatusCode(Blog.STATUS_PROCESSING);

        return response;
    }

    /**
     * Create Blog-packet.
     *
     * @param blog              blog to create packet for.
     * @param communityFields   community fields associated with the blog.
     * @param inUtf8            TRUE to store string data in UTF-8 byte arrays.
     *
     * @return blog packet.
     */
    public static MetaResponse createBlogResponse(Blog blog, Hashtable communityFields,
                                                  boolean inUtf8)
    {
        MetaResponse response = new MetaResponse();

        response.setStatusCode(blog.getStatus());
        response.setTitle(blog.getTitle(), inUtf8);
        response.setAuthor(blog.getAuthor(), inUtf8);
        response.setDescription(blog.getDescription(), inUtf8);
        response.setHtmlUrl(blog.getHtmlUrl());
        response.setDataUrl(blog.getDataUrl());
        response.setInboundLinks(blog.getInboundLinks());
        response.setRank(blog.getRank());
        response.setCategory(blog.getCategory());
        response.setLocation(blog.getLocation());
        response.setCommunityFields(communityFields);

        return response;
    }

    private void setStatusCode(int code)
    {
        put(KEY_STATUS_CODE, new Integer(code));
    }

    private void setTitle(String title, boolean inUtf8)
    {
        if (title != null)
        {
            Object titleObject = title;
            if (inUtf8) titleObject = StringUtils.toUTF8(title);

            put(KEY_TITLE, titleObject);
        }
    }

    private void setAuthor(String author, boolean inUtf8)
    {
        if (author != null)
        {
            Object authorObject = author;
            if (inUtf8) authorObject = StringUtils.toUTF8(author);

            put(KEY_AUTHOR, authorObject);
        }
    }

    private void setDescription(String description, boolean inUtf8)
    {
        if (description != null)
        {
            Object descriptionObject = description;
            if (inUtf8) descriptionObject = StringUtils.toUTF8(description);

            put(KEY_DESCRIPTION, descriptionObject);
        }
    }

    private void setHtmlUrl(String htmlUrl)
    {
        if (htmlUrl != null) put(KEY_HTML_URL, htmlUrl);
    }

    private void setDataUrl(String dataUrl)
    {
        if (dataUrl != null) put(KEY_DATA_URL, dataUrl);
    }

    private void setInboundLinks(int links)
    {
        put(KEY_INBOUND_LINKS, new Integer(links));
    }

    private void setRank(int rank)
    {
        put(KEY_RANK, new Integer(rank));
    }

    private void setCategory(String category)
    {
        if (category != null) put(KEY_CATEGORY, category);
    }

    private void setLocation(String location)
    {
        if (location != null) put(KEY_LOCATION, location);
    }

    private void setCommunityFields(Hashtable communityFields)
    {
        if (communityFields != null && communityFields.size() > 0)
        {
            put(KEY_COMMUNITY_FIELDS, communityFields);
        }
    }
}
