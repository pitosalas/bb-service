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
// $Id: VersionsSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:40 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.Version;
import com.salas.bbservice.domain.VersionChange;
import com.salas.bbservice.domain.dao.TimeRange;
import com.salas.bbservice.persistence.IVersionsDao;

import java.util.ArrayList;
import java.util.List;

/**
 * SqlMap implementation for Versions DAO.
 */
public class VersionsSqlMapDao extends SqlMapDaoTemplate implements IVersionsDao
{
    /**
     * Creates DAO for Versions.
     *
     * @param daoManager manager.
     */
    public VersionsSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds new version to the list.
     *
     * @param version version to add.
     */
    public void addVersion(Version version)
    {
        insert("Versions.addVersion", version);
    }

    /**
     * Returns most recent production version object.
     *
     * @param productionOnly find latest production version.
     *
     * @return most recent version.
     */
    public Version getRecentVersion(boolean productionOnly)
    {
        return (Version)queryForObject("Versions.getRecentVersion",
            productionOnly ? "WHERE production=1" : "");
    }

    /**
     * Removes version.
     *
     * @param versionId version ID.
     */
    public void removeVersion(int versionId)
    {
        delete("Versions.removeVersion", new Integer(versionId));
    }

    /**
     * Adds verion change item.
     *
     * @param change    version change to add.
     */
    public void addVersionChange(VersionChange change)
    {
        insert("Versions.addVersionChange", change);
    }

    /**
     * Removes change.
     *
     * @param changeId change ID.
     */
    public void removeVersionChange(int changeId)
    {
        delete("Versions.removeVersionChange", new Integer(changeId));
    }

    /**
     * Returns list of changes happened since given version.
     *
     * @param version version to check.
     * @param productionOnly find latest production version.
     *
     * @return list of <code>VersionChange</code> objects in most recent top order,
     *         empty list if new production version is not available and <code>NULL</code>
     *         if source version wasn't found.
     */
    public List listChangesFrom(String version, boolean productionOnly)
    {
        List list = null;

        Version sourceVersion = findVersion(version);
        if (sourceVersion != null)
        {
            Version latestProduction = getRecentVersion(productionOnly);
            if (latestProduction != null && !latestProduction.equals(sourceVersion))
            {
                long startTime = sourceVersion.getReleaseTime();
                long finishTime = latestProduction.getReleaseTime();

                list = queryForList("Versions.listVersionChangesBetween",
                    new TimeRange(startTime, finishTime));
            } else list = new ArrayList(0);
        }

        return list;
    }

    /**
     * Looks for version in database.
     *
     * @param version version name.
     *
     * @return version object or <code>NULL</code> if not found.
     */
    public Version findVersion(String version)
    {
        return (Version)queryForObject("Versions.findVersion", version);
    }

    /**
     * Finds version by its ID.
     *
     * @param aVersionId version ID.
     *
     * @return version.
     */
    public Version findVersion(int aVersionId)
    {
        return (Version)queryForObject("Versions.findVersionByID", new Integer(aVersionId));
    }

    /**
     * Returns list of latest versions.
     *
     * @param max maximum number of versions to return.
     *
     * @return list of latest versions.
     */
    public List getLatestVersions(int max)
    {
        return queryForList("Versions.getLatestVersions", new Integer(max));
    }

    /**
     * Returns list of changes which are recorded to this version.
     *
     * @param aVersionId ID of the version.
     *
     * @return list of changes.
     */
    public List listChangesOfVersion(int aVersionId)
    {
        return queryForList("Versions.getVersionChanges", new Integer(aVersionId));
    }
}
