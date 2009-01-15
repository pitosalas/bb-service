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
// $Id: TestUserSharedTagsDao.java,v 1.1.1.1 2006/10/23 13:55:57 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.*;

import java.util.List;

/**
 * @see com.salas.bbservice.persistence.IUserSharedTagsDao
 */
public class TestUserSharedTagsDao extends BasicDaoTestCase
{
    /**
     * Tests tagging of some URL.
     */
    public void testTag()
    {
        User u;
        UserSharedTags tags;

        u = new User("A", "B", "P", "L", false);
        tags = new UserSharedTags(-1, "file://_test", true, "a b c", "test", "test1");

        try
        {
            // add
            userDao.add(u);
            tags.setUserId(u.getId());
            userSharedTagsDao.tagLink(tags);

            // check
            List tagsList = userSharedTagsDao.getLinkTags(tags.getUrl(), -1);
            assertEquals(1, tagsList.size());
            assertEquals(tags.getTags(), (String)tagsList.get(0));
        } finally
        {
            // cleanup
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * Tests updating tags of some URL.
     */
    public void testUpdateTags()
    {
        User u;
        UserSharedTags tags;

        u = new User("A", "B", "P", "L", false);
        tags = new UserSharedTags(-1, "file://_test", true, "a b c", "test", "test1");

        try
        {
            // add
            userDao.add(u);
            tags.setUserId(u.getId());
            userSharedTagsDao.tagLink(tags);

            tags.setTags("new tags");
            userSharedTagsDao.tagLink(tags);

            // check
            List tagsList = userSharedTagsDao.getLinkTags(tags.getUrl(), -1);
            assertEquals(1, tagsList.size());
            assertEquals(tags.getTags(), (String)tagsList.get(0));
        } finally
        {
            // cleanup
            if (u.getId() != -1) userDao.delete(u);
        }
    }
    /**
     * Tests untagging some URL.
     */
    public void testUntag()
    {
        User u;
        UserSharedTags tags;

        u = new User("A", "B", "P", "L", false);
        tags = new UserSharedTags(-1, "file://_test", true, "a b c", "test", "test1");

        try
        {
            // add
            userDao.add(u);
            tags.setUserId(u.getId());
            userSharedTagsDao.tagLink(tags);

            // untag
            tags.setTags("");
            tags.setDescription("");
            userSharedTagsDao.tagLink(tags);

            // check
            List tagsList = userSharedTagsDao.getLinkTags(tags.getUrl(), -1);
            assertEquals(0, tagsList.size());
        } finally
        {
            // cleanup
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * Tests fetching shared tags except for owner user.
     */
    public void testFetchinSharedTags()
    {
        User u1, u2;
        UserSharedTags tags;

        String firstUserTags = "user 1 tags";
        String secondUserTags = "user 2 tags";

        u1 = new User("A", "B", "P", "L", false);
        u2 = new User("B", "C", "Q", "M", false);
        tags = new UserSharedTags(-1, "file://_test", true, "a b c", "test", "test1");

        try
        {
            // add
            userDao.add(u1);
            userDao.add(u2);

            tags.setTags(firstUserTags);
            tags.setUserId(u1.getId());
            userSharedTagsDao.tagLink(tags);

            tags.setTags(secondUserTags);
            tags.setUserId(u2.getId());
            userSharedTagsDao.tagLink(tags);

            // check taking both tags lists
            List tagsList = userSharedTagsDao.getLinkTags(tags.getUrl(), -1);
            assertEquals(2, tagsList.size());
            assertTrue(tagsList.contains(firstUserTags));
            assertTrue(tagsList.contains(secondUserTags));

            // check taking second tags list only
            tagsList = userSharedTagsDao.getLinkTags(tags.getUrl(), u1.getId());
            assertEquals(1, tagsList.size());
            assertTrue(tagsList.contains(secondUserTags));
        } finally
        {
            // cleanup
            if (u1.getId() != -1) userDao.delete(u1);
            if (u2.getId() != -1) userDao.delete(u2);
        }
    }
}
