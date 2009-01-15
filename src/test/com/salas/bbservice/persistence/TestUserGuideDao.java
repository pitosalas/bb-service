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
// $Id: TestUserGuideDao.java,v 1.2 2007/04/18 12:21:26 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Channel;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.domain.UserChannel;
import com.salas.bbservice.domain.UserGuide;

import java.util.List;

/**
 * @see IUserGuideDao
 */
public class TestUserGuideDao extends BasicDaoTestCase
{
    /**
     * @see UserGuide#UserGuide
     */
    public void testCreateGuide()
    {
        UserGuide g = guide3();
        assertEquals(1, g.getUserId());
        assertEquals("A", g.getTitle());
        assertEquals("B", g.getIconKey());
        assertEquals(2, g.getIndex());

        assertTrue(g.isPublishingEnabled());
        assertTrue(g.isPublishingPublic());
        assertEquals("A", g.getPublishingTitle());
        assertEquals("B", g.getPublishingTags());

        assertTrue(g.isNotificationsAllowed());

        assertTrue(g.isAutoFeedsDiscovery());
    }

    /**
     * @see IUserGuideDao#add
     */
    public void testAdd()
    {
        User u;
        UserGuide g, g2;

        u = new User("A", "B", "P", "L", false);
        g = guide1();

        try
        {
            // add
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            assertFalse(-1 == g.getId());

            // check
            g2 = userGuideDao.findById(g.getId());
            assertNotNull(g2);
            assertEquals(g, g2);
        } finally
        {
            // cleanup
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see IUserGuideDao#add
     */
    public void testAddNonUnique()
    {
        User u;
        UserGuide g;

        u = new User("A", "B", "P", "L", false);
        g = guide1();

        try
        {
            // add
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            try
            {
                userGuideDao.add(g);
                fail("Exception expected. Duplicate index.");
            } catch (Exception e)
            {
                // Expected behavior
            }
        } finally
        {
            // cleanup
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see IUserGuideDao#delete
     */
    public void testDelete()
    {
        User u;
        UserGuide g;

        u = new User("A", "B", "P", "L", false);
        g = guide1();

        try
        {
            // add
            userDao.add(u);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            int gId = g.getId();

            // delete
            userGuideDao.delete(g);
            assertEquals(-1, g.getId());
            assertNull(userGuideDao.findById(gId));
        } finally
        {
            // cleanup
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see IUserGuideDao#delete
     */
    public void testDeleteCascade()
    {
        User u;
        Channel ch;
        UserGuide g;
        UserChannel c;

        u = new User("A", "B", "P", "L", false);
        ch = new Channel("A", "H", "X");
        g = guide1();
        c = new UserChannel(-1, -1, 1, 2, "a", "a", -1, null, null, null, null, null, null, null, false, 1, false, -1, null);

        try
        {
            // add parents
            userDao.add(u);
            channelDao.add(ch);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            c.setUserGuideId(g.getId());
            c.setChannelId(ch.getId());
            userChannelDao.add(c);

            // delete
            userGuideDao.delete(g);

            // check
            assertNotNull(userDao.findById(u.getId()));
            assertNull(userGuideDao.findById(g.getId()));
            assertNull(userChannelDao.findById(c.getId()));
            assertNotNull(channelDao.findById(ch.getId()));
        } finally
        {
            // cleanup
            // cascading delete of all user-related stuff
            if (u.getId() != -1) userDao.delete(u);
            if (ch.getId() != -1) channelDao.delete(ch);
        }
    }

    /**
     * @see IUserGuideDao#findByUserId
     */
    public void testFindByUserId()
    {
        User u;
        UserGuide g1, g2;

        u = new User("A", "B", "P", "L", false);
        g1 = guide1();
        g2 = guide2();

        try
        {
            // add stuff
            userDao.add(u);
            g1.setUserId(u.getId());
            g2.setUserId(u.getId());

            // backward order of addition is intentional - checking ordering by index
            userGuideDao.add(g2);
            userGuideDao.add(g1);

            // check
            List guides = userGuideDao.findByUserId(u.getId());
            assertNotNull(guides);
            assertEquals(2, guides.size());

            UserGuide gc1 = (UserGuide)guides.get(0);
            assertEquals(g1.getTitle(), gc1.getTitle());
            assertEquals(g1.getIndex(), gc1.getIndex());
            assertEquals(g1.isPublishingPublic(), gc1.isPublishingPublic());
            assertEquals(g1.isNotificationsAllowed(), gc1.isNotificationsAllowed());
            UserGuide gc2 = (UserGuide)guides.get(1);
            assertEquals(g2.getTitle(), gc2.getTitle());
            assertEquals(g2.getIndex(), gc2.getIndex());
            assertEquals(g2.isPublishingPublic(), gc2.isPublishingPublic());
            assertEquals(g2.isNotificationsAllowed(), gc2.isNotificationsAllowed());

            // check non-existing user
            guides = userGuideDao.findByUserId(-1);
            assertNotNull(guides);
            assertEquals(0, guides.size());
        } finally
        {
            // cleanup
            // cascading delete of all user-related stuff
            if (u.getId() != -1) userDao.delete(u);
        }
    }
}
