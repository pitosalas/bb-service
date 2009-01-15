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
// $Id: NamedThreadFactory.java,v 1.1 2007/06/06 10:24:03 alg Exp $
//

package com.salas.bbservice.utils;

import java.util.concurrent.ThreadFactory;

/**
 * The factory that gives names to its daemon threads.
 */
public class NamedThreadFactory implements ThreadFactory
{
    private final String threadName;
    private long seq = 1;

    /**
     * Creates a named thread factory.
     *
     * @param threadName thread name.
     */
    public NamedThreadFactory(String threadName)
    {
        this.threadName = threadName;
    }

    /**
     * Constructs a new <tt>Thread</tt>.  Implementations may also initialize priority, name, daemon status,
     * <tt>ThreadGroup</tt>, etc.
     *
     * @param r a runnable to be executed by new thread instance
     *
     * @return constructed thread
     */
    public Thread newThread(Runnable r)
    {
        Thread th = new Thread(r, threadName + " " + (seq++));
        th.setDaemon(true);
        return th;
    }
}
