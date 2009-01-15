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
// $Id: TestBasicBlogUpdateTask.java,v 1.1.1.1 2006/10/23 13:56:00 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.persistence.BasicDaoTestCase;
import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.BlogLink;

/**
 * @see BasicBlogUpdateTask
 */
public class TestBasicBlogUpdateTask extends BasicDaoTestCase
{
    /**
     * @see BasicBlogUpdateTask#updateDatabase
     */
    public void testUpdateDatabase()
    {
        Blog blog = new Blog("A", "B", "C", null, "DATA", 1, null, null, Blog.STATUS_VALID, 1);
        blogDao.add(blog);

        try
        {
            // Here we test how the blog is updated within the task.
            // It's enough to update one of the blog fields to test if blog updating takes place
            // and we need to change the URL's to see how links are created.
            blog.setTitle("K");
            blog.setHtmlUrl("HTML");

            BasicBlogUpdateTask bbut = new BasicBlogUpdateTask(null, blogDao, blogLinkDao, "Test", 10000)
            {
                protected Blog getNextBlogForUpdate()
                {
                    return null;
                }
            };

            bbut.updateDatabase(blog);

            // Another data url update
            blog.setDataUrl("DATA2");
            bbut.updateDatabase(blog);

            // Test how links are created
            BlogLink link = blogLinkDao.findByUrl("HTML");
            assertNotNull(link);
            assertEquals(blog.getId(), link.getBlogId().intValue());

            link = blogLinkDao.findByUrl("DATA");
            assertNotNull(link);
            assertEquals(blog.getId(), link.getBlogId().intValue());

            link = blogLinkDao.findByUrl("DATA2");
            assertNotNull(link);
            assertEquals(blog.getId(), link.getBlogId().intValue());

            assertEquals(blog, blogDao.findById(link.getBlogId().intValue()));
        } finally
        {
            blogDao.delete(blog);
        }
    }
}
