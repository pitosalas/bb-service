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
// $Id: BasicBlogUpdateTask.java,v 1.2 2008/06/04 06:04:43 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.BlogLink;
import com.salas.bbservice.persistence.IBlogDao;
import com.salas.bbservice.persistence.IBlogLinkDao;
import com.salas.bbservice.utils.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Timer task for blog updates.
 */
abstract class BasicBlogUpdateTask extends Thread
{
    private static final List reservations = new ArrayList();

    private IBlogDiscoverer discoverer;
    protected IBlogDao      blogDao;
    protected IBlogLinkDao  blogLinkDao;
    private long            taskPeriod;

    /**
     * Constructs task.
     *
     * @param aDiscoverer   discoverer to use.
     * @param aBlogDao      blog dao to use.
     * @param aBlogLinkDao  blog link dao to use.
     * @param name          name of task.
     * @param aTaskPeriod   period of task execution.
     */
    protected BasicBlogUpdateTask(IBlogDiscoverer aDiscoverer, IBlogDao aBlogDao,
                                  IBlogLinkDao aBlogLinkDao, String name, long aTaskPeriod)
    {
        super(name);
        setDaemon(true);

        discoverer = aDiscoverer;
        blogDao = aBlogDao;
        blogLinkDao = aBlogLinkDao;
        taskPeriod = aTaskPeriod;
    }

    /**
     * Returns next blog to update.
     *
     * @return blog for update.
     */
    protected abstract Blog getNextBlogForUpdate();

    /**
     * The action to be performed by this timer task.
     */
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep(taskPeriod);

                Blog blog;
                while ((blog = getNextBlogForUpdate()) != null)
                {
                    if (blog != null && lock(blog))
                    {
                        toLog("U>> Started updating: " + blog.getDataUrl());

                        try
                        {
                            processUpdate(blog);
                        } catch (Exception e)
                        {
                            toLog("U!! Failed to process update: " + e.getMessage());
                            e.printStackTrace();
                        } finally
                        {
                            unlock(blog);
                        }

//                        toLog("U<< Finished updating: " + blog.getDataUrl());
                    }
                }
            } catch (Exception e)
            {
                toLog("U!! Unexpected exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /** Updates blog. */
    private void processUpdate(Blog blog)
    {
        int oldInboundLinks = blog.getInboundLinks();
        try
        {
            // We start from html URL if it's present because we most of online services
            // will provide data URL to us if it isn't present. Moreover we need inlinks
            // of html URL only and starting from html URL will give us everything we need
            // in most short time. If we don't have html URL then we will discover it
            // with starting from data URL and then discoverer will make another loop
            // for html URL to get proper inlinks value.
            String url = blog.getHtmlUrl();
            if (StringUtils.isEmpty(url)) url = blog.getDataUrl();
            if (!StringUtils.isEmpty(url)) discoverInformation(blog, url);
        } catch (IOException e)
        {
            toLog("U!! Failed to discover information: " + e.getMessage());
            e.printStackTrace();
        } finally
        {
            if (blog.getInboundLinks() == Blog.UNDISCOVERED)
            {
                blog.setInboundLinks(oldInboundLinks);
            }
            updateDatabase(blog);
        }
    }

    private void discoverInformation(Blog aBlog, String url) throws IOException
    {
        aBlog.setInboundLinks(Blog.UNDISCOVERED);
        URL dataUrl = new URL(url);
        discoverer.discover(aBlog, dataUrl);
    }

    /**
     * Updates blog database record and adds new links if necessary.
     *
     * @param aBlog blog to update.
     */
    void updateDatabase(Blog aBlog)
    {
        aBlog.setLastUpdateTime(System.currentTimeMillis());

//        toLog("U-- Updating database: " + aBlog);
        blogDao.update(aBlog);

        // Also make necessary registrations of links
        Integer blogId = new Integer(aBlog.getId());
        String dataUrl = aBlog.getDataUrl();
        String htmlUrl = aBlog.getHtmlUrl();

        if (!StringUtils.isEmpty(dataUrl) && blogLinkDao.findByUrl(dataUrl) == null)
        {
            blogLinkDao.add(new BlogLink(blogId, dataUrl));
//            toLog("   added data URL to BlogLinks: " + dataUrl);
        }

        if (!StringUtils.isEmpty(htmlUrl) && blogLinkDao.findByUrl(htmlUrl) == null)
        {
            blogLinkDao.add(new BlogLink(blogId, htmlUrl));
//            toLog("   added HTML URL to BlogLinks: " + htmlUrl);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Logging
    // ---------------------------------------------------------------------------------------------

    private static final DateFormat DATE_FORMAT =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    /** Writes message to log. */
    private void toLog(String str)
    {
        System.out.println(DATE_FORMAT.format(new Date()) +
            " [" + Thread.currentThread().getName() + "] " + str);
    }

    // ---------------------------------------------------------------------------------------------
    // Locking
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns true if blog was successfully locked.
     *
     * @param blog blog to lock.
     *
     * @return true if locked.
     */
    protected boolean lock(Blog blog)
    {
        Integer key = new Integer(blog.getId());
        synchronized (reservations)
        {
            if (reservations.contains(key)) return false;
            reservations.add(key);
        }

        return true;
    }

    /**
     * Unlocks previously locked blog.
     *
     * @param blog blog to unlock.
     */
    protected void unlock(Blog blog)
    {
        Integer key = new Integer(blog.getId());
        synchronized (reservations)
        {
            reservations.remove(key);
        }
    }
}
