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
// $Id: TestVersionsDao.java,v 1.1.1.1 2006/10/23 13:55:58 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.*;

import java.util.List;

/**
 * @see IVersionsDao
 */
public class TestVersionsDao extends BasicDaoTestCase
{
    /**
     * Tests adding version.
     */
    public void testAddingVersion()
    {
        long currentTime = System.currentTimeMillis();
        Version version = new Version("0.0", currentTime, true);
        Version version2 = new Version("0.2", currentTime + 10, false);

        versionsDao.addVersion(version);
        versionsDao.addVersion(version2);

        try
        {
            Version fetchedVersion = versionsDao.getRecentVersion(true);
            assertEquals(version, fetchedVersion);

            fetchedVersion = versionsDao.getRecentVersion(false);
            assertEquals(version2, fetchedVersion);

            fetchedVersion = versionsDao.findVersion("0.0");
            assertEquals(version, fetchedVersion);

            fetchedVersion = versionsDao.findVersion("0.1");
            assertNull("No such version were added.", fetchedVersion);
        } finally
        {
            int versionId = version.getId();
            if (versionId != -1) versionsDao.removeVersion(versionId);
            versionId = version2.getId();
            if (versionId != -1) versionsDao.removeVersion(versionId);
        }
    }

    /**
     * Tests adding versions with changes and fetching list of changes from
     * some given version.
     */
    public void testAddingVersionChanges()
    {
        long currentTime = System.currentTimeMillis();
        Version version00 = new Version("0.0", currentTime-2, true);
        versionsDao.addVersion(version00);
        Version version01 = new Version("0.1", currentTime-1, false);
        versionsDao.addVersion(version01);
        Version version02 = new Version("0.2", currentTime, true);
        versionsDao.addVersion(version02);
        Version version03 = new Version("0.3", currentTime + 1, false);
        versionsDao.addVersion(version03);

        int ver00Id = version00.getId();
        int ver01Id = version01.getId();
        int ver02Id = version02.getId();
        int ver03Id = version03.getId();

        try
        {
            // Changes to version 0.0
            VersionChange change001 = new VersionChange(ver00Id, VersionChange.TYPE_FEATURE, "fe-0.0");
            VersionChange change002 = new VersionChange(ver00Id, VersionChange.TYPE_FIX, "fi-0.0");
            versionsDao.addVersionChange(change001);
            versionsDao.addVersionChange(change002);

            // Changes to version 0.1
            VersionChange change011 = new VersionChange(ver01Id, VersionChange.TYPE_FEATURE, "fe-0.1");
            VersionChange change012 = new VersionChange(ver01Id, VersionChange.TYPE_FIX, "fi-0.1");
            versionsDao.addVersionChange(change011);
            versionsDao.addVersionChange(change012);

            // Changes to version 0.2
            VersionChange change021 = new VersionChange(ver02Id, VersionChange.TYPE_FEATURE, "fe-0.2");
            VersionChange change022 = new VersionChange(ver02Id, VersionChange.TYPE_FIX, "fi-0.2");
            versionsDao.addVersionChange(change021);
            versionsDao.addVersionChange(change022);

            // Changes to version 0.3
            VersionChange change031 = new VersionChange(ver03Id, VersionChange.TYPE_FEATURE, "fe-0.3");
            VersionChange change032 = new VersionChange(ver03Id, VersionChange.TYPE_FIX, "fi-0.3");
            versionsDao.addVersionChange(change031);
            versionsDao.addVersionChange(change032);

            List changes;
            changes = versionsDao.listChangesFrom("0.0", true);
            assertEquals(4, changes.size());
            assertEquals(change021, changes.get(0));
            assertEquals(change022, changes.get(1));
            assertEquals(change011, changes.get(2));
            assertEquals(change012, changes.get(3));

            changes = versionsDao.listChangesFrom("0.0", false);
            assertEquals(6, changes.size());
            assertEquals(change031, changes.get(0));
            assertEquals(change032, changes.get(1));
            assertEquals(change021, changes.get(2));
            assertEquals(change022, changes.get(3));
            assertEquals(change011, changes.get(4));
            assertEquals(change012, changes.get(5));

            changes = versionsDao.listChangesFrom("0.1", true);
            assertEquals(2, changes.size());
            assertEquals(change021, changes.get(0));
            assertEquals(change022, changes.get(1));

            changes = versionsDao.listChangesFrom("0.2", true);
            assertEquals(0, changes.size());

            changes = versionsDao.listChangesFrom("0.3", true);
            assertEquals(0, changes.size());

            changes = versionsDao.listChangesFrom("0.4", true);
            assertNull(changes);
        } finally
        {
            if (ver00Id != -1) versionsDao.removeVersion(ver00Id);
            if (ver01Id != -1) versionsDao.removeVersion(ver01Id);
            if (ver02Id != -1) versionsDao.removeVersion(ver02Id);
            if (ver03Id != -1) versionsDao.removeVersion(ver03Id);
        }
    }

    /**
     * Tests reporting no changes because of missing target production
     * version.
     */
    public void testReportingNoChanges()
    {
        long currentTime = System.currentTimeMillis();
        Version version00 = new Version("0.0", currentTime-1, true);
        versionsDao.addVersion(version00);
        Version version01 = new Version("0.1", currentTime, false);
        versionsDao.addVersion(version01);

        int ver00Id = version00.getId();
        int ver01Id = version01.getId();

        try
        {
            // Changes to version 0.0
            VersionChange change001 = new VersionChange(ver00Id, VersionChange.TYPE_FEATURE, "fe-0.0");
            VersionChange change002 = new VersionChange(ver00Id, VersionChange.TYPE_FIX, "fi-0.0");
            versionsDao.addVersionChange(change001);
            versionsDao.addVersionChange(change002);

            // Changes to version 0.1
            VersionChange change011 = new VersionChange(ver01Id, VersionChange.TYPE_FEATURE, "fe-0.1");
            VersionChange change012 = new VersionChange(ver01Id, VersionChange.TYPE_FIX, "fi-0.1");
            versionsDao.addVersionChange(change011);
            versionsDao.addVersionChange(change012);

            List changes;
            changes = versionsDao.listChangesFrom("0.0", true);
            assertEquals(0, changes.size());

            changes = versionsDao.listChangesFrom("0.1", true);
            assertEquals(0, changes.size());
        } finally
        {
            if (ver00Id != -1) versionsDao.removeVersion(ver00Id);
            if (ver01Id != -1) versionsDao.removeVersion(ver01Id);
        }
    }

    /**
     * Tests fetching of latest versions.
     */
    public void testFetchingOfVersions()
    {
        long currentTime = System.currentTimeMillis();
        Version version00 = new Version("0.0", currentTime-2, true);
        versionsDao.addVersion(version00);
        Version version01 = new Version("0.1", currentTime-1, false);
        versionsDao.addVersion(version01);
        Version version02 = new Version("0.2", currentTime, true);
        versionsDao.addVersion(version02);

        int ver00Id = version00.getId();
        int ver01Id = version01.getId();
        int ver02Id = version02.getId();

        try
        {
            List latestVersions;

            latestVersions = versionsDao.getLatestVersions(1);
            assertEquals(1, latestVersions.size());
            assertEquals(version02, latestVersions.get(0));

            latestVersions = versionsDao.getLatestVersions(2);
            assertEquals(2, latestVersions.size());
            assertEquals(version02, latestVersions.get(0));
            assertEquals(version01, latestVersions.get(1));

            latestVersions = versionsDao.getLatestVersions(10);
            assertTrue(latestVersions.size() >= 3);
            assertEquals(version02, latestVersions.get(0));
            assertEquals(version01, latestVersions.get(1));
            assertEquals(version00, latestVersions.get(2));
        } finally
        {
            if (ver00Id != -1) versionsDao.removeVersion(ver00Id);
            if (ver01Id != -1) versionsDao.removeVersion(ver01Id);
            if (ver02Id != -1) versionsDao.removeVersion(ver02Id);
        }
    }
}
