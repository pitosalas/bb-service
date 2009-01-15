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
// $Id: Forum.java,v 1.1.1.1 2006/10/23 13:55:33 alg Exp $
//

package com.salas.bbservice.domain;

/**
 * Forum record.
 */
public class Forum
{
    private int     id;
    private String  name;

    /**
     * Empty constructor.
     */
    public Forum()
    {
    }

    /**
     * Creates forum record.
     *
     * @param aId   ID of the forum.
     * @param aName name of the forum.
     */
    public Forum(int aId, String aName)
    {
        id = aId;
        name = aName;
    }

    /**
     * Gets forum id.
     *
     * @return id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets forum id.
     *
     * @param aId forum id.
     */
    public void setId(int aId)
    {
        id = aId;
    }

    /**
     * Gets forum name.
     *
     * @return forum name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets forum name.
     *
     * @param aName forum name.
     */
    public void setName(String aName)
    {
        name = aName;
    }

    /**
     * Compares two objects.
     *
     * @param o another object.
     *
     * @return <code>TRUE</code> if equal.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Forum forum = (Forum)o;

        if (id != forum.id) return false;
        if (!name.equals(forum.name)) return false;

        return true;
    }

    /**
     * Returns the hash code.
     *
     * @return hash code.
     */
    public int hashCode()
    {
        return id;
    }
}
