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

package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.ReadingListInfo;
import com.salas.bbservice.domain.dao.LabelCountPercentage;
import com.salas.bbservice.domain.dao.ValueCount;
import com.salas.bbservice.persistence.IDataMiningDao;

import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Sql Map Data Mining reports DAO implementation.
 */
public class DataMiningSqlMapDao extends SqlMapDaoTemplate implements IDataMiningDao
{
    /**
     * Creates new DAO object.
     *
     * @param daoManager manager.
     */
    public DataMiningSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Returns top-used guide names.
     *
     * @param max maximum number of records to return.
     * @return top-used guide names.
     */
    public ValueCount[] getTopGuideNames(int max)
    {
        return queryForValueCountList("DataMining.getTopGuideName", new Integer(max));
    }

    /**
     * Returns the report on how many users having equal number of guides
     * sorted by number of user counts descending.
     *
     * @return report.
     */
    public LabelCountPercentage[] getUsersToGuides()
    {
        return LabelCountPercentage.consolidate(
            queryForValueCountList("DataMining.getUsersToGuides"));
    }

    /**
     * Returns the report on how many guides having equal number of feeds
     * sorted by number of guide counts descending.
     *
     * @return report.
     */
    public LabelCountPercentage[] getGuidesToFeeds()
    {
        return LabelCountPercentage.consolidate(
            ValueCount.groupValues(queryForValueCountList("DataMining.getGuidesToFeeds")));
    }

    /**
     * Queries for the list of value counds.
     *
     * @param query query ID.
     *
     * @return list of value counts.
     */
    private ValueCount[] queryForValueCountList(String query)
    {
        return queryForValueCountList(query, null);
    }

    /**
     * Queries for the list of value counds.
     *
     * @param query query ID.
     * @param param parameter.
     *
     * @return list of value counts.
     */
    private ValueCount[] queryForValueCountList(String query, Object param)
    {
        List list = queryForList(query, param);
        return list == null ? new ValueCount[0] : (ValueCount[])list.toArray(new ValueCount[list.size()]);
    }

    /**
     * Returns top visited reading lists.
     *
     * @param max maximum number of records to return.
     *
     * @return top-visited reading lists.
     */
    public ReadingListInfo[] getTopVisitedReadingLists(int max)
    {
        List listsL = queryForList("DataMining.getTopVisitedRLs", max);
        ReadingListInfo[] lists = new ReadingListInfo[listsL.size()];

        for (int i = 0; i < listsL.size(); i++)
        {
            Map map = (Map)listsL.get(i);

            ReadingListInfo info = new ReadingListInfo();
            info.userId = (Integer)map.get("userId");
            info.userFullName = (String)map.get("userFullName");
            info.userEmail = (String)map.get("userEmail");
            info.lastSyncTime = tsToDate((Timestamp)map.get("lastSyncTime"));

            Integer enabled = ((Integer)map.get("publishingEnabled"));
            info.active = enabled != null && enabled == 1;
            info.title = (String)map.get("title");
            info.totalVisits = ((Long)map.get("totalVisits")).intValue();
            info.uniqueVisits = ((Long)map.get("uniqueVisits")).intValue();

            // This is necessary for non-access counting when the total is reported >= 1 all
            // the time because of presence of at least one record. We have to adjust it
            // manually.
            if (info.uniqueVisits == 0) info.totalVisits = 0;

            lists[i] = info;
        }

        return lists;
    }

    /**
     * Converts timestamp to a date string.
     *
     * @param ts timestamp.
     *
     * @return date string.
     */
    private static String tsToDate(Timestamp ts)
    {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm").format(ts);
    }

    /**
     * Returns longest reading lists.
     *
     * @param max maximum number of records to return.
     *
     * @return longest reading lists.
     */
    public ReadingListInfo[] getLongestReadingLists(int max)
    {
        List listsL = queryForList("DataMining.getLongestRLs", new Integer(max));
        ReadingListInfo[] lists = new ReadingListInfo[listsL.size()];

        for (int i = 0; i < listsL.size(); i++)
        {
            Map map = (Map)listsL.get(i);

            ReadingListInfo info = new ReadingListInfo();

            info.userId = ((Integer)map.get("userId")).intValue();
            info.userFullName = (String)map.get("userFullName");
            info.userEmail = (String)map.get("userEmail");
            info.lastSyncTime = tsToDate((Timestamp)map.get("lastSyncTime"));

            Integer enabled = ((Integer)map.get("publishingEnabled"));
            info.active = enabled != null && enabled.intValue() == 1;
            info.title = (String)map.get("publishingTitle");
            info.feeds = ((Long)map.get("feeds")).intValue();

            lists[i] = info;
        }

        return lists;
    }
}
