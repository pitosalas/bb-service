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
// $Id: TestBucketQueueFunctional.java,v 1.2 2007/06/05 16:11:19 alg Exp $
//

package com.salas.bbservice.utils;

/**
 * Tests {@link com.salas.bbservice.utils.BucketQueue}
 */
public class TestBucketQueueFunctional extends AbstractBucketQueueTest
{
    /**
     * Tests handling when the first bucket in loop empties.
     */
    public void testRemovingFirstBucket()
    {
        addItem(1, 2, 2, 3, 3);
        assertItem(1, 2, 3, 2, 3);
    }

    /**
     * Tests handling when the middle bucket in loop empties.
     */
    public void testRemovingMiddleBucket()
    {
        addItem(1, 1, 2, 3, 3);
        assertItem(1, 2, 3, 1, 3);
    }
    
    /**
     * Tests handling when the last bucket in loop empties.
     */
    public void testRemovingLastBucket()
    {
        addItem(1, 2, 3, 1, 2);
        assertItem(1, 2, 3, 1, 2);
    }

    /**
     * Tests balancing when the buckets aren't equally loaded.
     */
    public void testBalancing()
    {
        addItem(1, 1, 1, 1, 1, 2, 3, 2);
        assertItem(1, 2, 3, 1, 2, 1, 1, 1);
    }

    /**
     * Tests how the queue manages heavy load -- 100000 items.
     */
    public void testHeavyLoad()
    {
        for (int i = 0; i < 100000; i++)
        {
            // Place items in random buckets
            addItem((int)(Math.random() * 10));
        }

        assertEquals("Wrong size", 100000, queue.size());
    }

    /**
     * Adds items to given buckets.
     *
     * @param buckets buckets list.
     */
    private void addItem(int ... buckets)
    {
        for (int b : buckets) queue.add(new SampleBucketQueueItem(b));
    }

    /**
     * Picks one item at a time and checks if it's from the right bucket.
     *
     * @param buckets buckets.
     */
    private void assertItem(int ... buckets)
    {
        for (int bucket : buckets)
        {
            SampleBucketQueueItem item = queue.poll();
            assertEquals("Item from the wrong bucket", bucket, item.getBucketId());
        }
    }
}