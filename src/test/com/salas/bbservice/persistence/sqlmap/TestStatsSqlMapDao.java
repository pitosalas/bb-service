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
// $Id: TestStatsSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:58 alg Exp $
//

package com.salas.bbservice.persistence.sqlmap;

import junit.framework.TestCase;

import java.util.*;

import com.salas.bbservice.domain.dao.ValueCount;

/**
 * @see StatsSqlMapDao
 */
public class TestStatsSqlMapDao extends TestCase
{
    /**
     * @see StatsSqlMapDao#putKeywordInMap
     */
    public void testPutKeywordInMap()
    {
        Map map = new HashMap();

        // NULL handling
        StatsSqlMapDao.putKeywordInMap(null, null);
        StatsSqlMapDao.putKeywordInMap("a", null);
        StatsSqlMapDao.putKeywordInMap(null, map);
        assertEquals(0, map.size());

        StatsSqlMapDao.putKeywordInMap("a", map);
        StatsSqlMapDao.putKeywordInMap("a", map);
        assertEquals(1, map.size());
        assertEquals(2, ((ValueCount)map.get("a")).getCount());
    }

    /**
     * @see StatsSqlMapDao#putKeywordsInMap
     */
    public void testPutKeywordsInMap()
    {
        Map map = new HashMap();

        // NULL handling
        StatsSqlMapDao.putKeywordsInMap(null, null);
        StatsSqlMapDao.putKeywordsInMap("a|b", null);
        StatsSqlMapDao.putKeywordsInMap(null, map);
        assertEquals(0, map.size());

        // We count only different users' keywords, so, duplicates are ignored
        StatsSqlMapDao.putKeywordsInMap("|a| A|b || B |||", map);
        assertEquals(2, map.size());
        assertEquals(1, ((ValueCount)map.get("a")).getCount());
        assertEquals(1, ((ValueCount)map.get("b")).getCount());
    }

    /**
     * @see StatsSqlMapDao#prepareKeywordsChart
     */
    public void testPrepareKeywordsChart()
    {
        List keywordsList = null;
        SortedSet chart;

        // NULL handling
        chart = StatsSqlMapDao.prepareKeywordsChart(null);
        assertNotNull(chart);
        assertEquals(0, chart.size());

        // Sample
        keywordsList = new ArrayList();
        keywordsList.add("a| B| c| D ||");
        keywordsList.add("|b |C | e");
        keywordsList.add("|f||a|c |");
        chart = StatsSqlMapDao.prepareKeywordsChart(keywordsList);
        assertNotNull(chart);
        assertEquals(6, chart.size());

        Iterator it = chart.iterator();
        ValueCount vc;
        vc = (ValueCount)it.next();
        assertEquals("c", vc.getValue());
        assertEquals(3, vc.getCount());
        vc = (ValueCount)it.next();
        assertEquals("a", vc.getValue());
        assertEquals(2, vc.getCount());
        vc = (ValueCount)it.next();
        assertEquals("b", vc.getValue());
        assertEquals(2, vc.getCount());
        vc = (ValueCount)it.next();
        assertEquals("d", vc.getValue());
        assertEquals(1, vc.getCount());
        vc = (ValueCount)it.next();
        assertEquals("e", vc.getValue());
        assertEquals(1, vc.getCount());
        vc = (ValueCount)it.next();
        assertEquals("f", vc.getValue());
        assertEquals(1, vc.getCount());
    }
}
