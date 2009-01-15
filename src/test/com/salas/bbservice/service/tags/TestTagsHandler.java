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
// mailto:pitosalas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: TestTagsHandler.java,v 1.1 2007/09/06 09:03:37 alg Exp $
//

package com.salas.bbservice.service.tags;

import com.salas.bbservice.domain.User;
import com.salas.bbservice.persistence.BasicDaoTestCase;

import java.util.Arrays;
import java.util.Vector;

/**
 * This suite contains tests for <code>TagsHandler</code> unit.
 */
public class TestTagsHandler extends BasicDaoTestCase
{
    private TagsHandler handler;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        handler = new CustomTagsHandler();
    }

    /**
     * Tests tagging for existing user.
     */
    public void testTagging()
    {
        User u = new User("A", "B", "C", "D", false);

        try
        {
            userDao.add(u);
            String response = handler.tag(u.getId(), "A".getBytes(), true,
                "a b".getBytes(), "test".getBytes());
            assertEquals("", response);

            // check
            Vector tags = handler.getTags(-1, "A".getBytes());
            assertEquals(1, tags.size());
            assertTrue(Arrays.equals("a b".getBytes(), (byte[])tags.get(0)));
        } finally
        {
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * Testsing tagging with invalid session ID.
     */
    public void testTaggingBadSession()
    {
        // Remeber that we have handler which is returning userId equal to session id
        // In our case we mimic the situation when session is missing
        String response = handler.tag(-1, "A".getBytes(), true,
            "a b".getBytes(), "test".getBytes());
        assertEquals(TagsHandler.ERR_NO_SESSION, response);
    }

    /**
     * Tests untagging the URL.
     */
    public void testUntagging()
    {
        User u = new User("A", "B", "C", "D", false);

        try
        {
            userDao.add(u);
            String response = handler.tag(u.getId(), "A".getBytes(), true,
                "a b".getBytes(), "test".getBytes());
            assertEquals("", response);

            // untagging
            response = handler.tag(u.getId(), "A".getBytes(), true,
                "".getBytes(), "".getBytes());
            assertEquals("", response);


            // check
            Vector tags = handler.getTags(-1, "A".getBytes());
            assertEquals(0, tags.size());
        } finally
        {
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * Tests reading tags back.
     */
    public void testFetchingTags()
    {
        User u = new User("A", "B", "C", "D", false);

        try
        {
            userDao.add(u);
            String response = handler.tag(u.getId(), "A".getBytes(), true,
                "a b".getBytes(), "test".getBytes());
            assertEquals("", response);

            // check
            Vector tags = handler.getTags(-1, "A".getBytes());
            assertEquals(1, tags.size());

            tags = handler.getTags(u.getId(), "A".getBytes());
            assertEquals(0, tags.size());
        } finally
        {
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * Tags handler which is always returning <code>sessionID</code> as user ID.
     */
    private static class CustomTagsHandler extends TagsHandler
    {
        /**
         * Returns ID of user owning the session or <code>-1</code> if session not exists.
         *
         * @param sessionId session ID.
         *
         * @return user ID or <code>-1</code> if session is missing.
         */
        int getUserID(int sessionId)
        {
            return sessionId;
        }
    }
}
