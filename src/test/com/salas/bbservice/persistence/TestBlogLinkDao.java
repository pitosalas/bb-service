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
// $Id: TestBlogLinkDao.java,v 1.1.1.1 2006/10/23 13:55:54 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.*;

/**
 * @see IBlogLinkDao
 */
public class TestBlogLinkDao extends BasicDaoTestCase
{
    /**
     * @see com.salas.bbservice.domain.BlogLink#BlogLink()
     */
    public void testCreateBlog()
    {
        BlogLink b = new BlogLink(new Integer(1), " A");
        assertEquals(-1, b.getId());
        assertEquals(new Integer(1), b.getBlogId());
        assertEquals("a".hashCode(), b.getUrlHashCode());
    }

    /**
     * @see IBlogLinkDao#add
     */
    public void testAdd()
    {
        BlogLink bhu = new BlogLink(null, " A");

        // add to no blog
        try
        {
            blogLinkDao.add(bhu);
        } catch (Exception e)
        {
            e.printStackTrace();
            fail("Can add URL links to not existing blogs.");
        } finally
        {
            blogLinkDao.delete(bhu);
        }

        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        blogDao.add(b);
        assertFalse(-1 == b.getId());

        bhu = new BlogLink(new Integer(b.getId()), " A");
        blogLinkDao.add(bhu);
        assertFalse(-1 == bhu.getId());

        try
        {
            blogLinkDao.add(bhu);
            fail("Duplicates are not allowed.");
        } catch (Exception e)
        {
            // Expected behavior
        } finally
        {
            blogLinkDao.delete(bhu);
            blogDao.delete(b);
        }
    }

    /**
     * @see IBlogLinkDao#delete
     */
    public void testDelete()
    {
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        blogDao.add(b);

        try
        {
            BlogLink bhu = new BlogLink(new Integer(b.getId()), " A");
            blogLinkDao.add(bhu);
            int id = bhu.getId();
            assertTrue(-1 != id);

            blogLinkDao.delete(bhu);
            assertEquals(-1, bhu.getId());
            assertNull(blogLinkDao.findById(id));
        } finally
        {
            blogDao.delete(b);
        }
    }

    /**
     * @see IBlogLinkDao#findById
     */
    public void testFindById()
    {
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        blogDao.add(b);

        BlogLink bhu = new BlogLink(new Integer(b.getId()), " A");
        try
        {
            blogLinkDao.add(bhu);

            // find in database by id
            BlogLink bhu2 = blogLinkDao.findById(bhu.getId());
            assertNotNull(bhu2);
            assertEquals(bhu, bhu2);
        } finally
        {
            blogLinkDao.delete(bhu);
            blogDao.delete(b);
        }
    }

    /**
     * @see IBlogLinkDao#findByUrl(String)
     */
    public void testFindByUrl()
    {
        Blog b = new Blog("A", "B", "C", "A", "B", 1, "C", "D", Blog.STATUS_PROCESSING, 1);
        blogDao.add(b);

        BlogLink bhu = new BlogLink(new Integer(b.getId()), "http://Tbray.org/ongoing/ongoing.rss");
        try
        {
            blogLinkDao.add(bhu);

            assertEquals(bhu, blogLinkDao.findByUrl("http://tbray.org/ongoing/ongoing.rss"));
            assertEquals(bhu, blogLinkDao.findByUrl("HTTP://TBRAY.ORG/ONGOING/ONGOING.RSS"));
            assertEquals(bhu, blogLinkDao.findByUrl(" http://tbray.org/ongoing/ongoing.rss "));
            assertNull(blogLinkDao.findByUrl(" aa "));
        } finally
        {
            blogLinkDao.delete(bhu);
            blogDao.delete(b);
        }
    }
}
