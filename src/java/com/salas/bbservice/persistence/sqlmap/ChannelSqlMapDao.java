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
// $Id: ChannelSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:38 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.Channel;
import com.salas.bbservice.persistence.IChannelDao;

import java.util.Map;
import java.util.HashMap;

/**
 * SqlMap implementation for Channel DAO.
 */
public class ChannelSqlMapDao extends SqlMapDaoTemplate implements IChannelDao
{
    /**
     * Creates DAO for Channel.
     *
     * @param daoManager manager.
     */
    public ChannelSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds channel to database.
     *
     * @param c channel to add.
     */
    public void add(Channel c)
    {
        if (findByXmlUrl(c.getXmlUrl()) != null)
        {
            throw new DaoException("Records with duplicate xmlURL are not allowed.");
        }

        insert("Channel.insert", c);
    }

    /**
     * Update channel information.
     *
     * @param c channel to update.
     */
    public void update(Channel c)
    {
        update("Channel.update", c);
    }

    /**
     * Deletes channel from database.
     *
     * @param c channel to delete.
     */
    public void delete(Channel c)
    {
        delete("Channel.delete", c);
        c.setId(-1);
    }

    /**
     * Finds channel by ID.
     *
     * @param id ID of the channel.
     * @return object or null.
     */
    public Channel findById(int id)
    {
        return (Channel)queryForObject("Channel.findById", new Integer(id));
    }

    /**
     * Finds channel by unique XML URL.
     *
     * @param xmlUrl XML URL.
     * @return object or null.
     */
    public Channel findByXmlUrl(String xmlUrl)
    {
        Map params = new HashMap(2);
        params.put("xmlUrl", xmlUrl);
        params.put("xmlUrlHash", xmlUrl == null ? new Integer(0) : new Integer(xmlUrl.hashCode()));
        return (Channel)queryForObject("Channel.findByXmlUrl", params);
    }
}
