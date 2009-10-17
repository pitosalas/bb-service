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
// $Id: TestUserQueryFeedDao.java,v 1.4 2007/04/18 13:26:44 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.User;
import com.salas.bbservice.domain.UserGuide;
import com.salas.bbservice.domain.UserQueryFeed;

import java.util.List;

/**
 * @see IUserQueryFeedDao
 */
public class TestUserQueryFeedDao extends BasicDaoTestCase
{
    /**
     * @see com.salas.bbservice.domain.UserQueryFeed#UserQueryFeed
     */
    public void testCreateChannel()
    {
        UserQueryFeed f = new UserQueryFeed(1, 2, "a", 1, "b", "c", "d", 1, 1, 1, true, 2, true, 0);
        assertEquals(-1, f.getId());
        assertEquals(1, f.getUserGuideId());
        assertEquals(2, f.getIndex());
        assertEquals("Wrong title.", "a", f.getTitle());
        assertEquals("Wrong query type.", 1, f.getQueryType());
        assertEquals("Wrong query param.", "b", f.getQueryParam());
        assertEquals("Wrong articles keys.", "c", f.getReadArticlesKeys());
        assertEquals("Wrong pinned keys.", "d", f.getPinnedArticlesKeys());
        assertEquals(1, 1, f.getLimit());
        assertEquals(1, 1, f.getRating());
        assertEquals(1, f.getViewType());
        assertTrue(f.isViewModeEnabled());
        assertEquals(2, f.getViewMode());
        assertTrue(f.getAscendingSorting());
    }

    /**
     * @see com.salas.bbservice.persistence.IUserChannelDao#add
     */
    public void testAddFailIntegrity()
    {
        UserQueryFeed f;

        f = new UserQueryFeed(-1, 2, "a", 1, "b", "c", "d", 1, -1, 1, false, 1, true, 0);

        try
        {
            userQueryFeedDao.add(f);
            fail("Exception expected. No UserGuide with id=-1.");
        } catch (Exception e)
        {
            // Expected behavior
        }
    }

    /**
     * @see com.salas.bbservice.persistence.IUserQueryFeedDao#add
     */
    public void testAdd()
    {
        User u;
        UserGuide g;
        UserQueryFeed f;

        u = new User("A", "B", "P", "L", false);
        g = guide1();
        f = new UserQueryFeed(-1, 2, "a", 1, "b", "c", "d", 1, 1, 1, true, 1, true, 0);
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
            userQueryFeedDao.add(f);
            assertFalse(-1 == f.getId());

            // check
            List feeds = userQueryFeedDao.findByUserGuideId(g.getId());
            assertEquals("Wrong number of feeds.", 1, feeds.size());

            UserQueryFeed c2 = (UserQueryFeed)feeds.get(0);
            assertEquals(f, c2);
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see com.salas.bbservice.persistence.IUserQueryFeedDao#add
     */
    public void testAddDuplicate()
    {
        User u;
        UserGuide g;
        UserQueryFeed f;

        u = new User("A", "B", "P", "L", false);
        g = guide1();
        f = new UserQueryFeed(-1, 2, "a", 1, "b", "c", "d", 1, -1, 1, true, 2, true, 0);

        try
        {
            // add parents
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            f.setUserGuideId(g.getId());

            // add
            userQueryFeedDao.add(f);
            try
            {
                userQueryFeedDao.add(f);
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
     * @see com.salas.bbservice.persistence.IChannelDao#delete
     */
    public void testDelete()
    {
        User u;
        UserGuide g;
        UserQueryFeed f;

        u = new User("A", "B", "P", "L", false);
        g = guide1();
        f = new UserQueryFeed(-1, 2, "a", 1, "b", "c", "d", 1, -1, 1, true, 2, true, 0);

        try
        {
            // add parents
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            f.setUserGuideId(g.getId());

            // add
            userQueryFeedDao.add(f);

            // delete
            userQueryFeedDao.delete(f);
            assertEquals("ID of deleted object should be reset.", -1, f.getId());
            assertEquals("The guide should become empty.", 0,
                userQueryFeedDao.findByUserGuideId(g.getId()).size());
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see com.salas.bbservice.persistence.IUserQueryFeedDao#findByUserGuideId
     */
    public void testFindByUserGuideId()
    {
        User u;
        UserGuide g;
        UserQueryFeed f1, f2;

        u = new User("A", "B", "C", "D", false);
        g = guide1();
        f1 = new UserQueryFeed(-1, 2, "a", 1, "b", "c", "d", 1, -1, 1, true, 2, true, 0);
        f2 = new UserQueryFeed(-1, 3, "b", 2, "c", "d", "e", -1, -1, 1, true, 2, false, 0);

        try
        {
            // add stuff
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            f1.setUserGuideId(g.getId());
            f2.setUserGuideId(g.getId());

            // backward order of addition is valuable - checking ordering by index
            userQueryFeedDao.add(f2);
            userQueryFeedDao.add(f1);

            // check
            List feeds = userQueryFeedDao.findByUserGuideId(g.getId());
            assertNotNull(feeds);
            assertEquals(2, feeds.size());

            UserQueryFeed lf1 = (UserQueryFeed)feeds.get(0);
            assertEquals("Loaded feed and stored differ.", f1, lf1);
            UserQueryFeed lf2 = (UserQueryFeed)feeds.get(1);
            assertEquals("Loaded feed and stored differ.", f2, lf2);
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }
}
