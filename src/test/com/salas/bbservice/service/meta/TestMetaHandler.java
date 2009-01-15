// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2006 by R. Pito Salas
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
// $Id $
//

package com.salas.bbservice.service.meta;

import junit.framework.TestCase;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Tests meta handler.
 */
public class TestMetaHandler extends TestCase
{
    /**
     * Tests reporting registered OPML URL's.
     */
    public void testGetOPMLURLs()
    {
        MetaHandler mh = new MetaHandler();
        Hashtable urls = mh.getOPMLURLs();

        assertEquals("Two URLs are currently registered.", 2, urls.size());
        Iterator it = urls.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            assertTrue("Wrong type of URL: " + entry.getKey(), entry.getValue() instanceof byte[]);
            assertTrue("Value shouldn't be empty. URL: " + entry.getKey(), ((byte[])entry.getValue()).length > 0);
        }
    }
}
