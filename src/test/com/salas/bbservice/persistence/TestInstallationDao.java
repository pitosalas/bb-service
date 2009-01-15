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
// $Id: TestInstallationDao.java,v 1.1.1.1 2006/10/23 13:55:55 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Installation;

/**
 * @see IInstallationDao
 */
public class TestInstallationDao extends BasicDaoTestCase
{
    /**
     * @see Installation#Installation(long, String, int, String, String)
     */
    public void testCreate()
    {
        Installation i = new Installation(1, "A", 1, "B", "C");
        assertEquals(1, i.getId());
        assertEquals("A", i.getVersion());
        assertEquals(1, i.getRuns());
        assertEquals("B", i.getOs());
        assertEquals("C", i.getJavaVersion());
    }

    /**
     * @see IInstallationDao#add
     */
    public void testAdd()
    {
        Installation i = new Installation(1, "A", 1, "B", "C");

        installationDao.add(i);

        try
        {
            installationDao.add(i);
            fail("Exception should be thrown.");
        } catch (Exception e)
        {
            // Normal behavior
        } finally
        {
            installationDao.delete(i);
        }
    }

    /**
     * @see IInstallationDao#delete
     */
    public void testDelete()
    {
        Installation i = new Installation(1, "A", 1, "B", "C");
        installationDao.add(i);
        long id = i.getId();

        installationDao.delete(i);
        assertNull(installationDao.findById(id));
    }

    /**
     * @see IInstallationDao#delete
     */
    public void testDeleteCascade()
    {
        // empty tests by now
    }

    /**
     * @see IInstallationDao#findById
     */
    public void testFindById()
    {
        Installation i1 = new Installation(1, "A", 1, "B", "C");
        installationDao.add(i1);

        try
        {
            // find installation in database by id
            Installation i2 = installationDao.findById(i1.getId());
            assertNotNull(i2);
            assertEquals(i1, i2);
        } finally
        {
            installationDao.delete(i1);
        }
    }

    /**
     * @see IInstallationDao#update
     */
    public void testUpdate()
    {
        Installation i1 = new Installation(1, "A", 1, "B", "C");
        installationDao.add(i1);

        try
        {
            // update data
            i1.setRuns(2);
            i1.setJavaVersion("D");

            installationDao.update(i1);
            Installation i2 = installationDao.findById(i1.getId());
            assertEquals(i1, i2);
        } finally
        {
            installationDao.delete(i1);
        }
    }
}
