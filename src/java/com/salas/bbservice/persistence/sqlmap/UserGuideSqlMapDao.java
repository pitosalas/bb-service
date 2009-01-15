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
// $Id: UserGuideSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:39 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.UserGuide;
import com.salas.bbservice.persistence.IUserGuideDao;

import java.util.List;

/**
 * SqlMap implementation for User Guide DAO.
 */
public class UserGuideSqlMapDao extends SqlMapDaoTemplate implements IUserGuideDao
{
    /**
     * Creates DAO for User Guide.
     *
     * @param daoManager manager.
     */
    public UserGuideSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds user guide to database.
     *
     * @param g guide to add.
     */
    public void add(UserGuide g)
    {
        insert("UserGuide.insert", g);
    }

    /**
     * Deletes user guide from database.
     *
     * @param g guide to delete.
     */
    public void delete(UserGuide g)
    {
        delete("UserGuide.delete", g);
        g.setId(-1);
    }

    /**
     * Finds guide by ID.
     *
     * @param id ID of the user guide.
     * @return object or null.
     */
    public UserGuide findById(int id)
    {
        return (UserGuide)queryForObject("UserGuide.findById", new Integer(id));
    }

    /**
     * Returns list of user's guides. List is always sorted by index in ascending order.
     *
     * @param userId ID of user.
     *
     * @return list of guides.
     */
    public List findByUserId(int userId)
    {
        return queryForList("UserGuide.findByUserId", new Integer(userId));
    }
}
