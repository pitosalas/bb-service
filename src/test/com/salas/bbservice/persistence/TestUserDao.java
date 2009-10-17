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
// $Id: TestUserDao.java,v 1.2 2007/04/18 12:21:26 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Channel;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.domain.UserChannel;
import com.salas.bbservice.domain.UserGuide;

import java.util.Date;
import java.util.List;

/**
 * @see IUserDao
 */
public class TestUserDao extends BasicDaoTestCase
{
    /**
     * @see User#User(String, String, String, String, boolean)
     */
    public void testCreate()
    {
        User u = new User("A", "B", "C", "D", true);
        assertEquals(-1, u.getId());
        assertEquals("A", u.getFullName());
        assertEquals("B", u.getEmail());
        assertEquals("C", u.getPassword());
        assertEquals("D", u.getLocale());
        assertTrue(u.isNotifyOnUpdates());
        assertFalse(u.isActivated());
        assertNull(u.getLastSyncTime());

        u.setLastSyncTime(new Date());
        assertNotNull(u.getLastSyncTime());
    }

    /**
     * @see IUserDao#add
     */
    public void testAdd()
    {
        User u = new User("A", "B", "C", "D", true);

        userDao.add(u);
        assertFalse(-1 == u.getId());

        try
        {
            userDao.add(u);
            fail("Exception should be thrown.");
        } catch (Exception e)
        {
            // Normal behavior
        } finally
        {
            userDao.delete(u);
        }
    }

    /**
     * @see IUserDao#delete
     */
    public void testDelete()
    {
        User u = new User("A", "B", "C", "D", true);
        userDao.add(u);
        int id = u.getId();
        assertTrue(-1 != id);

        userDao.delete(u);
        assertEquals(-1, u.getId());
        assertNull(userDao.findById(id));
    }

    /**
     * @see IUserDao#delete
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
        c = new UserChannel(-1, -1, 1, 2, "a", "a", -1, null, null, null, null, null, null, null, false, 1, false, 1, null, 0);

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
            userDao.delete(u);

            // check
            assertNull(userChannelDao.findById(c.getId()));
            assertNull(userGuideDao.findById(g.getId()));
            assertNull(userDao.findById(u.getId()));
            assertNotNull(channelDao.findById(ch.getId()));
        } finally
        {
            // cleanup
            if (c.getId() != -1) userChannelDao.delete(c);
            if (g.getId() != -1) userGuideDao.delete(g);
            if (ch.getId() != -1) channelDao.delete(ch);
            if (u.getId() != -1) userDao.delete(u);
        }

    }

    /**
     * @see IUserDao#deleteAllGuides
     */
    public void testDeleteAllGuides()
    {
        User u;
        Channel ch;
        UserGuide g1, g2;
        UserChannel c;

        u = new User("A", "B", "P", "L", false);
        ch = new Channel("A", "H", "X");
        g1 = guide1();
        g2 = guide2();
        c = new UserChannel(-1, -1, 1, 2, "a", "a", -1, null, null, null, null, null, null, null, false, 1, false, 1, null, 0);

        try
        {
            // add parents
            userDao.add(u);
            channelDao.add(ch);
            g1.setUserId(u.getId());
            g2.setUserId(u.getId());
            userGuideDao.add(g1);
            userGuideDao.add(g2);
            c.setUserGuideId(g1.getId());
            c.setChannelId(ch.getId());
            userChannelDao.add(c);

            // delete
            userDao.deleteAllGuides(u);

            // check
            assertNull(userChannelDao.findById(c.getId()));
            assertNull(userGuideDao.findById(g1.getId()));
            assertNull(userGuideDao.findById(g2.getId()));
            assertNotNull(userDao.findById(u.getId()));
            assertNotNull(channelDao.findById(ch.getId()));

            // add everything again
            g1.setUserId(u.getId());
            g2.setUserId(u.getId());
            userGuideDao.add(g1);
            userGuideDao.add(g2);
            c.setUserGuideId(g1.getId());
            c.setChannelId(ch.getId());
            userChannelDao.add(c);
        } finally
        {
            // cleanup
            if (c.getId() != -1) userChannelDao.delete(c);
            if (g1.getId() != -1) userGuideDao.delete(g1);
            if (g2.getId() != -1) userGuideDao.delete(g2);
            if (ch.getId() != -1) channelDao.delete(ch);
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see IUserDao#findById
     */
    public void testFindById()
    {
        // create user
        User u1 = new User("A", "B", "C", "D", true);

        // add user to database
        userDao.add(u1);

        try
        {
            // find user in database by id
            User u2 = userDao.findById(u1.getId());
            assertNotNull(u2);
            assertEquals(u1, u2);
        } finally
        {
            userDao.delete(u1);
        }
    }

    /**
     * @see IUserDao#findByEmail
     */
    public void testFindByEmail()
    {
        // create user
        User u1 = new User("A", "B", "C", "D", true);

        // add user to database
        userDao.add(u1);

        try
        {
            // find user
            User u2 = userDao.findByEmail("B");
            assertNotNull(u2);
            assertEquals(u1, u2);

            // find non-existing user
            assertNull(userDao.findByEmail("C"));
        } finally
        {
            userDao.delete(u1);
        }
    }

    /**
     * @see IUserDao#update
     */
    public void testUpdate()
    {
        // create user
        User u1 = new User("A", "B", "C", "D", true);

        // add user to database
        userDao.add(u1);

        // find by existing and not existing email
        User u2 = userDao.findByEmail("B");
        try
        {
            // update data
            u1.setFullName("Z");
            u1.setEmail("Y");
            u1.setPassword("X");
            u1.setActivated(true);
            u1.setNotifyOnUpdates(false);
            u1.setLocale("E");
            u1.setLastSyncTime(new Date());

            userDao.update(u1);
            u2 = userDao.findByEmail("Y");
            assertEquals(u1, u2);
            assertNull(userDao.findByEmail("B"));
        } finally
        {
            userDao.delete(u1);
        }
    }

    /**
     * @see IUserDao#update
     */
    public void testUpdateToDuplicate()
    {
        // create users
        User u1 = new User("A", "B", "C", "D", true);
        User u2 = new User("A", "X", "C", "D", true);

        // add user to database
        userDao.add(u1);
        userDao.add(u2);

        try
        {
            // change email to duplicate
            u2.setEmail("B");
            try
            {
                userDao.update(u2);
                fail("Exception should be thrown.");
            } catch (Exception e)
            {
                // Expected behavior
            }
        } finally
        {
            userDao.delete(u1);
            userDao.delete(u2);
        }
    }

    /**
     * Tests returning the list of most recent users registrations.
     */
    public void testGetRecentUsers()
        throws InterruptedException
    {
        // create users
        User u1 = new User("A", "BB", "C", "D", true);
        Thread.sleep(1000);
        User u2 = new User("A", "ZZ", "Z", "Z", true);

        // add user to database
        userDao.add(u1);
        userDao.add(u2);

        try
        {
            List recentUsers = userDao.getRecentUsers(2);
            assertEquals("We have enough registered users in our database.",
                2, recentUsers.size());
            assertEquals(u2, recentUsers.get(0));
            assertEquals(u1, recentUsers.get(1));

            recentUsers = userDao.getRecentUsers(1);
            assertEquals("Exactly one user should be returned.",
                1, recentUsers.size());
            assertEquals(u2, recentUsers.get(0));
        } finally
        {
            userDao.delete(u1);
            userDao.delete(u2);
        }
    }

    /**
     * Tests returning of all recent users from today.
     */
    public void testGetRecentUsersToday()
    {
        User u1 = new User("A", "_A", "C", "D", false);
        User u2 = new User("A", "_B", "C", "D", false);
        User u3 = new User("A", "_C", "C", "D", false);

        userDao.add(u1);
        userDao.add(u2);
        userDao.add(u3);

        try
        {
            List recentUsers = userDao.getRecentUsers(0);
            assertEquals(3, recentUsers.size());
        } finally
        {
            userDao.delete(u1);
            userDao.delete(u2);
            userDao.delete(u3);
        }
    }

    /**
     * Tests getting users.
     */
    public void testGetUsers()
    {
        User u1 = new User("A", "_A", "C", "D", false);
        User u2 = new User("A", "_B", "C", "D", false);
        User u3 = new User("A", "_C", "C", "D", false);

        userDao.add(u1);
        userDao.add(u2);
        userDao.add(u3);

        try
        {
            List users = userDao.getUsers(0, 3, null);
            assertEquals(3, users.size());
        } finally
        {
            userDao.delete(u1);
            userDao.delete(u2);
            userDao.delete(u3);
        }
    }

    /**
     * Tests getting users according to some pattern.
     */
    public void testGetUsersPattern()
    {
        User u1 = new User("A", "_ABBBC", "C", "D", false);
        User u2 = new User("BBB", "_CCD", "C", "D", false);
        User u3 = new User("C", "_CDE", "C", "D", false);

        userDao.add(u1);
        userDao.add(u2);
        userDao.add(u3);

        try
        {
            List users = userDao.getUsers(0, 5, "bbb");
            assertTrue("U1 has 'b' in his email.", users.contains(u1));
            assertTrue("U1 has 'b' in his name.", users.contains(u2));
            assertFalse(users.contains(u3));
        } finally
        {
            userDao.delete(u1);
            userDao.delete(u2);
            userDao.delete(u3);
        }
    }
}
