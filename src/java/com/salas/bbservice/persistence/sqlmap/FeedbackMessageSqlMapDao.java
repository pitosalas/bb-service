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
// $Id: FeedbackMessageSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:38 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.FeedbackMessage;
import com.salas.bbservice.persistence.IFeedbackMessageDao;

import java.util.List;

/**
 * SqlMap implementation for FeedbackMessage DAO.
 */
public class FeedbackMessageSqlMapDao extends SqlMapDaoTemplate implements IFeedbackMessageDao
{
    /**
     * Creates DAO.
     *
     * @param daoManager manager.
     */
    public FeedbackMessageSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds object to database.
     *
     * @param o object to add.
     */
    public void add(FeedbackMessage o)
    {
        insert("FeedbackMessage.insert", o);
    }

    /**
     * Deletes object from database.
     *
     * @param o object to delete.
     */
    public void delete(FeedbackMessage o)
    {
        delete("FeedbackMessage.delete", o);
        o.setId(-1);
    }

    /**
     * Deletes all objects from database.
     */
    public void deleteAll()
    {
        delete("FeedbackMessage.deleteAll", null);
    }

    /**
     * Selects messages sorted in descending time order.
     *
     * @param offset start from.
     * @param limit  maximum records.
     *
     * @return list of messages.
     */
    public FeedbackMessage[] select(int offset, int limit)
    {
        List list = queryForList("FeedbackMessage.select", "LIMIT " + offset + ", " + limit);

        return (FeedbackMessage[])list.toArray(new FeedbackMessage[list.size()]);
    }

    /**
     * Returns number of messages records.
     *
     * @return count.
     */
    public int getMessagesCount()
    {
        return ((Integer)queryForObject("FeedbackMessage.count", null)).intValue();
    }

    /**
     * Finds message by its id.
     *
     * @param id id of the message.
     *
     * @return message or NULL.
     */
    public FeedbackMessage findById(int id)
    {
        return (FeedbackMessage)queryForObject("FeedbackMessage.findById", new Integer(id));
    }
}
