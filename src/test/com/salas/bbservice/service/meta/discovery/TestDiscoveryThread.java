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
// $Id: TestDiscoveryThread.java,v 1.2 2007/06/06 10:24:03 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.ibatis.dao.client.DaoManager;
import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.BlogLink;
import com.salas.bbservice.persistence.BasicDaoTestCase;
import com.salas.bbservice.persistence.DaoConfig;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @see DiscoveryTask
 */
public class TestDiscoveryThread extends BasicDaoTestCase
{
    /**
     * @see DiscoveryTask#reconstructUrl
     */
    public void testReconstructUrl()
    {
        checkReconstruction("http://www.bbc.com/", "http://www.bbc.com/");
        checkReconstruction("http://www.bbc.com/", "http://www.bbc.com");
        checkReconstruction("http://www.bbc.com/test", "http://www.bbc.com/test");
        checkReconstruction("http://www.bbc.com/test/", "http://www.bbc.com/test/");
        checkReconstruction("http://www.bbc.com/test/index.jsp",
                            "http://www.bbc.com/test/index.jsp");
        checkReconstruction("http://www.bbc.com/test/index.jsp",
                            "http://www.bbc.com/test/index.jsp#test");
        checkReconstruction("http://www.bbc.com/test/index.jsp?qry=1&qry2=2",
                            "http://www.bbc.com/test/index.jsp?qry=1&qry2=2");
    }

    public void testIsValidLink() throws Exception
    {
        // File protocol isn't allowed
        ClassLoader classLoader = TestDiscoveryThread.class.getClassLoader();
        URL l1 = classLoader.getResource(
            "com/salas/bbservice/service/meta/discovery/TestDiscoveryThread.class");

        assertFalse(DiscoveryTask.isValidLink(l1));

        URL l2 = new URL("http://ppp.lll.com");
        assertTrue(DiscoveryTask.isValidLink(l2));

        URL l3 = new URL("file:///home/unknown.xml");
        assertFalse(DiscoveryTask.isValidLink(l3));

        URL l4 = new URL("http://127.0.0.1:5584/test.xml");
        assertFalse(DiscoveryTask.isValidLink(l4));

        URL l5 = new URL("http://192.168.1.1/test.xml");
        assertFalse(DiscoveryTask.isValidLink(l5));

        URL l6 = new URL("http://localhost:8080/test.xml");
        assertFalse(DiscoveryTask.isValidLink(l6));
    }

    public void testFindMatchingBlogId()
    {
        // Create blog
        Blog blog = new Blog("A", "B", "C", "http://some.strange.url.com/index.html",
            "http://some.strange.url.com/rss.xml", 2401, null, null, Blog.STATUS_VALID, 0);
        blogDao.add(blog);

        final DaoManager daoManager = DaoConfig.getDaoManager();
        try
        {
            // Create links to the blog with html and data urls
            BlogLink linkHtml = new BlogLink(blog.getId(), blog.getHtmlUrl());
            BlogLink linkData = new BlogLink(blog.getId(), blog.getDataUrl());
            blogLinkDao.add(linkHtml);
            blogLinkDao.add(linkData);

            daoManager.startTransaction();
            try
            {
                DiscoveryTask dt = new DiscoveryTask(blogDao, blogLinkDao, null, null, -1);
                assertEquals(new Integer(blog.getId()), dt.findMatchingBlogId(blog));
            } finally
            {
                daoManager.endTransaction();
            }
        } finally
        {
            blogDao.delete(blog);
        }
    }

    /**
     * Tests situation when there's a link to the bad blog record. Then the discovery
     * finally resolves the blog and it becomes good. The link should be updated to
     * point to a valid blog record now.
     */
    public void testUpdateDatabaseFromBadToGood()
    {
        String url = "http://some.blog/";
        String xmlUrl = url + "index.xml";
        DiscoveryTask dt = new DiscoveryTask(blogDao, blogLinkDao, null, null, -1);
        Blog badBlog = blogDao.findByDataUrl("~bad_blog~");

        // Cleanup
        BlogLink clink = blogLinkDao.findByUrl(url);
        if (clink != null) blogLinkDao.delete(clink);
        clink = blogLinkDao.findByUrl(xmlUrl);
        if (clink != null) blogLinkDao.delete(clink);
        Blog cblog = blogDao.findByDataUrl(xmlUrl);
        if (cblog != null) blogDao.delete(cblog);

        // Create bad blog record first and point both xml and site urls to it
        BlogLink link = new BlogLink(null, xmlUrl);
        dt.updateDatabase(null, link);
        assertNull(dt.filterBadBlogLink(link));
        assertEquals(badBlog.getId(), link.getBlogId().intValue());
        link = new BlogLink(null, url);
        dt.updateDatabase(null, link);
        assertNull(dt.filterBadBlogLink(link));
        assertEquals(badBlog.getId(), link.getBlogId().intValue());

        // Resolve the blog and update the database
        Blog blog = new Blog("_Test blog", "_A", "_D", url, xmlUrl, -2, null, null, Blog.STATUS_VALID, -2);
        dt.updateDatabase(blog, link);
        assertEquals(blog.getId(), link.getBlogId().intValue());
        assertEquals(blog.getId(), blogLinkDao.findByUrl(url).getBlogId().intValue());
        assertEquals(blog.getId(), blogLinkDao.findByUrl(xmlUrl).getBlogId().intValue());
    }

    private void checkReconstruction(String dest, String source)
    {
        try
        {
            assertEquals(dest, DiscoveryTask.reconstructUrl(source).toString());
        } catch (MalformedURLException e)
        {
            fail();
        }
    }
}
