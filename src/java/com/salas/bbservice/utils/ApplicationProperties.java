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
// $Id: ApplicationProperties.java,v 1.3 2008/10/27 07:33:14 alg Exp $
//

package com.salas.bbservice.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central repository of application properies.
 */
public final class ApplicationProperties
{
    private static final Logger LOG = Logger.getLogger(ApplicationProperties.class.getName());

    // ---------------------------------------------------------------------------------------------
    // Properties names
    // ---------------------------------------------------------------------------------------------

    /** Key of Technorati service. */
    public static final String META_TECHNORATI_DISCOVERER_KEY   = "meta.discoverer.technorati.key";

    /** List of discoverers. */
    public static final String META_DISCOVERERS_LIST            = "meta.discoverers";

    /** Prefix of discoverer property. */
    public static final String META_DISCOVERER_PREFIX           = "meta.discoverer.";

    /** Period of meta-information database cleanup in minutes. */
    public static final String META_BLOGS_CLEANUP_PERIOD        = "meta.cleanup.period";

    /** Maximum blog inactivity age in minutes. */
    public static final String META_BLOG_LIFE_SPAN              = "meta.blog.lifespan";

    /** Minimum number of blog records to store in database. */
    public static final String META_MIN_BLOGS_IN_DATABASE       = "meta.min.blogs.in.database";

    /** Time to wait before starting rediscovery of incomplete blog. */
    public static final String META_UPDATE_INCOMPLETED_PERIOD   = "meta.update.incompleted.period";

    /** How much to sleep between checks for incomplete blogs. */
    public static final String META_UPDATE_INCOMPLETED_TASK_PERIOD  =
        "meta.update.incompleted.task.period";

    /** Minimal time between updates of blog. */
    public static final String META_UPDATE_SCHEDULED_PERIOD     = "meta.update.scheduled.period";

    /** Time to sleep beteen two updates. */
    public static final String META_UPDATE_SCHEDULED_TASK_PERIOD =
        "meta.update.scheduled.task.period";

    /** Patterns to ignore. */
    public static final String META_IGNORE                      = "meta.ignore";

    /** URL to reporting package. */
    public static final String REPORTING_URL    = "reporting.url";
    /** URL of deployment directory. */
    public static final String DEPLOY_URL       = "deploy.url";
    /** Path to deployment directory. */
    public static final String DEPLOY_PATH      = "deploy.path";

    /** Base URL of the forum. */
    public static final String BASE_FORUM_URL = "base.forum.url";

    /** OPML Starting points URL */
    public static final String OPML_STARTING_POINTS_URL = "opml.starting.points.url";
    /** OPML Experts URL */
    public static final String OPML_EXPERTS_URL = "opml.experts.url";

    private static Properties props;

    static
    {
        try
        {
            props = new Properties();
            ClassLoader cl = ApplicationProperties.class.getClassLoader();
            props.load(cl.getResourceAsStream("app.properties"));
        } catch (IOException e)
        {
            LOG.log(Level.SEVERE, "Could not initialize application properties.", e);
        }
    }

    /**
     * Hidden utility calss constructor.
     */
    private ApplicationProperties()
    {
    }

    /**
     * Returns list of keys starting with defined prefix.
     *
     * @param prefix prefix.
     *
     * @return list of keys.
     */
    public static List<String> getKeys(String prefix)
    {
        List<String> lst = new ArrayList<String>();
        String[] keys = props.keySet().toArray(new String[0]);
        for (String key : keys) if (key != null && key.startsWith(prefix)) lst.add(key);

        return lst;
    }

    /**
     * Returns string property.
     *
     * @param key       key of property.
     * @param def       default value.
     *
     * @return value.
     */
    public static String get(String key, String def)
    {
        return props.keySet().contains(key) ? props.getProperty(key) : def;
    }

    /**
     * Returns long property.
     *
     * @param key       key of property.
     * @param def       default value.
     *
     * @return value.
     */
    public static long getLong(String key, long def)
    {
        String valueS = get(key, null);
        long val = def;
        try
        {
            val = Long.parseLong(valueS);
        } catch (NumberFormatException e)
        {
            // Invalid format -- apply default
        }

        return val;
    }

    /**
     * Returns integer property.
     *
     * @param key       key of property.
     * @param def       default value.
     *
     * @return value.
     */
    public static int getInteger(String key, int def)
    {
        String valueS = get(key, null);
        int val = def;
        try
        {
            val = Integer.parseInt(valueS);
        } catch (NumberFormatException e)
        {
            // Invalid format -- apply default
        }

        return val;
    }

    /**
     * Returns the array of strings.
     *
     * @param key key prefix.
     *
     * @return array.
     */
    public static String[] getArray(String key)
    {
        List<String> keys = getKeys(key);
        String[] array = new String[keys.size()];

        int i = 0;
        for (String k : keys) array[i++] = get(k, null);

        return array;
    }
}
