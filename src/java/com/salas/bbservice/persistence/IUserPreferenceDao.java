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
// $Id: IUserPreferenceDao.java,v 1.1.1.1 2006/10/23 13:55:37 alg Exp $
//
package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.UserPreference;

import java.util.List;

/**
 * User Preference DAO interface.
 */
public interface IUserPreferenceDao
{
    /**
     * Adds user preference to database.
     *
     * @param p preference to add.
     */
    void add(UserPreference p);

    /**
     * Deletes user preference from database.
     *
     * @param p preference to delete.
     */
    void delete(UserPreference p);

    /**
     * Updates user preference value in database.
     *
     * @param p preference to udpate.
     */
    void update(UserPreference p);

    /**
     * Selects all preferences assigned to the user specified by ID.
     *
     * @param userId ID of the user.
     *
     * @return list of associated preferences.
     */
    List selectByUserId(int userId);
}
