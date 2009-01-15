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
// $Id: StatsTopRatedParam.java,v 1.1.1.1 2006/10/23 13:55:36 alg Exp $
//

package com.salas.bbservice.domain.dao;

/**
 * Parameter object for top rated feeds statistics.
 */
public final class StatsTopRatedParam
{
    private int     limit;
    private double  subscriptionWeight;

    /**
     * Creates parameter object.
     *
     * @param aLimit                number of records to return.
     * @param aSubscriptionWeight   weight of single subscriptin in overal rating.
     */
    public StatsTopRatedParam(int aLimit, double aSubscriptionWeight)
    {
        limit = aLimit;
        subscriptionWeight = aSubscriptionWeight;
    }

    /**
     * Returns number of records to return.
     *
     * @return number of records to return.
     */
    public int getLimit()
    {
        return limit;
    }

    /**
     * Returns weight of single subscription in overal rating.
     *
     * @return weight of single subscription in overal rating. 
     */
    public double getSubscriptionWeight()
    {
        return subscriptionWeight;
    }
}
