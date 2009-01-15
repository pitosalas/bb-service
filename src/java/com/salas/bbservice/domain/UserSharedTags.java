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
// $Id: UserSharedTags.java,v 1.1.1.1 2006/10/23 13:55:35 alg Exp $
//

package com.salas.bbservice.domain;

/**
 * Holder for list of user shared tags with description assigned to some URL.
 */
public class UserSharedTags
{
    private long    userId;
    private String  url;
    private boolean feed;
    private String  tags;
    private String  description;
    private String  extended;

    /**
     * Creates empty object.
     */
    public UserSharedTags()
    {
    }

    /**
     * Creates object.
     *
     * @param aUserId       ID of user record to associate this object to.
     * @param aUrl          URL to tag.
     * @param aFeed         <code>TRUE</code> if URL belongs to feed.
     * @param aTags         space-delimited and quoted list of tags.
     * @param aDescription  textual description of URL.
     * @param aExtended     extended textual description of URL.
     */
    public UserSharedTags(long aUserId, String aUrl, boolean aFeed, String aTags,
        String aDescription, String aExtended)
    {
        userId = aUserId;
        url = aUrl;
        feed = aFeed;
        tags = aTags;
        description = aDescription;
        extended = aExtended;
    }

    /**
     * Returns user ID.
     *
     * @return user ID.
     */
    public long getUserId()
    {
        return userId;
    }

    /**
     * Sets user ID.
     *
     * @param aUserId user ID.
     */
    public void setUserId(long aUserId)
    {
        userId = aUserId;
    }

    /**
     * Returns URL.
     *
     * @return url.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets URL.
     *
     * @param aUrl url.
     */
    public void setUrl(String aUrl)
    {
        url = aUrl;
    }

    /**
     * Returns <code>TRUE</code> if the URL corresponds to feed.
     *
     * @return <code>TRUE</code> if the URL corresponds to feed.
     */
    public boolean isFeed()
    {
        return feed;
    }

    /**
     * Sets the value of feed flag.
     *
     * @param aFeed flag.
     */
    public void setFeed(boolean aFeed)
    {
        feed = aFeed;
    }

    /**
     * Returns the list of tags.
     *
     * @return tags.
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Sets the tags.
     *
     * @param aTags tags.
     */
    public void setTags(String aTags)
    {
        tags = aTags;
    }

    /**
     * Returns the description.
     *
     * @return description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param aDescription description.
     */
    public void setDescription(String aDescription)
    {
        description = aDescription;
    }

    /**
     * Returns extended textual description.
     *
     * @return extended textual description.
     */
    public String getExtended()
    {
        return extended;
    }

    /**
     * Sets exteneded textual description.
     *
     * @param aExtended extended description. 
     */
    public void setExtended(String aExtended)
    {
        extended = aExtended;
    }

    /**
     * Compares two objects.
     *
     * @param o another object to compare to.
     *
     * @return result of comparison.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UserSharedTags that = (UserSharedTags)o;

        if (feed != that.feed) return false;
        if (userId != that.userId) return false;
        if (description != null ? !description.equals(
            that.description) : that.description != null) return false;
        if (extended != null ? !extended.equals(
            that.extended) : that.extended != null) return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
        if (!url.equals(that.url)) return false;

        return true;
    }

    /**
     * Returns the hash code.
     *
     * @return hash code.
     */
    public int hashCode()
    {
        int result;
        result = (int)(userId ^ (userId >>> 32));
        result = 29 * result + url.hashCode();
        return result;
    }
}
