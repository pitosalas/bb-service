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
// $Id: BlogLink.java,v 1.1.1.1 2006/10/23 13:55:32 alg Exp $
//

package com.salas.bbservice.domain;

/**
 * Single URL pointing inside some blog.
 */
public class BlogLink
{
    private int     id = -1;

    private Integer blogId;
    private int     urlHashCode;
    private long    lastAccessTime = -1;

    /**
     * Creates empty object.
     */
    public BlogLink()
    {
        this(null, null);
    }

    /**
     * Creates new HTML URL reference.
     *
     * @param aBlogId   particular blog.
     * @param url       URL.
     */
    public BlogLink(Integer aBlogId, String url)
    {
        blogId = aBlogId;
        urlHashCode = url2hashCode(url);

        lastAccessTime = System.currentTimeMillis();
    }

    /**
     * Returns ID.
     *
     * @return ID.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets ID of this record.
     *
     * @param aId ID.
     */
    public void setId(int aId)
    {
        id = aId;
    }

    /**
     * Returns ID of the blog.
     *
     * @return ID of the blog.
     */
    public Integer getBlogId()
    {
        return blogId;
    }

    /**
     * Sets ID of the blog.
     *
     * @param aBlogId ID of the blog.
     */
    public void setBlogId(Integer aBlogId)
    {
        blogId = aBlogId;
    }

    /**
     * Returns URL hash code.
     *
     * @return URL hash code.
     */
    public int getUrlHashCode()
    {
        return urlHashCode;
    }

    /**
     * Sets the URL hash code.
     *
     * @param aUrlHashCode hash code.
     */
    public void setUrlHashCode(int aUrlHashCode)
    {
        urlHashCode = aUrlHashCode;
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
     * @param aLastAccessTime last access time.
     */
    public void setLastAccessTime(long aLastAccessTime)
    {
        lastAccessTime = aLastAccessTime;
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
        if (!(o instanceof BlogLink)) return false;

        final BlogLink blogLink = (BlogLink)o;

        if (lastAccessTime != blogLink.lastAccessTime) return false;
        if (urlHashCode != blogLink.urlHashCode) return false;

        return !(blogId != null ? !blogId.equals(blogLink.blogId) : blogLink.blogId != null);
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

        result = (blogId == null ? 0 : blogId.hashCode());
        result = 29 * result + urlHashCode;

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
            append("BlogLink: id=").append(id).
            append(", blogId=").append(blogId).
            append(", urlHashCode=").append(urlHashCode).toString();
    }

    /**
     * Returns hash code for given link.
     *
     * @param url link.
     *
     * @return hash code.
     */
    public static int url2hashCode(String url)
    {
        return url == null ? 0 : url.trim().toLowerCase().hashCode();
    }
}
