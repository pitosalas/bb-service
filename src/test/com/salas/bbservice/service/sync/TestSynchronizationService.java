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
// $Id: TestSynchronizationService.java,v 1.7 2007/07/18 15:07:58 alg Exp $
//

package com.salas.bbservice.service.sync;

import com.salas.bbservice.domain.*;
import com.salas.bbservice.persistence.BasicDaoTestCase;
import com.salas.bbservice.utils.StringUtils;
import com.salas.bbutilities.opml.objects.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @see SynchronizationService
 */
public class TestSynchronizationService extends BasicDaoTestCase
{
    private SynchronizationService ss;

    protected void setUp() throws Exception
    {
        super.setUp();
        ss = SynchronizationService.getInstance();
    }

    /**
     * @see SynchronizationService#updateIfNecessary
     */
    public void testUpdateIfNecessary()
    {
        Channel c;
        DirectOPMLFeed oc1, oc2;

        c = new Channel("", "", "xml");
        oc1 = new DirectOPMLFeed("titleA", "xml", "htmlA", 1, null, null, -1, null, null, null, null, null, null, true, 1, true, 1, true);
        oc2 = new DirectOPMLFeed("titleB", "xml", "htmlB", 1, null, null, -1, null, null, null, null, null, null, false, 1, false, 2, false);

        // add global channel
        cleanChannelByXmlUrl("xml");
        channelDao.add(c);

        try
        {
            // update with data from oc1
            ss.updateIfNecessary(c, oc1);

            Channel c1 = channelDao.findByXmlUrl("xml");
            assertEquals(c1.getTitle(), oc1.getTitle());
            assertEquals(c1.getHtmlUrl(), oc1.getHtmlURL());

            // update with data from oc2
            ss.updateIfNecessary(c, oc2);

            Channel c2 = channelDao.findByXmlUrl("xml");
            assertEquals(c2.getTitle(), oc2.getTitle());
            assertEquals(c2.getHtmlUrl(), oc1.getHtmlURL());
        } catch (Exception e)
        {
            // cleanup
            channelDao.delete(c);
        }
    }

    /**
     * Tests creation of channel if it doesn't exist.
     *
     * @see SynchronizationService#syncChannel
     */
    public void testSyncChannelCreate()
    {
        Channel c;
        UserChannel uc;
        DirectOPMLFeed oc;

        oc = new DirectOPMLFeed("titleA", "xml", "htmlA", 1, "a,b", "c,d", 2, "a", "b", "c", "d", "e", "f", false, 1, false, 1, false);
        oc.setUpdatePeriod(2l);

        cleanChannelByXmlUrl("xml");
        try
        {
            uc = ss.syncChannel(oc);
            c = channelDao.findByXmlUrl("xml");

            assertNotNull(c);
            assertEquals(oc.getTitle(), c.getTitle());
            assertEquals(oc.getHtmlURL(), c.getHtmlUrl());
            assertEquals(-1, uc.getIndex());
            assertEquals(1, uc.getRating());
            assertEquals("a,b", uc.getReadArticlesKeys());
            assertEquals("c,d", uc.getPinnedArticlesKeys());
            assertEquals(2, uc.getPurgeLimit());
            assertEquals("a", uc.getCustomTitle());
            assertEquals("b", uc.getCustomCreator());
            assertEquals("c", uc.getCustomDescription());
            assertEquals(-1, uc.getUserGuideId());
            assertEquals(c.getId(), uc.getChannelId());
            assertEquals(oc.getUpdatePeriod(), new Long(uc.getUpdatePeriod()));
        } finally
        {
            // cleanup
            cleanChannelByXmlUrl("xml");
        }
    }

    /**
     * Tests association with existing global channel.
     *
     * @see SynchronizationService#syncChannel
     */
    public void testSyncChannelAssociate()
    {
        Channel c;
        UserChannel uc;
        DirectOPMLFeed oc;

        c = new Channel("", "", "xml");
        oc = new DirectOPMLFeed("titleA", "xml", "htmlA", 1, "a,b", "c,d", -1, "a", "b", "c", "d", "e", "f", false, 1, false, 1, false);

        cleanChannelByXmlUrl("xml");
        channelDao.add(c);
        try
        {
            uc = ss.syncChannel(oc);
            c = channelDao.findByXmlUrl("xml");

            assertNotNull(c);
            assertEquals(oc.getTitle(), c.getTitle());
            assertEquals(oc.getHtmlURL(), c.getHtmlUrl());
            assertEquals(-1, uc.getIndex());
            assertEquals(1, uc.getRating());
            assertEquals("a,b", uc.getReadArticlesKeys());
            assertEquals("c,d", uc.getPinnedArticlesKeys());
            assertEquals(-1, uc.getPurgeLimit());
            assertEquals("a", uc.getCustomTitle());
            assertEquals("b", uc.getCustomCreator());
            assertEquals("c", uc.getCustomDescription());
            assertEquals(-1, uc.getUserGuideId());
            assertEquals(c.getId(), uc.getChannelId());
        } finally
        {
            // cleanup
            cleanChannelByXmlUrl("xml");
        }
    }

    /**
     * @see SynchronizationService#storeDomainFeeds
     */
    public void testStoreChannels()
    {
        User u;
        Channel c1, c2;
        UserGuide ug;
        UserChannel uc1, uc2;
        UserQueryFeed uqf1, uqf2;
        UserSearchFeed usf;

        u = new User("A", "B", "C", "D", false);
        c1 = new Channel("A", "B", "C");
        c2 = new Channel("D", "E", "F");
        ug = guide1();
        uc1 = new UserChannel(-1, -1, 1, 0, null, null, -1, null, null, null, null, null, null, null, false, 1, true, 2, true);
        uc2 = new UserChannel(-1, -1, 2, 1, null, null, 2, "a", "b", "c", "d", "e", "f", null, false, 1, true, 2, false);
        uc2.setUpdatePeriod(1);
        uqf1 = new UserQueryFeed(-1, 3, "a", 1, "b", "c", "d", 1, 2, 1, true, 2, null);
        uqf1.setDedupEnabled(true);
        uqf1.setDedupFrom(5);
        uqf1.setDedupTo(6);
        uqf2 = new UserQueryFeed(-1, 4, "b", 1, "c", "d", "e", 1, 2, 1, true, 2, null);
        uqf2.setUpdatePeriod(2);
        usf = new UserSearchFeed(-1, 5, "a", "b", 1, 2, 1, true, 2, null);
        usf.setDedupEnabled(true);
        usf.setDedupFrom(2);
        usf.setDedupTo(3);

        cleanChannelByXmlUrl("C");
        cleanChannelByXmlUrl("F");

        try
        {
            // add stuff
            userDao.add(u);
            channelDao.add(c1);
            channelDao.add(c2);

            ug.setUserId(u.getId());
            userGuideDao.add(ug);

            // associate user channels with global channels
            uc1.setChannelId(c1.getId());
            uc2.setChannelId(c2.getId());

            // perform mass storing
            ss.storeDomainFeeds(ug.getId(), Arrays.asList(uc1, uc2, uqf1, uqf2, usf));

            // check
            UserChannel ucc1 = userChannelDao.findById(uc1.getId());
            assertNotNull(ucc1);
            assertEquals(ug.getId(), ucc1.getUserGuideId());
            assertEquals(-1, ucc1.getPurgeLimit());

            UserChannel ucc2 = userChannelDao.findById(uc2.getId());
            assertNotNull(ucc2);
            assertEquals(ug.getId(), ucc2.getUserGuideId());
            assertEquals("a", ucc2.getCustomTitle());
            assertEquals("b", ucc2.getCustomCreator());
            assertEquals("c", ucc2.getCustomDescription());
            assertEquals(2, ucc2.getPurgeLimit());

            List queryFeeds = userQueryFeedDao.findByUserGuideId(ug.getId());
            assertEquals(2, queryFeeds.size());
            assertEquals(uqf1, queryFeeds.get(0));
            assertEquals(uqf2, queryFeeds.get(1));

            List searchFeeds = userSearchFeedDao.findByUserGuideId(ug.getId());
            assertEquals(1, searchFeeds.size());
            assertEquals(usf, searchFeeds.get(0));
        } finally
        {
            // cleanup
            // cascade delete all the user-related stuff
            if (u.getId() != -1) userDao.delete(u);
            if (c1.getId() != -1) channelDao.delete(c1);
            if (c2.getId() != -1) channelDao.delete(c2);
        }
    }

    /**
     * Storing duplicate user channel records. The duplicates aren't allowed, so that
     * we store one of the copies.
     *
     * @see SynchronizationService#storeDomainFeeds
     */
    public void testStoreChannelsDuplicate()
    {
        User u;
        Channel c1;
        UserGuide ug;
        UserChannel uc1, uc2;

        u = new User("A", "B", "C", "D", false);
        c1 = new Channel("A", "B", "C");
        ug = guide1();
        uc1 = new UserChannel(-1, -1, 1, 0, null, null, -1, null, null, null, null, null, null, null, false, 1, true, 2, true);
        uc2 = new UserChannel(-1, -1, 2, 1, null, null, 2, "a", "b", "c", "d", "e", "f", null, false, 1, true, 2, false);

        cleanChannelByXmlUrl("C");

        try
        {
            // add stuff
            userDao.add(u);
            channelDao.add(c1);

            ug.setUserId(u.getId());
            userGuideDao.add(ug);

            // associate user channels with global channels
            uc1.setChannelId(c1.getId());
            uc2.setChannelId(c1.getId());

            // perform mass storing
            ss.storeDomainFeeds(ug.getId(), Arrays.asList(uc1, uc2));

            // check
            UserChannel ucc1 = userChannelDao.findById(uc1.getId());
            assertNotNull(ucc1);
            assertEquals(ug.getId(), ucc1.getUserGuideId());
            assertEquals(-1, ucc1.getPurgeLimit());

            List channels = userChannelDao.select(ug.getId(), null);
            assertEquals(1, channels.size());
        } finally
        {
            // cleanup
            // cascade delete all the user-related stuff
            if (u.getId() != -1) userDao.delete(u);
            if (c1.getId() != -1) channelDao.delete(c1);
        }
    }

    /**
     * Tests storing query feeds.
     */
    public void testStoreQueryFeeds()
    {
        User u;
        UserGuide ug;
        UserQueryFeed f1, f2;

        u = new User("A", "B", "C", "D", false);
        ug = guide1();
        f1 = new UserQueryFeed(-1, 1, "1", 1, "1 2", "1", "1", -1, -1, 1, true, 2, true);
        f2 = new UserQueryFeed(-1, 2, "2", 2, "2 3", "2", "2", 1, 1, 1, true, 2, null);
        f2.setDedupEnabled(true);
        f2.setDedupFrom(2);
        f2.setDedupTo(3);
        f2.setUpdatePeriod(2);

        try
        {
            // add stuff
            userDao.add(u);

            ug.setUserId(u.getId());
            userGuideDao.add(ug);

            // perform mass storing
            ss.storeQueryFeeds(ug.getId(), new UserQueryFeed[] { f2, f1 });

            // check
            List feeds = userQueryFeedDao.findByUserGuideId(ug.getId());
            assertEquals("Wrong number of feeds.", 2, feeds.size());
            assertEquals("Wrong feed.", f1, feeds.get(0));
            assertEquals("Wrong feed.", f2, feeds.get(1));
        } finally
        {
            // cleanup
            // cascade delete all the user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see SynchronizationService#storeGuide
     */
    public void testStoreGuide()
    {
        User u;
        OPMLGuide g;
        Channel cc1, cc2;
        ArrayList<DefaultOPMLFeed> items;

        u = new User("A", "_B", "C", "D", false);
        g = new OPMLGuide("A", "B", false, "A", "B", true, 2, true, false);
        items = new ArrayList<DefaultOPMLFeed>();
        items.add(new DirectOPMLFeed("titleA", "xmlA", "htmlA", 1, "a", "a", -1, null, null, null, null, null, null, false, 1, false, 1, false));
        items.add(new QueryOPMLFeed("qfA", 0, "A B", "A", "A", "A", -1, -1, 1, false, 2, true));
        items.add(new QueryOPMLFeed("qfB", 1, "B", "B", "", "B", 1, 1, 1, false, 2, null));
        items.add(new DirectOPMLFeed("titleB", "xmlB", "htmlB", 2, "b", "b", -1, null, null, null, null, null, null, false, 1, false, 2, null));
        g.setFeeds(items);

        OPMLReadingList rl = new OPMLReadingList("title", "file://test");
        items = new ArrayList<DefaultOPMLFeed>();
        items.add(new DirectOPMLFeed("titleC", "xmlC", "htmlC", 3, "c", "c", -1, null, null, null, null, null, null, false, 0, false, 1, false));
        rl.setFeeds(items);
        g.add(rl);

        cleanChannelByXmlUrl("xmlA");
        cleanChannelByXmlUrl("xmlB");

        cc1 = null;
        cc2 = null;

        try
        {
            // add stuff
            userDao.add(u);
            ss.storeGuide(u, g, 0);

            // check
            // find for global channels that should be created
            cc1 = channelDao.findByXmlUrl("xmlA");
            cc2 = channelDao.findByXmlUrl("xmlB");
            assertNotNull(cc1);
            assertNotNull(cc2);

            // look for guides of user and check
            List guides = userGuideDao.findByUserId(u.getId());
            assertNotNull(guides);
            assertEquals(1, guides.size());

            UserGuide ug = (UserGuide)guides.get(0);
            assertNotNull(ug);
            assertEquals(0, ug.getIndex());
            assertEquals("A", ug.getTitle());
            assertEquals("B", ug.getIconKey());
            assertTrue(ug.isAutoFeedsDiscovery());
            assertTrue(ug.isPublishingPublic());

            // look for reading lists
            List rls = userReadingListDao.findByUserGuideId(ug.getId());
            assertEquals(1, rls.size());
            assertEquals(rl.getTitle(), ((UserReadingList)rls.get(0)).getTitle());
            assertEquals(rl.getURL(), ((UserReadingList)rls.get(0)).getXmlUrl());
            Integer rlId = ((UserReadingList)rls.get(0)).getId();

            // look for channels in guide and check
            List chans = userChannelDao.select(ug.getId(), null);
            assertEquals(2, chans.size());

            UserChannel uc1 = (UserChannel)chans.get(0);
            assertEquals(1, uc1.getIndex());
            assertEquals(cc1.getId(), uc1.getChannelId());
            assertNull(uc1.getUserReadingListId());

            UserChannel uc2 = (UserChannel)chans.get(1);
            assertEquals(4, uc2.getIndex());
            assertEquals(cc2.getId(), uc2.getChannelId());
            assertNull(uc2.getUserReadingListId());

            chans = userChannelDao.select(ug.getId(), rlId);
            assertEquals(1, chans.size());

            UserChannel uc3 = (UserChannel)chans.get(0);
            assertEquals(0, uc3.getIndex());
            assertEquals(rlId, uc3.getUserReadingListId());

            // look for query feeds in guide
            List queryFeeds = userQueryFeedDao.findByUserGuideId(ug.getId());
            assertEquals(2, queryFeeds.size());

            UserQueryFeed uqf0 = (UserQueryFeed)queryFeeds.get(0);
            assertEquals("qfA", uqf0.getTitle());
            assertEquals(0, uqf0.getQueryType());
            assertEquals(2, uqf0.getIndex());
            assertEquals("A B", uqf0.getQueryParam());
            assertEquals("A", uqf0.getReadArticlesKeys());

            UserQueryFeed uqf1 = (UserQueryFeed)queryFeeds.get(1);
            assertEquals(1, uqf1.getQueryType());
            assertEquals(3, uqf1.getIndex());
        } finally
        {
            // cleanup
            // cascade delete all the user-related stuff
            if (u.getId() != -1) userDao.delete(u);
            if (cc1 != null && cc1.getId() != -1) channelDao.delete(cc1);
            if (cc2 != null && cc2.getId() != -1) channelDao.delete(cc2);
        }
    }

    /**
     * @see SynchronizationService#buildChannel
     */
    public void testBuildChannel()
    {
        Channel c;
        UserChannel uc;
        DirectOPMLFeed oc;

        c = new Channel("A", "B", "C");
        uc = new UserChannel(-1, -1, 1, 0, "a", "b", 1, "a", "b", "c", "d", "e", "f", null, false, 1, true, 2, true);
        uc.setUpdatePeriod(1l);

        cleanChannelByXmlUrl("C");
        channelDao.add(c);
        try
        {
            uc.setChannelId(c.getId());

            // we don't need to add UserChannel to database as we need only channelId() from it
            // and some minor stuff like rating and etc
            oc = ss.buildChannel(uc);
            assertNotNull(oc);
            assertEquals(c.getTitle(), oc.getTitle());
            assertEquals(c.getXmlUrl(), oc.getXmlURL());
            assertEquals(c.getHtmlUrl(), oc.getHtmlURL());
            assertEquals(uc.getRating(), oc.getRating());
            assertEquals(uc.getReadArticlesKeys(), oc.getReadArticlesKeys());
            assertEquals(uc.getPurgeLimit(), oc.getLimit());
            assertEquals(uc.getCustomTitle(), oc.getCustomTitle());
            assertEquals(uc.getCustomCreator(), oc.getCustomCreator());
            assertEquals(uc.getCustomDescription(), oc.getCustomDescription());
            assertEquals(new Long(uc.getUpdatePeriod()), oc.getUpdatePeriod());
        } finally
        {
            cleanChannelByXmlUrl("C");
        }
    }

    /**
     * @see SynchronizationService#buildGuide
     */
    public void testBuildGuide()
    {
        User u;
        Channel c1, c2, c3;
        UserGuide ug;
        UserChannel uc1, uc2;
        UserReadingList rl;
        UserChannel rlch;

        u = new User("A", "_B", "C", "D", false);
        c1 = new Channel("A", "B", "C");
        c2 = new Channel("D", "E", "F");
        c3 = new Channel("G", "H", "I");
        ug = guide1();
        uc1 = new UserChannel(-1, -1, 1, 0, null, null, -1, null, null, null, null, null, null, null, false, 1, true, 2, null);
        uc2 = new UserChannel(-1, -1, 2, 1, null, null, 1, null, null, null, null, null, null, null, false, 1, true, 2, true);
        rl = new UserReadingList(-1, "test", "file://test");
        rlch = new UserChannel(-1, -1, 2, 2, null, null, 1, null, null, null, null, null, null, null, false, 1, true, 2, false);

        cleanChannelByXmlUrl("C");
        cleanChannelByXmlUrl("F");

        try
        {
            // add stuff
            userDao.add(u);
            channelDao.add(c1);
            channelDao.add(c2);
            channelDao.add(c3);

            ug.setUserId(u.getId());
            userGuideDao.add(ug);

            // associate user channels with global channels
            uc1.setChannelId(c1.getId());
            uc2.setChannelId(c2.getId());
            rlch.setChannelId(c3.getId());

            // add reading list
            rl.setUserGuideId(ug.getId());
            userReadingListDao.add(rl);
            rlch.setUserReadingListId(rl.getId());

            // perform mass storing
            ss.storeDomainFeeds(ug.getId(), Arrays.asList(rlch, uc1, uc2));

            // check
            OPMLGuide guide = ss.buildGuide(ug);
            assertNotNull(guide);
            assertEquals(guide.getTitle(), ug.getTitle());
            assertEquals(guide.getIcon(), ug.getIconKey());
            assertEquals(guide.isPublishingPublic(), ug.isPublishingPublic());

            // check channels
            List channels = guide.getFeeds();
            assertEquals(2, channels.size());
            assertEquals(c1.getXmlUrl(), ((DirectOPMLFeed)channels.get(0)).getXmlURL());
            assertEquals(c2.getXmlUrl(), ((DirectOPMLFeed)channels.get(1)).getXmlURL());

            // check reading lists
            OPMLReadingList[] lists = guide.getReadingLists();
            assertEquals(1, lists.length);
            assertEquals(rl.getTitle(), lists[0].getTitle());
            assertEquals(rl.getXmlUrl(), lists[0].getURL());

            List listFeeds = lists[0].getFeeds();
            assertEquals(1, listFeeds.size());
            assertEquals(c3.getXmlUrl(), ((DirectOPMLFeed)listFeeds.get(0)).getXmlURL());
        } finally
        {
            // cleanup
            // cascade delete all the user-related stuff
            if (u.getId() != -1) userDao.delete(u);
            if (c1.getId() != -1) channelDao.delete(c1);
            if (c2.getId() != -1) channelDao.delete(c2);
            if (c3.getId() != -1) channelDao.delete(c3);
        }
    }

    /**
     * @see SynchronizationService#buildGuidesList
     */
    public void testBuildGuidesList()
    {
        User u;
        UserGuide ug1, ug2;

        u = new User("A", "_B", "C", "D", false);
        ug1 = guide1();
        ug2 = guide2();

        cleanChannelByXmlUrl("C");
        cleanChannelByXmlUrl("F");

        try
        {
            // add stuff
            userDao.add(u);

            ug1.setUserId(u.getId());
            userGuideDao.add(ug1);
            ug2.setUserId(u.getId());
            userGuideDao.add(ug2);

            // check
            OPMLGuide[] guides = ss.buildGuidesList(u);
            assertEquals(2, guides.length);
            assertEquals(ug1.getTitle(), guides[0].getTitle());
            assertEquals(ug2.getTitle(), guides[1].getTitle());
        } finally
        {
            // cleanup
            // cascade delete all the user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * Tests how preferences are stored.
     */
    public void testStorePreferences()
    {
        User u = new User("A", "_B", "C", "D", false);
        userDao.add(u);

        try
        {
            Hashtable<String, byte[]> prefs = new Hashtable<String, byte[] >();
            prefs.put("A", StringUtils.toUTF8("A"));
            prefs.put("B", StringUtils.toUTF8("\u2014"));

            // store preferences
            ss.storePrefs(u, prefs);

            // restore and check
            Hashtable restored = ss.restorePrefs(u);
            compareMaps(prefs, restored);
        } finally
        {
            // cleanup
            // cascade delete all the user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * Tests how preferences are updated, inserted and deleted.
     */
    public void testUpdateInsertDeletePreferences()
    {
        User u = new User("A", "_B", "C", "D", false);
        userDao.add(u);

        try
        {
            Hashtable<String, byte[]> prefs = new Hashtable<String, byte[] >();
            prefs.put("A", StringUtils.toUTF8("A"));
            prefs.put("B", StringUtils.toUTF8("\u2014"));
            prefs.put("D", StringUtils.toUTF8("\u2014"));

            // store preferences
            ss.storePrefs(u, prefs);

            prefs.put("B", StringUtils.toUTF8("New"));
            prefs.put("C", StringUtils.toUTF8("New"));
            prefs.remove("A");

            // update preferences
            ss.storePrefs(u, prefs);

            // restore and check
            Hashtable restored = ss.restorePrefs(u);
            compareMaps(prefs, restored);
        } finally
        {
            // cleanup
            // cascade delete all the user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * Tests combining the empty lists of channels and query feeds.
     */
    public void testCombineFeedsEmpty()
    {
        List channels = new ArrayList();
        List queryFeeds = new ArrayList();
        List searchFeeds = new ArrayList();

        List res = SynchronizationService.combineFeeds(channels, queryFeeds, searchFeeds);
        assertEquals("Nothing to combine. The list should be empty.", 0, res.size());
    }

    /**
     * Tests combining only channels or only query feeds.
     */
    public void testCombineFeedsOneSideOnly()
    {
        List<UserChannel> channels = new ArrayList<UserChannel>();
        List<UserQueryFeed> queryFeeds = new ArrayList<UserQueryFeed>();
        List<UserSearchFeed> searchFeeds = new ArrayList<UserSearchFeed>();
        List res;

        // Channels only
        channels.add(new UserChannel(1, 0, 0, 0, "0", "0", 0, "0", "0", "0", "0", "0", "0", null, false, 1, true, 2, null));
        channels.add(new UserChannel(1, 1, 1, 1, "1", "1", 1, "1", "1", "1", "1", "1", "1", null, false, 1, true, 2, null));

        res = SynchronizationService.combineFeeds(channels, queryFeeds, searchFeeds);
        assertEquals("There should be two channels in the list.", 2, res.size());
        assertEquals("The channel with lowest index should go first.",
            0, ((UserChannel)res.get(0)).getIndex());
        assertEquals("Second channel is not found.",
            1, ((UserChannel)res.get(1)).getIndex());

        // Query feeds only
        channels.clear();
        queryFeeds.add(new UserQueryFeed(0, 0, "0", 0, "0", "0", "0", -1, -1, 1, true, 2, true));
        queryFeeds.add(new UserQueryFeed(1, 1, "1", 1, "1", "1", "1", 1, 1, 1, true, 2, true));

        res = SynchronizationService.combineFeeds(channels, queryFeeds, searchFeeds);
        assertEquals("There should be two query feeds in the list.", 2, res.size());
        assertEquals("The query feed with lowest index should go first.",
            0, ((UserQueryFeed)res.get(0)).getIndex());
        assertEquals("Second query feed is not found.",
            1, ((UserQueryFeed)res.get(1)).getIndex());

        // Query feeds only
        channels.clear();
        queryFeeds.clear();
        searchFeeds.add(new UserSearchFeed(0, 0, "0", "0", -1, -1, 1, true, 2, true));
        searchFeeds.add(new UserSearchFeed(1, 1, "1", "1", 1, 1, 1, true, 2, true));

        res = SynchronizationService.combineFeeds(channels, queryFeeds, searchFeeds);
        assertEquals("There should be two search feeds in the list.", 2, res.size());
        assertEquals("The search feed with lowest index should go first.",
            0, ((UserSearchFeed)res.get(0)).getIndex());
        assertEquals("Second search feed is not found.",
            1, ((UserSearchFeed)res.get(1)).getIndex());
    }

    /**
     * Tests combining and resolving conflicts in indexes.
     */
    public void testCombineFeeds()
    {
        List<UserChannel> channels = new ArrayList<UserChannel>();
        List<UserQueryFeed> queryFeeds = new ArrayList<UserQueryFeed>();
        List<UserSearchFeed> searchFeeds = new ArrayList<UserSearchFeed>();

        // C0, Q1, C1, C2, Q3 -- conflicting index 1 (query feed goes first)
        channels.add(new UserChannel(1, 0, 0, 0, "0", "0", 0, "0", "0", "0", "0", "0", "0", null, false, 1, true, 2, null));
        channels.add(new UserChannel(1, 1, 1, 1, "1", "1", 1, "1", "1", "1", "1", "1", "1", null, false, 1, true, 2, null));
        channels.add(new UserChannel(1, 2, 2, 2, "2", "2", 2, "2", "2", "2", "2", "2", "2", null, false, 1, true, 2, null));
        queryFeeds.add(new UserQueryFeed(1, 1, "0", 0, "0", "0", "0", -1, -1, 1, true, 2, true));
        queryFeeds.add(new UserQueryFeed(1, 3, "3", 3, "3", "3", "3", 1, 1, 1, true, 2, true));
        searchFeeds.add(new UserSearchFeed(1, 4, "4", "4", 1, 1, 1, true, 2, true));

        List res = SynchronizationService.combineFeeds(channels, queryFeeds, searchFeeds);
        assertEquals("Wrong number of items.", 6, res.size());
        assertEquals(0, ((UserChannel)res.get(0)).getIndex());
        assertEquals(1, ((UserQueryFeed)res.get(1)).getIndex());
        assertEquals(1, ((UserChannel)res.get(2)).getIndex());
        assertEquals(2, ((UserChannel)res.get(3)).getIndex());
        assertEquals(3, ((UserQueryFeed)res.get(4)).getIndex());
        assertEquals(4, ((UserSearchFeed)res.get(5)).getIndex());
    }

    /**
     * Tests recording and reporting the time of last sync.
     */
    public void testRecordingLastUpdateDate()
    {
        SynchronizationService ss = SynchronizationService.getInstance();

        // Create user
        User u = new User("__Test", "e@mail", "a", "b", false);
        userDao.add(u);

        try
        {
            String date = "Tue, 24 Jan 2006 10:00:00 EET";
            String opml = "<opml version='1.1'><head><dateModified>" + date +
                "</dateModified></head><body><outline text='gd'/></body></opml>";
            ss.store(u, opml);

            String res = ss.restore(u, 0);
            Pattern pat = Pattern.compile("<dateModified>" + date + "</dateModified>");
            assertTrue(pat.matcher(res).find());
        } catch (SynchronizationException e)
        {
            e.printStackTrace();
            fail();
        } finally
        {
            userDao.delete(u);
        }
    }

    /**
     * Tests recording and reporting the time of last sync.
     */
    public void testRecordingNoLastUpdateDate()
    {
        SynchronizationService ss = SynchronizationService.getInstance();

        // Create user
        User u = new User("__Test", "e@mail", "a", "b", false);
        userDao.add(u);

        try
        {
            String opml = "<opml version='1.1'><head/><body><outline text='gd'/></body></opml>";
            ss.store(u, opml);

            String res = ss.restore(u, 0);
            Pattern pat = Pattern.compile("<head><title>[^<]+</title></head>");
            assertTrue(res, pat.matcher(res).find());
        } catch (SynchronizationException e)
        {
            e.printStackTrace();
            fail();
        } finally
        {
            userDao.delete(u);
        }
    }

    private void compareMaps(Hashtable stored, Hashtable restored)
    {
        if (stored.size() != restored.size())
        {
            fail("Maps sizes differ: stored=" + stored.size() + ", restored=" + restored.size());
        }

        for (Object o : stored.entrySet())
        {
            Map.Entry entry = (Map.Entry)o;
            if (!restored.containsKey(entry.getKey()))
            {
                fail("Key missing in restored: key=" + entry.getKey());
            }

            byte[] oldValue = (byte[])stored.get(entry.getKey());
            byte[] newValue = (byte[])restored.get(entry.getKey());

            if ((oldValue == null && newValue != null) ||
                (oldValue != null && newValue == null) ||
                (oldValue != null && !Arrays.equals(oldValue, newValue)))
            {
                fail("Values differ for the key=" + entry.getKey() + ": oldValue=" + oldValue +
                    ", newValue=" + newValue);
            }
        }
    }

    /**
     * Finds and removes channel with specified XML URL.
     *
     * @param xmlUrl    XML URL.
     */
    private void cleanChannelByXmlUrl(String xmlUrl)
    {
        Channel c = channelDao.findByXmlUrl(xmlUrl);
        if (c != null) channelDao.delete(c);
    }
}
