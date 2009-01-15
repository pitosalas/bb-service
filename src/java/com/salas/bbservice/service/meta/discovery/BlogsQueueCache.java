// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2007 by R. Pito Salas
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
// $Id: BlogsQueueCache.java,v 1.1 2007/09/06 10:49:19 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The cache of queued Blogs.
 */
abstract class BlogsQueueCache extends LinkedList<Blog>
{
    private final ReadWriteLock blogsListLock = new ReentrantReadWriteLock();
    private final Lock r = blogsListLock.readLock();
    private final Lock w = blogsListLock.writeLock();

    /** Amount of time to wait before asking for the next fetch. */
    private final long antiThrottlingPeriod;

    /** The time of last fetch attempt. */
    private long lastEmptyFetchTime;

    /**
     * Creates a blogs queue with anti-throttling period.
     *
     * @param antiThrottlingPeriod period in ms.
     */
    protected BlogsQueueCache(long antiThrottlingPeriod)
    {
        this.antiThrottlingPeriod = antiThrottlingPeriod;
    }

    @Override
    public Blog poll()
    {
        Blog blog;

        r.lock();
        if (isEmpty())
        {
            // Upgrade a lock
            r.unlock();
            w.lock();

            // If the blogs list is still empty after the lock upgrade,
            // load a new portion of blogs.
            if (isEmpty() && (System.currentTimeMillis() - lastEmptyFetchTime > antiThrottlingPeriod))
            {
                List<Blog> newItems = fetchNewItems();
                if (newItems != null)
                {
                    addAll(newItems);
                    lastEmptyFetchTime = 0;
                } else
                {
                    lastEmptyFetchTime = System.currentTimeMillis();
                }
            }

            // Downgrade a lock
            r.lock();
            w.unlock();
        }

        blog = super.poll();
        r.unlock();

        return blog;
    }

    /**
     * Provides new items for the cache.
     *
     * @return new items.
     */
    protected abstract List<Blog> fetchNewItems();
}
