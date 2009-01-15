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
// $Id: StatsTopRatedResult.java,v 1.1.1.1 2006/10/23 13:55:36 alg Exp $
//

package com.salas.bbservice.domain.dao;

/**
 * Objects of this class will hold the results of top rated feeds list query.
 */
public class StatsTopRatedResult
{
    private String      title;
    private String      htmlUrl;
    private String      xmlUrl;
    private double      averageRating;
    private int         subscriptions;
    private double      finalRating;

    /**
     * Creates empty object.
     */
    public StatsTopRatedResult()
    {
    }

    /**
     * Returns feed title.
     *
     * @return feed title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets feed title.
     *
     * @param aTitle feed title.
     */
    public void setTitle(String aTitle)
    {
        title = aTitle;
    }

    /**
     * Return HTML URL of the feed.
     *
     * @return HTML URL of the feed.
     */
    public String getHtmlUrl()
    {
        return htmlUrl;
    }

    /**
     * Sets HTML URL of the feed.
     *
     * @param aHtmlUrl HTML URL of the feed.
     */
    public void setHtmlUrl(String aHtmlUrl)
    {
        htmlUrl = aHtmlUrl;
    }

    /**
     * Returns XML URL of the feed.
     *
     * @return XML URL of the feed.
     */
    public String getXmlUrl()
    {
        return xmlUrl;
    }

    /**
     * Sets XML URL of the feed.
     *
     * @param aXmlUrl XML URL of the feed.
     */
    public void setXmlUrl(String aXmlUrl)
    {
        xmlUrl = aXmlUrl;
    }

    /**
     * Returns average rating.
     *
     * @return average rating.
     */
    public double getAverageRating()
    {
        return averageRating;
    }

    /**
     * Sets average rating.
     *
     * @param aAverageRating average rating.
     */
    public void setAverageRating(double aAverageRating)
    {
        averageRating = aAverageRating;
    }

    /**
     * Returns total number of subscriptions.
     *
     * @return total number of subscriptions.
     */
    public int getSubscriptions()
    {
        return subscriptions;
    }

    /**
     * Sets total number of subscriptions.
     *
     * @param aSubscriptions total number of subscriptions.
     */
    public void setSubscriptions(int aSubscriptions)
    {
        subscriptions = aSubscriptions;
    }

    /**
     * Returns final rating of the feed.
     *
     * @return final rating of the feed.
     */
    public double getFinalRating()
    {
        return finalRating;
    }

    /**
     * Sets final rating of the feed.
     *
     * @param aFinalRating final rating of the feed.
     */
    public void setFinalRating(double aFinalRating)
    {
        finalRating = aFinalRating;
    }
}
