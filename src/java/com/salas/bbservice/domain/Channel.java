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
// $Id: Channel.java,v 1.1.1.1 2006/10/23 13:55:32 alg Exp $
//
package com.salas.bbservice.domain;

/**
 * RSS feed (channel) stored on the server. XmlUrl is a business key as we
 * consider two channels equal if they are taken from the same address.
 */
public class Channel
{
    private int id = -1;
    private String title;
    private String htmlUrl;
    private String xmlUrl;

    /**
     * Constructs empty channel.
     */
    public Channel()
    {
    }

    /**
     * Constructs channel initialized with information.
     *
     * @param title     title.
     * @param htmlUrl   HTML URL.
     * @param xmlUrl    XML URL.
     */
    public Channel(String title, String htmlUrl, String xmlUrl)
    {
        this.title = title;
        this.htmlUrl = htmlUrl;
        this.xmlUrl = xmlUrl;
    }

    /**
     * Returns identificator of record.
     *
     * @return id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets identificator of record.
     *
     * @param id identificator.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Returns title of the channel.
     *
     * @return title of the channel.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets title of the channel.
     *
     * @param title title of the channel.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns HTML URL of the channel.
     *
     * @return HTML URL of the channel.
     */
    public String getHtmlUrl()
    {
        return htmlUrl;
    }

    /**
     * Sets HTML URL of the channel.
     *
     * @param htmlUrl HTML URL of the channel.
     */
    public void setHtmlUrl(String htmlUrl)
    {
        this.htmlUrl = htmlUrl;
    }

    /**
     * Returns XML URL of the channel.
     *
     * @return XML URL of the channel.
     */
    public String getXmlUrl()
    {
        return xmlUrl;
    }

    /**
     * Sets XML URL of the channel.
     *
     * @param xmlUrl XML URL of the channel.
     */
    public void setXmlUrl(String xmlUrl)
    {
        this.xmlUrl = xmlUrl;
    }

    /**
     * Returns hash code of the XML URL.
     *
     * @return hash code.
     */
    public int getXmlUrlHash()
    {
        return xmlUrl == null ? 1 : xmlUrl.hashCode();
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
        if (!(o instanceof Channel)) return false;

        final Channel channel = (Channel)o;

        if (htmlUrl != null ? !htmlUrl.equals(channel.htmlUrl) : channel.htmlUrl != null)
            return false;

        if (title != null ? !title.equals(channel.title) : channel.title != null) return false;
        if (xmlUrl != null ? !xmlUrl.equals(channel.xmlUrl) : channel.xmlUrl != null) return false;

        return true;
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
        return (xmlUrl != null ? xmlUrl.hashCode() : 0);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "Channel: id=" + id +
                ", title=" + (title != null ? title : "null") +
                ", xmlUrl=" + (xmlUrl != null ? xmlUrl : "null") +
                ", htmlUrl=" + (htmlUrl != null ? htmlUrl : "null");
    }
}
