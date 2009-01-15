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
// $Id: IInstallationDao.java,v 1.1.1.1 2006/10/23 13:55:37 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Installation;

/**
 * Installation DAO interface.
 */
public interface IInstallationDao
{
    /**
     * Adds installation to database.
     *
     * @param i installation to add.
     */
    void add(Installation i);

    /**
     * Removes installation from database.
     *
     * @param i installation to remove.
     */
    void delete(Installation i);

    /**
     * Updates installation information in database.
     *
     * @param i installation to update.
     */
    void update(Installation i);

    /**
     * Finds installation in database by ID.
     *
     * @param id ID of the installation.
     *
     * @return installation object or null.
     */
    Installation findById(long id);

    /**
     * Registers run in database.
     *
     * @param i installation which run to register.
     */
    void registerRun(Installation i);

    /**
     * Associates user (by ID) with some installation (by ID).
     *
     * @param userId            user ID.
     * @param installationId    installation ID.
     */
    void associateUserWithInstallation(int userId, long installationId);
}
