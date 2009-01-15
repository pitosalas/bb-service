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
// $Id: ForumSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:39 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.persistence.IForumDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlMap implementation for Forum DAO.
 */
public class ForumSqlMapDao extends SqlMapDaoTemplate implements IForumDao
{
    /**
     * Creates DAO.
     *
     * @param daoManager manager.
     */
    public ForumSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Returns the list of Forum objects describing the available forums.
     *
     * @return the list of Forum objects.
     */
    public List listForums()
    {
        return queryForList("Forum.list", null);
    }

    /**
     * Return <code>TRUE</code> if the specified forum exists.
     *
     * @param aForumId ID of the forum.
     *
     * @return <code>TRUE</code> if the specified forum exists.
     */
    public boolean forumExists(int aForumId)
    {
        return queryForList("Forum.list", new Integer(aForumId)).size() > 0;
    }
}
