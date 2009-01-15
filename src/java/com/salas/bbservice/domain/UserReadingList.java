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
// $Id: UserReadingList.java,v 1.1.1.1 2006/10/23 13:55:34 alg Exp $
//

package com.salas.bbservice.domain;

/**
 * Reading list object.
 */
public class UserReadingList
{
    private int id          = -1;
    private int userGuideId = -1;

    private String title;
    private String xmlUrl;

    /**
     * Creates reading list.
     */
    public UserReadingList()
    {
    }

    /**
     * Creates reading list.
     *
     * @param aUserGuideId  ID of user guide.
     * @param aTitle        title of reading list.
     * @param aXmlUrl       XML URL.
     */
    public UserReadingList(int aUserGuideId, String aTitle, String aXmlUrl)
    {
        userGuideId = aUserGuideId;
        title = aTitle;
        xmlUrl = aXmlUrl;
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
     * Sets ID.
     *
     * @param aId ID.
     */
    public void setId(int aId)
    {
        id = aId;
    }

    /**
     * Returns title.
     *
     * @return title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets title.
     *
     * @param aTitle title.
     */
    public void setTitle(String aTitle)
    {
        title = aTitle;
    }

    /**
     * Returns user guide ID.
     *
     * @return user guide ID.
     */
    public int getUserGuideId()
    {
        return userGuideId;
    }

    /**
     * Sets user guide ID.
     *
     * @param aUserGuideId user guide ID.
     */
    public void setUserGuideId(int aUserGuideId)
    {
        userGuideId = aUserGuideId;
    }

    /**
     * Returns XML URL.
     *
     * @return XML URL.
     */
    public String getXmlUrl()
    {
        return xmlUrl;
    }

    /**
     * Sets XML URL.
     *
     * @param aXmlUrl XML URL.
     */
    public void setXmlUrl(String aXmlUrl)
    {
        xmlUrl = aXmlUrl;
    }

    /**
     * Compares objects.
     *
     * @param o other reading list.
     *
     * @return <code>TRUE</code> if equal.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UserReadingList that = (UserReadingList)o;

        if (userGuideId != that.userGuideId) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (!xmlUrl.toString().equals(that.xmlUrl.toString())) return false;

        return true;
    }

    /**
     * Returns hash code.
     *
     * @return hash code.
     */
    public int hashCode()
    {
        return xmlUrl.toString().hashCode();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "Reading List: id=" + id +
            ", userGuideId=" + userGuideId +
            ", title=" + title +
            ", xmlUrl=" + xmlUrl;
    }
}
