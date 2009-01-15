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
// $Id: UserPreference.java,v 1.1.1.1 2006/10/23 13:55:34 alg Exp $
//

package com.salas.bbservice.domain;

/**
 * User preference holder.
 */
public class UserPreference
{
    private int         userId = -1;
    private String      name;
    private String      value;

    /**
     * Creates empty preference object.
     */
    public UserPreference()
    {
    }

    /**
     * Creates preference object.
     *
     * @param aUserId   id if user record in database.
     * @param aName     name of the preference.
     * @param aValue    value of the preference.
     */
    public UserPreference(int aUserId, String aName, String aValue)
    {
        userId = aUserId;
        name = aName;
        value = aValue;
    }

    /**
     * Returns ID of the user record.
     *
     * @return ID of the user record.
     */
    public int getUserId()
    {
        return userId;
    }

    /**
     * Sets ID of the user record.
     *
     * @param aUserId ID of the user record.
     */
    public void setUserId(int aUserId)
    {
        userId = aUserId;
    }

    /**
     * Returns name of the preference.
     *
     * @return name of the preference.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the preference.
     *
     * @param aName name of the preference.
     */
    public void setName(String aName)
    {
        name = aName;
    }

    /**
     * Returns the value of the preference.
     *
     * @return the value of the preference.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets new value of the preference.
     *
     * @param aValue the value of the preference.
     */
    public void setValue(String aValue)
    {
        value = aValue;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare.
     *
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof UserPreference)) return false;

        final UserPreference userPreference = (UserPreference)o;

        if (userId != userPreference.userId) return false;
        if (!name.equals(userPreference.name)) return false;
        if (value != null ? !value.equals(userPreference.value)
            : userPreference.value != null) return false;

        return true;
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
     *
     * @return  a hash code value for this object.
     */
    public int hashCode()
    {
        int result;
        result = userId;
        result = 29 * result + name.hashCode();
        return result;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "UserPreference: userId=" + userId +
                ", name=" + name +
                ", value=" + value;
    }
}
