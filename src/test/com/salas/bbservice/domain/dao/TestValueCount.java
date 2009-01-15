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
// $Id $
//

package com.salas.bbservice.domain.dao;

import junit.framework.TestCase;

/**
 * Tests value count helper methods.
 */
public class TestValueCount extends TestCase
{
    /**
     * Tests invalid input handling.
     */
    public void testGroupValues_Empty()
    {
        assertNull(ValueCount.groupValues(null));
        assertEquals(0, ValueCount.groupValues(new ValueCount[0]).length);
    }

    /**
     * Tests passing without regrouping.
     */
    public void testGroupValues_1()
    {
        ValueCount[] cntsD;
        ValueCount[] cntsS = new ValueCount[]
        {
            new ValueCount("a", 1),
            new ValueCount("b", 2),
        };

        cntsD = ValueCount.groupValues(cntsS);
        assertEquals(2, cntsD.length);
        assertVC(cntsS[0], cntsD[0]);
        assertVC(cntsS[1], cntsD[1]);
    }

    /**
     * Tests grouping.
     */
    public void testGroupValues_2()
    {
        ValueCount[] cntsD;
        ValueCount[] cntsS = new ValueCount[]
        {
            new ValueCount("a", 1),
            new ValueCount("a", 2),
        };

        cntsD = ValueCount.groupValues(cntsS);
        assertEquals(1, cntsD.length);
        assertVC(new ValueCount("a", 3), cntsD[0]);
    }

    private static void assertVC(ValueCount cntS, ValueCount cntD)
    {
        assertEquals(cntS.getValue(), cntD.getValue());
        assertEquals(cntS.getCount(), cntD.getCount());
    }
}
