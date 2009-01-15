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
// $Id: IVersionsDao.java,v 1.1.1.1 2006/10/23 13:55:38 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Version;
import com.salas.bbservice.domain.VersionChange;

import java.util.List;

/**
 * DAO for working with versions information.
 */
public interface IVersionsDao
{
    /**
     * Adds new version to the list.
     *
     * @param version version to add.
     */
    void addVersion(Version version);

    /**
     * Removes version.
     *
     * @param versionId version ID.
     */
    void removeVersion(int versionId);

    /**
     * Adds verion change item.
     *
     * @param change    version change to add.
     */
    void addVersionChange(VersionChange change);

    /**
     * Removes change.
     *
     * @param changeId change ID.
     */
    void removeVersionChange(int changeId);

    /**
     * Returns list of changes happened since given version.
     *
     * @param version version to check.
     * @param productionOnly find latest production version.
     *
     * @return list of <code>VersionChange</code> objects in most recent top order.
     */
    List listChangesFrom(String version, boolean productionOnly);

    /**
     * Returns most recent production version object.
     *
     * @param productionOnly find latest production version.
     *
     * @return most recent version.
     */
    Version getRecentVersion(boolean productionOnly);

    /**
     * Returns list of latest versions.
     *
     * @param max   maximum number of versions to return.
     *
     * @return list of latest versions.
     */
    List getLatestVersions(int max);

    /**
     * Looks for version in database.
     *
     * @param version version name.
     *
     * @return version object or <code>NULL</code> if not found.
     */
    Version findVersion(String version);

    /**
     * Finds version by its ID.
     *
     * @param aVersionId version ID.
     *
     * @return version.
     */
    Version findVersion(int aVersionId);

    /**
     * Returns list of changes which are recorded to this version.
     *
     * @param aVersionId ID of the version.
     *
     * @return list of changes.
     */
    List listChangesOfVersion(int aVersionId);
}
