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
// $Id: PlansHandler.java,v 1.4 2007/01/26 12:47:31 alg Exp $
//

package com.salas.bbservice.plans;

import com.salas.bbservice.persistence.IPlansDao;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.domain.dao.UserPlan;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * Plans handler.
 */
public class PlansHandler
{
    private final IPlansDao dao = getDAO();

    /**
     * Returns the DAO.
     *
     * @return DAO.
     */
    protected IPlansDao getDAO()
    {
        return (IPlansDao)DaoConfig.getDao(IPlansDao.class);
    }

    /**
     * Returns the plan setup hash.
     *
     * @param email     email of the account.
     * @param password  password of the account.
     *
     * @return the hash value.
     */
    public String getHash(String email, String password)
    {
        return dao.getHash(email, password);
    }

    /**
     * Returns the map of all features available for the plan and
     * the plan details.
     *
     * @param email     email of the account.
     * @param password  password of the account.
     *
     * @return the map.
     */
    public Map<String, String> getFeatures(String email, String password)
    {
        UserPlan up = dao.getUserPlan(email, password);
        if (up == null) up = dao.getUserPlan(null, null);

        Map<String, String> map = dao.getFeatures(up.getPlan_id());

        map.put("_name", up.getPlan_name());
        map.put("_exp_date", up.getPlan_exp_date() != null ? Long.toString(up.getPlan_exp_date().getTime()) : "0");
        map.put("_price", Float.toString(up.getPlan_price()));
        map.put("_period_months", Integer.toString(up.getPlan_period_months()));
        map.put("_is_trial", up.isPlan_is_trial() ? "1" : "0");

        return map;
    }
}
