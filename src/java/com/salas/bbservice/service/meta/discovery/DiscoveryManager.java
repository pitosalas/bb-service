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
// $Id: DiscoveryManager.java,v 1.2 2007/06/06 10:24:03 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.persistence.IBlogDao;
import com.salas.bbservice.persistence.IBlogLinkDao;
import com.salas.bbservice.utils.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Discovery manager is responsible for background discovery
 * of Blogs. It maintains the queue of tasks and provides them
 * too <code>DiscoveryThreads</code> which are running all the way.
 */
public class DiscoveryManager
{
    private static final Logger LOG = Logger.getLogger(DiscoveryManager.class.getName());

    private IBlogDao            blogDao;
    private IBlogLinkDao        blogLinkDao;

    private IBlogDiscoverer     discoverer;

    private BucketQueue         queue;
    private Executor            executor;

    /**
     * Creates discovery manager.
     *
     * @param aBlogDao      DAO for blogs.
     * @param aBlogLinkDao  DAO for blog links.
     */
    public DiscoveryManager(IBlogDao aBlogDao, IBlogLinkDao aBlogLinkDao)
    {
        blogDao = aBlogDao;
        blogLinkDao = aBlogLinkDao;

        discoverer = setupDiscoverer();

        queue = new BucketQueue();

        int threads = Configuration.getDiscoveryThreads();
        executor = new ThreadPoolExecutor(threads, threads, 30, TimeUnit.SECONDS,
            queue, new NamedThreadFactory("Discoverer"));

        // Setup periodical updates.
        setupScheduledUpdates();
        setupIncompleteRediscovery();
    }

    private void setupIncompleteRediscovery()
    {
        IncompleteRediscoveryTask task = new IncompleteRediscoveryTask(discoverer, blogDao,
            blogLinkDao,
            Configuration.getIncompleteUpdatePeriod(),
            Configuration.getIncompleteCheckPeriod());

        task.start();
    }

    private void setupScheduledUpdates()
    {
        ScheduledUpdateTask task = new ScheduledUpdateTask(discoverer, blogDao,
            blogLinkDao,
            Configuration.getScheduledUpdatePeriod(),
            Configuration.getScheduledCheckPeriod());

        task.start();
    }

    private IBlogDiscoverer setupDiscoverer()
    {
        IBlogDiscoverer[] discs;
        Map[] discoverers = Configuration.getDiscoverers();
        List<IBlogDiscoverer> discsArray = new ArrayList<IBlogDiscoverer>();
        for (Map discovererProperties : discoverers)
        {
            IBlogDiscoverer disc = initDiscoverer(discovererProperties);
            if (disc != null) discsArray.add(disc);
        }

        discs = discsArray.toArray(new IBlogDiscoverer[0]);

        LOG.info(discs.length + " discoverers initialized.");

        return new CompositeDiscoverer(discs);
    }

    private IBlogDiscoverer initDiscoverer(Map properties)
    {
        IBlogDiscoverer instance = null;
        String className = (String)properties.get("class");
        if (!StringUtils.isEmpty(className))
        {
            try
            {
                // Create dicoverer
                instance = (IBlogDiscoverer)Class.forName(className).newInstance();
                instance.setProperties(properties);

                // Apply fuse if necessary
                String fuse = (String)properties.get("fuse");
                boolean fused = false;
                if (fuse != null && Boolean.valueOf(fuse))
                {
                    instance = new AutoDisablingDiscovererWrapper(instance);
                    fused = true;
                }

                // Output processing info
                LOG.info("Created discoverer: " + className +
                    (fused ? " [fused]" : Constants.EMPTY_STRING));
            } catch (Exception e)
            {
                LOG.log(Level.SEVERE, "Could not initialize discoverer: " + className, e);
            }
        }

        return instance;
    }

    /**
     * Performs scheduling discovery of specified URL.
     *
     * @param aUrl URL to discover.
     * @param clientId ID of the client.
     *
     * @return <code>FALSE</code> if the task is already on the queue.
     */
    public boolean scheduleRegularDiscovery(String aUrl, int clientId)
    {
        return scheduleTask(new DiscoveryTask(blogDao, blogLinkDao, discoverer, aUrl, clientId));
    }

    /**
     * Schedules discovery of feed URL and associates the resulting blog with
     * url in addition to all standard associations.
     *
     * @param aUrl      URL associate with resulting blog.
     * @param aFeedUrl  URL of feed to discover.
     * @param clientId  ID of the client.
     */
    public void scheduleSuggestedDiscovery(String aUrl, String aFeedUrl, int clientId)
    {
        scheduleTask(new SuggestedDiscoveryTask(blogDao, blogLinkDao, discoverer, aFeedUrl, clientId, aUrl));
    }

    /**
     * Schedules discovery of feed by URL and setting of community fields in
     * successful case.
     *
     * @param aFeedUrl  URL of feed to discover.
     * @param aFields   fields to set.
     * @param clientId  ID of the client.
     */
    public void scheduleSetCommunityFieldsDiscovery(String aFeedUrl, Hashtable aFields, int clientId)
    {
        scheduleTask(new SetCommunityFieldsDiscoveryTask(blogDao, blogLinkDao, discoverer, aFeedUrl, clientId, aFields));
    }

    // schedules task.
    private boolean scheduleTask(DiscoveryTask aTask)
    {
        executor.execute(aTask);
        return true;
    }

    /**
     * Causes all threads to stop.
     */
    public void terminate()
    {
    }

    /**
     * Returns current discovery queue size.
     *
     * @return queue size; 
     */
    public int getDiscoveryQueueSize()
    {
        return queue.size();
    }
}
