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
// $Id $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.dao.ValueCount;
import com.salas.bbservice.domain.dao.LabelCountPercentage;
import com.salas.bbservice.domain.ReadingListInfo;

/**
 * Data mining reports DAO.
 */
public interface IDataMiningDao
{
    /**
     * Returns top-used guide names.
     *
     * @param max   maximum number of records to return.
     *
     * @return top-used guide names.
     */
    ValueCount[] getTopGuideNames(int max);

    /**
     * Returns the report on how many users having equal number of guides
     * sorted by number of user counts descending.
     *
     * @return report.
     */
    LabelCountPercentage[] getUsersToGuides();

    /**
     * Returns the report on how many guides having equal number of feeds
     * sorted by number of guide counts descending.
     *
     * @return report.
     */
    LabelCountPercentage[] getGuidesToFeeds();

    /**
     * Returns top visited reading lists.
     *
     * @param max   maximum number of records to return.
     *
     * @return top-visited reading lists.
     */
    ReadingListInfo[] getTopVisitedReadingLists(int max);

    /**
     * Returns longest reading lists.
     *
     * @param max   maximum number of records to return.
     *
     * @return longest reading lists.
     */
    ReadingListInfo[] getLongestReadingLists(int max);
}
