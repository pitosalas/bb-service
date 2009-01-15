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
// $Id: InstallationSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:39 alg Exp $
//

package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.Installation;
import com.salas.bbservice.domain.dao.UserInstallationPair;
import com.salas.bbservice.persistence.IInstallationDao;

/**
 * Sql Map implementation of Installation DAO.
 */
public class InstallationSqlMapDao extends SqlMapDaoTemplate implements IInstallationDao
{
    /**
     * Creates DAO for installation.
     *
     * @param daoManager DAO manager.
     */
    public InstallationSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds installation to database.
     *
     * @param i installation to add.
     */
    public void add(Installation i)
    {
        insert("Installation.insert", i);
    }

    /**
     * Removes installation from database.
     *
     * @param i installation to remove.
     */
    public void delete(Installation i)
    {
        delete("Installation.delete", i);
        delete("Installation.deleteRuns", i);
    }

    /**
     * Updates installation information in database.
     *
     * @param i installation to update.
     */
    public void update(Installation i)
    {
        update("Installation.update", i);
    }

    /**
     * Finds installation in database by ID.
     *
     * @param id ID of the installation.
     *
     * @return installation object or null.
     */
    public Installation findById(long id)
    {
        return (Installation)queryForObject("Installation.findById", new Long(id));
    }

    /**
     * Registers run in database.
     *
     * @param i installation which run to register.
     */
    public void registerRun(Installation i)
    {
        insert("Installation.registerRun", i);
    }

    /**
     * Associates user (by ID) with some installation (by ID).
     *
     * @param userId         user ID.
     * @param installationId installation ID.
     */
    public void associateUserWithInstallation(int userId, long installationId)
    {
        update("Installation.associateWithUser",
            new UserInstallationPair(userId, installationId));
    }
}
