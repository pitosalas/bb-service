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
// mailto:pitosalas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: BlogSqlMapDao.java,v 1.4 2007/09/14 18:36:29 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.persistence.IBlogDao;

import java.util.HashMap;
import java.util.List;

/**
 * SqlMap implementation for Blog DAO.
 */
public class BlogSqlMapDao extends SqlMapDaoTemplate implements IBlogDao
{
    private static final Blog[] EMPTY_BLOGS_ARRAY = new Blog[0];

    /**
     * Creates DAO.
     *
     * @param daoManager manager.
     */
    public BlogSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds object to database.
     *
     * @param o object to add.
     */
    public void add(Blog o)
    {
        if (findByDataUrl(o.getDataUrl()) != null)
        {
            throw new DaoException("Records with duplicate data URL's are not allowed.");
        }

        insert("Blog.insert", o);
    }

    /**
     * Update object information.
     *
     * @param o object to update.
     */
    public void update(Blog o)
    {
        update("Blog.update", o);
    }

    /**
     * Deletes object from database.
     *
     * @param o object to delete.
     */
    public void delete(Blog o)
    {
        delete("Blog.delete", o);
        o.setId(-1);
    }

    /**
     * Finds object by ID.
     *
     * @param id ID of the object.
     * @return object or null.
     */
    public Blog findById(int id)
    {
        return (Blog)queryForObject("Blog.findById", new Integer(id));
    }

    /**
     * Finds object by data URL.
     *
     * @param dataUrl data URL.
     * @return object or null.
     */
    public Blog findByDataUrl(String dataUrl)
    {
        return (Blog)queryForObject("Blog.findByDataUrl",
            dataUrl == null ? null : dataUrl.toLowerCase());
    }

    /**
     * Deletes all blog records whose age is more than specified life span.
     *
     * @param lifeSpan maximum age of blog.
     * @param minBlogs minimum number of blogs to leave.
     */
    public void deleteOld(long lifeSpan, int minBlogs)
    {
        long currentTime = System.currentTimeMillis();
        long timeThreshold = currentTime - lifeSpan;

        int total = getTotalRecords();
        int toRemove = (total > minBlogs) ? total - minBlogs : 0;
        if (toRemove > 0)
        {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("lastAccessTime", timeThreshold);
            hm.put("max", toRemove);

            delete("Blog.deleteOld", hm);
//            List outdatedBlogIds = queryForList("Blog.findAccessedBefore",
//                new Long(timeThreshold));
//
//            Blog blog = new Blog();
//            int listSize = outdatedBlogIds.size();
//            for (int i = 0; i < listSize && i < toRemove; i++)
//            {
//                blog.setId(((Integer)outdatedBlogIds.get(i)).intValue());
//                delete(blog);
//            }
        }
    }

    /**
     * Returns the blogs updated before the given time last time.
     *
     * @param time time.
     * @param max maximum number of blogs to return.
     *
     * @return blogs or NULL.
     */
    public List<Blog> findBlogsUpdatedBefore(long time, int max)
    {
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("lastUpdateTime", time);
        hm.put("max", max);

        return (List<Blog>)queryForList("Blog.findUpdatedBefore", hm);
    }

    /**
     * Returns the blogs marked as incompleted discovery before the given time.
     *
     * @param time time.
     * @param max maximum number of blogs to return.
     *
     * @return blogs or NULL.
     */
    public List<Blog> findBlogsIncompletedBefore(long time, int max)
    {
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("lastUpdateTime", time);
        hm.put("max", max);

        return (List<Blog>)queryForList("Blog.findIncompletedBefore", hm);
    }

    /**
     * Returns the list of less recently used blogs.
     *
     * @param limit number of blogs records allowed in database.
     *
     * @return LRU blogs.
     */
    public Blog[] getLRUBlogs(int limit)
    {
        Blog[] blogs = EMPTY_BLOGS_ARRAY;
        int total = getTotalRecords();
        int delta = total - limit;

        if (delta > 0)
        {
            List list = queryForList("Blog.selectLRU", delta);
            blogs = (Blog[])list.toArray(new Blog[list.size()]);
        }

        return blogs;
    }

    /**
     * Returns total number of records.
     *
     * @return total number of records.
     */
    public int getTotalRecords()
    {
        return (Integer)queryForObject("Blog.getTotalRecords", null);
    }
}
