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
// $Id: StatsProcessor.java,v 1.3 2007/01/17 12:51:29 alg Exp $
//

package com.salas.bbservice.stats;

import com.salas.bbservice.domain.ReadingListInfo;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.domain.dao.DatesRange;
import com.salas.bbservice.domain.dao.StatsTopRatedResult;
import com.salas.bbservice.domain.dao.ValueCount;
import com.salas.bbservice.domain.dao.ValueDouble;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.IStatsDao;
import com.salas.bbservice.persistence.IUserDao;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Processes statistics in database.
 */
public class StatsProcessor
{
    private static final SimpleDateFormat DATE_FORMAT_MONTHLY = new SimpleDateFormat("MMM-yyyy");
    private static final long MILLIS_IN_DAY = 24*60*60*1000;

    private static IStatsDao statsDao;
    private static IUserDao userDao;

    // periods and ranges counts
    private static final int MAX_PERIODS = 5;
    private static final int MAX_RANGES = 4;

    // Max months to dump in monthly stats (invalid range protection)
    private static final int MAX_MONTHS = 36;

    // number of runs
    private static final int RUNS_INF = 999999;
    private static final int RUNS_51 = 51;
    private static final int RUNS_50 = 50;
    private static final int RUNS_25 = 25;
    private static final int RUNS_24 = 24;
    private static final int RUNS_10 = 10;
    private static final int RUNS_9 = 9;
    private static final int RUNS_0 = 0;

    static
    {
        statsDao = (IStatsDao)DaoConfig.getDaoManager().getDao(IStatsDao.class);
        userDao = (IUserDao)DaoConfig.getDaoManager().getDao(IUserDao.class);
    }

    /**
     * Returns table of stats for installations per versions.
     *
     * @return stats.
     */
    public StatsTable getInstallationsPerVersion()
    {
        String[] columns = { "Version", "Ever", "This Year", "This Month", "This Week", "Today" };

        List lEver = statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_EVER, null);
        List lYear = statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_YEAR, null);
        List lMonth = statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_MONTH, null);
        List lWeek = statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_WEEK, null);
        List lToday = statsDao.getInstallationsCount(IStatsDao.COUNT_PERIOD_TODAY, null);

        Map stats = new TreeMap();
        int index = 0;
        putStats(stats, lEver, index++, MAX_PERIODS);
        putStats(stats, lYear, index++, MAX_PERIODS);
        putStats(stats, lMonth, index++, MAX_PERIODS);
        putStats(stats, lWeek, index++, MAX_PERIODS);
        putStats(stats, lToday, index, MAX_PERIODS);

        String[] rows = (String[])stats.keySet().toArray(new String[0]);
        double[][] data = new double[rows.length][columns.length];
        for (int i = 0; i < rows.length; i++)
        {
            data[i] = (double[])stats.get(rows[i]);
        }

        return new StatsTable(columns, rows, data);
    }

    /**
     * Returns table of stats for runs per versions.
     *
     * @return table of stats.
     */
    public StatsTable getRunsPerVersion()
    {
        String[] columns = { "Version", "Ever", "This Year", "This Month", "This Week", "Today" };

        List lEver = statsDao.getRunsPerVersion(IStatsDao.COUNT_PERIOD_EVER, null);
        List lYear = statsDao.getRunsPerVersion(IStatsDao.COUNT_PERIOD_YEAR, null);
        List lMonth = statsDao.getRunsPerVersion(IStatsDao.COUNT_PERIOD_MONTH, null);
        List lWeek = statsDao.getRunsPerVersion(IStatsDao.COUNT_PERIOD_WEEK, null);
        List lToday = statsDao.getRunsPerVersion(IStatsDao.COUNT_PERIOD_TODAY, null);

        Map stats = new TreeMap();
        int index = 0;
        putStats(stats, lEver, index++, MAX_PERIODS);
        putStats(stats, lYear, index++, MAX_PERIODS);
        putStats(stats, lMonth, index++, MAX_PERIODS);
        putStats(stats, lWeek, index++, MAX_PERIODS);
        putStats(stats, lToday, index, MAX_PERIODS);

        String[] rows = (String[])stats.keySet().toArray(new String[0]);
        double[][] data = new double[rows.length][columns.length];
        for (int i = 0; i < rows.length; i++)
        {
            data[i] = (double[])stats.get(rows[i]);
        }

        return new StatsTable(columns, rows, data);
    }

    /**
     * Returns table of stats for installations per version distribution.
     *
     * @return table of stats.
     */
    public StatsTable getInstallationsPerVersionDistribution()
    {
        String[] columns = { "Version", ">50 Runs", "25-50 Runs", "10-24 Runs", "1-9 Runs" };

        List r1to9 = statsDao.getInstallationsPerVersionDist(RUNS_0, RUNS_9);
        List r10to24 = statsDao.getInstallationsPerVersionDist(RUNS_10, RUNS_24);
        List r25to50 = statsDao.getInstallationsPerVersionDist(RUNS_25, RUNS_50);
        List r50 = statsDao.getInstallationsPerVersionDist(RUNS_51, RUNS_INF);

        Map stats = new TreeMap();
        int index = 0;
        putStats(stats, r50, index++, MAX_RANGES);
        putStats(stats, r25to50, index++, MAX_RANGES);
        putStats(stats, r10to24, index++, MAX_RANGES);
        putStats(stats, r1to9, index, MAX_RANGES);

        String[] rows = (String[])stats.keySet().toArray(new String[0]);
        double[][] data = new double[rows.length][columns.length];
        for (int i = 0; i < rows.length; i++)
        {
            data[i] = (double[])stats.get(rows[i]);
        }

        return new StatsTable(columns, rows, data);
    }

    /**
     * Returns table with miscellaneous stats.
     *
     * @return table with stats.
     */
    public StatsTable getMiscStats()
    {
        String[] columns = { "Parameter", "Value" };
        String[] rows = { "Average runs per day", "Average runs per installation" };
        double[][] data = {
            { statsDao.getAvgRunsPerDay() },
            { statsDao.getAvgRunsPerInstallation() }
        };

        return new StatsTable(columns, rows, data);
    }

    /**
     * Returns table with OS usage statistics.
     *
     * @return OS usage statistics.
     */
    public StatsTable getOsUsageStats()
    {
        return prepareCountsStats("OS", statsDao.getOsUsage());
    }

    /**
     * Returns table with Java Version usage statistics.
     *
     * @return Java version usage statistics.
     */
    public StatsTable getJavaVersionUsageStats()
    {
        return prepareCountsStats("Version", statsDao.getJavaVersionUsage());
    }

    /**
     * Returns table with Locale statistics.
     *
     * @return statistics.
     */
    public StatsTable getLocaleStats()
    {
        return prepareCountsStats("Locale", statsDao.getLocaleStats());
    }

    /**
     * Returns table with top read channels.
     *
     * @param limit max number of channels to fetch.
     *
     * @return statistics.
     */
    public StatsTable getTopReadFeeds(int limit)
    {
        return prepareStats("Title", "References", statsDao.getTopReadChannels(limit));
    }

    /**
     * Returns table with top rated channels.
     *
     * @param limit                 max number of channels to fetch.
     * @param subscriptionWeight    weight of each subscription in rating points.
     *
     * @return statistics.
     */
    public StatsTable getTopRatedFeeds(int limit, double subscriptionWeight)
    {
        List results = statsDao.getTopRatedChannels(limit, subscriptionWeight);
        int records = results.size();

        String[] columns = { "Feed", "Subscriptions", "Average Rating", "Final Rating" };
        String[] rows = new String[records];
        double[][] data = new double[records][3];

        Iterator iterator = results.iterator();
        int row = 0;
        while (iterator.hasNext())
        {
            StatsTopRatedResult result = (StatsTopRatedResult)iterator.next();

            MessageFormat format = new MessageFormat("<a href='{0}'>{1}</a> [{2}]");
            rows[row] = format.format(new Object[] { result.getHtmlUrl(), result.getTitle(),
                result.getXmlUrl() });
            data[row][0] = result.getSubscriptions();
            data[row][1] = result.getAverageRating();
            data[row][2] = result.getFinalRating();
            row++;
        }

        return new StatsTable(columns, rows, data);
    }

    /**
     * Creates two-column stats table with second column titled as 'Count'.
     *
     * @param title title for the first column.
     * @param data  data to put into column. List should contain only <code>ValueCount</code>
     *              objects.
     *
     * @return stats table.
     */
    private StatsTable prepareCountsStats(String title, List data)
    {
        return prepareStats(title, "Count", data);
    }

    /**
     * Creates two-column stats table.
     *
     * @param first     title for the first column.
     * @param second    title for the second column.
     * @param data      data to put into column. List should contain only <code>ValueCount</code>
     *                  objects.
     *
     * @return stats table.
     */
    private StatsTable prepareStats(final String first, final String second, List data)
    {
        String[] columns = { first, second };

        int size = data.size();

        String[] rows = new String[size];
        double[][] dataA = new double[size][1];

        for (int i = 0; i < size; i++)
        {
            Object item = data.get(i);
            if (item instanceof ValueCount)
            {
                ValueCount valueCount = (ValueCount)item;
                rows[i] = valueCount.getValue();
                dataA[i][0] = valueCount.getCount();
            } else
            {
                ValueDouble valueDouble = (ValueDouble)item;
                rows[i] = valueDouble.getValue();
                dataA[i][0] = valueDouble.getCount();
            }
        }

        return new StatsTable(columns, rows, dataA);
    }

    /**
     * Puts the counts from list of <code>ValueCount</code> objects into column
     * specified by <code>i</code> and rows with keys equal to <code>value</code>.
     * New arrays are initialized with length <code>max</code>.
     *
     * @param stats stats map.
     * @param list  list of <code>ValueCount</code> objects.
     * @param i     column index.
     * @param max   maximum number of columns for initialization.
     */
    void putStats(Map stats, List list, int i, int max)
    {
        for (int j = 0; j < list.size(); j++)
        {
            ValueCount valueCount = (ValueCount)list.get(j);

            double[] array = (double[])stats.get(valueCount.getValue());
            if (array == null)
            {
                array = new double[max];
                stats.put(valueCount.getValue(), array);
            }

            array[i] = valueCount.getCount();
        }
    }

    /**
     * Returns the list of users taken from database starting from defined
     * offset with total count no more than <code>count</code>.
     *
     * @param offset    offset.
     * @param count     maximum.
     * @param pattern   pattern to be present in the name or email (optional).
     *
     * @return list of users.
     */
    public static User[] getUsers(int offset, int count, String pattern)
    {
        return (User[])userDao.getUsers(offset, count, pattern).toArray(new User[0]);
    }

    /**
     * Returns the list of users registrations taken from database sorted
     * by the registration date from the most recent to older.
     *
     * @param count number of records to return.
     *
     * @return users.
     */
    public static User[] getRecentUsers(int count)
    {
        return (User[])userDao.getRecentUsers(count).toArray(new User[0]);
    }

    /**
     * Returns total number of users.
     *
     * @param pattern pattern to match against the name or email or <code>NULL</code>.
     *
     * @return number of users.
     */
    public static int getUsersCount(String pattern)
    {
        return userDao.getUsersCount(pattern);
    }

    /**
     * Returns list of latest versions in ascending order.
     *
     * @param maxCount maximum number of versions in list.
     *
     * @return list of versions.
     */
    public static String[] getLatestVersions(int maxCount)
    {
        String[] versions;
        List versionsL = statsDao.getLatestVersions(maxCount);

        int size = versionsL == null ? 0 : versionsL.size();
        versions = new String[size];
        for (int i = 0; i < size; i++) versions[i] = (String)versionsL.get(size - i - 1);

        return versions;
    }

    /**
     * Returns short installation statistics for the latest <code>versionsCount</code> number
     * of versions.
     *
     * @param versionsCount number of versions (maximum) to display.
     * @param fixedVersions list of versions to display in any case.
     *
     * @return stats table.
     */
    public StatsTable getLatestVersionsInstallsStats(int versionsCount, String[] fixedVersions)
    {
        String[] versions;
        if (versionsCount > 0)
        {
            versions = joinVersionsLists(fixedVersions, getLatestVersions(versionsCount));
        } else versions = fixedVersions;

        // Prepare columns labels
        String[] cols = new String[versions.length + 1];
        cols[0] = "Version";
        for (int i = 0; i < versions.length; i++) cols[i + 1] = versions[i];

        // Prepare rows labels
        String[] rows = { "Today", "This week", "This month" };

        // Prepare data
        double data[][] = new double[3][versions.length];
        for (int i = 0; i < versions.length; i++)
        {
            String version = versions[i];

            data[0][i] = valueCountListToCount(statsDao.getInstallationsCount(
                IStatsDao.COUNT_PERIOD_TODAY, version));

            data[1][i] = valueCountListToCount(statsDao.getInstallationsCount(
                IStatsDao.COUNT_PERIOD_WEEK, version));

            data[2][i] = valueCountListToCount(statsDao.getInstallationsCount(
                IStatsDao.COUNT_PERIOD_MONTH, version));
        }

        return new StatsTable(cols, rows, data);
    }

    /**
     * Joins and de-duplicates the two lists of versions. The order of items in lists is preserved.
     * The fixed versions list is always going before the latest versions list. If the version is
     * in the both lists it will be added as fixed version.
     *
     * @param aFixedVersions    list of fixed versions.
     * @param aLatestVersions   list of latest versions.
     *
     * @return resulting de-duplicateed copy of two joined lists.
     */
    static String[] joinVersionsLists(String[] aFixedVersions, String[] aLatestVersions)
    {
        Set s = new LinkedHashSet(aFixedVersions.length + aLatestVersions.length);
        s.addAll(Arrays.asList(aFixedVersions));
        s.addAll(Arrays.asList(aLatestVersions));

        return (String[])s.toArray(new String[s.size()]);
    }

    private int valueCountListToCount(List lst)
    {
        int cnt = 0;

        if (lst != null && lst.size() > 0)
        {
            ValueCount valueCount = (ValueCount)lst.get(0);
            cnt = valueCount == null ? 0 : valueCount.getCount();
        }
        return cnt;
    }

    /**
     * Returns short runs statistics for the latest <code>versionsCount</code> number
     * of versions.
     *
     * @param versionsCount number of versions (maximum) to display.
     * @param fixedVersions list of versions to be displayed in any case.
     *
     * @return stats table.
     */
    public StatsTable getLatestVersionsRunsStats(int versionsCount, String[] fixedVersions)
    {
        String[] versions;
        if (versionsCount > 0)
        {
            versions = joinVersionsLists(fixedVersions, getLatestVersions(versionsCount));
        } else versions = fixedVersions;

        // Prepare columns labels
        String[] cols = new String[versions.length + 1];
        cols[0] = "Version";
        for (int i = 0; i < versions.length; i++) cols[i + 1] = versions[i];

        // Prepare rows labels
        String[] rows = { "Today", "This week", "This month" };

        // Prepare data
        double data[][] = new double[3][versions.length];
        for (int i = 0; i < versions.length; i++)
        {
            String version = versions[i];

            data[0][i] = valueCountListToCount(statsDao.getRunsPerVersion(
                IStatsDao.COUNT_PERIOD_TODAY, version));

            data[1][i] = valueCountListToCount(statsDao.getRunsPerVersion(
                IStatsDao.COUNT_PERIOD_WEEK, version));

            data[2][i] = valueCountListToCount(statsDao.getRunsPerVersion(
                IStatsDao.COUNT_PERIOD_MONTH, version));
        }

        return new StatsTable(cols, rows, data);
    }

    /**
     * Returns table with registration stats for today, this week and this month.
     *
     * @return table with stats.
     */
    public StatsTable getUserRegistrations()
    {
        String[] cols = { "Period", "Count" };
        String[] rows = { "Today", "This week", "This month" };
        double[][] data = new double[3][1];

        data[0][0] = statsDao.getRegistrationsCount(IStatsDao.COUNT_PERIOD_TODAY);
        data[1][0] = statsDao.getRegistrationsCount(IStatsDao.COUNT_PERIOD_WEEK);
        data[2][0] = statsDao.getRegistrationsCount(IStatsDao.COUNT_PERIOD_MONTH);

        return new StatsTable(cols, rows, data);
    }

    /**
     * Returns table with top <code>max</code> most popular keywords.
     *
     * @param max maximum rows in the list.
     *
     * @return table with stats.
     */
    public static StatsTable getKeywordsChart(int max)
    {
        return prepareKeywordsChart(statsDao.getKeywordsChart(), max);
    }

    /**
     * Build statistics table from chart.
     *
     * @param chart     chart set to use.
     * @param maxRows   maximum number of rows in table.
     *
     * @return table with stats.
     */
    static StatsTable prepareKeywordsChart(SortedSet chart, int maxRows)
    {
        StatsTable statsTable;

        String[] cols = { "Keyword", "Count" };

        if (chart == null || maxRows <= 0)
        {
            statsTable = new StatsTable(cols, new String[0], new double[0][1]);
        } else
        {
            int rowsCount = Math.min(chart.size(), maxRows);

            double[][] data = new double[rowsCount][1];
            String[] rows = new String[rowsCount];

            Iterator it = chart.iterator();
            int row = 0;
            while (row < rowsCount && it.hasNext())
            {
                ValueCount vc = (ValueCount)it.next();
                rows[row] = vc.getValue();
                data[row][0] = vc.getCount();
                row++;
            }
            statsTable = new StatsTable(cols, rows, data);
        }

        return statsTable;
    }

    /**
     * Collects statistics and builds table with installations/runs/registrations per month/day.
     *
     * @param aStartDate    start date in format "YYYYMM".
     * @param aEndDate      end date in format "YYYYMM".
     *
     * @return stats table.
     */
    public StatsTable getMonthlyStats(String aStartDate, String aEndDate)
    {
        Calendar end = convertToCalendar(aEndDate, null);
        Calendar start = convertToCalendar(aStartDate, end);

        // Swap calendars if start is after end
        if (start.after(end))
        {
            Calendar temp = end;
            end = start;
            start = temp;
        }

        List monthsStats = getMonthlyIRRStats(start, end);
        List weekDayStats = isSameMonthAsToday(end) ? getWeekDayIRRStats() : null;
        RangeStats totalStats = getTotalIRRStats();

        // Prepare table
        int months = monthsStats.size();
        int extraLength = weekDayStats == null ? 0 : weekDayStats.size() / 2;
        String[] columns = new String[months + extraLength + 2];
        String[] rows = {
            "Installations<br>(monthly)", "Installations<br>(daily)",
            "Installations<br>change&nbsp;(%)",
            "Runs<br>(monthly)", "Runs<br>(daily)",
            "Runs<br>change&nbsp;(%)",
            "Registrations<br>(monthly)", "Registrations<br>(daily)",
            "Registrations<br>change&nbsp;(%)" };
        double[][] data = new double[9][months + extraLength + 1];

        columns[0] = "&nbsp;";

        int nextColumn = putMonthsStatsInDataGrid(monthsStats, columns, data);

        if (extraLength > 0)
        {
            for (int i = 0; i < weekDayStats.size(); i+=2)
            {
                RangeStats prevStats = (RangeStats)weekDayStats.get(i);
                RangeStats stats = (RangeStats)weekDayStats.get(i + 1);

                int instChange = calcChange(prevStats.installations, stats.installations);
                int runsChange = calcChange(prevStats.runs, stats.runs);
                int regsChange = calcChange(prevStats.registrations, stats.registrations);

                putStatsInDataGrid(stats, data, columns, nextColumn++, instChange, runsChange, regsChange);
            }
        }

        // Paste total
        putStatsInDataGrid(totalStats, data, columns, nextColumn, 0, 0, 0);

        return new StatsTable(columns, rows, data);
    }

    /**
     * Returns total Installations/Registrations/Runs stats.
     *
     * @return total stats.
     */
    private static RangeStats getTotalIRRStats()
    {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date dateTomorrow = cal.getTime();

        // Calculate total
        Date dateInception = null;
        String firstInstallationDate = statsDao.getFirstInstallationDate();

        if (firstInstallationDate != null)
        {
            try
            {
                dateInception = new SimpleDateFormat("yyyy-MM-dd").parse(firstInstallationDate);
            } catch (ParseException e)
            {
                // Do not do anything.
            }
        }

        if (dateInception == null) dateInception = dateTomorrow;

        return getStatsForRange(dateInception, dateTomorrow, "Total");
    }

    /**
     * Returns last week and today's Installations/Registrations/Runs stats.
     *
     * @return last week and today's stats.
     */
    private static List getWeekDayIRRStats()
    {
        List extraRanges;

        Calendar cal = new GregorianCalendar();
        Date dateToday = cal.getTime();

        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date dateTomorrow = cal.getTime();

        extraRanges = new ArrayList(4);

        cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date dateYesterday = cal.getTime();

        cal.add(Calendar.DAY_OF_YEAR, -6);
        Date dateThisWeek = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        Date datePrevWeek = cal.getTime();

        extraRanges.add(getStatsForRange(datePrevWeek, dateThisWeek, "Prev Week"));
        extraRanges.add(getStatsForRange(dateThisWeek, dateTomorrow, "This Week"));
        extraRanges.add(getStatsForRange(dateYesterday, dateToday, "Yesterday"));
        extraRanges.add(getStatsForRange(dateToday, dateTomorrow, "Today"));

        return extraRanges;
    }

    /**
     * Returns <code>TRUE</code> if the date in current year/month.
     *
     * @param aDate date to check.
     *
     * @return <code>TRUE</code> if the date in current year/month.
     */
    private static boolean isSameMonthAsToday(Calendar aDate)
    {
        boolean sameMonth = false;

        if (aDate != null)
        {
            Calendar today = new GregorianCalendar();
            sameMonth = today.get(Calendar.YEAR) == aDate.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == aDate.get(Calendar.MONTH);
        }

        return sameMonth;
    }

    /**
     * Returns stats broken into monthly ranges in initial dates range.
     *
     * @param aStart    start date.
     * @param aEnd      end date.
     *
     * @return monthly stats.
     */
    private static List getMonthlyIRRStats(Calendar aStart, Calendar aEnd)
    {
        List monthsStats;

        // Init iterator with start date
        Calendar iterator = new GregorianCalendar();
        iterator.setTime(aStart.getTime());

        // Collect stats for given months
        monthsStats = new ArrayList();
        int statsCount = 0;
        while (!iterator.after(aEnd) && statsCount < MAX_MONTHS)
        {
            Date startDate = iterator.getTime();
            iterator.add(Calendar.MONTH, 1);
            Date endDate = iterator.getTime();

            RangeStats stats = getStatsForMonth(startDate, endDate);
            monthsStats.add(stats);

            // Jump to next month
            statsCount++;
        }

        return monthsStats;
    }

    /**
     * Puts all months stats from <code>monthStats</code> list to the <code>dataGrid</code>. The names of
     * months are put in the <code>columns</code> array.
     *
     * @param monthsStats   list of months stats.
     * @param columns       names of the columns (ranges) - names of months.
     * @param dataGrid      data grid to fill.
     *
     * @return first column after the last filled with data.
     */
    private int putMonthsStatsInDataGrid(List monthsStats, String[] columns, double[][] dataGrid)
    {
        int column = 0;

        int instPrevMonth = 0;
        int runsPrevMonth = 0;
        int regsPrevMonth = 0;

        Iterator it = monthsStats.iterator();
        while (it.hasNext())
        {
            RangeStats stats = (RangeStats)it.next();

            int instChange = calcChange(instPrevMonth, stats.installations);
            int runsChange = calcChange(runsPrevMonth, stats.runs);
            int regsChange = calcChange(regsPrevMonth, stats.registrations);

            putStatsInDataGrid(stats, dataGrid, columns, column, instChange, runsChange, regsChange);

            instPrevMonth = stats.installations;
            runsPrevMonth = stats.runs;
            regsPrevMonth = stats.registrations;

            column++;
        }

        return column;
    }

    /**
     * Puts range stats into the <code>dataGrid</code> at the specified <code>column</code>.
     * Installations, runs and registrations change values are also put into the corresponding rows.
     *
     * @param stats                 stats to put into the grid.
     * @param dataGrid              data grid object.
     * @param columns               names of the columns (ranges).
     * @param column                column to put data at.
     * @param installationsChange   installations change.
     * @param runsChange            runs change.
     * @param registrationsChange   registrations change.
     */
    private static void putStatsInDataGrid(RangeStats stats, double[][] dataGrid, String[] columns, int column,
        int installationsChange, int runsChange, int registrationsChange)
    {
        int days = stats.days == 0 ? 1 : stats.days;

        columns[column + 1] = stats.date;

        dataGrid[0][column] = stats.installations;
        dataGrid[1][column] = stats.installations / days;
        dataGrid[2][column] = installationsChange;

        dataGrid[3][column] = stats.runs;
        dataGrid[4][column] = stats.runs / days;
        dataGrid[5][column] = runsChange;

        dataGrid[6][column] = stats.registrations;
        dataGrid[7][column] = stats.registrations / days;
        dataGrid[8][column] = registrationsChange;
    }

    /**
     * Calculates change percentage.
     *
     * @param previousValue previous value or <code>0</code>.
     * @param currentValue  current value.
     *
     * @return 0 if previous value is equal to <code>0</code> or
     */
    private int calcChange(int previousValue, int currentValue)
    {
        return previousValue == 0 ? 0 : (int)((currentValue - previousValue) * 100.0 / previousValue);
    }

    /**
     * Returns installations, runs and registrations in the given months range.
     *
     * @param start start date.
     * @param end   end date.
     *
     * @return stats.
     */
    private static RangeStats getStatsForMonth(Date start, Date end)
    {
        return getStatsForRange(start, end, DATE_FORMAT_MONTHLY.format(start));
    }

    /**
     * Returns installations, runs and registrations in the given dates range.
     *
     * @param start         start date.
     * @param end           end date.
     * @param rangeTitle    title of the range.
     *
     * @return stats.
     */
    private static RangeStats getStatsForRange(Date start, Date end, String rangeTitle)
    {
        RangeStats stats = new RangeStats();
        DatesRange range = new DatesRange(start, end);

        stats.date = rangeTitle;
        stats.installations = statsDao.getInstallationsInRange(range);
        stats.runs = statsDao.getRunsInRange(range);
        stats.registrations = statsDao.getRegistrationsInRange(range);
        stats.days = calcNumberOfDays(start, end);

        return stats;
    }

    /**
     * Calculates number of days between the dates. If the start date is after today's date there
     * will always be 0 days. If the end date is after the today's date number of days will
     * be counted between start date and today's date. The dates can be automatically swapped
     * if start date is after the end date.
     *
     * @param aStart    start date.
     * @param aEnd      end date.
     *
     * @return number of days between dates of between start date and today's date.
     *
     * @throws NullPointerException if one of the dates is not set.
     */
    static int calcNumberOfDays(Date aStart, Date aEnd)
    {
        long startTime = aStart.getTime();
        long endTime = aEnd.getTime();
        long currentTime = System.currentTimeMillis();

        // Swap times if necessary
        if (startTime > endTime)
        {
            long temp = startTime;
            startTime = endTime;
            endTime = temp;
        }

        // Don't allow times to span across current time
        startTime = Math.min(startTime, currentTime);
        endTime = Math.min(endTime, currentTime);

        return (int)((endTime - startTime) / MILLIS_IN_DAY);
    }

    /**
     * Converts date in format "YYYYMM" to calendar. If format is incorrect or
     * source date equals to <code>NULL</code> returned calendar points to current time.
     *
     * @param date          source date.
     * @param countdownDate countdown date will be used when main date isn't specified or invalid.
     *                      In this case if countdown date is specified the application will
     *                      substract 6 months from it and use this result.
     *
     * @return calendar.
     */
    private static Calendar convertToCalendar(String date, Calendar countdownDate)
    {
        Calendar cal = null;
        try
        {
            if (date != null)
            {
                int year = Integer.parseInt(date.substring(0, 4));
                int month = Integer.parseInt(date.substring(4, 6));

                cal = new GregorianCalendar();
                cal.set(year, month - 1, 1);
            }
        } catch (NumberFormatException e)
        {
            // Date has invalid format the next steps will recover from that.
        }

        if (cal == null)
        {
            cal = new GregorianCalendar();
            if (countdownDate != null)
            {
                cal.setTime(countdownDate.getTime());
                cal.add(Calendar.MONTH, -6);
            }
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }

        return cal;
    }

    /**
     * Returns the number of registrations during the last specified number of days.
     *
     * @param days number of days to display.
     *
     * @return date to number mapping.
     */
    public static ValueCount[] getRecentRegistrationTrends(int days)
    {
        return (ValueCount[])statsDao.getRegistrationTrends(days).toArray(new ValueCount[0]);
    }

    /**
     * Returns total number of reading lists recorded in visits table or overall RL count.
     *
     * @param access    <code>TRUE</code> to count accessed RL's (visits table).
     *
     * @return total.
     */
    public int getReadingListsCount(boolean access)
    {
        return statsDao.getReadingListsCount(access);
    }

    /**
     * Returns the list of info's about the reading lists.
     *
     * @param access    <code>TRUE</code> to return access-oriented stats.
     * @param offset    the first reading list to start from.
     * @param limit     number of reading lits to return.
     *
     * @return info's.
     */
    public ReadingListInfo[] getReadingListsInfo(boolean access, int offset, int limit)
    {
        return access
            ? statsDao.getReadingListsAccessInfo(offset, limit)
            : statsDao.getReadingListsInfo(offset, limit);
    }

    /**
     * Holder for monthly stats.
     */
    private static class RangeStats
    {
        private String  date;
        private int     installations;
        private int     runs;
        private int     registrations;
        private int     days;
    }
}
