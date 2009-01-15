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
// $Id: TestBlogDao.java,v 1.5 2007/09/06 10:48:34 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.BlogLink;

import java.util.List;

/**
 * @see IBlogDao
 */
public class TestBlogDao extends BasicDaoTestCase
{
    /**
     * @see com.salas.bbservice.domain.Blog#Blog()
     */
    public void testCreateBlog()
    {
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        assertEquals(-1, b.getId());
        assertEquals("A", b.getTitle());
        assertEquals("B", b.getAuthor());
        assertEquals("C", b.getDescription());
        assertEquals("a", b.getHtmlUrl());
        assertEquals("b", b.getDataUrl());
        assertEquals(1, b.getInboundLinks());
        assertEquals(1, b.getRank());
        assertEquals("C", b.getCategory());
        assertEquals("D", b.getLocation());
        assertEquals(Blog.STATUS_PROCESSING, b.getStatus());
        assertTrue(System.currentTimeMillis() - b.getLastAccessTime() < 1000);
    }

    /**
     * @see IBlogDao#add
     */
    public void testAdd()
    {
        clean("B");
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);

        blogDao.add(b);
        assertFalse(-1 == b.getId());

        try
        {
            blogDao.add(b);
            fail("Duplicates are not allowed.");
        } catch (Exception e)
        {
            // Expected behavior
        } finally
        {
            blogDao.delete(b);
        }
    }

    /**
     * @see IBlogDao#update
     */
    public void testUpdate()
    {
        clean("B");
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);

        blogDao.add(b);
        try
        {
            b.setTitle("CC");
            b.setAuthor("B");
            b.setDescription("A");
            b.setDataUrl("M");
            b.setHtmlUrl("N");
            b.setCategory("O");
            b.setLocation("P");
            b.setInboundLinks(2);
            b.setRank(2);
            b.setStatus(Blog.STATUS_VALID);

            blogDao.update(b);
            assertEquals(b, blogDao.findById(b.getId()));
            blogDao.update(blogDao.findById(b.getId()));
            assertEquals(b, blogDao.findById(b.getId()));
        } finally
        {
            // cleanup
            blogDao.delete(b);
        }
    }

    /**
     * @see IBlogDao#delete
     */
    public void testDelete()
    {
        clean("B");
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        blogDao.add(b);
        int id = b.getId();
        assertTrue(-1 != id);

        blogDao.delete(b);
        assertEquals(-1, b.getId());
        assertNull(blogDao.findById(id));
    }

    /**
     * @see IBlogDao#deleteOld
     */
    public void testDeleteOld()
    {
        clean(new String[] { "B", "C"});

        Blog b1, b2, b3;
        b1 = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        b2 = new Blog("A", "B", "C", "B", "C", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        b3 = new Blog("A", "B", "C", "B", "D", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        b1.setLastAccessTime(0);
        b2.setLastAccessTime(4000);
        b3.setLastAccessTime(0);
        blogDao.add(b1);
        blogDao.add(b2);
        blogDao.add(b3);

        int total = blogDao.getTotalRecords();

        try
        {
            blogCommunityFieldDao.set(b3.getId(), "test", "test");

            // Nothing should be removed because of limit=total+1
            blogDao.deleteOld(System.currentTimeMillis() - 1, total + 1);
            assertEquals(total, blogDao.getTotalRecords());

            // Nothing should be removed because of limit=total
            blogDao.deleteOld(System.currentTimeMillis() - 1, total);
            assertEquals(total, blogDao.getTotalRecords());

            // B1, B3 are old
            blogDao.deleteOld(System.currentTimeMillis() - 1, total - 2);
            assertEquals(total - 2, blogDao.getTotalRecords());
            assertNull(blogDao.findById(b1.getId()));
            assertNull(blogDao.findById(b3.getId()));

            // Nothing should be removed
            blogDao.deleteOld(System.currentTimeMillis() - 1, total - 2);
            assertEquals(total - 2, blogDao.getTotalRecords());

            // B2 is old, but we can't get lower the minimum
            blogDao.deleteOld(System.currentTimeMillis() - 4001, total - 1);
            assertEquals(total - 2, blogDao.getTotalRecords());

            // B2 is old and should be removed
            blogDao.deleteOld(System.currentTimeMillis() - 4001, total - 3);
            assertEquals(total - 3, blogDao.getTotalRecords());

            // Final state check
            assertNull(blogDao.findById(b1.getId()));
            assertNull(blogDao.findById(b2.getId()));
            assertNull(blogDao.findById(b3.getId()));
        } finally
        {
            blogDao.delete(b1);
            blogDao.delete(b2);
            blogDao.delete(b3);
        }
    }

    /**
     * @see IBlogDao#delete
     */
    public void testDeleteCascade()
    {
        clean("B");
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        blogDao.add(b);

        BlogLink bhu = new BlogLink(new Integer(b.getId()), "A");
        try
        {
            blogLinkDao.add(bhu);
            assertTrue(-1 != bhu.getId());

            assertEquals(bhu, blogLinkDao.findById(bhu.getId()));

            blogDao.delete(b);
            assertNull(blogLinkDao.findById(bhu.getId()));
        } finally
        {
            if (b.getId() != -1) blogDao.delete(b);
            if (bhu.getId() != -1) blogLinkDao.delete(bhu);
        }
    }

    /**
     * @see IBlogDao#findById
     */
    public void testFindById()
    {
        clean("B");
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        blogDao.add(b);

        try
        {
            // find in database by id
            Blog b2 = blogDao.findById(b.getId());
            assertNotNull(b2);
            assertEquals(b, b2);
        } finally
        {
            blogDao.delete(b);
        }
    }

    /**
     * @see IBlogDao#findByDataUrl
     */
    public void testFindByDataUrl()
    {
        clean("B");
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        blogDao.add(b);

        try
        {
            // find in database by id
            Blog b2 = blogDao.findByDataUrl(b.getDataUrl());
            assertNotNull(b2);
            assertEquals(b, b2);
        } finally
        {
            blogDao.delete(b);
        }
    }

    /**
     * @see IBlogDao#getTotalRecords
     */
    public void testGetTotalRecords()
    {
        // Clean everything
        int total = blogDao.getTotalRecords();

        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        blogDao.add(b);

        try
        {
            assertEquals(total + 1, blogDao.getTotalRecords());
        } finally
        {
            blogDao.delete(b);
        }

        assertEquals(total, blogDao.getTotalRecords());
    }

    /**
     * @see IBlogDao#getLRUBlogs
     */
    public void testGetLRUBlogs()
    {
        Blog b;

        b = new Blog("A", "B", "C", "B", "C", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        b.setLastAccessTime(-1);
        blogDao.add(b);
        int total = blogDao.getTotalRecords();

        try
        {
            Blog[] lruBlogs;
            assertEquals(0, blogDao.getLRUBlogs(total).length);
            assertEquals(0, blogDao.getLRUBlogs(total + 1).length);
            lruBlogs = blogDao.getLRUBlogs(total - 1);
            assertEquals(1, lruBlogs.length);
            assertEquals(b, lruBlogs[0]);

            blogCommunityFieldDao.set(b.getId(), "test", "test");
            lruBlogs = blogDao.getLRUBlogs(total - 1);
            if (lruBlogs.length == 1)
            {
                assertFalse(b.equals(lruBlogs[0]));
            } else assertEquals(0, lruBlogs.length);

            assertEquals(0, blogDao.getLRUBlogs(total + 1).length);
        } finally
        {
            blogDao.delete(b);
        }
    }

    /**
     * Tests looking for blogs.
     */
    public void testFindBlogsUpdatedBefore_Nothing()
    {
        List<Blog> blogs = blogDao.findBlogsUpdatedBefore(5, 10);
        assertEquals(0, blogs.size());
    }

    /**
     * Tests looking for blogs with some in the database.
     */
    public void testFindBlogsUpdatedBefore_Existing()
    {
        // Remove old stuff
        blogDao.deleteOld(0, 0);

        // Add two blogs
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_VALID, 1);
        b.setLastUpdateTime(1);
        blogDao.add(b);
        assertNotNull(blogDao.findById(b.getId()));

        Blog b2 = new Blog("A", "B", "C", "A", "C", 1, "C", "D", Blog.STATUS_VALID, 1);
        b2.setLastUpdateTime(2);
        blogDao.add(b2);
        assertNotNull(blogDao.findById(b2.getId()));

        try
        {
            List<Blog> blogs = blogDao.findBlogsUpdatedBefore(5, 10);
            assertEquals(2, blogs.size());
            assertEquals(b.getId(), blogs.get(0).getId());
            assertEquals(b2.getId(), blogs.get(1).getId());
        } finally
        {
            blogDao.delete(b);
            blogDao.delete(b2);
        }
    }


    /**
     * Tests looking for blogs.
     */
    public void testFindIncompletedBefore_Nothing()
    {
        List<Blog> blogs = blogDao.findBlogsIncompletedBefore(5, 10);
        assertEquals(0, blogs.size());
    }

    /**
     * Tests looking for blogs with some in the database.
     */
    public void testFindIncompletedBefore_Existing()
    {
        // Remove old stuff
        blogDao.deleteOld(0, 0);

        // Add two blogs
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_VALID, 1);
        b.setLastUpdateTime(1);
        b.setIncompleteDiscovery(true);
        blogDao.add(b);
        assertNotNull(blogDao.findById(b.getId()));

        Blog b2 = new Blog("A", "B", "C", "A", "C", 1, "C", "D", Blog.STATUS_VALID, 1);
        b2.setLastUpdateTime(2);
        b2.setIncompleteDiscovery(true);
        blogDao.add(b2);
        assertNotNull(blogDao.findById(b2.getId()));

        try
        {
            List<Blog> blogs = blogDao.findBlogsIncompletedBefore(5, 10);
            assertEquals(2, blogs.size());
            assertEquals(b.getId(), blogs.get(0).getId());
            assertEquals(b2.getId(), blogs.get(1).getId());
        } finally
        {
            blogDao.delete(b);
            blogDao.delete(b2);
        }
    }

    private void clean(String[] dataUrls)
    {
        for (String dataUrl : dataUrls) clean(dataUrl);
    }

    private void clean(String dataUrl)
    {
        final Blog blog = blogDao.findByDataUrl(dataUrl);
        if(blog != null) blogDao.delete(blog);
    }
}
