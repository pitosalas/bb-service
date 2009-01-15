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
// $Id: UserQueryFeedSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:40 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.UserChannel;
import com.salas.bbservice.domain.UserQueryFeed;
import com.salas.bbservice.persistence.IUserChannelDao;
import com.salas.bbservice.persistence.IUserQueryFeedDao;

import java.util.List;

/**
 * SqlMap implementation for User Query Feed DAO.
 */
public class UserQueryFeedSqlMapDao extends SqlMapDaoTemplate implements IUserQueryFeedDao
{
    /**
     * Creates DAO for User Query Feed.
     *
     * @param daoManager manager.
     */
    public UserQueryFeedSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds user feed to database.
     *
     * @param f feed to add.
     */
    public void add(UserQueryFeed f)
    {
        insert("UserQueryFeed.insert", f);
    }

    /**
     * Deletes user feed from database.
     *
     * @param f feed to delete.
     */
    public void delete(UserQueryFeed f)
    {
        delete("UserQueryFeed.delete", f);
        f.setId(-1);
    }

    /**
     * Finds all user feeds by user guide ID. List is always sorted by index in ascending order.
     *
     * @param userGuideId ID of user guide.
     *
     * @return list of feeds.
     */
    public List findByUserGuideId(int userGuideId)
    {
        return queryForList("UserQueryFeed.findByUserGuideId",
            new Integer(userGuideId));
    }
}
