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
// $Id: PlansSqlMapDao.java,v 1.3 2007/01/25 16:58:45 alg Exp $
//

package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.dao.UserPlan;
import com.salas.bbservice.domain.dao.KeyValue;
import com.salas.bbservice.persistence.IPlansDao;
import com.salas.bbservice.utils.StringUtils;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class PlansSqlMapDao extends SqlMapDaoTemplate implements IPlansDao
{
    /**
     * Creates new DAO object.
     *
     * @param daoManager manager.
     */
    public PlansSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Returns the plan setup hash.
     *
     * @param email    email of the account.
     * @param password password of the account.
     *
     * @return the hash value.
     */
    public String getHash(String email, String password)
    {
        String hash;

        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password))
        {
            hash = (String)queryForObject("Plan.hash_no_user", null);
        } else
        {
            Map<String, String> m = new HashMap<String, String>();
            m.put("email", email);
            m.put("password", password);
            hash = (String)queryForObject("Plan.hash", m);

            // Re-query the hash for non-existing user if
            // the given email/pass aren't registered
            if (hash == null) hash = getHash(null, null);
        }

        return hash;
    }

    /**
     * Returns info on the plan the user is assigned to.
     *
     * @param email     email of the account.
     * @param password  password of the account.
     *
     * @return the info on user plan.
     */
    public UserPlan getUserPlan(String email, String password)
    {
        Map<String, String> m = null;
        String method = "Plan.userPlan_no_user";

        if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password))
        {
            method = "Plan.up";

            m = new HashMap<String, String>();
            m.put("email", email);
            m.put("password", password);
        }

        return (UserPlan)queryForObject(method, m);
    }

    /**
     * Returns the map of all features available for the plan.
     *
     * @param planId ID of the plan to learn features for.
     *
     * @return the map.
     */
    public Map<String, String> getFeatures(int planId)
    {
        Map<String, String> features = new HashMap<String, String>();

        List<KeyValue> fl = (List<KeyValue>)queryForList("Plan.features", planId);
        for (KeyValue kv : fl) features.put(kv.getKey(), kv.getValue());

        return features;
    }
}
