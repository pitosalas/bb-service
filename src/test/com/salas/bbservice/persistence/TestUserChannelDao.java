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
// $Id: TestUserChannelDao.java,v 1.2 2007/04/18 12:21:26 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Channel;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.domain.UserChannel;
import com.salas.bbservice.domain.UserGuide;

import java.util.List;

/**
 * @see IUserChannelDao
 */
public class TestUserChannelDao extends BasicDaoTestCase
{
    /**
     * @see UserChannel#UserChannel
     */
    public void testCreateChannel()
    {
        UserChannel c = createFullTestUserChannel();
        assertEquals(-1, c.getId());
        assertEquals(1, c.getUserGuideId());
        assertEquals(2, c.getChannelId());
        assertEquals(3, c.getRating());
        assertEquals(4, c.getIndex());
        assertEquals(5, c.getPurgeLimit());
        assertEquals("a", c.getReadArticlesKeys());
        assertEquals("a", c.getPinnedArticlesKeys());
        assertEquals("b", c.getCustomTitle());
        assertEquals("c", c.getCustomCreator());
        assertEquals("d", c.getCustomDescription());
        assertTrue(c.isDisabled());
        assertTrue(c.getAscendingSorting());
    }

    /**
     * @see IUserChannelDao#add
     */
    public void testAddFailIntegrity()
    {
        UserChannel c;

        c = createFullTestUserChannel();
        c.setChannelId(-1);
        c.setUserGuideId(-1);

        try
        {
            userChannelDao.add(c);
            fail("Expcetion expected. No UserGuide and Channel with id=-1.");
        } catch (Exception e)
        {
            // Expected behavior
        }
    }

    /**
     * @see IUserChannelDao#add
     */
    public void testAdd()
    {
        User u;
        Channel ch;
        UserGuide g;
        UserChannel c;

        u = new User("A", "B", "P", "L", false);
        ch = new Channel("A", "H", "X");
        g = guide1();
        c = createFullTestUserChannel();

        try
        {
            // add parents
            userDao.add(u);
            channelDao.add(ch);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            c.setUserGuideId(g.getId());
            c.setChannelId(ch.getId());

            // add
            userChannelDao.add(c);
            assertFalse(-1 == c.getId());

            // check
            UserChannel c2 = userChannelDao.findById(c.getId());
            assertNotNull(c2);
            assertEquals(c, c2);
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
            if (ch.getId() != -1) channelDao.delete(ch);
        }
    }

    /**
     * @see IUserChannelDao#add
     */
    public void testAddDuplicate()
    {
        User u;
        Channel ch;
        UserGuide g;
        UserChannel c;

        u = new User("A", "B", "P", "L", false);
        ch = new Channel("A", "H", "X");
        g = guide1();
        c = createFullTestUserChannel();

        try
        {
            // add parents
            userDao.add(u);
            channelDao.add(ch);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            c.setUserGuideId(g.getId());
            c.setChannelId(ch.getId());

            // add
            userChannelDao.add(c);
            try
            {
                userChannelDao.add(c);
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
            if (ch.getId() != -1) channelDao.delete(ch);
        }
    }

    /**
     * @see IChannelDao#delete
     */
    public void testDelete()
    {
        User u;
        Channel ch;
        UserGuide g;
        UserChannel c;

        u = new User("A", "B", "P", "L", false);
        ch = new Channel("A", "H", "X");
        g = guide1();
        c = createFullTestUserChannel();

        try
        {
            // add parents
            userDao.add(u);
            channelDao.add(ch);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            c.setUserGuideId(g.getId());
            c.setChannelId(ch.getId());

            // add
            userChannelDao.add(c);
            int cId = c.getId();

            // delete
            userChannelDao.delete(c);
            assertEquals(-1, c.getId());
            assertNull(userChannelDao.findById(cId));
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
            if (ch.getId() != -1) channelDao.delete(ch);
        }
    }

    /**
     * @see IUserChannelDao#select
     */
    public void testFindByUserGuideId()
    {
        User u;
        Channel c1, c2;
        UserGuide g;
        UserChannel uc1, uc2;

        u = new User("A", "_B", "C", "D", false);
        c1 = new Channel("A", "B", "C");
        c2 = new Channel("D", "E", "F");
        g = guide1();
        uc1 = createFullTestUserChannel();
        uc2 = createEmptyTestUserChannel();

        try
        {
            // add stuff
            userDao.add(u);
            channelDao.add(c1);
            channelDao.add(c2);
            g.setUserId(u.getId());
            userGuideDao.add(g);
            uc1.setChannelId(c1.getId());
            uc1.setUserGuideId(g.getId());
            uc2.setChannelId(c2.getId());
            uc2.setUserGuideId(g.getId());

            // backward order of addition is valuable - checking ordering by index
            userChannelDao.add(uc2);
            userChannelDao.add(uc1);

            // check
            List channels = userChannelDao.select(g.getId(), null);
            assertNotNull(channels);
            assertEquals(2, channels.size());

            UserChannel ucc1 = (UserChannel)channels.get(0);
            assertNotNull(ucc1);
            assertEquals(c1.getId(), ucc1.getChannelId());
            UserChannel ucc2 = (UserChannel)channels.get(1);
            assertNotNull(ucc2);
            assertEquals(c2.getId(), ucc2.getChannelId());
            assertEquals(uc2, ucc2);
        } finally
        {
            // cleanup
            // cascading delete of user-related stuff
            if (u.getId() != -1) userDao.delete(u);
            if (c1.getId() != -1) channelDao.delete(c1);
            if (c2.getId() != -1) channelDao.delete(c2);
        }
    }

    private static UserChannel createFullTestUserChannel()
    {
        return new UserChannel(1, 2, 3, 4, "a", "a", 5, "b", "c", "d", "e", "f", "g", null, true, 1, true, 2, true);
    }

    private static UserChannel createEmptyTestUserChannel()
    {
        return new UserChannel(-1, -1, 1, 5, null, null, -1, null, null, null, null, null, null, null, false, -1, false, -1, null);
    }
}
