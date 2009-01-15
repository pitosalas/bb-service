// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2007 by R. Pito Salas
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
// $Id: UserDataFeed.java,v 1.1 2007/04/30 14:47:26 alg Exp $
//

package com.salas.bbservice.domain;

/**
 * Abstract class for data feed properties.
 */
public abstract class UserDataFeed
{
    private long updatePeriod = -1;

    /**
     * Returns the update period.
     *
     * @return period.
     */
    public long getUpdatePeriod()
    {
        return updatePeriod;
    }

    /**
     * Sets the update period.
     *
     * @param updatePeriod period.
     */
    public void setUpdatePeriod(long updatePeriod)
    {
        this.updatePeriod = updatePeriod;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDataFeed that = (UserDataFeed)o;

        if (updatePeriod != that.updatePeriod) return false;

        return true;
    }

    @Override
    public String toString()
    {
        return ", updatePeriod=" + updatePeriod;
    }
}
