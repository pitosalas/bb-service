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
// $Id: Statistics.java,v 1.1.1.1 2006/10/23 13:55:48 alg Exp $
//

package com.salas.bbservice.stats;

import com.salas.bbservice.service.meta.discovery.IBlogDiscoverer;
import com.salas.bbservice.persistence.IStatsDao;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.utils.Constants;

import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Collections;

/**
 * Collection of different statistics.
 */
public final class Statistics
{
    private static final int MILLIS_IN_DAY      = 86400000;
    private static final int MILLIS_IN_HOUR     = 3600000;
    private static final int MILLIS_IN_MINUTE   = 60000;
    private static final int MILLIS_IN_SECOND   = 1000;

    private static long startTime = -1;

    private static volatile long metaQueries = 0;
    private static volatile long metaDatabaseHits = 0;
    private static volatile long metaNewDiscoveries = 0;
    private static volatile int metaSuggestionDiscoveries = 0;
    private static volatile long discoveryTime = 0;
    private static volatile long discoveries = 0;
    private static volatile int syncStore = 0;
    private static volatile int syncRestore = 0;

    private static volatile Map discovererCalls;
    private static volatile Map discovererErrors;

    private static IStatsDao dao;

    static
    {
        discovererCalls = Collections.synchronizedMap(new IdentityHashMap());
        discovererErrors = Collections.synchronizedMap(new IdentityHashMap());
    }

    /**
     * Hidden constructor of utility class.
     */
    private Statistics()
    {
    }

    /**
     * Registers application start time.
     */
    public static void registerStart()
    {
        if (startTime == -1) startTime = System.currentTimeMillis();
    }

    /**
     * Returns time of being in running state.
     *
     * @return time in millis.
     */
    public static long getUptime()
    {
        if (startTime == -1) registerStart();
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Returns uptime in human-readable form.
     *
     * @return sring.
     */
    public static String getUptimeString()
    {
        long uptime = getUptime();
        int days = (int)(uptime / MILLIS_IN_DAY);
        uptime -= days * MILLIS_IN_DAY;
        int hours = (int)(uptime / MILLIS_IN_HOUR);
        uptime -= hours * MILLIS_IN_HOUR;
        int minutes = (int)(uptime / MILLIS_IN_MINUTE);
        uptime -= minutes * MILLIS_IN_MINUTE;
        int seconds = (int)(uptime / MILLIS_IN_SECOND);

        StringBuffer buf = new StringBuffer();
        if (days > 0) buf.append(days).append(" days ");
        if (hours > 0) buf.append(hours).append(" hour(s) ");
        if (minutes > 0) buf.append(minutes).append(" min ");
        if (seconds > 0) buf.append(seconds).append(" sec");

        return buf.toString();
    }

    /**
     * Registers new meta-data query from client.
     */
    public static void registerMetaQuery()
    {
        metaQueries++;
    }

    /**
     * Registers successful database hit.
     */
    public static void registerMetaDatabaseHit()
    {
        metaDatabaseHits++;
    }

    /**
     * Registers scheduling of new discovery.
     */
    public static void registerMetaNewDiscovery()
    {
        metaNewDiscoveries++;
    }

    /**
     * Regiters scheulding of suggestion discovery.
     */
    public static void registerMetaSuggestion()
    {
        metaSuggestionDiscoveries++;
    }

    /**
     * Registers call to the given discoverer and its outcome.
     *
     * @param discoverer    discoverer.
     * @param success       <code>true</code> if call was successful.
     */
    public static void registerMetaDiscovererCall(IBlogDiscoverer discoverer, boolean success)
    {
        incrementCounter(discoverer, discovererCalls);
        if (!success) incrementCounter(discoverer, discovererErrors);
    }

    /** Increments counter in map for a given discoverer. */
    private static void incrementCounter(IBlogDiscoverer discoverer, Map integerMap)
    {
        Integer counterI = (Integer)integerMap.get(discoverer);
        counterI = (counterI == null) ? Constants.INTEGER_1 : new Integer(counterI.intValue() + 1);
        integerMap.put(discoverer, counterI);
    }

    /**
     * Registers call to 'store' function of synchronization service.
     */
    public static void registerSyncStore()
    {
        syncStore++;
    }

    /**
     * Registers call to 'restore' function of synchronization service.
     */
    public static void registerSyncRestore()
    {
        syncRestore++;
    }

    /**
     * Returns number of meta-data queries.
     *
     * @return number of queries.
     */
    public static long getMetaQueries()
    {
        return metaQueries;
    }

    /**
     * Returns number of database hits.
     *
     * @return number of database hits.
     */
    public static long getMetaDatabaseHits()
    {
        return metaDatabaseHits;
    }

    /**
     * Returns number of newly scheduled discoveries.
     *
     * @return number of newly scheduled discoveries.
     */
    public static long getMetaNewDiscoveries()
    {
        return metaNewDiscoveries;
    }

    /**
     * Returns number of scheduled suggestion discoveries.
     *
     * @return number of scheduled suggestion discoveries.
     */
    public static int getMetaSuggestionDiscoveries()
    {
        return metaSuggestionDiscoveries;
    }

    /**
     * Returns map of discoverers to the counters of their calls (including failed).
     *
     * @return map (discoverer:number of calls).
     */
    public static Map getMetaDiscoverersCalls()
    {
        return discovererCalls;
    }

    /**
     * Returns map of discoverers to the counters of their failes.
     *
     * @return map (discoverer:number of fails).
     */
    public static Map getMataDicoverersErrors()
    {
        return discovererErrors;
    }

    /**
     * Returns stats DAO.
     *
     * @return DAO.
     */
    private static synchronized IStatsDao getDao()
    {
        if (dao == null)
        {
            dao = (IStatsDao)DaoConfig.getDaoManager().getDao(IStatsDao.class);
        }

        return dao;
    }

    /**
     * Returns number of Blogs recorded in database (including incomplete).
     *
     * @return blogs count.
     */
    public static int getBlogsCount()
    {
        return getDao().getBlogsCount(false);
    }

    /**
     * Returns number of incomplete Blogs in database.
     *
     * @return number of incomplete Blogs.
     */
    public static int getIncompleteBlogsCount()
    {
        return getDao().getBlogsCount(true);
    }

    /**
     * Returns number of links to Blogs recorded in database (including bag).
     *
     * @return number of links to Blogs.
     */
    public static int getBlogsLinksCount()
    {
        return getDao().getBlogLinksCount(false);
    }

    /**
     * Returns number of bad links to Blogs recorded in database.
     *
     * @return number of bad links to Blogs.
     */
    public static int getBadBlogLinksCount()
    {
        return getDao().getBlogLinksCount(true);
    }

    /**
     * Returns number of calls to sync-store function.
     *
     * @return number of calls to sync-store function.
     */
    public static int getSyncStoreCount()
    {
        return syncStore;
    }

    /**
     * Returns number of calls to sync-restore function.
     *
     * @return number of calls to sync-restore function.
     */
    public static int getSyncRestoreCount()
    {
        return syncRestore;
    }

    /**
     * Registers time spent in discovery of blog.
     *
     * @param time time.
     */
    public static void registerTimeInDiscovery(long time)
    {
        discoveryTime += time;
        discoveries++;
    }

    /**
     * Returns average time we spend in discoverying each blog.
     *
     * @return average discovery time.
     */
    public static double getAverageDiscoveryTimePerBlog()
    {
        return discoveries == 0 ? 0 : (double)discoveryTime / discoveries;
    }
}
