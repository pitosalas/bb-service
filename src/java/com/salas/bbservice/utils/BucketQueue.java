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
// $Id: BucketQueue.java,v 1.2 2007/06/06 10:24:03 alg Exp $
//

package com.salas.bbservice.utils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bucket queue is a {@link java.util.concurrent.BlockingQueue} that organizes its items into multiple buckets and
 * returns the items from these buckets from one bucket at a time. This is a very effective way of handling all buckets
 * simultaneously.
 * <p/>
 * This implementation is not thread-safe.
 */
public class BucketQueue<E extends BucketQueueItem> extends AbstractQueue<E> implements BlockingQueue<E>
{
    /** Buckets list. */
    private final List<Bucket<E>> buckets = new ArrayList<Bucket<E>>();

    /** The map of bucket ID's to buckets for a quicker lookup when adding new items. */
    private final Map<Long, Bucket<E>> idToBuckets = new HashMap<Long, Bucket<E>>();

    /** The lock for blocking. */
    private final ReentrantLock lock = new ReentrantLock(true);
    /** The condition for timely operations. */
    private final Condition notEmpty = lock.newCondition();

    /** The number of items in this queue (in all buckets). */
    private int size = 0;

    /** Currently selected bucket. */
    private Bucket<E> currentBucket;
    /** Currently selected bucket index in the {@link #buckets} list. */
    private int currentBucketIndex;

    /**
     * Returns the number of elements in this collection.  If the collection contains more than
     * <tt>Integer.MAX_VALUE</tt> elements, returns <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this collection.
     */
    public int size()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            return size;
        } finally
        {
            lock.unlock();
        }
    }

    /**
     * Returns an iterator over the elements contained in this collection.
     *
     * @return an iterator over the elements contained in this collection.
     */
    public Iterator<E> iterator()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Inserts the specified element into this queue, if possible.  When using queues that may impose insertion
     * restrictions (for example capacity bounds), method <tt>offer</tt> is generally preferable to method {@link
     * java.util.Collection#add}, which can fail to insert an element only by throwing an exception.
     *
     * @param item the element to insert.
     *
     * @return <tt>true</tt> if it was possible to add the element to this queue, else <tt>false</tt>
     */
    public boolean offer(E item)
    {
        boolean added;

        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            // Get bucket
            long bid = item.getBucketId();
            Bucket<E> bucket = idToBuckets.get(bid);

            // If bucket isn't there, create a new one
            if (bucket == null) bucket = createBucket(bid);

            // Offer an item to the bucket
            added = bucket.offer(item);

            // ???
            notEmpty.signal();

            // Increment the size counter if item was added successfully
            if (added) size++;
        } finally
        {
            lock.unlock();
        }

        return added;
    }

    /**
     * Creates the bucket and registers it among the lists and map.
     *
     * @param id ID of the bucket.
     *
     * @return bucket.
     */
    private Bucket<E> createBucket(long id)
    {
        Bucket<E> bucket = new Bucket<E>(id);

        // Register the bucket
        idToBuckets.put(id, bucket);
        buckets.add(bucket);

        // If this is the first bucket, select it
        if (buckets.size() == 1)
        {
            currentBucket = bucket;
            currentBucketIndex = 0;
        }

        return bucket;
    }

    /**
     * Retrieves and removes the head of this queue, or <tt>null</tt> if this queue is empty.
     *
     * @return the head of this queue, or <tt>null</tt> if this queue is empty.
     */
    public E poll()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            return poll0();
        } finally
        {
            lock.unlock();
        }
    }

    /**
     * Returns the item and removes it from a bucket.
     *
     * @return item or <code>NULL</code> id no items left.
     */
    private E poll0()
    {
        // Get next bucket and take an item from it
        Bucket<E> bucket = getBucket(true);
        if (bucket == null) return null;
        
        E item = bucket.poll();

        // Decrement the size counter if something was really taken
        if (item != null) size--;

        // If bucket is empty, remove it
        if (bucket.size() == 0) removeBucket(bucket);
        
        return item;
    }

    /**
     * Retrieves, but does not remove, the head of this queue, returning <tt>null</tt> if this queue is empty.
     *
     * @return the head of this queue, or <tt>null</tt> if this queue is empty.
     */
    public E peek()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            // Get next bucket and take an item from it
            Bucket<E> bucket = getBucket(false);
            return bucket == null ? null : bucket.peek();
        } finally
        {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element into this queue, waiting if necessary up to the specified wait time for space to
     * become available.
     *
     * @param o       the element to add
     * @param timeout how long to wait before giving up, in units of <tt>unit</tt>
     * @param unit    a <tt>TimeUnit</tt> determining how to interpret the <tt>timeout</tt> parameter
     *
     * @return <tt>true</tt> if successful, or <tt>false</tt> if the specified waiting time elapses before space is
     *         available.
     *
     * @throws InterruptedException if interrupted while waiting.
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    public boolean offer(E o, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        return offer(o);
    }

    /**
     * Retrieves and removes the head of this queue, waiting if necessary up to the specified wait time if no elements
     * are present on this queue.
     *
     * @param timeout how long to wait before giving up, in units of <tt>unit</tt>
     * @param unit    a <tt>TimeUnit</tt> determining how to interpret the <tt>timeout</tt> parameter
     *
     * @return the head of this queue, or <tt>null</tt> if the specified waiting time elapses before an element is
     *         present.
     *
     * @throws InterruptedException if interrupted while waiting.
     */
    public E poll(long timeout, TimeUnit unit) throws InterruptedException
    {
        // Convert timeout into nanoseconds
        long nanos = unit.toNanos(timeout);

        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try
        {
            for (; ;)
            {
                E x = poll0();
                if (x != null) return x;
                if (nanos <= 0) return null;

                try
                {
                    nanos = notEmpty.awaitNanos(nanos);
                } catch (InterruptedException ie)
                {
                    notEmpty.signal(); // propagate to non-interrupted thread
                    throw ie;
                }
            }
        } finally
        {
            lock.unlock();
        }
    }

    /**
     * Retrieves and removes the head of this queue, waiting if no elements are present on this queue.
     *
     * @return the head of this queue
     *
     * @throws InterruptedException if interrupted while waiting.
     */
    public E take() throws InterruptedException
    {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try
        {
            try
            {
                while (size() == 0) notEmpty.await();
            } catch (InterruptedException ie)
            {
                notEmpty.signal(); // propagate to non-interrupted thread
                throw ie;
            }

            return poll0();
        } finally
        {
            lock.unlock();
        }
    }

    /**
     * Adds the specified element to this queue, waiting if necessary for space to become available.
     *
     * @param o the element to add
     *
     * @throws InterruptedException if interrupted while waiting.
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    public void put(E o)
            throws InterruptedException
    {
        offer(o);
    }

    /**
     * Returns the number of elements that this queue can ideally (in the absence of memory or resource constraints)
     * accept without blocking, or <tt>Integer.MAX_VALUE</tt> if there is no intrinsic limit. <p>Note that you
     * <em>cannot</em> always tell if an attempt to <tt>add</tt> an element will succeed by inspecting
     * <tt>remainingCapacity</tt> because it may be the case that another thread is about to <tt>put</tt> or
     * <tt>take</tt> an element.
     *
     * @return the remaining capacity
     */
    public int remainingCapacity()
    {
        return Integer.MAX_VALUE;
    }

    /**
     * Removes all available elements from this queue and adds them into the given collection.  This operation may be
     * more efficient than repeatedly polling this queue.  A failure encountered while attempting to <tt>add</tt>
     * elements to collection <tt>c</tt> may result in elements being in neither, either or both collections when the
     * associated exception is thrown. Attempts to drain a queue to itself result in <tt>IllegalArgumentException</tt>.
     * Further, the behavior of this operation is undefined if the specified collection is modified while the operation
     * is in progress.
     *
     * @param c the collection to transfer elements into
     *
     * @return the number of elements transferred.
     *
     * @throws NullPointerException     if c is null
     * @throws IllegalArgumentException if c is this queue
     */
    public int drainTo(Collection<? super E> c)
    {
        return drainTo(c, Integer.MAX_VALUE);
    }

    /**
     * Removes at most the given number of available elements from this queue and adds them into the given collection.
     * A failure encountered while attempting to <tt>add</tt> elements to collection <tt>c</tt> may result in elements
     * being in neither, either or both collections when the associated exception is thrown. Attempts to drain a queue
     * to itself result in <tt>IllegalArgumentException</tt>. Further, the behavior of this operation is undefined if
     * the specified collection is modified while the operation is in progress.
     *
     * @param c           the collection to transfer elements into
     * @param maxElements the maximum number of elements to transfer
     *
     * @return the number of elements transferred.
     *
     * @throws NullPointerException     if c is null
     * @throws IllegalArgumentException if c is this queue
     */
    public int drainTo(Collection<? super E> c, int maxElements)
    {
        if (c == null) throw new NullPointerException();
        if (c == this) throw new IllegalArgumentException();
        if (maxElements <= 0) return 0;

        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            int n = 0;
            E e;
            while (n < maxElements && (e = poll0()) != null)
            {
                c.add(e);
                ++n;
            }
            return n;
        } finally
        {
            lock.unlock();
        }
    }

    // ------------------------------------------------------------------------
    // Supplementary methods
    // ------------------------------------------------------------------------

    @Override
    public boolean remove(Object o)
    {
        boolean removed = false;

        if (o instanceof BucketQueueItem)
        {
            BucketQueueItem item = (BucketQueueItem)o;

            final ReentrantLock lock = this.lock;
            lock.lock();
            try
            {
                // Find the bucket
                Bucket<E> bucket = idToBuckets.get(item.getBucketId());
                if (bucket != null)
                {
                    removed = bucket.remove(item);
                    if (removed) size--;
                }
            } finally
            {
                lock.unlock();
            }
        }

        return removed;
    }

    @Override
    public boolean contains(Object o)
    {
        boolean contains = false;

        if (o instanceof BucketQueueItem)
        {
            BucketQueueItem item = (BucketQueueItem)o;

            final ReentrantLock lock = this.lock;
            lock.lock();
            try
            {
                // Find the bucket
                Bucket<E> bucket = idToBuckets.get(item.getBucketId());
                if (bucket != null) contains = bucket.contains(item);
            } finally
            {
                lock.unlock();
            }
        }

        return contains;
    }

    @Override
    public Object[] toArray()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            Object[] items = new Object[size];
            int pos = 0;
            for (Bucket<E> bucket : buckets)
            {
                Object[] objs = bucket.toArray();
                System.arraycopy(objs, 0, items, pos, objs.length);
                pos += objs.length;
            }

            return items;
        } finally
        {
            lock.unlock();
        }
    }

    /**
     * Returns the bucket and switches to the next if the parameter is <code>TRUE</code>.
     *
     * @param next <code>TRUE</code> to switch to the next bucket.
     *
     * @return bucket or <code>NULL</code> if none present.
     */
    private Bucket<E> getBucket(boolean next)
    {
        if (currentBucket == null)
        {
            // Try to pick the first non-empty bucket
            Bucket bucket = null;
            Iterator<Bucket<E>> it = buckets.iterator();
            while (bucket == null && it.hasNext())
            {
                Bucket<E> b = it.next();
                if (b.size() > 0)
                {
                    bucket = b;
                } else
                {
                    removeBucket(b);
                }
            }
        }

        // Move to the next if necessary
        Bucket<E> bucket = currentBucket;
        if (next)
        {
            currentBucketIndex++;

            if (currentBucketIndex >= buckets.size())
            {
                currentBucketIndex = buckets.size() == 0 ? -1 : 0;
            }

            currentBucket = currentBucketIndex == -1 ? null : buckets.get(currentBucketIndex);
        }

        return bucket;
    }

    /**
     * Removes the bucket from everywhere and updates the pointer.
     *
     * @param bucket bucket.
     *
     * @throws IllegalStateException if the bucket we are removing isn't empty.
     */
    private void removeBucket(Bucket<E> bucket)
    {
        if (bucket.size() > 0) throw new IllegalStateException("Removing non-empty bucket.");

        buckets.remove(bucket);
        idToBuckets.remove(bucket.getId());

        // Update current bucket pointer
        if (currentBucket == bucket)
        {
            // Select a different bucket close to this one
            if (currentBucketIndex < buckets.size())
            {
                // Unchanged
            } else if (buckets.size() > 0)
            {
                // Take the first because the previous was last
                currentBucketIndex = 0;
            } else
            {
                // No buckets
                currentBucketIndex = -1;
            }

            // Init the bucket
            currentBucket = currentBucketIndex == -1 ? null : buckets.get(currentBucketIndex);
        } else
        {
            // Update current bucket index
            currentBucketIndex = buckets.indexOf(currentBucket);
        }
    }

    /** The bucket queue which is used in the main queue. */
    static class Bucket<T> extends LinkedBlockingQueue<T>
    {
        /** Bucket ID. */
        private final long id;

        /**
         * Creates the bucket.
         *
         * @param id bucket ID.
         */
        public Bucket(long id)
        {
            this.id = id;
        }

        /**
         * Returns the bucket ID.
         *
         * @return ID.
         */
        public long getId()
        {
            return id;
        }
    }
}
