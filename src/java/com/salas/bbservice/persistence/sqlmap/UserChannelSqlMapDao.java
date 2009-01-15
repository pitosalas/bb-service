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
// $Id: UserChannelSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:39 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.UserChannel;
import com.salas.bbservice.persistence.IUserChannelDao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * SqlMap implementation for User Channel DAO.
 */
public class UserChannelSqlMapDao extends SqlMapDaoTemplate implements IUserChannelDao
{
    /**
     * Creates DAO for User Channel.
     *
     * @param daoManager manager.
     */
    public UserChannelSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds user channel to database.
     *
     * @param c channel to add.
     */
    public void add(UserChannel c)
    {
        insert("UserChannel.insert", c);
    }

    /**
     * Deletes user channel from database.
     *
     * @param c channel to delete.
     */
    public void delete(UserChannel c)
    {
        delete("UserChannel.delete", c);
        c.setId(-1);
    }

    /**
     * Finds channel by ID.
     *
     * @param id ID of the user channel.
     * @return object or null.
     */
    public UserChannel findById(int id)
    {
        return (UserChannel)queryForObject("UserChannel.findById", new Integer(id));
    }

    /**
     * Finds all user channels by user guide ID. List is always sorted by index in ascending order.
     *
     * @param userGuideId       ID of user guide.
     * @param userReadingListId ID of user reading or <code>NULL</code> for unassigned feeds.
     *
     * @return list of channels.
     */
    public List select(int userGuideId, Integer userReadingListId)
    {
        Map parameters = new HashMap();
        parameters.put("guideId", new Integer(userGuideId));
        parameters.put("readingListId", userReadingListId);

        return queryForList("UserChannel.select", parameters);
    }
}
