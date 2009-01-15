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
// $Id: TestUserPreferenceDao.java,v 1.1.1.1 2006/10/23 13:55:57 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.*;

import java.util.List;

/**
 * @see IUserPreferenceDao
 */
public class TestUserPreferenceDao extends BasicDaoTestCase
{
    /**
     * @see com.salas.bbservice.domain.UserPreference#UserPreference()
     */
    public void testCreatePreference()
    {
        UserPreference p = new UserPreference(1, "a", "b");
        assertEquals(1, p.getUserId());
        assertEquals("a", p.getName());
        assertEquals("b", p.getValue());
    }

    /**
     * @see IUserPreferenceDao#add
     */
    public void testAdd()
    {
        User u;
        UserPreference p;

        u = new User("A", "B", "P", "L", false);
        p = new UserPreference(-1, "A", "B");

        try
        {
            // add
            userDao.add(u);
            p.setUserId(u.getId());
            userPreferenceDao.add(p);

            // check
            List prefs = userPreferenceDao.selectByUserId(u.getId());
            assertNotNull(prefs);
            assertEquals(1, prefs.size());
            assertEquals(p, prefs.get(0));
        } finally
        {
            // cleanup
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see IUserPreferenceDao#add
     */
    public void testAddNonUnique()
    {
        User u;
        UserPreference p;

        u = new User("A", "B", "P", "L", false);
        p = new UserPreference(-1, "A", "B");

        try
        {
            // add
            userDao.add(u);
            p.setUserId(u.getId());
            userPreferenceDao.add(p);
            try
            {
                userPreferenceDao.add(p);
                fail("Exception expected. Duplicate record.");
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
     * @see IUserPreferenceDao#delete
     */
    public void testDelete()
    {
        User u;
        UserPreference p;

        u = new User("A", "B", "P", "L", false);
        p = new UserPreference(-1, "A", "B");

        try
        {
            // add
            userDao.add(u);
            p.setUserId(u.getId());
            userPreferenceDao.add(p);

            // delete
            userPreferenceDao.delete(p);
            assertEquals(0, userPreferenceDao.selectByUserId(u.getId()).size());
        } finally
        {
            // cleanup
            if (u.getId() != -1) userDao.delete(u);
        }
    }

    /**
     * @see IUserPreferenceDao#update
     */
    public void testUpdate()
    {
        User u;
        UserPreference p;

        u = new User("A", "B", "P", "L", false);
        p = new UserPreference(-1, "A", "B");

        try
        {
            // add
            userDao.add(u);
            p.setUserId(u.getId());
            userPreferenceDao.add(p);

            // update
            p.setValue("C");
            userPreferenceDao.update(p);

            // check
            List prefs = userPreferenceDao.selectByUserId(u.getId());
            assertNotNull(prefs);
            assertEquals(1, prefs.size());
            assertEquals(p, prefs.get(0));
        } finally
        {
            // cleanup
            if (u.getId() != -1) userDao.delete(u);
        }
    }
}
