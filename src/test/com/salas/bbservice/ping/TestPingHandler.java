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
// $Id: TestPingHandler.java,v 1.1.1.1 2006/10/23 13:55:58 alg Exp $
//

package com.salas.bbservice.ping;

import junit.framework.TestCase;
import com.salas.bbservice.persistence.IStatsDao;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.IInstallationDao;
import com.salas.bbservice.persistence.IUserDao;
import com.salas.bbservice.domain.dao.ValueCount;
import com.salas.bbservice.domain.Installation;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.utils.StringUtils;
import com.ibatis.dao.client.DaoManager;

import java.util.List;
import java.security.NoSuchAlgorithmException;

/**
 * @see PingHandler
 */
public class TestPingHandler extends TestCase
{
    /** Strings */
    
    private static final String TEST = "test";
    private static final String TEST_USER = "Test User";
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "test password";
    private static final String TEST_LANGUAGE = "en_US";
    private static final String TEST_PASSWORD_2 = "some other password";

    private PingHandler         ph;
    private IStatsDao           statsDao;
    private IInstallationDao    installationDao;
    private IUserDao            userDao;

    protected void setUp() throws Exception
    {
        ph = new PingHandler();
        final DaoManager daoManager = DaoConfig.getDaoManager();
        statsDao = (IStatsDao)daoManager.getDao(IStatsDao.class);
        installationDao = (IInstallationDao)daoManager.getDao(IInstallationDao.class);
        userDao = (IUserDao)daoManager.getDao(IUserDao.class);
    }

    /**
     * @see PingHandler#ping1
     */
    public void testPing1()
    {
        List counts = statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_TODAY, null);
        ValueCount vc = findCountForValue(counts, TEST);
        int countInst1 = vc == null ? 0 : vc.getCount();
        counts = statsDao.getRunsPerVersion(IStatsDao.COUNT_PERIOD_TODAY, null);
        vc = findCountForValue(counts, TEST);
        int countRuns1 = vc == null ? 0 : vc.getCount();

        ph.ping1("1", TEST, 1, TEST, TEST);

        try
        {
            counts = statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_TODAY, null);
            vc = findCountForValue(counts, TEST);
            int countInst2 = vc == null ? 0 : vc.getCount();
            counts = statsDao.getRunsPerVersion(IStatsDao.COUNT_PERIOD_TODAY, null);
            vc = findCountForValue(counts, TEST);
            int countRuns2 = vc == null ? 0 : vc.getCount();

            assertEquals(countInst1 + 1, countInst2);
            assertEquals(countRuns1 + 1, countRuns2);

            ph.ping1("1", TEST, 1, TEST, TEST);

            counts = statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_TODAY, null);
            vc = findCountForValue(counts, TEST);
            int countInst3 = vc == null ? 0 : vc.getCount();
            counts = statsDao.getRunsPerVersion(IStatsDao.COUNT_PERIOD_TODAY, null);
            vc = findCountForValue(counts, TEST);
            int countRuns3 = vc == null ? 0 : vc.getCount();

            assertEquals(countInst1 + 1, countInst3);
            assertEquals(countRuns1 + 2, countRuns3);

            // unregister installation
            Installation i = installationDao.findById(1);
            if (i != null) installationDao.delete(i);

            counts = statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_TODAY, null);
            vc = findCountForValue(counts, TEST);
            int countInst4 = vc == null ? 0 : vc.getCount();
            counts = statsDao.getRunsPerVersion(IStatsDao.COUNT_PERIOD_TODAY, null);
            vc = findCountForValue(counts, TEST);
            int countRuns4 = vc == null ? 0 : vc.getCount();

            assertEquals(0, countInst4);
            assertEquals(0, countRuns4);
        } finally
        {
            Installation i = installationDao.findById(1);
            if (i != null) installationDao.delete(i);
        }
    }

    /**
     * Tests verification of digested emails.
     */
    public void testIsCorrectUserAccount()
        throws NoSuchAlgorithmException
    {
        User testUser = new User(TEST_USER, TEST_EMAIL, TEST_PASSWORD, TEST_LANGUAGE, false);

        byte[] correctDigestedEmail = StringUtils.digestMD5(testUser.getEmail(), testUser.getPassword());
        assertTrue("Verification fails.", PingHandler.isCorrectUserAccount(testUser, correctDigestedEmail));

        byte[] incorrectDigestedEmail = StringUtils.digestMD5(testUser.getEmail(), TEST_PASSWORD_2);
        assertFalse("Verification fails.", PingHandler.isCorrectUserAccount(testUser, incorrectDigestedEmail));
    }

    /**
     * Verifies that methods work. We don't have means to check the results for now.
     */
    public void testAssociateUser()
        throws NoSuchAlgorithmException
    {
        User testUser = new User(TEST_USER, TEST_EMAIL, TEST_PASSWORD, TEST_LANGUAGE, false);

        userDao.add(testUser);
        try
        {
            // Ping without email as if user hasn't registered account yet.
            ph.ping1("1", "0.0", 1, TEST, "1.4.2");

            // Ping with email as if user has registered account or just entered info.
            String email = testUser.getEmail();
            byte[] digestedEmail = StringUtils.digestMD5(email, testUser.getPassword());
            ph.ping1("1", "0.0", 2, TEST, "1.4.2", email, digestedEmail);
        } finally
        {
            Installation i = installationDao.findById(1);
            if (i != null) installationDao.delete(i);

            userDao.delete(testUser);
        }
    }

    /**
     * Finds <code>ValueCount</code> object for specific value.
     *
     * @param counts    list of counts.
     * @param value     value.
     *
     * @return object or null.
     */
    private ValueCount findCountForValue(List counts, String value)
    {
        ValueCount res = null;

        int i;
        for (i = 0; i < counts.size() &&
            !((ValueCount)counts.get(i)).getValue().equals(value); i++);

        if (i < counts.size())
        {
            res = (ValueCount)counts.get(i);
        }

        return res;
    }
}
