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
// $Id: UserChannel.java,v 1.4 2007/04/30 14:47:26 alg Exp $
//
package com.salas.bbservice.domain;

/**
 * Link between channels (common) and user guides. It's also the place
 * where we store the rating of the channel per user.
 */
public class UserChannel extends UserDataFeed
{
    private static final int HASH_MULTIPLIER = 29;

    private int id          = -1;
    private int userGuideId = -1;
    private int channelId   = -1;
    private int rating      = 1;
    private int index       = -1;
    private int purgeLimit  = -1;

    private String readArticlesKeys;
    private String pinnedArticlesKeys;

    private String customTitle;
    private String customCreator;
    private String customDescription;

    private String tags;
    private String tagsDescription;
    private String tagsExtended;

    private Integer userReadingListId;

    private boolean disabled;

    private int viewType = -1;
    private boolean viewModeEnabled = false;
    private int viewMode = -1;
    private int handlingType = 0;

    private Boolean ascendingSorting = null;

    /**
     * Creates empty user channel object.
     */
    public UserChannel()
    {
    }

    /**
     * Constructs object with given values.
     *
     * @param userGuideId       ID of user's guide.
     * @param channelId         ID of channel.
     * @param rating            rating of this channel set by user.
     * @param index             index of the channel in list.
     * @param readArticlesKeys  comma-delimetered list of keys for read articles.
     * @param pinnedArticlesKeys comma-delimetered list of keys for pinned articles.
     * @param purgeLimit        purge limit.
     * @param customTitle       custom title.
     * @param customCreator     custom creator.
     * @param customDescription custom description.
     * @param tags              tags.
     * @param tagsDescription   tags description.
     * @param tagsExtended      tags extended description.
     * @param readingListId     optional ID of reading list.
     * @param disabled          disabled flag.
     * @param viewType          type of view.
     * @param viewModeEnabled   the state of view mode.
     * @param viewMode          the view mode.
     * @param aAscendingSorting the ascending sorting flag.
     * @param aHandlingType     handling type.
     */
    public UserChannel(int userGuideId, int channelId, int rating, int index,
                       String readArticlesKeys, String pinnedArticlesKeys, int purgeLimit, String customTitle, String customCreator,
                       String customDescription, String tags, String tagsDescription, String tagsExtended,
                       Integer readingListId, boolean disabled, int viewType, boolean viewModeEnabled, int viewMode,
                       Boolean aAscendingSorting, int aHandlingType)
    {
        this.userGuideId = userGuideId;
        this.channelId = channelId;
        this.rating = rating;
        this.index = index;
        this.readArticlesKeys = readArticlesKeys;
        this.pinnedArticlesKeys = pinnedArticlesKeys;
        this.purgeLimit = purgeLimit;
        this.customTitle = customTitle;
        this.customCreator = customCreator;
        this.customDescription = customDescription;
        this.tags = tags;
        this.tagsDescription = tagsDescription;
        this.tagsExtended = tagsExtended;
        this.userReadingListId = readingListId;
        this.disabled = disabled;
        this.viewType = viewType;
        this.viewModeEnabled = viewModeEnabled;
        this.viewMode = viewMode;
        this.ascendingSorting = aAscendingSorting;
        this.handlingType = aHandlingType;
    }

    /**
     * Returns the identifier.
     *
     * @return identifier.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the identifier.
     *
     * @param id identifier.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Returns the ID of the user's guide.
     *
     * @return ID of the guide.
     *
     * @see UserGuide
     */
    public int getUserGuideId()
    {
        return userGuideId;
    }

    /**
     * Sets ID of the user's guide.
     *
     * @param userGuideId ID of the guide.
     *
     * @see UserGuide
     */
    public void setUserGuideId(int userGuideId)
    {
        this.userGuideId = userGuideId;
    }

    /**
     * Returns ID of the channel.
     *
     * @return ID of the channel.
     *
     * @see Channel
     */
    public int getChannelId()
    {
        return channelId;
    }

    /**
     * Sets the ID of channel (common).
     *
     * @param channelId ID of the channel.
     *
     * @see Channel
     */
    public void setChannelId(int channelId)
    {
        this.channelId = channelId;
    }

    /**
     * Returns rating of the channel set by user.
     *
     * @return rating of the channel.
     */
    public int getRating()
    {
        return rating;
    }

    /**
     * Sets user rating of this channel.
     *
     * @param rating rating of the channel set by user.
     */
    public void setRating(int rating)
    {
        this.rating = rating;
    }

    /**
     * Returns the index in list of channels within the guide.
     *
     * @return index in list.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Sets the index in list of channels within the guide.
     *
     * @param index index in list.
     */
    public void setIndex(int index)
    {
        this.index = index;
    }

    /**
     * Returns the list of keys of read articles.
     * The list is delimetered with comma.
     *
     * @return list of keys.
     */
    public String getReadArticlesKeys()
    {
        return readArticlesKeys;
    }

    /**
     * Sets the list of keys of read articles.
     *
     * @param readArticlesKeys list of articles keys.
     */
    public void setReadArticlesKeys(String readArticlesKeys)
    {
        this.readArticlesKeys = readArticlesKeys;
    }

    /**
     * Returns the list of keys of pinned articles.
     * The list is delimetered with comma.
     *
     * @return list of keys.
     */
    public String getPinnedArticlesKeys()
    {
        return pinnedArticlesKeys;
    }

    /**
     * Sets the list of keys of pinned articles.
     *
     * @param pinnedArticlesKeys list of articles keys.
     */
    public void setPinnedArticlesKeys(String pinnedArticlesKeys)
    {
        this.pinnedArticlesKeys = pinnedArticlesKeys;
    }

    /**
     * Returns purge limit.
     *
     * @return purge limit.
     */
    public int getPurgeLimit()
    {
        return purgeLimit;
    }

    /**
     * Sets new purge limit.
     *
     * @param aPurgeLimit purge limit.
     */
    public void setPurgeLimit(int aPurgeLimit)
    {
        purgeLimit = aPurgeLimit;
    }

    /**
     * Returns custom title.
     *
     * @return custom title.
     */
    public String getCustomTitle()
    {
        return customTitle;
    }

    /**
     * Sets custom title.
     *
     * @param aCustomTitle custom title.
     */
    public void setCustomTitle(String aCustomTitle)
    {
        customTitle = aCustomTitle;
    }

    /**
     * Returns custom creator.
     *
     * @return custom creator.
     */
    public String getCustomCreator()
    {
        return customCreator;
    }

    /**
     * Sets custom creator.
     *
     * @param aCustomCreator custom creator.
     */
    public void setCustomCreator(String aCustomCreator)
    {
        customCreator = aCustomCreator;
    }

    /**
     * Returns custom description.
     *
     * @return custom description.
     */
    public String getCustomDescription()
    {
        return customDescription;
    }

    /**
     * Sets custom description.
     * @param aCustomDescription custom description.
     */
    public void setCustomDescription(String aCustomDescription)
    {
        customDescription = aCustomDescription;
    }

    /**
     * Returns tags.
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
     * Returns tags description.
     *
     * @return tags description.
     */
    public String getTagsDescription()
    {
        return tagsDescription;
    }

    /**
     * Sets tags description.
     *
     * @param aTagsDescription tags description.
     */
    public void setTagsDescription(String aTagsDescription)
    {
        tagsDescription = aTagsDescription;
    }

    /**
     * Returns extended tags description.
     *
     * @return extended tags description.
     */
    public String getTagsExtended()
    {
        return tagsExtended;
    }

    /**
     * Sets extended tags description.
     *
     * @param aTagsExtended extended tags description.
     */
    public void setTagsExtended(String aTagsExtended)
    {
        tagsExtended = aTagsExtended;
    }

    /**
     * Returns view type.
     *
     * @return type.
     */
    public int getViewType()
    {
        return viewType;
    }

    /**
     * Sets view type.
     *
     * @param aViewType view type.
     */
    public void setViewType(int aViewType)
    {
        viewType = aViewType;
    }

    /**
     * Returns reading list ID.
     *
     * @return reading list ID.
     */
    public Integer getUserReadingListId()
    {
        return userReadingListId;
    }

    /**
     * Sets reading list ID.
     *
     * @param id reading list ID.
     */
    public void setUserReadingListId(Integer id)
    {
        userReadingListId = id;
    }

    /**
     * Returns disableness state.
     *
     * @return state.
     */
    public boolean isDisabled()
    {
        return disabled;
    }

    /**
     * Sets disableness state.
     *
     * @param disabled state.
     */
    public void setDisabled(boolean disabled)
    {
        this.disabled = disabled;
    }

    /**
     * Returns the state of view mode enabled flag.
     *
     * @return <code>TRUE</code> when custom view mode is enabled.
     */
    public boolean isViewModeEnabled()
    {
        return viewModeEnabled;
    }

    /**
     * Sets the state of view mode enabled flag.
     *
     * @param en <code>TRUE</code> to enable custom view mode.
     */
    public void setViewModeEnabled(boolean en)
    {
        this.viewModeEnabled = en;
    }

    /**
     * Returns custom view mode.
     *
     * @return custom view mode.
     */
    public int getViewMode()
    {
        return viewMode;
    }

    /**
     * Sets custom view mode.
     *
     * @param mode custom view mode.
     */
    public void setViewMode(int mode)
    {
        this.viewMode = mode;
    }

    /**
     * Returns the ascending sorting state.
     *
     * @return ascending sorting.
     */
    public Boolean getAscendingSorting()
    {
        return ascendingSorting;
    }

    /**
     * Sets the ascending sorting state.
     *
     * @param asc state.
     */
    public void setAscendingSorting(Boolean asc)
    {
        this.ascendingSorting = asc;
    }


    /**
     * Returns the handling type.
     *
     * @return handling type.
     */
    public int getHandlingType()
    {
        return handlingType;
    }

    /**
     * Sets the handling type.
     *
     * @param type type.
     */
    public void setHandlingType(int type)
    {
        this.handlingType = type;
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
        if (!(o instanceof UserChannel)) return false;
        if (!super.equals(o)) return false;

        final UserChannel feed = (UserChannel)o;

        if (channelId != feed.channelId) return false;
        if (index != feed.index) return false;
        if (rating != feed.rating) return false;
        if (userGuideId != feed.userGuideId) return false;
        if (purgeLimit != feed.purgeLimit) return false;
        if (customCreator != null ? !customCreator.equals(feed.customCreator)
            : feed.customCreator != null) return false;
        if (customDescription != null ? !customDescription.equals(feed.customDescription)
            : feed.customDescription != null) return false;
        if (customTitle != null ? !customTitle.equals(feed.customTitle)
            : feed.customTitle != null) return false;
        if (readArticlesKeys != null ? !readArticlesKeys.equals(feed.readArticlesKeys)
            : feed.readArticlesKeys != null) return false;
        if (pinnedArticlesKeys != null ? !pinnedArticlesKeys.equals(feed.pinnedArticlesKeys)
            : feed.pinnedArticlesKeys != null) return false;
        if (tags != null ? !tags.equals(feed.tags) : feed.tags != null) return false;
        if (tagsDescription != null ? !tagsDescription.equals(feed.tagsDescription)
            : feed.tagsDescription != null) return false;
        if (tagsExtended != null ? !tagsExtended.equals(feed.tagsExtended)
            : feed.tagsExtended != null) return false;
        if (userReadingListId != null ? !userReadingListId.equals(feed.userReadingListId)
            : feed.userReadingListId != null) return false;

        if (viewType != feed.viewType) return false;
        if (viewMode != feed.viewMode) return false;
        if (viewModeEnabled != feed.viewModeEnabled) return false;
        if (handlingType != feed.handlingType) return false;

        if (ascendingSorting != null
            ? !ascendingSorting.equals(feed.ascendingSorting)
            : feed.ascendingSorting != null) return false;

        return disabled == feed.disabled;
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
        result = userGuideId;
        result = HASH_MULTIPLIER * result + channelId;
        return result;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "UserChannel: id=" + id +
            ", userGuideId=" + userGuideId +
            ", channelId=" + channelId +
            ", rating=" + rating +
            ", index=" + index +
            ", readArticlesKeys=" + readArticlesKeys +
            ", pinnedArticlesKeys=" + pinnedArticlesKeys +
            ", purgeLimit=" + purgeLimit +
            ", customTitle=" + customTitle +
            ", customCreator=" + customCreator +
            ", customDescription=" + customDescription +
            ", tags=" + tags +
            ", tagsDescription=" + tagsDescription +
            ", tagsExtended=" + tagsExtended +
            ", readingListId=" + userReadingListId +
            ", disabled=" + disabled +
            ", viewType=" + viewType +
            ", viewModeEnabled=" + viewModeEnabled +
            ", viewMode=" + viewMode +
            ", ascendingSorting=" + ascendingSorting +
            ", handlingType=" + handlingType +
            super.toString();
    }
}
