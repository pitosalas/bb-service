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
// $Id: ConnectHandler.java,v 1.1 2007/07/04 08:49:27 alg Exp $
//

package com.salas.bbservice.service.connect;

import com.salas.bbservice.domain.Message;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.IMessageDao;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * BB Connect handler.
 */
public class ConnectHandler
{
    /**
     * Returns the list of messages for a given version plus latest messages for every version.
     *
     * @param version   version.
     * @param lastVID   last versioned message ID.
     * @param lastUID   last unversioned message ID.
     *
     * @return list of messages.
     */
    public Vector getMessages(String version, int lastVID, int lastUID)
    {
        IMessageDao dao = (IMessageDao)DaoConfig.getDao(IMessageDao.class);
        List<Message> messages = dao.getMessages(version, lastVID, lastUID);

        Vector<Hashtable> res = new Vector<Hashtable>(messages.size());
        for (Message message : messages) res.add(message.toHashtable());

        return res;
    }

}
