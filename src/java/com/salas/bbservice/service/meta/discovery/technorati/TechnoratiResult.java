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
// $Id: TechnoratiResult.java,v 1.1.1.1 2006/10/23 13:55:47 alg Exp $
//

package com.salas.bbservice.service.meta.discovery.technorati;

import com.salas.bbservice.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Outline received from Technorati (www.technorati.com).
 */
public class TechnoratiResult implements TechnoratiResponse
{
    private String  url;
    private Integer inboundBlogs;
    private Integer inboundLinks;
    private List    weblogs;

    /**
     * Creates empty outline.
     */
    public TechnoratiResult()
    {
        url = Constants.EMPTY_STRING;
        inboundBlogs = Constants.INTEGER_0;
        inboundLinks = Constants.INTEGER_0;
        weblogs = new ArrayList();
    }

    /**
     * Returns URL of requested blog.
     *
     * @return url of blog.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets URL of requested blog.
     *
     * @param aUrl URL of blog.
     */
    public void setUrl(String aUrl)
    {
        this.url = aUrl;
    }

    /**
     * Returns number of inbound blogs.
     *
     * @return optional number of inbound blogs.
     */
    public Integer getInboundBlogs()
    {
        return inboundBlogs;
    }

    /**
     * Sets number of inbound blogs.
     *
     * @param aInboundBlogs number of inbound blogs.
     */
    public void setInboundBlogs(Integer aInboundBlogs)
    {
        this.inboundBlogs = aInboundBlogs;
    }

    /**
     * Returns number of inbound links.
     *
     * @return optional number of inbound links.
     */
    public Integer getInboundLinks()
    {
        return inboundLinks;
    }

    /**
     * Sets number of inbound links.
     *
     * @param aInboundLinks number of inbound links.
     */
    public void setInboundLinks(Integer aInboundLinks)
    {
        this.inboundLinks = aInboundLinks;
    }

    /**
     * Adds weblog record to the list.
     *
     * @param weblog weblog record.
     */
    public void addWebLog(TechnoratiResultWeblog weblog)
    {
        weblogs.add(weblog);
    }

    /**
     * Returns all weblogs recorded.
     *
     * @return recorded weblogs list.
     */
    public TechnoratiResultWeblog[] getWebLogs()
    {
        return (TechnoratiResultWeblog[])weblogs.toArray(
            new TechnoratiResultWeblog[weblogs.size()]);
    }
}
