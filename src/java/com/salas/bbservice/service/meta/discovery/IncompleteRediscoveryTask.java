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
// $Id: IncompleteRediscoveryTask.java,v 1.2 2007/09/06 10:48:34 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.persistence.IBlogDao;
import com.salas.bbservice.persistence.IBlogLinkDao;

import java.util.List;

/**
 * Task for rediscovery of incomplete blogs.
 */
public class IncompleteRediscoveryTask extends BasicBlogUpdateTask
{
    private static final String NAME = "Incomplete Rediscovery";

    private long    minimumPause;

    /** The size of the cache to fetch blogs in blocks. */
    private static final int CACHE_SIZE = 1000;
    /** Time to wait before asking the database for the next portion if the previous one was empty. */
    private static final int ANTI_THROTTLING_PERIOD = 60 * 1000;

    /** Queue of tasks. */
    private final BlogsQueueCache queue = new BlogsQueueCache(ANTI_THROTTLING_PERIOD)
    {
        protected List<Blog> fetchNewItems()
        {
            long time = System.currentTimeMillis() - minimumPause;
            return blogDao.findBlogsIncompletedBefore(time, CACHE_SIZE);
        }
    };

    /**
     * Constructs task.
     *
     * @param aDiscoverer   discoverer to use.
     * @param aBlogDao      blog dao to use.
     * @param aBlogLinkDao  blog link dao to use.
     * @param aMinimumPause minimum pause between rediscoveries in ms.
     * @param aTaskPeriod   period of task execution.
     */
    protected IncompleteRediscoveryTask(IBlogDiscoverer aDiscoverer, IBlogDao aBlogDao,
                                        IBlogLinkDao aBlogLinkDao, long aMinimumPause,
                                        long aTaskPeriod)
    {
        super(aDiscoverer, aBlogDao, aBlogLinkDao, NAME, aTaskPeriod);
        minimumPause = aMinimumPause;
    }

    /**
     * Returns next blog to update.
     *
     * @return blog for update.
     */
    protected Blog getNextBlogForUpdate()
    {
        return queue.poll();
    }
}
