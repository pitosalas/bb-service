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
// $Id: IUserChannelDao.java,v 1.1.1.1 2006/10/23 13:55:37 alg Exp $
//
package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.UserChannel;

import java.util.List;

/**
 * User Channel DAO interface.
 */
public interface IUserChannelDao
{
    /**
     * Adds user channel to database.
     *
     * @param c channel to add.
     */
    void add(UserChannel c);

    /**
     * Deletes user channel from database.
     *
     * @param c channel to delete.
     */
    void delete(UserChannel c);

    /**
     * Finds channel by ID.
     *
     * @param id ID of the user channel.
     *
     * @return object or null.
     */
    UserChannel findById(int id);

    /**
     * Finds all user channels by user guide ID. List is always sorted by index in ascending order.
     *
     * @param userGuideId       ID of user guide.
     * @param userReadingListId ID of user reading or <code>NULL</code> for unassigned feeds.
     *
     * @return list of channels.
     */
    List select(int userGuideId, Integer userReadingListId);
}
