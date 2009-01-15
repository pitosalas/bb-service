// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2006 by R. Pito Salas
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
// $Id: ReadingListInfo.java,v 1.2 2007/01/17 12:51:29 alg Exp $
//

package com.salas.bbservice.domain;

import java.util.Date;

/**
 * Reading list statistical information.
 */
public class ReadingListInfo
{
    // Author Info ---------------------------------------------------------------------------------

    public int      userId;
    public String   userFullName;
    public String   userEmail;
    public String   lastSyncTime;     // the date when the author last accessed the service

    // List Info -----------------------------------------------------------------------------------

    public String   title;
    public boolean  active;
    public int      feeds;

    // Visits statistics ---------------------------------------------------------------------------

    public int      totalVisits;
    public int      uniqueVisits;
    public int[]    lastWeekCirculation;

    /**
     * Normalizes the values using some base (number of steps max).
     * For example, if the base=10 then the resulting array will be proportionally
     * filled with values in range [0;10].
     *
     * @param values list of original values.
     * @param base  normalization base.
     *
     * @return normalized values.
     */
    public static int[] normalize(int[] values, int base)
    {
        // Find maximum
        int max = 0;
        for (int i = 0; i < values.length; i++)
            if (max < values[i]) max = values[i];

        int[] normalized = new int[values.length];
        for (int i = 0; i < values.length; i++)
        {
            int cnt = values[i];
            normalized[i] = (int)Math.ceil((float)base * cnt / max);
        }

        return normalized;
    }
}
