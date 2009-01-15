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
 * Tests label count percentage helper functions.
 */
public class TestLabelCountPercentage extends TestCase
{
    /**
     * Tests handling invalid input.
     */
    public void testConsolidate_Empty()
    {
        assertNull(LabelCountPercentage.consolidate(null));
        assertEquals(0, LabelCountPercentage.consolidate(new ValueCount[0]).length);
    }

    /**
     * Tests consolidation of a simple report.
     */
    public void testConsolidate_Simple()
    {
        ValueCount[] cnts = new ValueCount[]
        {
            new ValueCount("a", 2),
            new ValueCount("b", 1)
        };

        LabelCountPercentage[] lcps = LabelCountPercentage.consolidate(cnts);
        assertEquals(2, lcps.length);
        assertLCP(1, 1, 50d, lcps[0]);
        assertLCP(2, 1, 50d, lcps[1]);
    }

    /**
     * Tests consolidation of a complex report.
     */
    public void testConsolidate_Complex()
    {
        ValueCount[] cnts = new ValueCount[]
        {
            new ValueCount("a", 500),
            new ValueCount("b", 2),
            new ValueCount("c", 1),
            new ValueCount("d", 2),
        };

        LabelCountPercentage[] lcps = LabelCountPercentage.consolidate(cnts);
        assertEquals(3, lcps.length);
        assertLCP(2, 2, 50d, lcps[0]);
        assertLCP(1, 1, 25d, lcps[1]);
        assertLCP(500, 1, 25d, lcps[2]);
    }

    private void assertLCP(int label, int count, double perc, LabelCountPercentage lcp)
    {
        assertEquals(Integer.toString(label), lcp.getLabel());
        assertEquals(count, lcp.getCount());
        assertEquals(perc, lcp.getPercentage(), 0.001d);
    }
}
