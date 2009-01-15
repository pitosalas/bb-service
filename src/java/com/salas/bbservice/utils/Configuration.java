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
// $Id: Configuration.java,v 1.5 2008/10/27 07:33:14 alg Exp $
//

package com.salas.bbservice.utils;

import com.salas.bbservice.stats.Statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Holder of configuration.
 */
public final class Configuration
{
    private static final Logger LOG = Logger.getLogger(Configuration.class.getName());

    private static final long DEFAULT_BLOGS_CLEANUP_PERIOD          = 1440;    // 1 day
    private static final long DEFAULT_BLOG_LIFE_SPAN                = 1440;    // 1 day
    private static final int DEFAULT_MIN_BLOGS_IN_DATABASE          = 5000;

    private static final int DEFAULT_BLOG_DISCOVERY_THREADS         = 10;
    private static final int DEFAULT_UPDATE_INCOMPLETED_PERIOD      = 600000;
    private static final int DEFAULT_UPDATE_INCOMPLETED_TASK_PERIOD = 30000;
    private static final int DEFAULT_UPDATE_SCHEDULED_PERIOD        = 86400000;
    private static final int DEFAULT_UPDATE_SCHEDULED_TASK_PERIOD   = 60000;

    private static final int TIMEOUT_CONNECT_MS = 5000;
    private static final int TIMEOUT_READ_MS = 5000;

    private static long blogCleanupPeriod;
    private static long blogLifespan;

    private static int discoveryThreads;
    private static long incompleteUpdatePeriod;
    private static long incompleteCheckPeriod;
    private static long scheduledUpdatePeriod;
    private static long scheduledCheckPeriod;

    private static int minBlogsInDatabase;

    private static Map[] discoverers;

    private static String reportingURL;
    private static String deployPath;
    private static String deployURL;
    private static String baseForumURL;

    private static Pattern ignorePattern;

    static
    {
        Statistics.registerStart();

        blogCleanupPeriod = ApplicationProperties.getLong(
            ApplicationProperties.META_BLOGS_CLEANUP_PERIOD,
            DEFAULT_BLOGS_CLEANUP_PERIOD);
        blogLifespan = ApplicationProperties.getLong(
            ApplicationProperties.META_BLOG_LIFE_SPAN,
            DEFAULT_BLOG_LIFE_SPAN);
        minBlogsInDatabase = ApplicationProperties.getInteger(
            ApplicationProperties.META_MIN_BLOGS_IN_DATABASE,
            DEFAULT_MIN_BLOGS_IN_DATABASE);

        discoveryThreads = DEFAULT_BLOG_DISCOVERY_THREADS;
        incompleteUpdatePeriod = ApplicationProperties.getLong(
            ApplicationProperties.META_UPDATE_INCOMPLETED_PERIOD,
            DEFAULT_UPDATE_INCOMPLETED_PERIOD);
        incompleteCheckPeriod = ApplicationProperties.getLong(
            ApplicationProperties.META_UPDATE_INCOMPLETED_TASK_PERIOD,
            DEFAULT_UPDATE_INCOMPLETED_TASK_PERIOD);
        scheduledUpdatePeriod = ApplicationProperties.getLong(
            ApplicationProperties.META_UPDATE_SCHEDULED_PERIOD,
            DEFAULT_UPDATE_SCHEDULED_PERIOD);
        scheduledCheckPeriod = ApplicationProperties.getLong(
            ApplicationProperties.META_UPDATE_SCHEDULED_TASK_PERIOD,
            DEFAULT_UPDATE_SCHEDULED_TASK_PERIOD);

        reportingURL = ApplicationProperties.get(
            ApplicationProperties.REPORTING_URL, null);

        deployPath = ApplicationProperties.get(
            ApplicationProperties.DEPLOY_PATH, null);
        deployURL = ApplicationProperties.get(
            ApplicationProperties.DEPLOY_URL, null);

        baseForumURL = ApplicationProperties.get(
            ApplicationProperties.BASE_FORUM_URL, null);

        String discoverersList = ApplicationProperties.get(
            ApplicationProperties.META_DISCOVERERS_LIST, Constants.EMPTY_STRING);
        StringTokenizer stringTokenizer = new StringTokenizer(discoverersList, ",");
        int count = stringTokenizer.countTokens();
        discoverers = new Map[count];
        for (int i = 0; i < count; i++)
        {
            String discoverer = stringTokenizer.nextToken();
            discoverers[i] = getDiscovererInfo(discoverer);
        }

        String[] ignore = ApplicationProperties.getArray(ApplicationProperties.META_IGNORE);
        if (ignore.length > 0)
        {
            ignorePattern = Pattern.compile("(" + StringUtils.join(ignore, ")|(") + ")", Pattern.CASE_INSENSITIVE);
        } else ignorePattern = null;

        outputConfiguration();
    }

    /**
     * Hidden constructor of utility class.
     */
    private Configuration()
    {
    }

    /**
     * Reads all properties related to the specified discoverer from the application properties
     * list.
     *
     * @param discoverer discoverer.
     *
     * @return map of properties.
     */
    private static Map<String, String> getDiscovererInfo(String discoverer)
    {
        Map<String, String> map = new HashMap<String, String>();

        String fullKey = ApplicationProperties.META_DISCOVERER_PREFIX + discoverer;
        List<String> keys = ApplicationProperties.getKeys(fullKey);

        for (String key : keys)
        {
            String property = key.substring(fullKey.length());
            if (property.length() == 0) property = ".class";

            if (property.startsWith("."))
            {
                property = property.substring(1);
                String value = ApplicationProperties.get(key, null);
                if (value != null) map.put(property, value);
            }
        }

        System.out.println("discoverer: " + map);

        return map;
    }

    /** Outputs configuration to log. */
    private static void outputConfiguration()
    {
        LOG.config("Incomplete Rediscovery: " +
            "Minimal period = " + incompleteUpdatePeriod / 1000 + " sec, " +
            "Task period = " + incompleteCheckPeriod / 1000 + " sec");

        LOG.config("Scheduled Updates: " +
            "Minimal period = " + scheduledUpdatePeriod / 1000 + " sec, " +
            "Task period = " + scheduledCheckPeriod / 1000 + " sec");

        LOG.config("Deployment: " +
            "Path = " + deployPath + " URL = " + deployURL);
    }

    /**
     * Returns cleanup period in minutes.
     *
     * @return period.
     */
    public static long getBlogCleanupPeriod()
    {
        return blogCleanupPeriod;
    }

    /**
     * Returns maximum blog age in minutes.
     *
     * @return age.
     */
    public static long getBlogLifespan()
    {
        return blogLifespan;
    }

    /**
     * Returns minimum number of blogs to store in database.
     *
     * @return number of blogs.
     */
    public static int getMinBlogsInDatabase()
    {
        return minBlogsInDatabase;
    }

    /**
     * Returns number of discovery threads.
     *
     * @return number of threads.
     */
    public static int getDiscoveryThreads()
    {
        return discoveryThreads;
    }

    /**
     * Returns period of updates of incomplete blogs.
     *
     * @return period in millis.
     */
    public static long getIncompleteUpdatePeriod()
    {
        return incompleteUpdatePeriod;
    }

    /**
     * Returns period of checks for incomplete blogs to update.
     *
     * @return period in millis.
     */
    public static long getIncompleteCheckPeriod()
    {
        return incompleteCheckPeriod;
    }

    /**
     * Returns period of scheduled updates of blogs.
     *
     * @return period in millis.
     */
    public static long getScheduledUpdatePeriod()
    {
        return scheduledUpdatePeriod;
    }

    /**
     * Returns period of checks for scheduled updates.
     *
     * @return period in millis.
     */
    public static long getScheduledCheckPeriod()
    {
        return scheduledCheckPeriod;
    }

    /**
     * Returns the list of maps with properties of discoverers setup read from app. props.
     *
     * @return list of discoverers. 
     */
    public static Map[] getDiscoverers()
    {
        return discoverers;
    }

    /**
     * Returns URL of reporting software.
     *
     * @return URL of reporting software.
     */
    public static String getReportingURL()
    {
        return reportingURL;
    }

    /**
     * Returns URL of deployment directory.
     *
     * @return URL of deployment directory.
     */
    public static String getDeployURL()
    {
        return deployURL;
    }

    /**
     * Returns path to deployment directory.
     *
     * @return path to deployment directory.
     */
    public static String getDeployPath()
    {
        return deployPath;
    }

    /**
     * Returns base URL of the forum.
     *
     * @return base forum URL.
     */
    public static String getBaseForumURL()
    {
        return baseForumURL;
    }

    /**
     * Returns connection timeout.
     *
     * @return timeout.
     */
    public static int getConnectTimeout()
    {
        return TIMEOUT_CONNECT_MS;
    }

    /**
     * Returns the reading timeout.
     *
     * @return timeout.
     */
    public static int getReadTimeout()
    {
        return TIMEOUT_READ_MS;
    }

    /**
     * Returns the pattern to ignore.
     *
     * @return pattern.
     */
    public static Pattern getIgnorePattern()
    {
        return ignorePattern;
    }
}
