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
// $Id: IMessageDao.java,v 1.1 2007/07/04 08:49:27 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Message;

import java.util.List;

/**
 * Messages DAO.
 */
public interface IMessageDao
{
    /**
     * Returns the list of messages since the given last message IDs.
     *
     * @param version   version of application to get messages for.
     * @param lastVID   last versioned message ID.
     * @param lastUID   last unversioned message ID.
     *
     * @return list of messages.
     */
    List<Message> getMessages(String version, int lastVID, int lastUID);
}
