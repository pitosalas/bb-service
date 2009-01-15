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
// $Id: DaoConfig.java,v 1.2 2007/01/24 09:41:31 alg Exp $
//
package com.salas.bbservice.persistence;

import com.ibatis.common.resources.Resources;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.DaoManagerBuilder;

import java.io.Reader;

/**
 * Persistence configuration. Use it to get DAO manager.
 */
public final class DaoConfig
{
    private static final DaoManager DAO_MANAGER;

    static
    {
        try
        {
            String resource = "com/salas/bbservice/persistence/dao.xml";
            Reader reader = Resources.getResourceAsReader(resource);
            DAO_MANAGER = DaoManagerBuilder.buildDaoManager(reader);
        } catch (Exception e)
        {
            throw new RuntimeException("Could not initialize DaoConfig.  Cause: " + e);
        }
    }

    /**
     * Hidden constructor of utility class.
     */
    private DaoConfig()
    {
    }

    /**
     * Returns DAO manager for current application.
     *
     * @return DAO manager.
     */
    public static DaoManager getDaoManager()
    {
        return DAO_MANAGER;
    }

    /**
     * Returns DAO corresponding to DAO interface.
     *
     * @param aClass class of DAO.
     *
     * @return DAO object.
     */
    public static Object getDao(Class aClass)
    {
        return getDaoManager().getDao(aClass);
    }

    /**
     * Returns plans dao.
     *
     * @return dao.
     */
    public static IPlansDao getPlansDao()
    {
        return (IPlansDao)getDao(IPlansDao.class);
    }
}
