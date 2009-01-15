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
// mailto:pito_salas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: TestCompositeDiscoverer.java,v 1.1.1.1 2006/10/23 13:56:00 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import junit.framework.TestCase;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @see CompositeDiscoverer
 */
public class TestCompositeDiscoverer extends TestCase
{
    /**
     * Tests how empty composite discoverer feels.
     */
    public void testEmpty() throws Exception
    {
        CompositeDiscoverer cd = new CompositeDiscoverer();

        assertEquals(0, cd.getProvidedFields());
        assertEquals(0, cd.discover(new Blog(), null));
    }

    /**
     * First shouldn't be called as it isn't promising to provide fields.
     * Second should be called, but it will not discover any fields.
     */
    public void testNoneOfDiscoverersWorked() throws Exception
    {
        CompositeDiscoverer cd;
        cd = new CompositeDiscoverer(
            new IBlogDiscoverer[]
            {
                new CustomDiscoverer(0, 2),
                new CustomDiscoverer(1, 0)
            }
        );

        assertEquals(0, cd.discover(new Blog(), null));
    }

    /**
     * First should be called, but without any results.
     * Second should be called with positive result.
     */
    public void testSecondWorked() throws Exception
    {
        CompositeDiscoverer cd;
        cd = new CompositeDiscoverer(
            new IBlogDiscoverer[]
            {
                new CustomDiscoverer(2, 0),
                new CustomDiscoverer(1, 1)
            }
        );

        assertEquals(1, cd.discover(new Blog(), new URL("file:///something")));
    }

    /**
     * Second discoverer shouldn't be called as it provides already resolved fields.
     * Third provides one more field and should be called.
     */
    public void testSecondShouldNotBeCalled() throws Exception
    {
        CompositeDiscoverer cd;
        cd = new CompositeDiscoverer(
            new IBlogDiscoverer[]
            {
                new CustomDiscoverer(2, 2),
                new CustomDiscoverer(2, 7),
                new CustomDiscoverer(3, 3)
            }
        );

        assertEquals(3, cd.discover(new Blog(), new URL("file:///something")));
    }

    public void testNoSiteDiscovery() throws Exception
    {
        CompositeDiscoverer cd = setupDiscoverer(
            new Object[][]
            {
                {
                    "file:/a.xml", new Blog(null, null, null, null, "file:///a.xml", 1, null, null, 0, 1)
                }
            }
        );

        Blog blog = new Blog();
        int result = IBlogDiscoverer.FIELD_DATA_URL | IBlogDiscoverer.FIELD_INBOUND_LINKS;
        assertEquals(result, cd.discover(blog, new URL("file:///a.xml")));
        assertEquals(Blog.UNKNOWN, blog.getInboundLinks());
        assertEquals("file:///a.xml", blog.getDataUrl());
        assertNull(blog.getHtmlUrl());
        assertFalse(blog.isIncompleteDiscovery());
    }

    public void testNoSiteDiscovery2() throws Exception
    {
        CompositeDiscoverer cd = setupDiscoverer(
            new Object[][]
            {
                {
                    "file:/a.xml", new Blog(null, null, null, "file:///b.html", "file:///a.xml", 1, null, null, 0, 1)
                }
            }
        );

        Blog blog = new Blog();
        int resolvedFields =
            IBlogDiscoverer.FIELD_DATA_URL |
            IBlogDiscoverer.FIELD_HTML_URL;

        assertEquals(resolvedFields, cd.discover(blog, new URL("file:///a.xml")));
        assertEquals(Blog.UNDISCOVERED, blog.getInboundLinks());
        assertEquals("file:///a.xml", blog.getDataUrl());
        assertEquals("file:///b.html", blog.getHtmlUrl());
        assertFalse(blog.isIncompleteDiscovery());
    }

    public void testSiteDiscovery() throws Exception
    {
        CompositeDiscoverer cd = setupDiscoverer(
            new Object[][]
            {
                {
                    "file:/a.xml", new Blog(null, null, null, "file:///b.html", "file:///a.xml", 1, null, null, 0, 1)
                },
                {
                    "file:/b.html", new Blog(null, null, null, "file:///b.html", "file:///a.xml", 2, null, null, 0, 1)
                }
            }
        );

        Blog blog = new Blog();
        int resolvedFields =
            IBlogDiscoverer.FIELD_DATA_URL |
            IBlogDiscoverer.FIELD_HTML_URL |
            IBlogDiscoverer.FIELD_INBOUND_LINKS;

        assertEquals(resolvedFields, cd.discover(blog, new URL("file:///a.xml")));
        assertEquals(2, blog.getInboundLinks());
        assertEquals("file:///a.xml", blog.getDataUrl());
        assertEquals("file:///b.html", blog.getHtmlUrl());
    }

    private CompositeDiscoverer setupDiscoverer(Object[][] mappings)
    {
        CustomTrickyDiscoverer customTrickyDiscoverer = new CustomTrickyDiscoverer();
        for (int i = 0; i < mappings.length; i++)
        {
            Object[] mapping = mappings[i];
            String url = (String)mapping[0];
            Blog blog = (Blog)mapping[1];
            customTrickyDiscoverer.addBlog(url, blog);
        }

        return new CompositeDiscoverer(new IBlogDiscoverer[] { customTrickyDiscoverer });
    }

    // ---------------------------------------------------------------------------------------------
    // Helper classes
    // ---------------------------------------------------------------------------------------------

    /**
     * Tricky discoverer.
     */
    private static class CustomTrickyDiscoverer implements IBlogDiscoverer
    {
        private Map map;

        /**
         * Creates discoverer.
         */
        public CustomTrickyDiscoverer()
        {
            map = new HashMap();
        }

        /**
         * Add blog under some URL.
         *
         * @param url   URL.
         * @param blog  blog.
         */
        public void addBlog(String url, Blog blog)
        {
            map.put(url, blog);
        }

        /**
         * Sets the properties for discoverer.
         *
         * @param properties properties.
         */
        public void setProperties(Map properties)
        {
        }

        /**
         * Returns the OR'ed list of fields provided by this discoverer.
         *
         * @return list of fields.
         */
        public int getProvidedFields()
        {
            return FIELD_DATA_URL | FIELD_HTML_URL | FIELD_INBOUND_LINKS;
        }

        /**
         * Makes a try to discover the URL and update information in the source blog.
         *
         * @param source source blog.
         * @param url    url to discover.
         *
         * @return fields actually discovered.
         *
         * @throws java.io.IOException in case of any communication problems.
         */
        public int discover(Blog source, URL url) throws IOException
        {
            Blog matchingBlog = findMatchingBlog(url);

            int resolvedFields = 0;

            // copy data from matching blog into source
            if (matchingBlog != null)
            {
                source.setDataUrl(matchingBlog.getDataUrl());
                source.setHtmlUrl(matchingBlog.getHtmlUrl());
                source.setInboundLinks(matchingBlog.getInboundLinks());

                resolvedFields |= FIELD_DATA_URL * (source.getDataUrl() != null ? 1 : 0);
                resolvedFields |= FIELD_HTML_URL * (source.getHtmlUrl() != null ? 1 : 0);
                resolvedFields |= FIELD_INBOUND_LINKS * (source.getInboundLinks() >= 0 ? 1 : 0);
            }

            return resolvedFields;
        }

        private Blog findMatchingBlog(URL aUrl)
        {
            String urlString = aUrl == null ? null : aUrl.toString();

            return urlString == null ? null : (Blog)map.get(urlString);
        }
    }

    /**
     * Custom test discoverer.
     */
    private static class CustomDiscoverer implements IBlogDiscoverer
    {
        private int providedFields;
        private int discoveredFields;

        /**
         * Sets the properties for discoverer.
         *
         * @param properties properties.
         */
        public void setProperties(Map properties)
        {
        }

        /**
         * Creates test discoverer.
         *
         * @param providedFields    fields provided.
         * @param discoveredFields  fields actually discovered.
         */
        public CustomDiscoverer(int providedFields, int discoveredFields)
        {
            this.providedFields = providedFields;
            this.discoveredFields = discoveredFields;
        }

        /**
         * Returns the OR'ed list of fields provided by this discoverer.
         *
         * @return list of fields.
         */
        public int getProvidedFields()
        {
            return providedFields;
        }

        /**
         * Makes a try to discover the URL and update information in the source blog.
         *
         * @param source source blog.
         * @param url    url to discover.
         *
         * @return fields actually discovered.
         */
        public int discover(Blog source, URL url)
        {
            return discoveredFields;
        }
    }
}
