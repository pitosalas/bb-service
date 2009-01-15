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
// $Id: TestMetaService.java,v 1.2 2007/06/06 10:24:03 alg Exp $
//

package com.salas.bbservice.service.meta;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.BlogCommunityField;
import com.salas.bbservice.persistence.BasicDaoTestCase;
import com.salas.bbservice.utils.StringUtils;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @see MetaService
 */
public class TestMetaService extends BasicDaoTestCase
{
    /**
     * @see MetaService#addFieldToMap
     */
    public void testAddFieldsToMap()
    {
        Hashtable fields = new Hashtable();
        BlogCommunityField field;

        // Adding null-field
        field = null;
        MetaService.addFieldToMap(fields, field);
        assertEquals(0, fields.size());

        // Adding field without name
        field = new BlogCommunityField(-1, null, null);
        MetaService.addFieldToMap(fields, field);
        assertEquals(0, fields.size());

        // Adding field without value
        field = new BlogCommunityField(-1, "test", null);
        MetaService.addFieldToMap(fields, field);
        assertEquals(0, fields.size());

        // Adding field
        field = new BlogCommunityField(-1, "test", "a");
        MetaService.addFieldToMap(fields, field);
        assertEquals(1, fields.size());
        assertTrue(fields.get("test") instanceof Vector);
        assertEquals(1, ((Vector)fields.get("test")).size());
        assertTrue(((Vector)fields.get("test")).get(0) instanceof byte[]);
        assertTrue(Arrays.equals(StringUtils.toUTF8("a"),
            (byte[])((Vector)fields.get("test")).get(0)));

        // Adding another value to the same field
        field = new BlogCommunityField(-1, "test", "b");
        MetaService.addFieldToMap(fields, field);
        assertEquals(1, fields.size());
        assertTrue(fields.get("test") instanceof Vector);
        assertEquals(2, ((Vector)fields.get("test")).size());
        assertTrue(((Vector)fields.get("test")).get(0) instanceof byte[]);
        assertTrue(Arrays.equals(StringUtils.toUTF8("a"),
            (byte[])((Vector)fields.get("test")).get(0)));
        assertTrue(((Vector)fields.get("test")).get(1) instanceof byte[]);
        assertTrue(Arrays.equals(StringUtils.toUTF8("b"),
            (byte[])((Vector)fields.get("test")).get(1)));

        // Adding another field field
        field = new BlogCommunityField(-1, "test2", "a");
        MetaService.addFieldToMap(fields, field);
        assertEquals(2, fields.size());
        assertTrue(fields.get("test2") instanceof Vector);
        assertEquals(1, ((Vector)fields.get("test2")).size());
        assertTrue(((Vector)fields.get("test2")).get(0) instanceof byte[]);
        assertTrue(Arrays.equals(StringUtils.toUTF8("a"),
            (byte[])((Vector)fields.get("test2")).get(0)));
    }

    /**
     * @see MetaService#setCommunityField(int, String, Object)
     */
    public void testSetCommunityField()
    {
        Blog blog;
        BlogCommunityField[] fields;
        MetaService ms = MetaService.getInstance();

        blog = new Blog("A", "B", "C", "D", "E", 1, null, null, Blog.STATUS_INVALID, 1);
        blogDao.add(blog);

        try
        {
            int blogId = blog.getId();

            // Placing test field with initial value "a"
            ms.setCommunityField(blogId, "test", StringUtils.toUTF8("a"));

            fields = blogCommunityFieldDao.get(blogId);
            assertEquals(1, fields.length);
            assertEquals("test", fields[0].getName());
            assertEquals("a", fields[0].getValue());

            // Checking how the value is overriden with a new value "b"
            ms.setCommunityField(blogId, "test", StringUtils.toUTF8("b"));

            fields = blogCommunityFieldDao.get(blogId);
            assertEquals(1, fields.length);
            assertEquals("test", fields[0].getName());
            assertEquals("b", fields[0].getValue());

            // Setting multiple values for the field
            ms.setCommunityField(blogId, "test", new Vector()
            {
                {
                    add(StringUtils.toUTF8("a"));
                    add(StringUtils.toUTF8("b"));
                }
            });

            fields = blogCommunityFieldDao.get(blogId);
            assertEquals(2, fields.length);
            assertEquals("test", fields[0].getName());
            assertEquals("a", fields[0].getValue());
            assertEquals("test", fields[1].getName());
            assertEquals("b", fields[1].getValue());

            // Cheching removing the values
            ms.setCommunityField(blogId, "test", StringUtils.toUTF8(""));
            fields = blogCommunityFieldDao.get(blogId);
            assertEquals(0, fields.length);
        } finally
        {
            if (blog.getId() != -1) blogDao.delete(blog);
        }
    }

    /**
     * @see MetaService#setCommunityFields(String, Hashtable)
     */
    public void testSetCommunityFields()
    {
        Blog blog;
        BlogCommunityField[] fields;
        Hashtable fieldsMap;
        MetaService ms = MetaService.getInstance();

        blog = new Blog("A", "B", "C", "D", "URL", 1, null, null, Blog.STATUS_INVALID, 1);
        blogDao.add(blog);

        try
        {
            int blogId = blog.getId();
            fieldsMap = new Hashtable();

            // Referring to non-existing Blog
            assertNotNull(ms.setCommunityFields("Non-existing URL", null, -1));

            // Placing empty fields map
            assertNull(ms.setCommunityFields("URL", fieldsMap, -1));
            fields = blogCommunityFieldDao.get(blogId);
            assertEquals(0, fields.length);

            // Placing single field
            fieldsMap.put("test", StringUtils.toUTF8("a"));
            assertNull(ms.setCommunityFields("URL", fieldsMap, -1));
            fields = blogCommunityFieldDao.get(blogId);
            assertEquals(1, fields.length);
            assertEquals("test", fields[0].getName());
            assertEquals("a", fields[0].getValue());

            // Overriding the previous value
            fieldsMap.put("test", StringUtils.toUTF8("b"));
            assertNull(ms.setCommunityFields("URL", fieldsMap, -1));
            fields = blogCommunityFieldDao.get(blogId);
            assertEquals(1, fields.length);
            assertEquals("test", fields[0].getName());
            assertEquals("b", fields[0].getValue());

            // Placing multiple values for the field
            fieldsMap.put("test", new Vector()
            {
                {
                    add(StringUtils.toUTF8("a"));
                    add(StringUtils.toUTF8("b"));
                }
            });
            assertNull(ms.setCommunityFields("URL", fieldsMap, -1));
            fields = blogCommunityFieldDao.get(blogId);
            assertEquals(2, fields.length);
            assertEquals("test", fields[0].getName());
            assertEquals("a", fields[0].getValue());
            assertEquals("test", fields[1].getName());
            assertEquals("b", fields[1].getValue());

            // Placing multiple fields
            fieldsMap.put("test", StringUtils.toUTF8("a"));
            fieldsMap.put("test2", StringUtils.toUTF8("b"));
            assertNull(ms.setCommunityFields("URL", fieldsMap, -1));
            fields = blogCommunityFieldDao.get(blogId);
            assertEquals(2, fields.length);
            assertEquals("test", fields[0].getName());
            assertEquals("a", fields[0].getValue());
            assertEquals("test2", fields[1].getName());
            assertEquals("b", fields[1].getValue());
        } finally
        {
            if (blog.getId() != -1) blogDao.delete(blog);
        }
    }

    /**
     * @see MetaService#getCommunityFields(int)
     */
    public void testGetCommunityFields1()
    {
        Blog blog;
        Hashtable fieldsMap;
        MetaService ms = MetaService.getInstance();

        blog = new Blog("A", "B", "C", "D", "URL", 1, null, null, Blog.STATUS_INVALID, 1);
        blogDao.add(blog);

        try
        {
            Hashtable fieldsMap2;

            int blogId = blog.getId();
            fieldsMap = new Hashtable();

            // Query for non-existing Blog
            fieldsMap2 = ms.getCommunityFields(Integer.MAX_VALUE);
            assertEquals(0, fieldsMap2.size());

            // Placing multiple values for the field
            fieldsMap.put("test", new Vector()
            {
                {
                    add(StringUtils.toUTF8("a"));
                    add(StringUtils.toUTF8("b"));
                }
            });
            fieldsMap.put("test2", StringUtils.toUTF8("b"));
            assertNull(ms.setCommunityFields("URL", fieldsMap, -1));

            fieldsMap2 = ms.getCommunityFields(blogId);
            assertNotNull(fieldsMap2);
            assertEquals(2, fieldsMap2.size());

            assertTrue(fieldsMap2.get("test") instanceof Vector);
            Vector field = ((Vector)fieldsMap2.get("test"));
            assertEquals(2, field.size());
            assertTrue(Arrays.equals(StringUtils.toUTF8("a"), (byte[])field.get(0)));
            assertTrue(Arrays.equals(StringUtils.toUTF8("b"), (byte[])field.get(1)));

            assertTrue(fieldsMap2.get("test2") instanceof Vector);
            field = ((Vector)fieldsMap2.get("test2"));
            assertEquals(1, field.size());
            assertTrue(Arrays.equals(StringUtils.toUTF8("b"), (byte[])field.get(0)));
        } finally
        {
            if (blog.getId() != -1) blogDao.delete(blog);
        }
    }

    /**
     * @see MetaService#getCommunityFields(String)
     */
    public void testGetCommunityFields2()
    {
        Blog blog;
        Hashtable fieldsMap;
        MetaService ms = MetaService.getInstance();

        blog = new Blog("A", "B", "C", "D", "URL", 1, null, null, Blog.STATUS_INVALID, 1);
        blogDao.add(blog);

        try
        {
            Hashtable fieldsMap2;
            fieldsMap = new Hashtable();

            // Query for non-existing Blog
            fieldsMap2 = ms.getCommunityFields("Non-Existing URL");
            assertEquals(0, fieldsMap2.size());

            // Placing multiple values for the field
            fieldsMap.put("test", new Vector()
            {
                {
                    add(StringUtils.toUTF8("a"));
                    add(StringUtils.toUTF8("b"));
                }
            });
            fieldsMap.put("test2", StringUtils.toUTF8("b"));
            assertNull(ms.setCommunityFields("URL", fieldsMap, -1));

            fieldsMap2 = ms.getCommunityFields("URL");
            assertNotNull(fieldsMap2);
            assertEquals(2, fieldsMap2.size());

            assertTrue(fieldsMap2.get("test") instanceof Vector);
            Vector field = ((Vector)fieldsMap2.get("test"));
            assertEquals(2, field.size());
            assertTrue(Arrays.equals(StringUtils.toUTF8("a"), (byte[])field.get(0)));
            assertTrue(Arrays.equals(StringUtils.toUTF8("b"), (byte[])field.get(1)));

            assertTrue(fieldsMap2.get("test2") instanceof Vector);
            field = ((Vector)fieldsMap2.get("test2"));
            assertEquals(1, field.size());
            assertTrue(Arrays.equals(StringUtils.toUTF8("b"), (byte[])field.get(0)));
        } finally
        {
            if (blog.getId() != -1) blogDao.delete(blog);
        }
    }
}
