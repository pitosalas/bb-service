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
// $Id: IChannelDao.java,v 1.1.1.1 2006/10/23 13:55:37 alg Exp $
//
package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Channel;

/**
 * Channel DAO interface.
 */
public interface IChannelDao
{
    /**
     * Adds channel to database.
     *
     * @param c channel to add.
     */
    void add(Channel c);

    /**
     * Update channel information.
     *
     * @param c channel to update.
     */
    void update(Channel c);

    /**
     * Deletes channel from database.
     *
     * @param c channel to delete.
     */
    void delete(Channel c);

    /**
     * Finds channel by ID.
     *
     * @param id ID of the channel.
     *
     * @return object or null.
     */
    Channel findById(int id);

    /**
     * Finds channel by unique XML URL.
     *
     * @param xmlUrl XML URL.
     *
     * @return object or null.
     */
    Channel findByXmlUrl(String xmlUrl);
}
