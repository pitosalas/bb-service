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
// $Id: TestUserSearchFeedDao.java,v 1.4 2007/04/18 13:26:45 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.User;
import com.salas.bbservice.domain.UserGuide;
import com.salas.bbservice.domain.UserSearchFeed;

import java.util.List;

/**
 * @see com.salas.bbservice.persistence.IUserSearchFeedDao
 */
public class TestUserSearchFeedDao extends BasicDaoTestCase
{
    /**
     * @see com.salas.bbservice.domain.UserSearchFeed#UserSearchFeed
     */
    public void testCreateChannel()
    {
        UserSearchFeed f = new UserSearchFeed(1, 2, "a", "b", 1, 1, 1, true, 2, true);
        assertEquals(-1, f.getId());
        assertEquals(1, f.getUserGuideId());
        assertEquals(2, f.getIndex());
        assertEquals("Wrong title.", "a", f.getTitle());
        assertEquals("Wrong query.", "b", f.getQuery());
        assertEquals(1, 1, f.getLimit());
        assertEquals(1, 1, f.getRating());
        assertEquals(1, f.getViewType());
        assertTrue(f.isViewModeEnabled());
        assertEquals(2, f.getViewMode());
        assertTrue(f.getAscendingSorting());
    }

    /**
     * @see IUserChannelDao#add
     */
    public void testAddFailIntegrity()
    {
        UserSearchFeed f;

        f = new UserSearchFeed(-1, 2, "a", "b", 1, -1, 1, true, 2, true);

        try
        {
            userSearchFeedDao.add(f);
            fail("Exception expected. No UserGuide with id=-1.");
        } catch (Exception e)
        {
            // Expected behavior
        }
    }

    /**
     * @see IUserSearchFeedDao#add
     */
    public void testAdd()
    {
        User u;
        UserGuide g;
        UserSearchFeed f;

        u = new User("A", "B", "P", "L", false);
        g = guide1();
        f = new UserSearchFeed(-1, 2, "a", "b", 1, 1, 1, true, 2, true);
        f.setDedupEnabled(true);
        f.setDedupFrom(2);
        f.setDedupTo(3);

        try
        {
            // add parents
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            f.setUserGuideId(g.getId());

            // add
            userSearchFeedDao.add(f);
            assertFalse(-1 == f.getId());

            // check
            List feeds = userSearchFeedDao.findByUserGuideId(g.getId());
            assertEquals("Wrong number of feeds.", 1, feeds.size());

            UserSearchFeed c2 = (UserSearchFeed)feeds.get(0);
            assertEquals(f, c2);
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see IUserSearchFeedDao#add
     */
    public void testAddDuplicate()
    {
        User u;
        UserGuide g;
        UserSearchFeed f;

        u = new User("A", "B", "P", "L", false);
        g = guide1();
        f = new UserSearchFeed(-1, 2, "a", "b", 1, -1, 1, true, 2, true);

        try
        {
            // add parents
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            f.setUserGuideId(g.getId());

            // add
            userSearchFeedDao.add(f);
            try
            {
                userSearchFeedDao.add(f);
                fail("Exception expected. Duplicate channel reference added.");
            } catch (Exception e)
            {
                // Expected behavior
            }
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see IChannelDao#delete
     */
    public void testDelete()
    {
        User u;
        UserGuide g;
        UserSearchFeed f;

        u = new User("A", "B", "P", "L", false);
        g = guide1();
        f = new UserSearchFeed(-1, 2, "a", "b", 1, -1, 1, true, 2, true);

        try
        {
            // add parents
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            f.setUserGuideId(g.getId());

            // add
            userSearchFeedDao.add(f);

            // delete
            userSearchFeedDao.delete(f);
            assertEquals("ID of deleted object should be reset.", -1, f.getId());
            assertEquals("The guide should become empty.", 0,
                userSearchFeedDao.findByUserGuideId(g.getId()).size());
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see IUserSearchFeedDao#findByUserGuideId
     */
    public void testFindByUserGuideId()
    {
        User u;
        UserGuide g;
        UserSearchFeed f1, f2;

        u = new User("A", "B", "C", "D", false);
        g = guide1();
        f1 = new UserSearchFeed(-1, 2, "a", "b",  1, -1, 1, true, 2, true);
        f2 = new UserSearchFeed(-1, 3, "b", "c", -1, -1, 1, true, 2, false);

        try
        {
            // add stuff
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            f1.setUserGuideId(g.getId());
            f2.setUserGuideId(g.getId());

            // backward order of addition is valuable - checking ordering by index
            userSearchFeedDao.add(f2);
            userSearchFeedDao.add(f1);

            // check
            List feeds = userSearchFeedDao.findByUserGuideId(g.getId());
            assertNotNull(feeds);
            assertEquals(2, feeds.size());

            UserSearchFeed lf1 = (UserSearchFeed)feeds.get(0);
            assertEquals("Loaded feed and stored differ.", f1, lf1);
            UserSearchFeed lf2 = (UserSearchFeed)feeds.get(1);
            assertEquals("Loaded feed and stored differ.", f2, lf2);
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }
}
