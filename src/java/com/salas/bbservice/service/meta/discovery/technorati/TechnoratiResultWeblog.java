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
// $Id: TechnoratiResultWeblog.java,v 1.1.1.1 2006/10/23 13:55:47 alg Exp $
//

package com.salas.bbservice.service.meta.discovery.technorati;

import java.util.Date;

/**
 * Weblog entry in outline.
 */
public class TechnoratiResultWeblog
{
    private String  name;
    private String  url;
    private String  rssUrl;
    private String  atomUrl;
    private int     inboundBlogs;
    private int     inboundLinks;
    private Date    lastUpdate;
    private Integer     rank;
    private Integer lang;

    /**
     * Creates empty weblog record object.
     */
    public TechnoratiResultWeblog()
    {
    }

    /**
     * Returns the name of weblog.
     *
     * @return name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of weblog.
     *
     * @param aName name of weblog.
     */
    public void setName(String aName)
    {
        this.name = aName;
    }

    /**
     * Returns URL of weblog.
     *
     * @return URL of weblog.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets URL of weblog.
     *
     * @param aUrl URL of weblog.
     */
    public void setUrl(String aUrl)
    {
        this.url = aUrl;
    }

    /**
     * Returns URL of weblog RSS feed.
     *
     * @return URL of weblog RSS feed.
     */
    public String getRssUrl()
    {
        return rssUrl;
    }

    /**
     * Sets URL of weblog RSS feed.
     *
     * @param aRssUrl URL of weblog RSS feed.
     */
    public void setRssUrl(String aRssUrl)
    {
        this.rssUrl = aRssUrl;
    }

    /**
     * Returns URL of weblog Atom feed.
     *
     * @return URL of weblog Atom feed.
     */
    public String getAtomUrl()
    {
        return atomUrl;
    }

    /**
     * Sets URL of weblog Atom feed.
     *
     * @param anAtomUrl URL of weblog Atom feed.
     */
    public void setAtomUrl(String anAtomUrl)
    {
        this.atomUrl = anAtomUrl;
    }

    /**
     * Returns number of inbound blogs.
     *
     * @return number of inbound blogs.
     */
    public int getInboundBlogs()
    {
        return inboundBlogs;
    }

    /**
     * Sets number of inbound blogs.
     *
     * @param anInboundBlogs number of inbound blogs.
     */
    public void setInboundBlogs(int anInboundBlogs)
    {
        this.inboundBlogs = anInboundBlogs;
    }

    /**
     * Returns number of inbound links.
     *
     * @return number of inbound links.
     */
    public int getInboundLinks()
    {
        return inboundLinks;
    }

    /**
     * Sets number of inbound links.
     *
     * @param anInboundLinks number of inbound links.
     */
    public void setInboundLinks(int anInboundLinks)
    {
        this.inboundLinks = anInboundLinks;
    }

    /**
     * Returns date of last update.
     *
     * @return optional date of last update.
     */
    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * Sets the date of last update.
     *
     * @param lastUpdateDate date of last update.
     */
    public void setLastUpdate(Date lastUpdateDate)
    {
        this.lastUpdate = lastUpdateDate;
    }

    /**
     * Returns weblog rank.
     *
     * @return weblog rank.
     */
    public Integer getRank()
    {
        return rank;
    }

    /**
     * Sets weblog rank.
     *
     * @param aRank weblog rank.
     */
    public void setRank(Integer aRank)
    {
        this.rank = aRank;
    }

    /**
     * Returns language ID.
     *
     * @return language ID.
     */
    public Integer getLang()
    {
        return lang;
    }

    /**
     * Sets language ID.
     *
     * @param aLang language ID.
     */
    public void setLang(Integer aLang)
    {
        this.lang = aLang;
    }
}
