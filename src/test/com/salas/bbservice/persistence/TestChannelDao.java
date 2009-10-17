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
// $Id: TestChannelDao.java,v 1.2 2007/04/18 12:21:26 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Channel;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.domain.UserChannel;
import com.salas.bbservice.domain.UserGuide;

/**
 * @see IChannelDao
 */
public class TestChannelDao extends BasicDaoTestCase
{

    /**
     * @see com.salas.bbservice.domain.Channel#Channel
     */
    public void testCreateChannel()
    {
        Channel c = new Channel("A", "B", "C");
        assertEquals(-1, c.getId());
        assertEquals("A", c.getTitle());
        assertEquals("B", c.getHtmlUrl());
        assertEquals("C", c.getXmlUrl());
    }

    /**
     * @see IChannelDao#add
     */
    public void testAdd()
    {
        Channel c = new Channel("A", "B", "C");

        channelDao.add(c);
        assertFalse(-1 == c.getId());

        try
        {
            channelDao.add(c);
            fail("Exception should be thrown.");
        } catch (Exception e)
        {
            // Normal behavior
        } finally
        {
            channelDao.delete(c);
        }
    }

    /**
     * @see IChannelDao#delete
     */
    public void testUpdate()
    {
        Channel c = new Channel("A", "B", "C");
        channelDao.add(c);

        try
        {
            c.setTitle("X");
            c.setHtmlUrl("Y");
            c.setXmlUrl("Z");
            channelDao.update(c);
            assertEquals(c, channelDao.findById(c.getId()));
        } finally
        {
            // cleanup
            channelDao.delete(c);
        }
    }

    /**
     * @see IChannelDao#delete
     */
    public void testDelete()
    {
        Channel c = new Channel("A", "B", "C");
        channelDao.add(c);
        int id = c.getId();
        assertTrue(-1 != id);

        channelDao.delete(c);
        assertEquals(-1, c.getId());
        assertNull(channelDao.findById(id));
    }

    /**
     * @see IChannelDao#delete
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
            channelDao.delete(ch);

            // check
            assertNotNull(userDao.findById(u.getId()));
            assertNotNull(userGuideDao.findById(g.getId()));
            assertNull(userChannelDao.findById(c.getId()));
            assertNull(channelDao.findById(ch.getId()));
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
     * @see IChannelDao#findById
     */
    public void testFindById()
    {
        // create channel
        Channel c = new Channel("A", "B", "C");

        // add to database
        channelDao.add(c);

        try
        {
            // find in database by id
            Channel c2 = channelDao.findById(c.getId());
            assertNotNull(c2);
            assertEquals(c, c2);
        } finally
        {
            channelDao.delete(c);
        }
    }

    /**
     * @see IChannelDao#findByXmlUrl
     */
    public void testFindByXmlUrl()
    {
        // create channel
        Channel c = new Channel("A", "B", "C");

        // add to database
        channelDao.add(c);

        try
        {
            // find in database by xmlUrl
            Channel c2 = channelDao.findByXmlUrl(c.getXmlUrl());
            assertNotNull(c2);
            assertEquals(c, c2);
        } finally
        {
            channelDao.delete(c);
        }
    }
}
