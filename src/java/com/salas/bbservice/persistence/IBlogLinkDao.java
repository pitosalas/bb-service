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
// $Id: IBlogLinkDao.java,v 1.1.1.1 2006/10/23 13:55:37 alg Exp $
//
package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.BlogLink;

/**
 * Blog DAO interface.
 */
public interface IBlogLinkDao
{
    /**
     * Adds object to database.
     *
     * @param o object to add.
     */
    void add(BlogLink o);

    /**
     * Updates object in database.
     *
     * @param o object to update.
     */
    void update(BlogLink o);

    /**
     * Deletes object from database.
     *
     * @param o object to delete.
     */
    void delete(BlogLink o);

    /**
     * Removes all unfinished reservations.
     */
    void deleteUnfinished();

    /**
     * Finds object by ID.
     *
     * @param id ID of the object.
     *
     * @return object or null.
     */
    BlogLink findById(int id);

    /**
     * Finds object by ID.
     *
     * @param url   URL.
     *
     * @return object or null.
     */
    BlogLink findByUrl(String url);
}
