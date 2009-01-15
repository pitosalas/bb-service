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
// $Id: StatsSqlMapDao.java,v 1.2 2007/01/17 12:51:29 alg Exp $
//

package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.persistence.IStatsDao;
import com.salas.bbservice.utils.Constants;
import com.salas.bbservice.utils.StringUtils;
import com.salas.bbservice.domain.dao.ValueCount;
import com.salas.bbservice.domain.dao.StatsTopRatedParam;
import com.salas.bbservice.domain.dao.DatesRange;
import com.salas.bbservice.domain.dao.ValueCountDate;
import com.salas.bbservice.domain.ReadingListInfo;

import java.util.*;

/**
 * Sql Map Stats DAO implementation.
 */
public class StatsSqlMapDao extends SqlMapDaoTemplate implements IStatsDao
{
    /**
     * Creates new DAO object.
     *
     * @param daoManager manager.
     */
    public StatsSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * <code>version</code> and <code>count</code> equal to number of version installations
     * in period selected by <code>period</code>.
     *
     * @param period  period (see <code>COUNT_PERIOD_XXXX</code> constants).
     * @param version version to fetch or null.
     *
     * @return list of counts.
     */
    public List getInstallationsCount(int period, String version)
    {
        String where;

        switch (period)
        {
            case COUNT_PERIOD_YEAR:
                where = "WHERE YEAR(InstallationDate) = YEAR(NOW())";
                break;
            case COUNT_PERIOD_MONTH:
                where = "WHERE YEAR(InstallationDate) = YEAR(NOW()) AND " +
                        "MONTH(InstallationDate) = MONTH(NOW())";
                break;
            case COUNT_PERIOD_WEEK:
                where = "WHERE YEAR(InstallationDate) = YEAR(NOW()) AND " +
                    "WEEK(InstallationDate, 1) = WEEK(NOW(), 1)";
                break;
            case COUNT_PERIOD_TODAY:
                where = "WHERE YEAR(InstallationDate) = YEAR(NOW()) AND " +
                    "DAYOFYEAR(InstallationDate) = DAYOFYEAR(NOW())";
                break;
            default:
                where = Constants.EMPTY_STRING;
                break;
        }

        // Append version limitation if any specified
        if (version != null)
        {
            if (!where.startsWith("WHERE"))
            {
                where = "WHERE ";
            } else
            {
                where += " AND ";
            }

            where += "Version = '" + version + "'";
        }

        return queryForList("Stats.getInstallationsCount", where);
    }

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * <code>version</code> and <code>count</code> equal to number of runs of this version
     * detected online in the period selected by <code>period</code>.
     *
     * @param period period (see <code>COUNT_PERIOD_XXXX</code> constants).
     * @param version version to get info for.
     *
     * @return list of counts.
     */
    public List getRunsPerVersion(int period, String version)
    {
        String where;

        switch (period)
        {
            case COUNT_PERIOD_YEAR:
                where = "WHERE YEAR(RunDate) = YEAR(NOW())";
                break;
            case COUNT_PERIOD_MONTH:
                where = "WHERE YEAR(RunDate) = YEAR(NOW()) AND MONTH(RunDate) = MONTH(NOW())";
                break;
            case COUNT_PERIOD_WEEK:
                where = "WHERE YEAR(RunDate) = YEAR(NOW()) AND WEEK(RunDate, 1) = WEEK(NOW(), 1)";
                break;
            case COUNT_PERIOD_TODAY:
                where = "WHERE YEAR(RunDate) = YEAR(NOW()) AND " +
                    "DAYOFYEAR(RunDate) = DAYOFYEAR(NOW())";
                break;
            default:
                where = "WHERE RunDate IS NOT NULL";
                break;
        }

        // Append version limitation if any specified
        if (version != null)
        {
            where += " AND Version = '" + version + "'";
        }

        return queryForList("Stats.getRunsCount", where);
    }

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * <code>version</code> and <code>count</code> equal to number of installations having
     * runs in specified range.
     *
     * @param from minimum runs.
     * @param to   maximum runs.
     * @return list of counts.
     */
    public List getInstallationsPerVersionDist(int from, int to)
    {
        final String where = "WHERE Runs BETWEEN " + from + " AND " + to;

        return queryForList("Stats.getInstVersionDistr", where);
    }

    /**
     * Returns average number of runs per day.
     *
     * @return average number of runs per day.
     */
    public double getAvgRunsPerDay()
    {
        Double res = (Double)queryForObject("Stats.avgRunsPerDay", null);

        return res == null ? -1 : res.doubleValue();
    }

    /**
     * Returns average number of runs per installation.
     *
     * @return average number of runs per installation.
     */
    public double getAvgRunsPerInstallation()
    {
        Double res = (Double)queryForObject("Stats.avgRunsPerInstallation", null);

        return res == null ? -1 : res.doubleValue();
    }

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * OS name and <code>count</code> equal to number of occurances.
     *
     * @return list of counts.
     */
    public List getOsUsage()
    {
        return queryForList("Stats.getOsUsage", null);
    }

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * JRE version and <code>count</code> equal to number of occurances.
     *
     * @return list of counts.
     */
    public List getJavaVersionUsage()
    {
        return queryForList("Stats.getJavaVersionUsage", null);
    }

    /**
     * Returns number of registered users.
     *
     * @return number of registered users.
     */
    public int getUsersCount()
    {
        Integer i = (Integer)queryForObject("Stats.getUsersCount", null);

        return i == null ? 0 : i.intValue();
    }

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * Locale and <code>count</code> equal to number of occurances.
     *
     * @return list of counts.
     */
    public List getLocaleStats()
    {
        return queryForList("Stats.getLocaleStats", null);
    }

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * channel title and <code>count</code> equal to number of references on it. Top-referenced
     * go first.
     *
     * @param max   maximum number of records.
     *
     * @return list of counts.
     */
    public List getTopReadChannels(int max)
    {
        return queryForList("Stats.getTopReadChannels", new Integer(max));
    }

    /**
     * Returns list of {@link com.salas.bbservice.domain.dao.StatsTopRatedResult} objects.
     * Top-rated go first.
     *
     * @param limit                 maximum number of records.
     * @param subscriptionWeight    rating points each subscription costs to feed.
     *
     * @return list of counts.
     */
    public List getTopRatedChannels(int limit, double subscriptionWeight)
    {
        StatsTopRatedParam param = new StatsTopRatedParam(limit, subscriptionWeight);
        return queryForList("Stats.getTopRatedChannelsRaw", param);
    }

    /**
     * Returns number of blogs registered.
     *
     * @param incompleteOnly count only blogs with incomplete discovery.
     *
     * @return blogs count.
     */
    public int getBlogsCount(boolean incompleteOnly)
    {
        String select = incompleteOnly ? "Stats.getIncompleteBlogsCount" : "Stats.getBlogsCount";
        Integer i = (Integer)queryForObject(select, null);

        return i == null ? 0 : i.intValue();
    }

    /**
     * Returns number of links registered.
     *
     * @param toBadBlogOnly count only links to bad blogs.
     *
     * @return blog links count.
     */
    public int getBlogLinksCount(boolean toBadBlogOnly)
    {
        String select = toBadBlogOnly ? "Stats.getBadBlogLinksCount" : "Stats.getBlogLinksCount";
        Integer i = (Integer)queryForObject(select, null);

        return i == null ? 0 : i.intValue();
    }

    /**
     * Returns list of latest versions in descending order.
     *
     * @param maxCount maximum number of versions in list.
     *
     * @return list of versions.
     */
    public List getLatestVersions(int maxCount)
    {
        return queryForList("Stats.getLatestVersions", new Integer(maxCount));
    }

    /**
     * Returns number of new users registrations in specified period.
     *
     * @param period period.
     *
     * @return count.
     */
    public int getRegistrationsCount(int period)
    {
        String where;

        switch (period)
        {
            case COUNT_PERIOD_YEAR:
                where = "WHERE YEAR(RegistrationDate) = YEAR(NOW())";
                break;
            case COUNT_PERIOD_MONTH:
                where = "WHERE YEAR(RegistrationDate) = YEAR(NOW()) AND " +
                        "MONTH(RegistrationDate) = MONTH(NOW())";
                break;
            case COUNT_PERIOD_WEEK:
                where = "WHERE YEAR(RegistrationDate) = YEAR(NOW()) AND " +
                    "WEEK(RegistrationDate, 1) = WEEK(NOW(), 1)";
                break;
            case COUNT_PERIOD_TODAY:
                where = "WHERE YEAR(RegistrationDate) = YEAR(NOW()) AND " +
                    "DAYOFYEAR(RegistrationDate) = DAYOFYEAR(NOW())";
                break;
            default:
                where = Constants.EMPTY_STRING;
                break;
        }

        Integer count = (Integer)queryForObject("Stats.getUsersCount", where);
        return count == null ? 0 : count.intValue();
    }

    /**
     * Returns list of most popular keywords.
     *
     * @return list of keywords with counts.
     */
    public SortedSet getKeywordsChart()
    {
        List lst = queryForList("Stats.getAllKeywords", null);

        return prepareKeywordsChart(lst);
    }

    /**
     * Returns number of installations in given dates range.
     *
     * @param range range of dates.
     *
     * @return number of installations.
     */
    public int getInstallationsInRange(DatesRange range)
    {
        Integer count = (Integer)queryForObject("Stats.getInstallationsInRange", range);
        return count == null ? 0 : count.intValue();
    }

    /**
     * Returns number of runs in given dates range.
     *
     * @param range range of dates.
     *
     * @return number of runs.
     */
    public int getRunsInRange(DatesRange range)
    {
        Integer count = (Integer)queryForObject("Stats.getRunsInRange", range);
        return count == null ? 0 : count.intValue();
    }

    /**
     * Returns number of fresh user registrations in given dates range.
     *
     * @param range range of dates.
     *
     * @return number of user registrations.
     */
    public int getRegistrationsInRange(DatesRange range)
    {
        Integer count = (Integer)queryForObject("Stats.getRegistrationsInRange", range);
        return count == null ? 0 : count.intValue();
    }

    /**
     * Returns the date of first installation in format 'YYYY-MM-DD'.
     *
     * @return date of first installation.
     */
    public String getFirstInstallationDate()
    {
        return (String)queryForObject("Stats.getFirstInstallationDate", null);
    }

    /**
     * Returns the list of <code>ValueCount</code> objects with names and subscriptions counts.
     *
     * @param aMax  maximum number of records.
     *
     * @return list of top subscribers.
     */
    public List<ValueCountDate> getTopSubscribers(int aMax)
    {
        return (List<ValueCountDate>)queryForList("Stats.getTopSubscribers", new Integer(aMax));
    }

    /**
     * Returns the list of <code>ValueCount</code> objects with names and synchronization counts.
     *
     * @param aMax maximum number of records.
     *
     * @return list of top synchronizers.
     */
    public List getTopSynchronizers(int aMax)
    {
        return (List)queryForList("Stats.getTopSynchronizers", new Integer(aMax));
    }

    /**
     * Returns the list of <code>ValueCount</code> objects with names and run counts.
     *
     * @param aMax maximum number of records.
     *
     * @return list of top runners.
     */
    public List<ValueCountDate> getTopRunners(int aMax)
    {
        return (List<ValueCountDate>)queryForList("Stats.getTopRunners", aMax);
    }

    /**
     * Returns the list of <code>ValueCount</code> objects with names and average syncs per day counts.
     *
     * @param aMax maximum number of records.
     *
     * @return list of top most frequent synchronizers.
     */
    public List<ValueCountDate> getTopActiveSynchronizers(int aMax)
    {
        return (List<ValueCountDate>)queryForList("Stats.selectTopActiveSynchronizers", new Integer(aMax));
    }

    /**
     * Returns the number of registrations during the last specified number of days.
     *
     * @param days number of days to display.
     *
     * @return date to number mapping.
     */
    public List getRegistrationTrends(int days)
    {
        return queryForList("Stats.getRegistrationTrends", new Integer(days));
    }

    /**
     * Returns number of different reading lists clients have visited.
     *
     * @param access    <code>TRUE</code> to count accessed RL's (visits table).
     *
     * @return number of different reading lists.
     */
    public int getReadingListsCount(boolean access)
    {
        String func = access ? "Stats.countVisitedReadingLists" : "Stats.countReadingLists";

        return ((Integer)queryForObject(func, null)).intValue();
    }

    /**
     * Returns the list of reading lists starting from the given offset.
     *
     * @param offset    first reading list to start from.
     * @param limit     max number of reading lists to return.
     *
     * @return the list of reading lists.
     */
    public ReadingListInfo[] getReadingListsInfo(int offset, int limit)
    {
        return getReadingListsStats(false, offset, limit);
    }

    /**
     * Returns the list of reading lists access records starting from the given offset.
     *
     * @param offset first reading list to start from.
     * @param limit  max number of reading lists to return.
     *
     * @return the list of reading lists.
     */
    public ReadingListInfo[] getReadingListsAccessInfo(int offset, int limit)
    {
        return getReadingListsStats(true, offset, limit);
    }

    /**
     * Returns the reading lists stats.
     *
     * @param access    <code>TRUE</code> if we wish to get access-oriented information.
     * @param offset    first reading list to start from.
     * @param limit     max number of reading lists.
     *
     * @return the list of reading lits.
     */
    private ReadingListInfo[] getReadingListsStats(boolean access, int offset, int limit)
    {
        int daysCount = 14;

        Map params = new HashMap();
        params.put("offset", new Integer(offset));
        params.put("limit", new Integer(limit));
        params.put("dayscount", new Integer(daysCount));
        String function = access ? "Stats.getReadingListsAccessInfo" : "Stats.getReadingListsInfo";
        List infos1 = queryForList(function, params);

        ReadingListInfo[] infos = new ReadingListInfo[infos1.size()];
        for (int i = 0; i < infos1.size(); i++)
        {
            Map map = (Map)infos1.get(i);

            ReadingListInfo info = new ReadingListInfo();
            info.userId = ((Integer)map.get("userId")).intValue();
            info.userFullName = (String)map.get("userFullName");
            info.userEmail = (String)map.get("userEmail");

            Integer enabled = ((Integer)map.get("publishingEnabled"));
            info.active = enabled != null && enabled.intValue() == 1;
            info.title = (String)map.get("title");
            info.totalVisits = ((Long)map.get("totalVisits")).intValue();
            info.uniqueVisits = ((Long)map.get("uniqueVisits")).intValue();

            // This is necessary for non-access counting when the total is reported >= 1 all
            // the time because of presence of at least one record. We have to adjust it
            // manually.
            if (info.uniqueVisits == 0) info.totalVisits = 0;

            // Getting number of feeds in the reading list
            params.put("userId", new Integer(info.userId));
            params.put("title", info.title);

            info.feeds = ((Integer)queryForObject("Stats.getFeedsInReadingList", params)).intValue();

            // Circulation
            info.lastWeekCirculation = new int[daysCount];

            if (info.totalVisits > 0)
            {
                List days2visits = queryForList("Stats.getReadListCirculation", params);
                for (int j = 0; j < days2visits.size(); j++)
                {
                    Map day2visits = (Map)days2visits.get(j);
                    int day = ((Long)day2visits.get("day")).intValue();
                    int visits = ((Long)day2visits.get("visits")).intValue();
                    info.lastWeekCirculation[daysCount - day - 1] = visits;
                }
            }
            infos[i] = info;
        }

        return infos;
    }

    /**
     * Clears all list-related stats.
     *
     * @param userId    user ID.
     * @param title     list title.
     */
    public void clearReadingListStats(int userId, String title)
    {
        Map params = new HashMap();
        params.put("userid", new Integer(userId));
        params.put("title", title);

        delete("Stats.clearReadingListStats", params);
    }

    /**
     * Prepares chart from data, extracted from database.
     *
     * @param keywordsList list of keyword.
     *
     * @return sorted set of keywords.
     */
    static SortedSet prepareKeywordsChart(List keywordsList)
    {
        SortedSet set = new TreeSet(new ValueCount.CVComparator(true));

        if (keywordsList != null)
        {
            Map keywordsMap = new HashMap();
            for (int i = 0; i < keywordsList.size(); i++)
            {
                String keywords = (String)keywordsList.get(i);
                putKeywordsInMap(keywords, keywordsMap);
            }

            set.addAll(keywordsMap.values());
        }

        return set;
    }

    /**
     * Puts keywords list in map or increments existing keywords counters.
     *
     * @param keywords  '|'-separated list of keywords.
     * @param map       map.
     */
    static void putKeywordsInMap(String keywords, Map map)
    {
        String[] keywordsList = StringUtils.keywordsToArray(keywords);

        if (keywordsList != null && keywordsList.length > 0)
        {
            List processed = new ArrayList(keywordsList.length);
            for (int i = 0; i < keywordsList.length; i++)
            {
                String keyword = keywordsList[i].trim().toLowerCase();
                if (!processed.contains(keyword))
                {
                    putKeywordInMap(keyword, map);
                    processed.add(keyword);
                }
            }
        }
    }

    /**
     * Puts single keyword in map or increments existing keywords counter.
     *
     * @param keyword   keyword.
     * @param map       map.
     */
    static void putKeywordInMap(String keyword, Map map)
    {
        if (keyword == null || map == null) return;

        ValueCount vc = (ValueCount)map.get(keyword);
        if (vc == null)
        {
            vc = new ValueCount(keyword, 1);
            map.put(keyword, vc);
        } else
        {
            vc.setCount(vc.getCount() + 1);
        }
    }
}
