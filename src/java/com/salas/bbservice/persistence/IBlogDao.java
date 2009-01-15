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
// $Id: IBlogDao.java,v 1.3 2007/09/06 10:48:34 alg Exp $
//
package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Blog;

import java.util.List;

/**
 * Blog DAO interface.
 */
public interface IBlogDao
{
    /**
     * Adds object to database.
     *
     * @param o object to add.
     */
    void add(Blog o);

    /**
     * Update object information.
     *
     * @param o object to update.
     */
    void update(Blog o);

    /**
     * Deletes object from database.
     *
     * @param o object to delete.
     */
    void delete(Blog o);

    /**
     * Finds object by ID.
     *
     * @param id ID of the object.
     *
     * @return object or null.
     */
    Blog findById(int id);

    /**
     * Finds object by data URL.
     *
     * @param dataUrl   data URL.
     *
     * @return object or null.
     */
    Blog findByDataUrl(String dataUrl);

    /**
     * Deletes all blog records whose age is more than specified life span.
     *
     * @param lifeSpan maximum age of blog.
     * @param minBlogs minimum number of blogs to leave.
     */
    void deleteOld(long lifeSpan, int minBlogs);

    /**
     * Returns the blogs updated before the given time last time.
     *
     * @param time time.
     * @param max maximum number of blogs to return.
     *
     * @return blogs or NULL.
     */
    List<Blog> findBlogsUpdatedBefore(long time, int max);

    /**
     * Returns the blogs marked as incompleted discovery before the given time.
     *
     * @param time  time.
     * @param max maximum number of blogs to return.
     *
     * @return blogs or NULL.
     */
    List<Blog> findBlogsIncompletedBefore(long time, int max);

    /**
     * Returns the list of less recently used blogs.
     *
     * @param limit number of blogs records allowed in database.
     *
     * @return LRU blogs.
     */
    Blog[] getLRUBlogs(int limit);

    /**
     * Returns total number of records.
     *
     * @return total number of records.
     */
    public int getTotalRecords();
}
