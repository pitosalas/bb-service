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
// $Id: TestStatsDao.java,v 1.1.1.1 2006/10/23 13:55:55 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Installation;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.domain.dao.DatesRange;
import com.salas.bbservice.domain.dao.ValueCount;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @see IStatsDao
 */
public class TestStatsDao extends BasicDaoTestCase
{
    /**
     * @see IStatsDao#getInstallationsCount
     */
    public void testGetInstallationCount()
    {
        int everTotal = calcTotal(
            statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_EVER, null));

        int yearTotal = calcTotal(
            statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_YEAR, null));

        int monthTotal = calcTotal(
            statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_MONTH, null));

        int weekTotal = calcTotal(
            statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_WEEK, null));

        int todayTotal = calcTotal(
            statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_TODAY, null));

        // register new installations
        Installation i = new Installation(1, "A", 1, "B", "C");
        installationDao.add(i);

        try
        {
            // count again
            int everTotal2 = calcTotal(
                statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_EVER, null));

            int yearTotal2 = calcTotal(
                statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_YEAR, null));

            int monthTotal2 = calcTotal(
                statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_MONTH, null));

            int weekTotal2 = calcTotal(
                statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_WEEK, null));

            int todayTotal2 = calcTotal(
                statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_TODAY, null));

            // check
            assertEquals(everTotal + 1, everTotal2);
            assertEquals(yearTotal + 1, yearTotal2);
            assertEquals(monthTotal + 1, monthTotal2);
            assertEquals(weekTotal + 1, weekTotal2);
            assertEquals(todayTotal + 1, todayTotal2);
        } catch (Exception e)
        {
            fail(e.getMessage());
        } finally
        {
            installationDao.delete(i);
        }
    }

    /**
     * Tests getting latest versions.
     */
    public void testGetLatestVersions()
    {
        Calendar c = new GregorianCalendar();

        Installation i0 = new Installation(10, "9.0.0", 1, "A", "A");
        c.set(3000, 5, 4);
        i0.setInstallationDate(c.getTime());

        Installation i1 = new Installation(11, "10.0.0", 1, "A", "A");
        c.set(3000, 5, 5);
        i1.setInstallationDate(c.getTime());

        Installation i2 = new Installation(12, "10.0.1", 1, "A", "A");
        c.set(3000, 5, 6);
        i2.setInstallationDate(c.getTime());

        Installation i3 = new Installation(13, "10.0.0", 1, "A", "A");
        c.set(3000, 5, 7);
        i3.setInstallationDate(c.getTime());

        installationDao.add(i0);
        installationDao.add(i1);
        installationDao.add(i2);
        installationDao.add(i3);

        try
        {
            List versions = statsDao.getLatestVersions(2);
            assertEquals(2, versions.size());
            assertEquals("10.0.1", versions.get(0));
            assertEquals("10.0.0", versions.get(1));
        } finally
        {
            installationDao.delete(i0);
            installationDao.delete(i1);
            installationDao.delete(i2);
            installationDao.delete(i3);
        }
    }

    /**
     * Tests reporting installations in given range.
     */
    public void testGetInstallationsInRange()
    {
        Calendar c = new GregorianCalendar();

        // Setup installations in 3000-01-04 .. 3000-03-04
        Installation[] installations = new Installation[3];
        for (int i = 0; i < 3; i++)
        {
            Installation inst = new Installation(20 + i, "9.0.0", 1, "A", "A");
            c.set(3000, i, 4);
            inst.setInstallationDate(c.getTime());
            installations[i] = inst;

            installationDao.add(inst);
        }

        String date_3000_01_01 = DatesRange.createDate(3000, 1, 1);
        String date_3000_02_01 = DatesRange.createDate(3000, 2, 1);
        String date_3000_03_01 = DatesRange.createDate(3000, 3, 1);
        String date_3000_04_01 = DatesRange.createDate(3000, 4, 1);
        int count;

        try
        {
            count = statsDao.getInstallationsInRange(new DatesRange(date_3000_01_01, date_3000_01_01));
            assertEquals(0, count);
            count = statsDao.getInstallationsInRange(new DatesRange(date_3000_01_01, date_3000_02_01));
            assertEquals(1, count);
            count = statsDao.getInstallationsInRange(new DatesRange(date_3000_01_01, date_3000_03_01));
            assertEquals(2, count);
            count = statsDao.getInstallationsInRange(new DatesRange(date_3000_01_01, date_3000_04_01));
            assertEquals(3, count);
        } finally
        {
            // Remove all test installations
            for (int i = 0; i < installations.length; i++)
            {
                Installation inst = installations[i];
                installationDao.delete(inst);
            }
        }
    }

    public void testGetRegistrationTrends()
        throws ParseException
    {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        User u1 = new User("01/01/2500", "_1", "A", "B", false);
        u1.setRegistrationDate(df.parse(u1.getFullName()));
        User u2 = new User("01/01/2500", "_2", "A", "B", false);
        u2.setRegistrationDate(df.parse(u2.getFullName()));
        User u3 = new User("01/02/2500", "_3", "A", "B", false);
        u3.setRegistrationDate(df.parse(u3.getFullName()));

        userDao.add(u1);
        userDao.add(u2);
        userDao.add(u3);

        try
        {
            ValueCount count;

            List trends = statsDao.getRegistrationTrends(0);
            assertEquals(0, trends.size());

            trends = statsDao.getRegistrationTrends(1);
            assertEquals(1, trends.size());
            count = (ValueCount)trends.get(0);
            assertEquals(u3.getFullName(), count.getValue());
            assertEquals(1, count.getCount());

            trends = statsDao.getRegistrationTrends(2);
            assertEquals(2, trends.size());
            count = (ValueCount)trends.get(0);
            assertEquals(u3.getFullName(), count.getValue());
            assertEquals(1, count.getCount());
            count = (ValueCount)trends.get(1);
            assertEquals(u1.getFullName(), count.getValue());
            assertEquals(2, count.getCount());
        } finally
        {
            userDao.delete(u1);
            userDao.delete(u2);
            userDao.delete(u3);
        }
    }

    private int calcTotal(List l)
    {
        int res = 0;

        for (int i = 0; i < l.size(); i++)
        {
            ValueCount valueCount = (ValueCount)l.get(i);
            res += valueCount.getCount();
        }

        return res;
    }
}
