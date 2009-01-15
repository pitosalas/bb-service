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
// $Id: BlogCommunityFieldSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:38 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.BlogCommunityField;
import com.salas.bbservice.persistence.IBlogCommunityFieldDao;
import com.salas.bbservice.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * SqlMap implementation for Blog Community Field DAO.
 */
public class BlogCommunityFieldSqlMapDao extends SqlMapDaoTemplate implements IBlogCommunityFieldDao
{
    /**
     * Creates DAO.
     *
     * @param daoManager manager.
     */
    public BlogCommunityFieldSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Sets the value of community field in database removing previous values first.
     *
     * @param blogId ID of the blog to associate values with.
     * @param name   name of the field.
     * @param value  new field value.
     */
    public void set(int blogId, String name, String value)
    {
        set(blogId, name, new String[] { value });
    }

    /**
     * Sets the value of community field in database removing previous values first.
     *
     * @param blogId ID of the blog to associate values with.
     * @param name   name of the field.
     * @param values new field values.
     */
    public void set(int blogId, String name, String[] values)
    {
        daoManager.startTransaction();

        try
        {
            BlogCommunityField field;

            // Create stub field
            field = new BlogCommunityField(blogId, name, Constants.EMPTY_STRING);

            // Remove all field values
            delete("BlogCommunityField.delete", field);

            // Add new values
            ArrayList commited = new ArrayList(values.length);
            for (int i = 0; i < values.length; i++)
            {
                String value = values[i].trim();

                // Skip empty fields
                if (value.length() > 0 && !commited.contains(value))
                {
                    field.setValue(value);
                    insert("BlogCommunityField.insert", field);
                    commited.add(value);
                }
            }

            daoManager.commitTransaction();
        } finally
        {
            daoManager.endTransaction();
        }
    }

    /**
     * Selects all fields assigned with the Blog.
     *
     * @param blogId ID of the blog.
     *
     * @return fields with values.
     */
    public BlogCommunityField[] get(int blogId)
    {
        List allFields = queryForList("BlogCommunityField.selectFields", new Integer(blogId));

        return (BlogCommunityField[])allFields.toArray(new BlogCommunityField[allFields.size()]);
    }
}
