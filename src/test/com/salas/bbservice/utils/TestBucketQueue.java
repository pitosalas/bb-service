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
// $Id: TestBucketQueue.java,v 1.1 2007/06/05 16:08:42 alg Exp $
//

package com.salas.bbservice.utils;

import java.util.ArrayList;
import java.util.List;

/** Tests {@link BucketQueue} */
public class TestBucketQueue extends AbstractBucketQueueTest
{
    private static final SampleBucketQueueItem item1 = new SampleBucketQueueItem(1);
    private static final SampleBucketQueueItem item2 = new SampleBucketQueueItem(2);

    /** Testing how the size changes. */
    public void testSize()
    {
        assertEquals("Shoud be empty", 0, queue.size());
    }

    /** Tests adding new items. */
    public void testAdd()
    {
        // Add an item and check
        queue.add(item1);
        assertEquals("Should be added", 1, queue.size());
    }

    /** Tests reporting unsupported operation. */
    public void testIterator()
    {
        try
        {
            queue.iterator();
            fail("UnsupportedOperationException is expected");
        } catch (UnsupportedOperationException ex)
        {
            // Expected
        }
    }

    /** Testing the offer command. */
    public void testOffer()
    {
        assertTrue("Should accept", queue.offer(item1));
    }

    /** Testing the offer command with the timeout. */
    public void testOfferTimely()
    {
        assertTrue("Should accept", queue.offer(item1));
    }

    /** Testing the poll command. */
    public void testPoll()
    {
        // Check the empty queue
        SampleBucketQueueItem item = queue.poll();
        assertNull("Queue is empty", item);

        // Add an item and check again
        queue.add(item1);
        assertTrue("Wrong item", item1 == queue.poll());
        assertNull("Queue is empty", queue.poll());
    }

    /** Testing the poll command with the timeout. */
    public void testPollTimely()
    {
    }

    /** Testing the peek command. */
    public void testPeek()
    {
        // Check the empty queue
        SampleBucketQueueItem item = queue.peek();
        assertNull("Queue is empty", item);

        // Add an item and check again
        queue.add(item1);
        assertTrue("Wrong item", item1 == queue.peek());
        assertTrue("Wrong item", item1 == queue.peek());
    }

    /**
     * Testing the take command.
     *
     * @throws InterruptedException never.
     **/
    public void testTake() throws InterruptedException
    {
        // Take immediately
        queue.add(item1);
        assertTrue("Wrong item", item1 == queue.take());

        // Tests that the thread is waiting
        Thread th = createTakeThread();
        th.start();

        // Sleep long enough to be sure the take thread is running
        Thread.sleep(200);

        assertTrue("Should be running and waiting for the queue", th.isAlive());

        // Interrupt
        th.interrupt();
        assertFalse("Shouldn't be running", th.isAlive());

        // Start again and sleep
        th = createTakeThread();
        th.start();
        Thread.sleep(200);

        // Put an item in the queue which is supposed to release the take thread
        queue.add(item1);

        // Sleep a bit to let the take thread process the item
        Thread.sleep(100);
        assertFalse("Shouldn't be interrupted", th.isInterrupted());
        assertFalse("Shouldn't be running", th.isAlive());
    }

    /**
     * Creates a helper take thread.
     *
     * @return thread.
     */
    private Thread createTakeThread()
    {
        return new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    queue.take();
                } catch (InterruptedException e)
                {
                    // Is expected
                }
            }
        };
    }

    /** Testing reporting maximum remaining capacity. */
    public void testRemainingCapacity()
    {
        assertEquals("Should be maximum", Integer.MAX_VALUE, queue.remainingCapacity());

        queue.add(item1);
        assertEquals("Should be maximum", Integer.MAX_VALUE, queue.remainingCapacity());
    }

    /** Tests draining items to the given collection. */
    public void testDrainTo()
    {
        List<SampleBucketQueueItem> collector = new ArrayList<SampleBucketQueueItem>();

        // Drain empty queue
        queue.drainTo(collector);
        assertEquals("Should be empty", 0, collector.size());

        // Add a couple of items to different buckets and drain
        queue.add(item1);
        queue.add(item2);
        queue.drainTo(collector);
        assertEquals("Should have both items", 2, collector.size());
    }

    /** Tests draining items with the limitation. */
    public void testDrainToMax()
    {
        List<SampleBucketQueueItem> collector = new ArrayList<SampleBucketQueueItem>();

        // Drain empty queue
        queue.drainTo(collector, 1);
        assertEquals("Should be empty", 0, collector.size());

        // Add a couple of items to different buckets and drain
        queue.add(item1);
        queue.add(item2);
        queue.drainTo(collector, 1);
        assertEquals("Should have one item", 1, collector.size());
        assertTrue("Wrong item", item1 == collector.get(0));
    }

    /** Tests removing items. */
    public void testRemove()
    {
        // Add two items
        queue.add(item1);
        queue.add(item2);
        assertEquals("Should be two items", 2, queue.size());

        // Remove them one by one and check the size
        assertTrue("Should be removed", queue.remove(item1));
        assertEquals("One should be left", 1, queue.size());
        assertFalse("Shouldn't be removed", queue.remove(item1));
        assertEquals("One should be left", 1, queue.size());

        assertTrue("Should be removed", queue.remove(item2));
        assertEquals("None should be left", 0, queue.size());
    }

    /** Tests contains check. */
    public void testContains()
    {
        queue.add(item1);
        assertTrue("Contains", queue.contains(item1));
        assertFalse("Doesn't contain", queue.contains(item2));
    }

    /** Tests converting the queue into the array. */
    public void testToArray()
    {
        // Get empty array
        Object[] ar = queue.toArray();
        assertEquals("Should be empty", 0, ar.length);

        // Add two items and get the array
        queue.add(item1);
        queue.add(item2);
        ar = queue.toArray();
        assertEquals("Both items should be there", 2, ar.length);
        assertTrue("Wrong item", item1 == ar[0]);
        assertTrue("Wrong item", item2 == ar[1]);
    }
}