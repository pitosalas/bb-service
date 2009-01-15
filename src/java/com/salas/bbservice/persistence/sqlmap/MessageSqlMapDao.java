// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2007 by R. Pito Salas
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
// $Id: MessageSqlMapDao.java,v 1.1 2007/07/04 08:49:27 alg Exp $
//

package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.Message;
import com.salas.bbservice.persistence.IMessageDao;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Message DAO.
 */
public class MessageSqlMapDao extends SqlMapDaoTemplate implements IMessageDao
{
    /**
     * Creates new DAO object.
     *
     * @param daoManager manager.
     */
    public MessageSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Returns the list of messages since the given last message IDs.
     *
     * @param version version of application to get messages for.
     * @param lastVID last versioned message ID.
     * @param lastUID last unversioned message ID.
     *
     * @return list of messages.
     */
    public List<Message> getMessages(String version, int lastVID, int lastUID)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("version", version);
        params.put("lastVID", lastVID);
        params.put("lastUID", lastUID);

        List msgs = queryForList("Message.find", params);
        List<Message> messages = new LinkedList<Message>();
        for (Object m : msgs) messages.add((Message)m);

        return messages;
    }
}
