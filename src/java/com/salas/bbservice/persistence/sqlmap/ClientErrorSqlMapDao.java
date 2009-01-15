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
// $Id: ClientErrorSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:38 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.ClientError;
import com.salas.bbservice.persistence.IBlogDao;
import com.salas.bbservice.persistence.IClientErrorDao;

import java.util.List;

/**
 * SqlMap implementation for ClientError DAO.
 */
public class ClientErrorSqlMapDao extends SqlMapDaoTemplate implements IClientErrorDao
{
    /**
     * Creates DAO.
     *
     * @param daoManager manager.
     */
    public ClientErrorSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds object to database.
     *
     * @param o object to add.
     */
    public void add(ClientError o)
    {
        insert("ClientError.insert", o);
    }

    /**
     * Deletes object from database.
     *
     * @param o object to delete.
     */
    public void delete(ClientError o)
    {
        delete("ClientError.delete", o);
        o.setId(-1);
    }

    /**
     * Deletes all objects from database.
     */
    public void deleteAll()
    {
        delete("ClientError.deleteAll", null);
    }

    /**
     * Selects errors sorted in descending time order.
     *
     * @param offset start from.
     * @param limit  maximum records.
     *
     * @return list of errors.
     */
    public ClientError[] select(int offset, int limit)
    {
        List list = queryForList("ClientError.select", "LIMIT " + offset + ", " + limit);

        return (ClientError[])list.toArray(new ClientError[list.size()]);
    }

    /**
     * Finds error record by its id.
     *
     * @param id id of record.
     *
     * @return record or null.
     */
    public ClientError findById(int id)
    {
        return (ClientError)queryForObject("ClientError.findById", new Integer(id));
    }

    /**
     * Returns number of error records.
     *
     * @return count.
     */
    public int getErrorsCount()
    {
        return ((Integer)queryForObject("ClientError.count", null)).intValue();
    }
}
