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
// $Id: TestStatsProcessor.java,v 1.2 2007/01/02 12:12:33 alg Exp $
//

package com.salas.bbservice.stats;

import com.salas.bbservice.domain.dao.ValueCount;
import com.salas.bbservice.utils.AbstractTestCase;

import java.util.*;

/**
 * @see StatsProcessor
 */
public class TestStatsProcessor extends AbstractTestCase
{
    private StatsProcessor sp;
    private static final int MILLIS_IN_DAY = 24*60*60*1000;

    protected void setUp() throws Exception
    {
        sp = new StatsProcessor();
    }

    /**
     * @see StatsProcessor#putStats
     */
    public void testPutStats()
    {
        Map stats = new TreeMap();
        List l1 = new ArrayList();
        List l2 = new ArrayList();

        l1.add(new ValueCount("A", 1));
        l1.add(new ValueCount("B", 2));

        l2.add(new ValueCount("A", 2));
        l2.add(new ValueCount("C", 1));

        sp.putStats(stats, l1, 0, 2);
        sp.putStats(stats, l2, 1, 2);

        Set keys = stats.keySet();
        assertEquals(2 + 1, keys.size());

        String[] keysS = (String[])keys.toArray(new String[0]);

        assertEquals("A", keysS[0]);
        assertEquals("B", keysS[1]);
        assertEquals("C", keysS[2]);

        double[] arrA = (double[])stats.get("A");
        assertEquals(2, arrA.length);
        assertEquals(1, (int)arrA[0]);
        assertEquals(2, (int)arrA[1]);

        double[] arrB = (double[])stats.get("B");
        assertEquals(2, arrB.length);
        assertEquals(2, (int)arrB[0]);
        assertEquals(0, (int)arrB[1]);

        double[] arrC = (double[])stats.get("C");
        assertEquals(2, arrC.length);
        assertEquals(0, (int)arrC[0]);
        assertEquals(1, (int)arrC[1]);
    }

    /**
     * @see StatsProcessor#prepareKeywordsChart
     */
    public void testPrepareKeywordsChart()
    {
        SortedSet chart = new TreeSet(new ValueCount.CVComparator(true));

        assertEquals(0, StatsProcessor.prepareKeywordsChart(null, 1).getRowsCount());
        assertEquals(0, StatsProcessor.prepareKeywordsChart(chart, -1).getRowsCount());
        assertEquals(0, StatsProcessor.prepareKeywordsChart(chart, 0).getRowsCount());

        chart.add(new ValueCount("a", 100));
        chart.add(new ValueCount("c", 25));
        chart.add(new ValueCount("b", 25));
        chart.add(new ValueCount("d", 12));

        assertEquals(0, StatsProcessor.prepareKeywordsChart(chart, 0).getRowsCount());
        assertEquals(1, StatsProcessor.prepareKeywordsChart(chart, 1).getRowsCount());
        assertEquals(2, StatsProcessor.prepareKeywordsChart(chart, 2).getRowsCount());
        assertEquals(3, StatsProcessor.prepareKeywordsChart(chart, 3).getRowsCount());
        assertEquals(4, StatsProcessor.prepareKeywordsChart(chart, 4).getRowsCount());
        assertEquals(4, StatsProcessor.prepareKeywordsChart(chart, 5).getRowsCount());

        StatsTable tbl = StatsProcessor.prepareKeywordsChart(chart, 5);
        assertEquals("a", tbl.getRowTitle(0));
        assertTrue(100 == (int)tbl.getData(0, 0));
        assertEquals("b", tbl.getRowTitle(1));
        assertTrue(25 == (int)tbl.getData(0, 1));
        assertEquals("c", tbl.getRowTitle(2));
        assertTrue(25 == (int)tbl.getData(0, 2));
        assertEquals("d", tbl.getRowTitle(3));
        assertTrue(12 == (int)tbl.getData(0, 3));
    }

    /**
     * Tests joining of lists of versions.
     */
    public void testJoinVersionsLists()
    {
        String[] fixed, latest, result;

        // Fixed - empty, latest - empty
        fixed = new String[0];
        latest = new String[0];
        result = StatsProcessor.joinVersionsLists(fixed, latest);
        assertEquals(0, result.length);

        // Fixed - inited, latest - empty
        fixed = new String[] { "1.0", "1.0", "1.1" };
        latest = new String[0];
        result = StatsProcessor.joinVersionsLists(fixed, latest);
        assertEquals(2, result.length);
        assertEquals("1.0", result[0]);
        assertEquals("1.1", result[1]);

        // Fixed - empty, latest - inited.
        fixed = new String[0];
        latest = new String[] { "1.0", "1.0", "1.1" };
        result = StatsProcessor.joinVersionsLists(fixed, latest);
        assertEquals(2, result.length);
        assertEquals("1.0", result[0]);
        assertEquals("1.1", result[1]);

        // Both lists inited and have intersecting entries
        fixed = new String[] { "0.9", "1.2" };
        latest = new String[] { "1.0", "1.2", "1.1" };
        result = StatsProcessor.joinVersionsLists(fixed, latest);
        assertEquals(4, result.length);
        assertEquals("0.9", result[0]);
        assertEquals("1.2", result[1]);
        assertEquals("1.0", result[2]);
        assertEquals("1.1", result[3]);
    }

    /**
     * Test calculating number of days between two dates.
     */
    public void testCalcNumberOfDays()
    {
        Date dateToday = new Date();
        Date dateNextWeek = new Date(dateToday.getTime() + 7 * MILLIS_IN_DAY);

        Calendar cal = new GregorianCalendar(2005, 0, 1);
        Date date010105 = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 5);
        Date date010505 = cal.getTime();

        cal.set(Calendar.MONTH, 1);
        Date date020505 = cal.getTime();

        assertEquals(4, StatsProcessor.calcNumberOfDays(date010105, date010505));
        assertEquals(4, StatsProcessor.calcNumberOfDays(date010505, date010105));

        assertEquals("There are 31 day in January.",
            31, StatsProcessor.calcNumberOfDays(date010505, date020505));

        int daysBetween010105AndToday = StatsProcessor.calcNumberOfDays(date010105, dateToday);

        assertEquals("We always measure by today.",
            daysBetween010105AndToday, StatsProcessor.calcNumberOfDays(date010105, dateNextWeek));

        assertEquals("We always limit dates with today's date.",
            0, StatsProcessor.calcNumberOfDays(dateToday, dateNextWeek));
    }
}
