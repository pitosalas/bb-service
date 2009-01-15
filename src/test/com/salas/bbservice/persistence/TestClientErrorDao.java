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
// $Id: TestClientErrorDao.java,v 1.1.1.1 2006/10/23 13:55:54 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.*;

/**
 * @see IClientErrorDao
 */
public class TestClientErrorDao extends BasicDaoTestCase
{                            
    /**
     * @see ClientError
     */
    public void testCreate()
    {
        ClientError ce = new ClientError("a", "b", "1.1.1");
        assertEquals(-1, ce.getId());
        assertEquals("a", ce.getMessage());
        assertEquals("b", ce.getDetails());
        assertTrue(System.currentTimeMillis() - ce.getTime() < 1000);
    }

    /**
     * @see IClientErrorDao#add
     */
    public void testAdd()
    {
        ClientError ce = new ClientError("a", "b", "1.1.1");

        clientErrorDao.add(ce);
        assertFalse(-1 == ce.getId());

        clientErrorDao.delete(ce);
    }
}
