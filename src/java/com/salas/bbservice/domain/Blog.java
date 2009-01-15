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
// $Id: Blog.java,v 1.1.1.1 2006/10/23 13:55:32 alg Exp $
//

package com.salas.bbservice.domain;

import com.salas.bbservice.utils.StringUtils;

/**
 * Blog object representing single blog record.
 */
public class Blog
{
    /** Valid blog. */
    public static final int STATUS_VALID = 0;

    /** Blog is under processing. */
    public static final int STATUS_PROCESSING = 1;

    /** Blog is invalid. */
    public static final int STATUS_INVALID = 2;

    /** Not discovered yet (inlinks and rank). */
    public static final int UNDISCOVERED    = -1;

    /** Reported to be unknown (inlinks and rank). */
    public static final int UNKNOWN         = -2;

    private int     id = -1;

    private String  author;
    private String  description;
    private String  title;
    private String  htmlUrl;
    private String  dataUrl;
    private String  category;
    private String  location;
    private long    lastAccessTime;
    private long    lastUpdateTime;
    private boolean incompleteDiscovery;
    private int     status;

    // Number of links pointing to this blog from outside.
    // Today we use negative part of integer range to marks some specific cases:
    //
    // '-1' - stands for 'Fetching' and means that this value wasn't reported by any of
    //        discoverers yet. When Technorati service discoverer is enabled it's queried
    //        for the value of inLinks. So, until it says something (number or 'unknown' value)
    //        the value remains in this state. In most cases it is in this state after discovery
    //        finished because of internal error of external service. If so the blog will be
    //        marked as incomplete (incompleteDiscovery = true) and it will enable express
    //        rediscovery procedure for it
    //
    // '-2' - stands for 'Unknown' and means that services (Technorati for now) know nothing
    //        about blog or number of inbound links. This value means that the response was
    //        received and it's final for now, so, the client does not need to wait for it to be
    //        updated soon.
    //
    //  Non-negative range stands for actual number of links reported by the services.
    private int     inboundLinks;

    // Technorati rank
    // The notion is the same as for inbound links (non-negative for the reply, -1 -- fetching, -2 -- unknown).
    private int     rank;

    /**
     * Creates empty blog object.
     */
    public Blog()
    {
        this(null, null, null, null, null, UNDISCOVERED, null, null, STATUS_INVALID, UNDISCOVERED);
    }

    /**
     * Creates and initializes blog object.
     *
     * @param title         title of the blog.
     * @param author        author of blog.
     * @param description   descriptiong of the blog.
     * @param htmlUrl       HTML URL of the blog.
     * @param dataUrl       one of data URL's.
     * @param inboundLinks  number of inbound links.
     * @param category      category of the blog.
     * @param location      location of the publisher.
     * @param status        status of this record.
     */
    public Blog(String title, String author, String description, String htmlUrl, String dataUrl,
                int inboundLinks, String category, String location, int status, int rating)
    {
        this.author = author;
        this.description = description;
        this.title = title;
        setHtmlUrl(htmlUrl);
        setDataUrl(dataUrl);
        this.inboundLinks = inboundLinks;
        this.category = category;
        this.location = location;
        this.status = status;

        this.lastAccessTime = System.currentTimeMillis();
        this.lastUpdateTime = this.lastAccessTime;
        this.incompleteDiscovery = false;

        this.rank = rating;
    }

    /**
     * Returns ID of the blog record.
     *
     * @return ID.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the ID of blog record.
     *
     * @param aId new ID.
     */
    public void setId(int aId)
    {
        this.id = aId;
    }

    /**
     * Returns author of the blog.
     *
     * @return author of the blog.
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * Sets the author of blog.
     *
     * @param aAuthor author of blog.
     */
    public void setAuthor(String aAuthor)
    {
        author = aAuthor;
    }

    /**
     * Returns descriptiong of blog.
     *
     * @return description of blog.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets description of blog.
     *
     * @param aDescription description of blog.
     */
    public void setDescription(String aDescription)
    {
        description = aDescription;
    }

    /**
     * Returns title of the blog.
     *
     * @return title of the blog.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title of blog.
     *
     * @param aTitle title of blog.
     */
    public void setTitle(String aTitle)
    {
        title = aTitle;
    }

    /**
     * Returns HTML URL.
     *
     * @return HTML URL.
     */
    public String getHtmlUrl()
    {
        return htmlUrl;
    }

    /**
     * Sets HTML URL.
     *
     * @param aHtmlUrl new HTML URL.
     */
    public void setHtmlUrl(String aHtmlUrl)
    {
        htmlUrl = StringUtils.isEmpty(aHtmlUrl) ? null : aHtmlUrl.trim().toLowerCase();
    }

    /**
     * Returns data URL.
     *
     * @return data URL.
     */
    public String getDataUrl()
    {
        return dataUrl;
    }

    /**
     * Sets data URL.
     *
     * @param aDataUrl data URL.
     */
    public void setDataUrl(String aDataUrl)
    {
        dataUrl = StringUtils.isEmpty(aDataUrl) ? null : aDataUrl.trim().toLowerCase();
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
     * @param aInboundLinks number of inbound links.
     */
    public void setInboundLinks(int aInboundLinks)
    {
        this.inboundLinks = aInboundLinks;
    }

    /**
     * Returns category.
     *
     * @return category.
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * Sets category.
     *
     * @param aCategory category.
     */
    public void setCategory(String aCategory)
    {
        this.category = aCategory;
    }

    /**
     * Returns location of publisher.
     *
     * @return location of publisher.
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Sets location of publisher.
     *
     * @param aLocation location of publisher.
     */
    public void setLocation(String aLocation)
    {
        this.location = aLocation;
    }

    /**
     * Returns time of last access.
     *
     * @return time of last access.
     */
    public long getLastAccessTime()
    {
        return lastAccessTime;
    }

    /**
     * Sets time of last access.
     *
     * @param aLastAccessTime time of last access.
     */
    public void setLastAccessTime(long aLastAccessTime)
    {
        lastAccessTime = aLastAccessTime;
    }

    /**
     * Returns time of last record update.
     *
     * @return time of last update.
     */
    public long getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    /**
     * Sets time of last update.
     *
     * @param aLastUpdateTime time of last update.
     */
    public void setLastUpdateTime(long aLastUpdateTime)
    {
        lastUpdateTime = aLastUpdateTime;
    }

    /**
     * Returns <code>TRUE</code> if discovery was incomplete.
     *
     * @return <code>TRUE</code> if discovery was incomplete.
     */
    public boolean isIncompleteDiscovery()
    {
        return incompleteDiscovery;
    }

    /**
     * Sets TRUE if discovery was incomplete and fast-rediscovery required.
     *
     * @param value new value.
     */
    public void setIncompleteDiscovery(boolean value)
    {
        incompleteDiscovery = value;
    }

    /**
     * Returns current record status.
     *
     * @return current record status.
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Sets status of a record.
     *
     * @param aStatus status.
     */
    public void setStatus(int aStatus)
    {
        status = aStatus;
    }

    /**
     * Returns current technorati rank.
     *
     * @return rank.
     */
    public int getRank()
    {
        return rank;
    }

    /**
     * Sets technorati rank.
     *
     * @param rank rank.
     */
    public void setRank(int rank)
    {
        this.rank = rank;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare.
     *
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Blog)) return false;

        final Blog blog = (Blog)o;

        if (inboundLinks != blog.inboundLinks) return false;
        if (rank != blog.rank) return false;
        if (lastAccessTime != blog.lastAccessTime) return false;
        if (lastUpdateTime != blog.lastUpdateTime) return false;
        if (incompleteDiscovery != blog.incompleteDiscovery) return false;
        if (status != blog.status) return false;
        if (category != null
            ? !category.equals(blog.category) : blog.category != null) return false;
        if (author != null ? !author.equals(blog.author) : blog.author != null) return false;
        if (description != null ? !description.equals(blog.description) : blog.description != null)
            return false;
        if (title != null ? !title.equals(blog.title) : blog.title != null) return false;
        if (dataUrl != null ? !dataUrl.equals(blog.dataUrl) : blog.dataUrl != null) return false;
        if (htmlUrl != null ? !htmlUrl.equals(blog.htmlUrl) : blog.htmlUrl != null) return false;

        return !(location != null
            ? !location.equals(blog.location) : blog.location != null);
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
     *
     * @return  a hash code value for this object.
     */
    public int hashCode()
    {
        int result;

        result = (htmlUrl != null ? htmlUrl.hashCode() : 0);
        result = 29 * result + (dataUrl != null ? dataUrl.hashCode() : 0);

        return result;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return new StringBuffer().
            append("Blog: id=").append(id).
            append(", title=").append(title != null ? title : "null").
            append(", author=").append(author != null ? author : "null").
            append(", description=").append(description != null ? description : "null").
            append(", htmlUrl=").append(htmlUrl != null ? htmlUrl : "null").
            append(", dataUrl=").append(dataUrl != null ? dataUrl : "null").
            append(", inboundLinks=").append(inboundLinks).
            append(", rank=").append(rank).
            append(", category=").append(category != null ? category : "null").
            append(", location=").append(location != null ? location : "null").
            append(", incompleteDiscovery=").append(incompleteDiscovery).toString();
    }
}
