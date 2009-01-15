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
// mailto:pitosalas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: TestDirectDiscoverer.java,v 1.1.1.1 2006/10/23 13:56:01 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import junit.framework.TestCase;

import java.net.URL;

/**
 * @see DirectDiscoverer
 */
public class TestDirectDiscoverer extends TestCase
{
    /**
     * Tests how invalid URL's are handled. No error should be generated.
     *
     * @throws Exception in case of error.
     */
    public void testErrorHandling() throws Exception
    {
        DirectDiscoverer dis = new DirectDiscoverer();

        // FileNotFound
        URL url1 = new URL("http://localhost/something/");

        // Auth required
        URL url2 = new URL("http://www.blogbridge.com:8080/bbservice/admin/");

        assertEquals(0, dis.discover(new Blog(), url1));
        assertEquals(0, dis.discover(new Blog(), url2));
    }
}
