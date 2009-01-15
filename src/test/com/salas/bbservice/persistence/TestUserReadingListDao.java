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
// $Id: TestUserReadingListDao.java,v 1.1.1.1 2006/10/23 13:55:57 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.*;

import java.util.List;

/**
 * @see com.salas.bbservice.persistence.IUserReadingListDao
 */
public class TestUserReadingListDao extends BasicDaoTestCase
{
    /**
     * @see IUserReadingListDao#add
     */
    public void testAdd()
    {
        User user = new User("_a", "_e", "_p", "", false);
        userDao.add(user);

        try
        {
            UserGuide guide = guide1();
            guide.setUserId(user.getId());
            userGuideDao.add(guide);

            UserReadingList rl = new UserReadingList(guide.getId(), "_a", "file://test");
            userReadingListDao.add(rl);

            assertFalse(-1 == rl.getId());

            // lookup
            List rls = userReadingListDao.findByUserGuideId(guide.getId());
            assertEquals("Should be one RL.", 1, rls.size());
            assertEquals("Wrong RL.", rl, rls.get(0));
        } finally
        {
            userDao.delete(user);
        }
    }
}
