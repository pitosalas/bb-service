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
// $Id: BlogCommunityField.java,v 1.1.1.1 2006/10/23 13:55:32 alg Exp $
//

package com.salas.bbservice.domain;

/**
 * Holder for community field information.
 */
public class BlogCommunityField
{
    private int         blogId;
    private String      name;
    private String      value;

    /**
     * Creates community field.
     */
    public BlogCommunityField()
    {
        this(-1, null, null);
    }

    /**
     * Creates community field associated to some blog.
     *
     * @param aBlogId   ID of blog record.
     * @param aName     name of field.
     * @param aValue    value of field.
     */
    public BlogCommunityField(int aBlogId, String aName, String aValue)
    {
        blogId = aBlogId;
        name = aName;
        value = aValue;
    }

    /**
     * Returns ID of associated blog record.
     *
     * @return ID of associated blog record.
     */
    public int getBlogId()
    {
        return blogId;
    }

    /**
     * Sets ID of associated blog record.
     *
     * @param aBlogId ID of associated blog record.
     */
    public void setBlogId(int aBlogId)
    {
        blogId = aBlogId;
    }

    /**
     * Returns name of the field.
     *
     * @return name of the field.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets name of the field.
     *
     * @param aName name of the field.
     */
    public void setName(String aName)
    {
        name = aName;
    }

    /**
     * Returns value of the field.
     *
     * @return value of the field.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets value of the field.
     *
     * @param aValue value of the field.
     */
    public void setValue(String aValue)
    {
        value = aValue;
    }

    /**
     * Checks if objects are equal.
     *
     * @param o object to compare with.
     *
     * @return result.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BlogCommunityField)) return false;

        final BlogCommunityField blogCommunityField = (BlogCommunityField)o;

        if (blogId != blogCommunityField.blogId) return false;
        if (!name.equals(blogCommunityField.name)) return false;
        if (!value.equals(blogCommunityField.value)) return false;

        return true;
    }

    /**
     * Returns hash code of the object.
     *
     * @return hash code of the object.
     */
    public int hashCode()
    {
        return blogId;
    }

    /**
     * Returns string representation.
     *
     * @return string representation.
     */
    public String toString()
    {
        return new StringBuffer().
            append("BlogCommunityField: name=").append(name).
            append(", value=").append(value).toString();
    }
}
