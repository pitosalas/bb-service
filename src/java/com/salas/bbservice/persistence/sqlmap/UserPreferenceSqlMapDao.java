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
// $Id: UserPreferenceSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:39 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.UserPreference;
import com.salas.bbservice.persistence.IUserPreferenceDao;

import java.util.List;

/**
 * SqlMap implementation for User Preference DAO.
 */
public class UserPreferenceSqlMapDao extends SqlMapDaoTemplate implements IUserPreferenceDao
{
    /**
     * Creates DAO for User Preference.
     *
     * @param daoManager manager.
     */
    public UserPreferenceSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds user preference to database.
     *
     * @param p preference to add.
     */
    public void add(UserPreference p)
    {
        insert("UserPreference.insert", p);
    }

    /**
     * Deletes user preference from database.
     *
     * @param p preference to delete.
     */
    public void delete(UserPreference p)
    {
        delete("UserPreference.delete", p);
    }

    /**
     * Updates user preference value in database.
     *
     * @param p preference to udpate.
     */
    public void update(UserPreference p)
    {
        update("UserPreference.update", p);
    }

    /**
     * Selects all preferences assigned to the user specified by ID.
     *
     * @param userId ID of the user.
     *
     * @return list of associated preferences.
     */
    public List selectByUserId(int userId)
    {
        return queryForList("UserPreference.selectByUserId", new Integer(userId));
    }
}
