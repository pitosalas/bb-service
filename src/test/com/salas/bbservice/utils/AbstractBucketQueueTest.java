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
// $Id: AbstractBucketQueueTest.java,v 1.1 2007/06/06 08:04:47 alg Exp $
//

package com.salas.bbservice.utils;

import junit.framework.TestCase;

/**
 * Abstract test with some tools.
 */
public class AbstractBucketQueueTest extends TestCase
{
    /**
     * Queue to test.
     */
    protected BucketQueue<SampleBucketQueueItem> queue;

    /**
     * Initializes environment.
     *
     * @throws Exception if anything goes wrong.
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        queue = new BucketQueue<SampleBucketQueueItem>();
    }

    /**
     * Sample item that knows only how to report the bucket id.
     */
    protected static class SampleBucketQueueItem implements BucketQueueItem
    {
        private final long bid;

        /**
         * Creates an item.
         *
         * @param bid bucket id.
         */
        public SampleBucketQueueItem(long bid)
        {
            this.bid = bid;
        }

        /**
         * Returns the ID of the bucket this item belongs to. It's allowed to return <code>-1</code> when unsure.
         *
         * @return ID of the bucket.
         */
        public long getBucketId()
        {
            return bid;
        }
    }
}
