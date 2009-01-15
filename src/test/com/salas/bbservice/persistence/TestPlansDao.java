// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2006 by R. Pito Salas
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
// $Id: TestPlansDao.java,v 1.4 2007/04/18 12:21:26 alg Exp $
//

package com.salas.bbservice.persistence;

import junit.framework.TestCase;

/**
 * The series of simple tests.
 */
public class TestPlansDao extends TestCase
{
    private IPlansDao dao;

    /** Preparations. */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        dao = DaoConfig.getPlansDao();
    }

    // ------------------------------------------------------------------------
    // Get Hash
    // ------------------------------------------------------------------------

    /** User doesn't exist */
    public void testGetHash_no_user()
    {
        String hash = dao.getHash("a", "");
        assertTrue(hash.matches("0-1-[0-9]+-0"));
    }
}
