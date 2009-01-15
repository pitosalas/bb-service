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
// $Id: BucketQueueItem.java,v 1.2 2007/06/06 10:24:03 alg Exp $
//

package com.salas.bbservice.utils;

/**
 * A bucket item is an object being added to the {@link com.salas.bbservice.utils.BucketQueue}.
 * The {@link #getBucketId()} an item reports is used to balance the fetching of the items from
 * the queue. If you provide the same ID for items you add, the queue will return the items
 * as if it was a normal blocking list-based queue.
 */
public interface BucketQueueItem
{
    /**
     * Returns the ID of the bucket this item belongs to. It's allowed to return <code>-1</code>
     * when unsure.
     *
     * @return ID of the bucket.
     */
    long getBucketId();
}
