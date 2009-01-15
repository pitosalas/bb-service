// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2006 by R. Pito Salas
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
// $Id: IPlansDao.java,v 1.1 2007/01/24 09:41:31 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.dao.UserPlan;

import java.util.Map;

/**
 * Plans data access interface.
 */
public interface IPlansDao
{
    /**
     * Returns the plan setup hash.
     *
     * @param email     email of the account.
     * @param password  password of the account.
     *
     * @return the hash value.
     */
    String getHash(String email, String password);

    /**
     * Returns info on the plan the user is assigned to.
     *
     * @param email     email of the account.
     * @param password  password of the account.
     *
     * @return the info on user plan.
     */
    UserPlan getUserPlan(String email, String password);

    /**
     * Returns the map of all features available for the plan.
     *
     * @param planId    ID of the plan to learn features for.
     *
     * @return the map.
     */
    Map<String, String> getFeatures(int planId);
}
