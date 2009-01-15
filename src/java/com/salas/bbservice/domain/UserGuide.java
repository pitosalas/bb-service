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
// $Id: UserGuide.java,v 1.1.1.1 2006/10/23 13:55:34 alg Exp $
//
package com.salas.bbservice.domain;

/**
 * Guide stored by user. Guide references the channels (common) by using
 * <code>UserChannel</code> objects which link them.
 */
public class UserGuide
{
    private static final int HASH_MULTIPLIER = 29;

    private int id = -1;
    private int userId = -1;
    private String title;
    private String iconKey;
    private int index = -1;

    private boolean publishingEnabled = false;
    private String publishingTitle;
    private String publishingTags;
    private boolean publishingPublic;
    private boolean notificationsAllowed = true;
    private int publishingRating;

    private boolean autoFeedsDiscovery = false;

    /**
     * Constructs the empty guide object.
     */
    public UserGuide()
    {
    }

    /**
     * Creates user guide object with specified values.
     *
     * @param userId            ID of user-owner.
     * @param title             title of the guide.
     * @param iconKey           value of icon key.
     * @param index             index in list.
     * @param publishingEnabled publishing enableness flag.
     * @param publishingTitle   publishing title.
     * @param publishingTags    publishing tags.
     * @param publishingPublic  TRUE to make public publishing.
     * @param publishingRating  minimum rating for feeds publishing.
     * @param autoFeedsDiscovery auto feeds discovery flag.
     * @param notificationsAllowed TRUE to allow notifications.
     */
    public UserGuide(int userId, String title, String iconKey, int index,
                     boolean publishingEnabled, String publishingTitle, String publishingTags,
                     boolean publishingPublic, int publishingRating, boolean autoFeedsDiscovery,
                     boolean notificationsAllowed)
    {
        this.userId = userId;
        this.title = title;
        this.iconKey = iconKey;
        this.index = index;

        this.publishingEnabled = publishingEnabled;
        this.publishingTitle = publishingTitle;
        this.publishingTags = publishingTags;
        this.publishingPublic = publishingPublic;
        this.publishingRating = publishingRating;

        this.notificationsAllowed = notificationsAllowed;

        this.autoFeedsDiscovery = autoFeedsDiscovery;
    }

    /**
     * Returns the identifier of this guide.
     *
     * @return the identifier.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the ID of this guide.
     *
     * @param id identifier.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Returns the ID of user owning this guide.
     *
     * @return user ID.
     */
    public int getUserId()
    {
        return userId;
    }

    /**
     * Sets the ID of user owning this guide.
     *
     * @param userId ID of user-owner.
     */
    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    /**
     * Returns the title of the guide.
     *
     * @return the title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title of the guide.
     *
     * @param title new title of the guide.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns value of icon key.
     *
     * @return icon key.
     */
    public String getIconKey()
    {
        return iconKey;
    }

    /**
     * Sets value of icon key.
     *
     * @param iconKey icon key.
     */
    public void setIconKey(String iconKey)
    {
        this.iconKey = iconKey;
    }

    /**
     * Returns index of this guide in overall list.
     *
     * @return index in list.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Sets new order index.
     *
     * @param index index in list.
     */
    public void setIndex(int index)
    {
        this.index = index;
    }

    /**
     * Returns publishing enableness state.
     *
     * @return publishing state.
     */
    public boolean isPublishingEnabled()
    {
        return publishingEnabled;
    }

    /**
     * Sets publishing enableness state.
     *
     * @param enabled new state.
     */
    public void setPublishingEnabled(boolean enabled)
    {
        publishingEnabled = enabled;
    }

    /**
     * Returns publishing title.
     *
     * @return title.
     */
    public String getPublishingTitle()
    {
        return publishingTitle;
    }

    /**
     * Sets publishing title.
     *
     * @param title new title.
     */
    public void setPublishingTitle(String title)
    {
        publishingTitle = title;
    }

    /**
     * Returns publishing tags.
     *
     * @return tags.
     */
    public String getPublishingTags()
    {
        return publishingTags;
    }

    /**
     * Sets publishing tags.
     *
     * @param tags tags.
     */
    public void setPublishingTags(String tags)
    {
        publishingTags = tags;
    }

    /**
     * Returns <code>TRUE</code> if publishing is set to public.
     *
     * @return <code>TRUE</code> if publishing is set to public.
     */
    public boolean isPublishingPublic()
    {
        return publishingPublic;
    }

    /**
     * Sets the public publishing flag.
     *
     * @param flag <code>TRUE</code> to make publication public.
     */
    public void setPublishingPublic(boolean flag)
    {
        this.publishingPublic = flag;
    }

    /**
     * Returns minimum rating necessary for feeds publishing.
     *
     * @return minimum rating.
     */
    public int getPublishingRating()
    {
        return publishingRating;
    }

    /**
     * Sets minimum rating necessary for feeds publishing.
     *
     * @param rating minimum rating.
     */
    public void setPublishingRating(int rating)
    {
        this.publishingRating = rating;
    }

    /**
     * Returns <code>TRUE</code> if automatic feeds discovery is enabled.
     *
     * @return <code>TRUE</code> if automatic feeds discovery is enabled.
     */
    public boolean isAutoFeedsDiscovery()
    {
        return autoFeedsDiscovery;
    }

    /**
     * Sets the value of automatic feeds discovery flag.
     *
     * @param enabled <code>TRUE</code> to enable.
     */
    public void setAutoFeedsDiscovery(boolean enabled)
    {
        autoFeedsDiscovery = enabled;
    }

    /**
     * Returns <code>TRUE</code> when notifications are allowed.
     *
     * @return <code>TRUE</code> when notifications are allowed.
     */
    public boolean isNotificationsAllowed()
    {
        return notificationsAllowed;
    }

    /**
     * Set <code>TRUE</code> when notifications are allowed.
     *
     * @param flag <code>TRUE</code> when notifications are allowed.
     */
    public void setNotificationsAllowed(boolean flag)
    {
        this.notificationsAllowed = flag;
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
        if (!(o instanceof UserGuide)) return false;

        final UserGuide userGuide = (UserGuide)o;

        if (index != userGuide.index) return false;
        if (userId != userGuide.userId) return false;

        if (iconKey != null ? !iconKey.equals(userGuide.iconKey) : userGuide.iconKey != null)
            return false;

        if (publishingEnabled != userGuide.publishingEnabled) return false;
        if (publishingPublic != userGuide.publishingPublic) return false;
        if (publishingTitle != null ? !publishingTitle.equals(userGuide.publishingTitle)
            : userGuide.publishingTitle != null) return false;
        if (publishingTags != null ? !publishingTags.equals(userGuide.publishingTags)
            : userGuide.publishingTags != null) return false;
        if (publishingRating != userGuide.publishingRating) return false;

        if (autoFeedsDiscovery != userGuide.autoFeedsDiscovery) return false;
        if (notificationsAllowed != userGuide.notificationsAllowed) return false;

        return !(title != null ? !title.equals(userGuide.title) : userGuide.title != null);
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
        return HASH_MULTIPLIER * userId + (title != null ? title.hashCode() : 0);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "UserGuide: id=" + id +
            ", userId=" + userId +
            ", title=" + (title != null ? title : "null") +
            ", iconKey=" + (iconKey != null ? iconKey : "null") +
            ", index=" + index +
            ", publisingEnabled=" + publishingEnabled +
            ", publishingTitle=" + publishingTitle +
            ", publishingTags=" + publishingTags +
            ", publishingPublic=" + publishingPublic +
            ", publishingRating=" + publishingRating +
            ", autoFeedsDiscovery=" + autoFeedsDiscovery +
            ", notificationsAllowed=" + notificationsAllowed;
    }
}
