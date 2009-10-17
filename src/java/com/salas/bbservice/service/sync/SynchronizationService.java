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
// $Id: SynchronizationService.java,v 1.9 2007/07/18 15:07:58 alg Exp $
//
package com.salas.bbservice.service.sync;

import com.ibatis.dao.client.DaoManager;
import com.salas.bbservice.domain.*;
import com.salas.bbservice.persistence.*;
import com.salas.bbservice.stats.Statistics;
import com.salas.bbservice.utils.StringUtils;
import com.salas.bbutilities.opml.Importer;
import com.salas.bbutilities.opml.ImporterException;
import com.salas.bbutilities.opml.export.Exporter;
import com.salas.bbutilities.opml.export.ExporterOld;
import com.salas.bbutilities.opml.export.IExporter;
import com.salas.bbutilities.opml.objects.*;
import com.salas.bbutilities.opml.utils.Transformation;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Guides list synchronization service.
 */
public final class SynchronizationService
{
    private static final Logger LOG = Logger.getLogger(SynchronizationService.class.getName());
    private static SynchronizationService instance;

    protected IUserDao userDao;
    protected IChannelDao channelDao;
    protected IUserGuideDao userGuideDao;
    protected IUserChannelDao userChannelDao;
    protected IUserQueryFeedDao userQueryFeedDao;
    protected IUserSearchFeedDao userSearchFeedDao;
    protected IUserPreferenceDao userPreferenceDao;
    protected IUserReadingListDao userReadingListDao;

    /**
     * Hidden singleton constructor.
     */
    private SynchronizationService()
    {
        DaoManager manager = DaoConfig.getDaoManager();
        userDao = (IUserDao)manager.getDao(IUserDao.class);
        channelDao = (IChannelDao)manager.getDao(IChannelDao.class);
        userGuideDao = (IUserGuideDao)manager.getDao(IUserGuideDao.class);
        userChannelDao = (IUserChannelDao)manager.getDao(IUserChannelDao.class);
        userQueryFeedDao = (IUserQueryFeedDao)manager.getDao(IUserQueryFeedDao.class);
        userSearchFeedDao = (IUserSearchFeedDao)manager.getDao(IUserSearchFeedDao.class);
        userPreferenceDao = (IUserPreferenceDao)manager.getDao(IUserPreferenceDao.class);
        userReadingListDao = (IUserReadingListDao)manager.getDao(IUserReadingListDao.class);
    }

    /**
     * Returns instance of the service.
     *
     * @return instance.
     */
    public static synchronized SynchronizationService getInstance()
    {
        if (instance == null) instance = new SynchronizationService();
        return instance;
    }

    /**
     * Stores guides list information received from user in database.
     *
     * @param u     user sent the information.
     * @param opml  information to store.
     *
     * @throws SynchronizationException in case of different errors.
     */
    public void store(User u, String opml)
        throws SynchronizationException
    {
        registerSyncStore(u);

        final Importer imp = new Importer();
        try
        {
            OPMLGuideSet guideSet = imp.processFromString(opml, false);

            final DaoManager sm = DaoConfig.getDaoManager();
            try
            {
                sm.startTransaction();
                clearGuides(u);
                storeGuides(u, guideSet.getGuides());
                updateLastSyncDate(u, guideSet);
                sm.commitTransaction();
            } finally
            {
                sm.endTransaction();
            }
        } catch (ImporterException e)
        {
            LOG.log(Level.SEVERE, "Unable to store data on the server. UserId=" +
                    (u == null ? "null" : Integer.toString(u.getId())) +
                    ", OPML=" + (opml == null ? "null" : opml), e);

            throw new SynchronizationException("Unable to save data on server.");
        } catch (Throwable t)
        {
            LOG.log(Level.SEVERE, "Unable to store data on the server. UserId=" +
                    (u == null ? "null" : Integer.toString(u.getId())) +
                    ", OPML=" + (opml == null ? "null" : opml), t);
        }
    }

    /**
     * Updates last sync time with time from guides set.
     *
     * @param u     user to update.
     * @param set   set to use.
     */
    private void updateLastSyncDate(User u, OPMLGuideSet set)
    {
        if (set.getDateModified() != null)
        {
            u.setLastSyncTime(set.getDateModified());
            userDao.update(u);
        }
    }

    /**
     * Returns the list of guides stored by user in database.
     *
     * @param u             user requesting his information.
     * @param opmlVersion   version of OPML format.
     *
     * @return guides list rendered in OPML.
     */
    public String restore(User u, int opmlVersion)
    {
        registerSyncRestore(u);

        OPMLGuide[] guides = buildGuidesList(u);
        OPMLGuideSet set = new OPMLGuideSet("BlogBridge Feeds", guides, u.getLastSyncTime());

        IExporter exporter = opmlVersion == 0 ? new ExporterOld(true) : new Exporter(true);
        return Transformation.documentToString(exporter.export(set));
    }

    /**
     * Builds the list of guides stored by user in database.
     *
     * @param u user-owner of guides.
     *
     * @return list of guides.
     */
    OPMLGuide[] buildGuidesList(User u)
    {
        List userGuides = userGuideDao.findByUserId(u.getId());

        final int amount = userGuides.size();
        OPMLGuide[] guides = new OPMLGuide[amount];
        for (int i = 0; i < amount; i++)
        {
            guides[i] = buildGuide((UserGuide)userGuides.get(i));
        }

        return guides;
    }

    /**
     * Builds guide with list of channels from database.
     *
     * @param userGuide users guide.
     *
     * @return OPML guide.
     */
    OPMLGuide buildGuide(UserGuide userGuide)
    {
        OPMLGuide guide = new OPMLGuide(userGuide.getTitle(), userGuide.getIconKey(),
            userGuide.isPublishingEnabled(), userGuide.getPublishingTitle(),
            userGuide.getPublishingTags(), userGuide.isPublishingPublic(),
            userGuide.getPublishingRating(), userGuide.isAutoFeedsDiscovery(),
            userGuide.isNotificationsAllowed(), userGuide.isMobile());

        readReadingLists(userGuide, guide);

        List userItems = readGuideItems(userGuide);
        guide.setFeeds(convertUserToOPMLItems(userItems));

        return guide;
    }

    private void readReadingLists(UserGuide aUserGuide, OPMLGuide aGuide)
    {
        int guideId = aUserGuide.getId();
        List lists = userReadingListDao.findByUserGuideId(guideId);
        for (Object lst : lists)
        {
            UserReadingList list = (UserReadingList)lst;

            OPMLReadingList opmlList = new OPMLReadingList(list.getTitle(), list.getXmlUrl());
            aGuide.add(opmlList);

            List feeds = userChannelDao.select(guideId, list.getId());
            List<DefaultOPMLFeed> efeeds = convertUserToOPMLItems(feeds);
            List<DirectOPMLFeed>  dfeeds = new ArrayList<DirectOPMLFeed>(efeeds.size());
            for (DefaultOPMLFeed feed: efeeds)
            {
                if (feed instanceof DirectOPMLFeed) dfeeds.add((DirectOPMLFeed)feed);
            }
            opmlList.setFeeds(dfeeds);
        }
    }

    /**
     * Converts all items in user items list into OPML items.
     *
     * @param userItems list of user items.
     *
     * @return list of OPML items.
     */
    private ArrayList<DefaultOPMLFeed> convertUserToOPMLItems(List userItems)
    {
        int count = userItems.size();
        ArrayList<DefaultOPMLFeed> opmlItems = new ArrayList<DefaultOPMLFeed>(count);

        for (int i = 0; i < count; i++)
        {
            Object item = userItems.get(i);

            DefaultOPMLFeed feed;
            if (item instanceof UserChannel)
            {
                feed = buildChannel((UserChannel)item);
            } else if (item instanceof UserQueryFeed)
            {
                feed = buildQueryFeed((UserQueryFeed)item);
            } else
            {
                feed = buildSearchFeed((UserSearchFeed)item);
            }

            opmlItems.add(feed);
        }

        return opmlItems;
    }

    /**
     * Reads items (channels and query feeds) assigned to the user guide.
     *
     * @param userGuide user guide to fetch items for.
     *
     * @return list of user-space items.
     *
     * @see UserChannel
     * @see UserQueryFeed
     */
    private List readGuideItems(UserGuide userGuide)
    {
        int guideId = userGuide.getId();

        List userDirectFeeds = userChannelDao.select(guideId, null);
        List userQueryFeeds = userQueryFeedDao.findByUserGuideId(guideId);
        List userSearchFeeds = userSearchFeedDao.findByUserGuideId(guideId);

        return combineFeeds(userDirectFeeds, userQueryFeeds, userSearchFeeds);
    }

    /**
     * Combines the guide items from several lists into single list according to their indexes.
     *
     * @param aUserDirectFeeds  list of user feeds.
     * @param aUserQueryFeeds   list of user query feeds.
     * @param aUserSearchFeeds  list of user search feeds.
     *
     * @return all user feeds in single list ordered by index.
     */
    static List combineFeeds(List aUserDirectFeeds, List aUserQueryFeeds, List aUserSearchFeeds)
    {
        SortedSet feeds = new TreeSet(new FeedIndexComparator());

        feeds.addAll(aUserDirectFeeds);
        feeds.addAll(aUserQueryFeeds);
        feeds.addAll(aUserSearchFeeds);

        return Arrays.asList(feeds.toArray());
    }

    /**
     * Builds OPML search feed from the user search feed.
     *
     * @param userSearchFeed user search feed.
     *
     * @return OPML search feed.
     */
    static SearchOPMLFeed buildSearchFeed(UserSearchFeed userSearchFeed)
    {
        String title = userSearchFeed.getTitle();
        String query = userSearchFeed.getQuery();
        int rating = userSearchFeed.getRating();
        int limit = userSearchFeed.getLimit();
        int viewType = userSearchFeed.getViewType();
        boolean viewModeEnabled = userSearchFeed.isViewModeEnabled();
        int viewMode = userSearchFeed.getViewMode();
        int handlingType = userSearchFeed.getHandlingType();

        SearchOPMLFeed feed = new SearchOPMLFeed(title, query, limit, rating, viewType, viewModeEnabled, viewMode,
            userSearchFeed.getAscendingSorting(), handlingType);

        feed.setDedupEnabled(userSearchFeed.isDedupEnabled());
        feed.setDedupFrom(userSearchFeed.getDedupFrom());
        feed.setDedupTo(userSearchFeed.getDedupTo());

        return feed;
    }

    /**
     * Builds OPML query feed from the user query feed.
     *
     * @param userQueryFeed user query feed.
     *
     * @return OPML query feed.
     */
    static QueryOPMLFeed buildQueryFeed(UserQueryFeed userQueryFeed)
    {
        String title = userQueryFeed.getTitle();
        int queryType = userQueryFeed.getQueryType();
        String parameter = userQueryFeed.getQueryParam();
        String readArticlesKeys = userQueryFeed.getReadArticlesKeys();
        String pinnedArticlesKeys = userQueryFeed.getPinnedArticlesKeys();
        int rating = userQueryFeed.getRating();
        int viewType = userQueryFeed.getViewType();
        boolean viewModeEnabled = userQueryFeed.isViewModeEnabled();
        int viewMode = userQueryFeed.getViewMode();

        QueryOPMLFeed ofeed = new QueryOPMLFeed(title, queryType, parameter, "",
            readArticlesKeys, pinnedArticlesKeys, userQueryFeed.getLimit(), rating,
            viewType, viewModeEnabled, viewMode, userQueryFeed.getAscendingSorting(),
            userQueryFeed.getHandlingType());

        ofeed.setDedupEnabled(userQueryFeed.isDedupEnabled());
        ofeed.setDedupFrom(userQueryFeed.getDedupFrom());
        ofeed.setDedupTo(userQueryFeed.getDedupTo());

        fillOPMLDataFeedProperties(ofeed, userQueryFeed);

        return ofeed;
    }

    /**
     * Fills OPML data feed with properties.
     *
     * @param ofeed target OPML data feed.
     * @param dfeed data feed.
     */
    private static void fillOPMLDataFeedProperties(DataOPMLFeed ofeed, UserDataFeed dfeed)
    {
        long period = dfeed.getUpdatePeriod();
        ofeed.setUpdatePeriod(period > 0 ? period : null);
    }

    /**
     * Builds OPML channel from information stored in database.
     *
     * @param userChannel   user channel object.
     *
     * @return OPML channel.
     */
    DirectOPMLFeed buildChannel(UserChannel userChannel)
    {
        Channel ch = channelDao.findById(userChannel.getChannelId());

        DirectOPMLFeed ofeed = new DirectOPMLFeed(
                ch.getTitle(),
                ch.getXmlUrl(),
                ch.getHtmlUrl(),
                userChannel.getRating(),
                userChannel.getReadArticlesKeys(),
                userChannel.getPinnedArticlesKeys(),
                userChannel.getPurgeLimit(),
                userChannel.getCustomTitle(),
                userChannel.getCustomCreator(),
                userChannel.getCustomDescription(),
                userChannel.getTags(),
                userChannel.getTagsDescription(),
                userChannel.getTagsExtended(),
                userChannel.isDisabled(),
                userChannel.getViewType(),
                userChannel.isViewModeEnabled(),
                userChannel.getViewMode(),
                userChannel.getAscendingSorting(),
                userChannel.getHandlingType());

        fillOPMLDataFeedProperties(ofeed, userChannel);

        return ofeed;
    }

    /**
     * Remove all current user-defined guides.
     *
     * @param u     user account.
     */
    private void clearGuides(User u)
    {
        userDao.deleteAllGuides(u);
    }

    /**
     * Writes guides to database.
     *
     * @param u         user-owner of guides.
     * @param guides    guides.
     */
    private void storeGuides(User u, OPMLGuide[] guides)
    {
        // iterate through guides and save them
        for (int i = 0; i < guides.length; i++)
        {
            storeGuide(u, guides[i], i);
        }
    }

    /**
     * Writes single guide to database.
     *
     * @param u         user-owner of guide.
     * @param guide     guide to write.
     * @param index     index in list.
     */
    void storeGuide(User u, OPMLGuide guide, int index)
    {
        UserGuide userGuide = new UserGuide(u.getId(), guide.getTitle(), guide.getIcon(), index,
            guide.isPublishingEnabled(), guide.getPublishingTitle(), guide.getPublishingTags(),
            guide.isPublishingPublic(), guide.getPublishingRating(), guide.isAutoFeedsDiscovery(),
            guide.isNotificationsAllowed(), guide.isMobile());
        userGuideDao.add(userGuide);

        int cnt = storeReadingLists(userGuide.getId(), guide);
        storeDomainFeeds(userGuide.getId(), convertToDomain(guide.getFeeds(), cnt, null));
    }

    /**
     * Stores all reading lists one by one.
     *
     * @param aGuideId  guide ID.
     * @param aGuide    guide to take reading lists from.
     *
     * @return number of feeds stored.
     */
    private int storeReadingLists(int aGuideId, OPMLGuide aGuide)
    {
        int count = 0;
        OPMLReadingList[] lists = aGuide.getReadingLists();
        for (OPMLReadingList list : lists)
        {
            UserReadingList rl = new UserReadingList(aGuideId, list.getTitle(), list.getURL());
            userReadingListDao.add(rl);

            List feeds = convertToDomain(list.getFeeds(), 0, rl.getId());
            storeDomainFeeds(aGuideId, feeds);

            count += feeds.size();
        }

        return count;
    }

    private List convertToDomain(List aFeeds, int aFirstIndex, Integer readingListId)
    {
        int count = aFeeds.size();

        List<Object> domainFeeds = new ArrayList<Object>(count);
        HashSet<String> directFeedsURLs = new HashSet<String>();

        // Selects only the OPML channels from the list
        for (int i = 0; i < count; i++)
        {
            Object feed = aFeeds.get(i);
            Object domainFeed = null;
            if (feed instanceof QueryOPMLFeed)
            {
                domainFeed = convertToDomain((QueryOPMLFeed)feed, aFirstIndex + i);
            } else if (feed instanceof SearchOPMLFeed)
            {
                domainFeed = convertToDomain((SearchOPMLFeed)feed, aFirstIndex + i);
            } else if (feed instanceof DirectOPMLFeed)
            {
                DirectOPMLFeed opmlDirectFeed = (DirectOPMLFeed)feed;

                UserChannel directFeed = syncChannel(opmlDirectFeed);
                String url = opmlDirectFeed.getXmlURL().trim();
                if (!directFeedsURLs.contains(url))
                {
                    directFeedsURLs.add(url);
                    directFeed.setIndex(aFirstIndex + i);
                    directFeed.setUserReadingListId(readingListId);

                    domainFeed = directFeed;
                }
            }

            domainFeeds.add(domainFeed);
        }

        return domainFeeds;
    }

    private static UserSearchFeed convertToDomain(SearchOPMLFeed aOpmlSearchFeed, int aIndex)
    {
        String title = aOpmlSearchFeed.getTitle();
        String query = aOpmlSearchFeed.getQuery();
        int limit = aOpmlSearchFeed.getLimit();
        int rating = aOpmlSearchFeed.getRating();

        int viewType = aOpmlSearchFeed.getViewType();
        boolean viewModeEnabled = aOpmlSearchFeed.isViewModeEnabled();
        int viewMode = aOpmlSearchFeed.getViewMode();
        int handlingType = aOpmlSearchFeed.getHandlingType();

        UserSearchFeed feed = new UserSearchFeed(-1, aIndex, title, query, limit, rating,
            viewType, viewModeEnabled, viewMode, aOpmlSearchFeed.getAscendingSorting(), handlingType);

        feed.setDedupEnabled(aOpmlSearchFeed.isDedupEnabled());
        feed.setDedupFrom(aOpmlSearchFeed.getDedupFrom());
        feed.setDedupTo(aOpmlSearchFeed.getDedupTo());

        return feed;
    }

    private static UserQueryFeed convertToDomain(QueryOPMLFeed ofeed, int aIndex)
    {
        String title = ofeed.getTitle();
        int queryType = ofeed.getQueryType();
        String parameter = ofeed.getQueryParam();
        String readArticlesKeys = ofeed.getReadArticlesKeys();
        String pinnedArticlesKeys = ofeed.getPinnedArticlesKeys();
        int limit = ofeed.getLimit();
        int rating = ofeed.getRating();

        int viewType = ofeed.getViewType();
        boolean viewModeEnabled = ofeed.isViewModeEnabled();
        int viewMode = ofeed.getViewMode();

        UserQueryFeed feed = new UserQueryFeed(-1, aIndex, title, queryType, parameter,
            readArticlesKeys, pinnedArticlesKeys, limit, rating, viewType, viewModeEnabled, viewMode,
            ofeed.getAscendingSorting(), ofeed.getHandlingType());

        feed.setDedupEnabled(ofeed.isDedupEnabled());
        feed.setDedupFrom(ofeed.getDedupFrom());
        feed.setDedupTo(ofeed.getDedupTo());

        fillDataFeedProperties(feed, ofeed);

        return feed;
    }

    /**
     * Stores feeds from list in database.
     *
     * @param userGuideId   ID of user's guide.
     * @param domainFeeds   list of feeds.
     */
    void storeDomainFeeds(int userGuideId, List domainFeeds)
    {
        // The list of IDs of saved channels in this guide
        // We maintain it to not allow saving the user channel for the same
        // channel twice as it's not allowed. The older versions of BB
        // could have the same feed mentioned twice in the same guide
        // but it's no longer legal.
        ArrayList<Integer> savedChannelIDs = new ArrayList<Integer>();

        int count = domainFeeds.size();
        for (int i = 0; i < count; i++)
        {
            Object feed = domainFeeds.get(i);

            try
            {
                if (feed instanceof UserChannel)
                {
                    UserChannel directFeed = (UserChannel)feed;
                    Integer cid = directFeed.getChannelId();
                    if (!savedChannelIDs.contains(cid))
                    {
                        savedChannelIDs.add(cid);
                        directFeed.setUserGuideId(userGuideId);
                        userChannelDao.add(directFeed);
                    }
                } else if (feed instanceof UserQueryFeed)
                {
                    UserQueryFeed queryFeed = (UserQueryFeed)feed;
                    queryFeed.setUserGuideId(userGuideId);
                    userQueryFeedDao.add(queryFeed);
                } else if (feed instanceof UserSearchFeed)
                {
                    UserSearchFeed searchFeed = (UserSearchFeed)feed;
                    searchFeed.setUserGuideId(userGuideId);
                    userSearchFeedDao.add(searchFeed);
                }
            } catch (Exception e)
            {
                LOG.log(Level.SEVERE, "Could not store feed: " + feed, e);
            }
        }
    }

    /**
     * Stores list of query feeds in database assigned to the guide given by ID.
     *
     * @param userGuideId   ID of the guide to assign query feeds to.
     * @param queryFeeds    query feeds to store.
     */
    void storeQueryFeeds(int userGuideId, UserQueryFeed[] queryFeeds)
    {
        for (final UserQueryFeed queryFeed : queryFeeds)
        {
            queryFeed.setUserGuideId(userGuideId);
            try
            {
                userQueryFeedDao.add(queryFeed);
            } catch (Exception e)
            {
                LOG.log(Level.SEVERE, "Could not store channel: " + queryFeed, e);
            }
        }
    }

    /**
     * Creates <code>UserChannel</code> on base of channel and global channels information. If
     * Channel is not registered in database it is registered. <code>userGuideId</code> and
     * <code>index</code> properties are not set. You should populate them manually.
     *
     * @param ofeed   channel from guide.
     *
     * @return user channel.
     */
    UserChannel syncChannel(DirectOPMLFeed ofeed)
    {
        Channel c = channelDao.findByXmlUrl(ofeed.getXmlURL());

        // If there's no channel with such XML URL in database create new record.
        if (c == null)
        {
            c = new Channel(ofeed.getTitle(), ofeed.getHtmlURL(), ofeed.getXmlURL());
            channelDao.add(c);
        } else
        {
            updateIfNecessary(c, ofeed);
        }

        UserChannel feed = new UserChannel(-1, c.getId(), ofeed.getRating(), -1,
                ofeed.getReadArticlesKeys(),
                ofeed.getPinnedArticlesKeys(),
                ofeed.getLimit(),
                ofeed.getCustomTitle(),
                ofeed.getCustomCreator(),
                ofeed.getCustomDescription(),
                ofeed.getTags(),
                ofeed.getTagsDescription(),
                ofeed.getTagsExtended(),
                null,
                ofeed.isDisabled(),
                ofeed.getViewType(),
                ofeed.isViewModeEnabled(),
                ofeed.getViewMode(),
                ofeed.getAscendingSorting(),
                ofeed.getHandlingType());

        fillDataFeedProperties(feed, ofeed);

        return feed;
    }

    /**
     * Fills data feed properties.
     *
     * @param dfeed target feed.
     * @param ofeed OPML data feed.
     */
    private static void fillDataFeedProperties(UserDataFeed dfeed, DataOPMLFeed ofeed)
    {
        Long period = ofeed.getUpdatePeriod();
        dfeed.setUpdatePeriod(period == null ? -1 : period);
    }

    /**
     * Checks if all fields are set in database-feed and if not takes info from ingoing.
     *
     * @param c     database instance.
     * @param feed  ingoing instance.
     */
    void updateIfNecessary(Channel c, DirectOPMLFeed feed)
    {
        boolean update = false;

        // update title if it's empty or equal to '[uninitialized channel]'
        String oldTitle = c.getTitle();
        String newTitle = feed.getTitle();
        if (!StringUtils.isEmpty(feed.getTitle()))
        {
            newTitle = newTitle.trim();

            if (StringUtils.isEmpty(oldTitle) ||
                oldTitle.equals("[uninitialized channel]") ||
                oldTitle.startsWith("http://") ||
                !oldTitle.equals(newTitle))
            {
                c.setTitle(feed.getTitle().trim());
                update = true;
            }
        }

        // update HTML URL if necessary
        if (StringUtils.isEmpty(c.getHtmlUrl()) && !StringUtils.isEmpty(feed.getHtmlURL()))
        {
            c.setHtmlUrl(feed.getHtmlURL().trim());
            update = true;
        }

        // update channel if updates happened
        if (update) channelDao.update(c);
    }

    /**
     * Stores preferences of the user into database.
     *
     * @param user          user to associate preferences with.
     * @param preferences   preferences to store.
     */
    public synchronized void storePrefs(User user, Hashtable preferences)
    {
        Map prefs = loadUserPreferences(user);

        UserPreference obj = new UserPreference(user.getId(), null, null);

        DaoConfig.getDaoManager().startTransaction();

        try
        {
            // Insert or update preferences
            for (Object o : preferences.entrySet())
            {
                Map.Entry entry = (Map.Entry)o;
                String name = (String)entry.getKey();

                boolean hasInOldPrefs = prefs.containsKey(name);
                byte[] oldValue = (byte[])prefs.get(name);
                byte[] newValue = (byte[])entry.getValue();

                obj.setName(name);
                obj.setValue(newValue == null ? null : StringUtils.fromUTF8(newValue));

                if (hasInOldPrefs)
                {
                    // update
                    if ((oldValue == null && newValue != null) ||
                        (oldValue != null && (newValue == null ||
                            !Arrays.equals(oldValue, newValue))))
                    {
                        userPreferenceDao.update(obj);
                    }

                    // remove the record as this map will be used for removals
                    prefs.remove(name);
                } else if (!hasInOldPrefs)
                {
                    // insert
                    userPreferenceDao.add(obj);
                }
            }

            // Remove all the rest
            for (Object o : prefs.entrySet())
            {
                Map.Entry entry = (Map.Entry)o;
                String name = (String)entry.getKey();

                obj.setName(name);
                userPreferenceDao.delete(obj);
            }

            DaoConfig.getDaoManager().commitTransaction();
        } finally
        {
            DaoConfig.getDaoManager().endTransaction();
        }
    }

    /**
     * Loads list of preferences from database associated with user. Values are presented
     * as arays of bytes in UTF-8 encoding.
     *
     * @param user user to load preferences for.
     *
     * @return preferences map.
     */
    private Hashtable loadUserPreferences(User user)
    {
        if (user == null || user.getId() < 0) return null;

        List prefsList = userPreferenceDao.selectByUserId(user.getId());

        Hashtable<String, byte[]> prefs = new Hashtable<String, byte[]>();
        for (Object aPrefsList : prefsList)
        {
            UserPreference userPreference = (UserPreference)aPrefsList;
            String value = userPreference.getValue();
            prefs.put(userPreference.getName(), value == null ? null : StringUtils.toUTF8(value));
        }

        return prefs;
    }

    /**
     * Restores preferences of the user.
     *
     * @param user user to get preferences for.
     *
     * @return preferences.
     */
    public Hashtable restorePrefs(User user)
    {
        return loadUserPreferences(user);
    }

    /**
     * Compares two feeds by their indexes.
     */
    private static class FeedIndexComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            int index1 = getFeedIndex(o1);
            int index2 = getFeedIndex(o2);

            // We can't return 0 because it means that objects are equal.
            // We let one of them be less than the other as it doesn't matter which one goes first.
            return index1 < index2 ? -1 : index1 == index2 ? -1 : 1;
        }

        private int getFeedIndex(Object feed)
        {
            int index;

            if (feed instanceof UserChannel)
            {
                index = ((UserChannel)feed).getIndex();
            } else if (feed instanceof UserQueryFeed)
            {
                index = ((UserQueryFeed)feed).getIndex();
            } else
            {
                index = ((UserSearchFeed)feed).getIndex();
            }

            return index;
        }
    }

    // --------------------------------------------------------------------------------------------
    // Synchronization registration
    // --------------------------------------------------------------------------------------------

    /**
     * Registers synchronization attempt of a user.
     *
     * @param u user.
     */
    private void registerSyncStore(User u)
    {
        Statistics.registerSyncStore();
        userDao.registerSync(u, true);
    }

    /**
     * Registers synchronization attempt of a user.
     *
     * @param u user.
     */
    private void registerSyncRestore(User u)
    {
        Statistics.registerSyncRestore();
        userDao.registerSync(u, false);
    }
}
