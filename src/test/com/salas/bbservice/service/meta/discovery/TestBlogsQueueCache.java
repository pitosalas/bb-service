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
// $Id: TestBlogsQueueCache.java,v 1.1 2007/09/06 10:48:34 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Tests blogs queue cache.
 */
public class TestBlogsQueueCache extends TestCase
{
    private LinkedList<List<Blog>> queue;
    private MyBQC bqc;

    protected void setUp()
            throws Exception
    {
        super.setUp();

        queue = new LinkedList<List<Blog>>();
        bqc = new MyBQC(-1000, queue);
    }

    public void testEmpty()
    {
        assertNull(bqc.poll());
    }

    public void testReal()
    {
        // Add two then one blog
        Blog b1 = new Blog();
        Blog b2 = new Blog();
        Blog b3 = new Blog();
        queue.add(blogs(b1, b2));

        // Check two blogs
        assertTrue(b1 == bqc.poll());
        assertTrue(b2 == bqc.poll());
        assertNull(bqc.poll());

        // Add one more
        queue.add(blogs(b3));
        assertTrue(b3 == bqc.poll());
        assertNull(bqc.poll());
    }

    public void testThrottling()
            throws InterruptedException
    {
        bqc = new MyBQC(500, queue);

        // Check to induce anti-throttling
        assertNull(bqc.poll());

        // Adding a blog shouldn't resolve the issue
        Blog b1 = new Blog();
        queue.add(blogs(b1));
        assertNull(bqc.poll());

        // Sleep for a little while to pass anti-th period
        Thread.sleep(500);

        // Now the blog should be returned
        Blog bl = bqc.poll();
        assertTrue(b1 == bl);
    }

    /**
     * Convert blogs to the list.
     *
     * @param blogs blogs.
     *
     * @return list.
     */
    private List<Blog> blogs(Blog ... blogs)
    {
        return Arrays.asList(blogs);
    }

    /** Custom test BQC. */
    private class MyBQC extends BlogsQueueCache
    {
        private final LinkedList<List<Blog>> data;

        /**
         * Creates a BQC.
         *
         * @param antiThrottlingPeriod period.
         * @param data array of data to return upon fetches.
         */
        private MyBQC(long antiThrottlingPeriod, LinkedList<List<Blog>> data)
        {
            super(antiThrottlingPeriod);
            this.data = data;
        }

        /**
         * Provides new items for the cache.
         *
         * @return new items.
         */
        protected List<Blog> fetchNewItems()
        {
            return data.isEmpty() ? null : data.poll();
        }
    }
}
