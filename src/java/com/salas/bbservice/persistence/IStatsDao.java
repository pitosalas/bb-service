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
// $Id: IStatsDao.java,v 1.2 2007/01/17 12:51:29 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.ReadingListInfo;
import com.salas.bbservice.domain.dao.DatesRange;
import com.salas.bbservice.domain.dao.ValueCountDate;

import java.util.List;
import java.util.SortedSet;

/**
 * Statistics DAO.
 */
public interface IStatsDao
{
    /** Whole period. */
    int COUNT_PERIOD_EVER = 0;

    /** Only current year. */
    int COUNT_PERIOD_YEAR = 1;

    /** Only current month. */
    int COUNT_PERIOD_MONTH = 2;

    /** Only current week. */
    int COUNT_PERIOD_WEEK = 3;

    /** Only today. */
    int COUNT_PERIOD_TODAY = 4;


    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * <code>version</code> and <code>count</code> equal to number of version installations
     * in period selected by <code>period</code>.
     *
     * @param period period (see <code>COUNT_PERIOD_XXXX</code> constants).
     * @param version version to fetch or null.
     *
     * @return list of counts.
     */
    List getInstallationsCount(int period, String version);

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
    List getRunsPerVersion(int period, String version);

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * <code>version</code> and <code>count</code> equal to number of installations having
     * runs in specified range.
     *
     * @param from  minimum runs.
     * @param to    maximum runs.
     *
     * @return list of counts.
     */
    List getInstallationsPerVersionDist(int from, int to);

    /**
     * Returns average number of runs per day.
     *
     * @return average number of runs per day.
     */
    double getAvgRunsPerDay();

    /**
     * Returns average number of runs per installation.
     *
     * @return average number of runs per installation.
     */
    double getAvgRunsPerInstallation();

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * OS name and <code>count</code> equal to number of occurances.
     *
     * @return list of counts.
     */
    List getOsUsage();

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * JRE version and <code>count</code> equal to number of occurances.
     *
     * @return list of counts.
     */
    List getJavaVersionUsage();

    /**
     * Returns number of registered users.
     *
     * @return number of registered users.
     */
    int getUsersCount();

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * Locale and <code>count</code> equal to number of occurances.
     *
     * @return list of counts.
     */
    List getLocaleStats();

    /**
     * Returns list of <code>ValueCount</code> objects having <code>value</code> equal to
     * channel title and <code>count</code> equal to number of references on it. Top-referenced
     * go first.
     *
     * @param max   maximum number of records.
     *
     * @return list of counts.
     */
    List getTopReadChannels(int max);

    /**
     * Returns list of {@link com.salas.bbservice.domain.dao.StatsTopRatedResult} objects.
     * Top-rated go first.
     *
     * @param limit                 maximum number of records.
     * @param subscriptionWeight    rating points each subscription costs to feed.
     *
     * @return list of counts.
     */
    List getTopRatedChannels(int limit, double subscriptionWeight);

    /**
     * Returns number of blogs registered.
     *
     * @param incompleteOnly    count only blogs with incomplete discovery.
     *
     * @return blogs count.
     */
    int getBlogsCount(boolean incompleteOnly);

    /**
     * Returns number of links registered.
     *
     * @param toBadBlogOnly     count only links to bad blogs.
     *
     * @return blog links count.
     */
    int getBlogLinksCount(boolean toBadBlogOnly);

    /**
     * Returns list of latest versions in descending order.
     *
     * @param maxCount maximum number of versions in list.
     *
     * @return list of versions.
     */
    List getLatestVersions(int maxCount);

    /**
     * Returns number of new users registrations in specified period.
     *
     * @param period period.
     *
     * @return count.
     */
    int getRegistrationsCount(int period);

    /**
     * Returns list of most popular keywords.
     *
     * @return list of keywords with counts.
     */
    SortedSet getKeywordsChart();

    /**
     * Returns number of installations in given dates range.
     *
     * @param range range of dates.
     *
     * @return number of installations.
     */
    int getInstallationsInRange(DatesRange range);

    /**
     * Returns number of runs in given dates range.
     *
     * @param range range of dates.
     *
     * @return number of runs.
     */
    int getRunsInRange(DatesRange range);

    /**
     * Returns number of fresh user registrations in given dates range.
     *
     * @param range range of dates.
     *
     * @return number of user registrations.
     */
    int getRegistrationsInRange(DatesRange range);

    /**
     * Returns the date of first installation in format 'YYYY-MM-DD'.
     *
     * @return date of first installation.
     */
    String getFirstInstallationDate();

    /**
     * Returns the list of <code>ValueCount</code> objects with names and subscriptions counts.
     *
     * @param aMax  maximum number of records.
     *
     * @return list of top subscribers.
     */
    List<ValueCountDate> getTopSubscribers(int aMax);

    /**
     * Returns the list of <code>ValueCount</code> objects with names and synchronization counts.
     *
     * @param aMax  maximum number of records.
     *
     * @return list of top synchronizers.
     */
    List<ValueCountDate> getTopSynchronizers(int aMax);

    /**
     * Returns the list of <code>ValueCount</code> objects with names and run counts.
     *
     * @param aMax  maximum number of records.
     *
     * @return list of top runners.
     */
    List<ValueCountDate> getTopRunners(int aMax);

    /**
     * Returns the list of <code>ValueCount</code> objects with names and average syncs per day counts.
     *
     * @param aMax maximum number of records.
     *
     * @return list of top most frequent synchronizers.
     */
    List<ValueCountDate> getTopActiveSynchronizers(int aMax);

    /**
     * Returns the number of registrations during the last specified number of days.
     *
     * @param days number of days to display.
     *
     * @return date to number mapping.
     */
    List getRegistrationTrends(int days);

    /**
     * Returns number of different reading lists clients have visited.
     *
     * @param access    <code>TRUE</code> to count accessed RL's (visits table).
     *
     * @return number of different reading lists.
     */
    int getReadingListsCount(boolean access);

    /**
     * Returns the list of reading lists starting from the given offset.
     *
     * @param offset    first reading list to start from.
     * @param limit     max number of reading lists to return.
     *
     * @return the list of reading lists.
     */
    ReadingListInfo[] getReadingListsInfo(int offset, int limit);

    /**
     * Returns the list of reading lists access records starting from the given offset.
     *
     * @param offset    first reading list to start from.
     * @param limit     max number of reading lists to return.
     *
     * @return the list of reading lists.
     */
    ReadingListInfo[] getReadingListsAccessInfo(int offset, int limit);

    /**
     * Clears all list-related stats.
     *
     * @param userId    user ID.
     * @param title     list title.
     */
    void clearReadingListStats(int userId, String title);
}
