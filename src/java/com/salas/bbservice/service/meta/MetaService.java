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
// $Id: MetaService.java,v 1.2 2007/06/06 10:24:03 alg Exp $
//

package com.salas.bbservice.service.meta;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.BlogCommunityField;
import com.salas.bbservice.domain.BlogLink;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.IBlogCommunityFieldDao;
import com.salas.bbservice.persistence.IBlogDao;
import com.salas.bbservice.persistence.IBlogLinkDao;
import com.salas.bbservice.service.meta.discovery.DiscoveryManager;
import com.salas.bbservice.stats.Statistics;
import com.salas.bbservice.utils.Configuration;
import com.salas.bbservice.utils.StringUtils;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * Service providing meta-information about blogs.
 */
public final class MetaService
{
    private static final Hashtable EMPTY_MAP = new Hashtable();

    private static MetaService      instance;

    private IBlogDao                blogDao;
    private IBlogLinkDao            linkDao;
    private IBlogCommunityFieldDao  blogCommunityFieldDao;

    private DiscoveryManager        discoveryManager;
    private MetaDataCleaner         dataCleaner;

    /**
     * Hidden singleton constructor.
     */
    private MetaService()
    {
        linkDao = (IBlogLinkDao)DaoConfig.getDao(IBlogLinkDao.class);
        blogDao = (IBlogDao)DaoConfig.getDao(IBlogDao.class);
        blogCommunityFieldDao =
            (IBlogCommunityFieldDao)DaoConfig.getDao(IBlogCommunityFieldDao.class);

        discoveryManager = new DiscoveryManager(blogDao, linkDao);
        dataCleaner = new MetaDataCleaner(
            Configuration.getBlogCleanupPeriod(),
            Configuration.getBlogLifespan(),
            Configuration.getMinBlogsInDatabase());

        linkDao.deleteUnfinished();
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * A subclass overrides the <code>finalize</code> method to dispose of
     * system resources or to perform other cleanup.
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    protected void finalize() throws Throwable
    {
        dataCleaner.terminate();
        discoveryManager.terminate();
    }

    /**
     * Returns instance of service.
     *
     * @return service instance.
     */
    public static synchronized MetaService getInstance()
    {
        if (instance == null) instance = new MetaService();

        return instance;
    }

    /**
     * Finds blog by specified URL.
     *
     * @param url url.
     * @param clientId ID of the client.
     *
     * @return blog or <code>null</code> if not found.
     */
    public Blog getBlogByUrl(String url, int clientId)
    {
        Blog blog = null;

        Statistics.registerMetaQuery();

        final BlogLink blogLink = linkDao.findByUrl(url);
        if (blogLink != null && blogLink.getBlogId() != null)
        {
            blog = getBlogByLink(blogLink);
        }

        // Schedule discovery only if blog is not present and URL isn't
        // under discovery already.
        if (blog == null && blogLink == null)
        {
            if (discoveryManager.scheduleRegularDiscovery(url, clientId))
            {
                Statistics.registerMetaNewDiscovery();
            }
        }

        return blog;
    }

    private Blog getBlogByLink(final BlogLink aBlogLink)
    {
        Blog blog;

        Statistics.registerMetaDatabaseHit();

        // association record present
        blog = blogDao.findById(aBlogLink.getBlogId().intValue());

        // update last access time
        long lastAccessTime = System.currentTimeMillis();
        aBlogLink.setLastAccessTime(lastAccessTime);
        linkDao.update(aBlogLink);

        blog.setLastAccessTime(lastAccessTime);
        blogDao.update(blog);
        return blog;
    }

    /**
     * Discovers <code>feedUrl</code> and associates it with <code>url</code>.
     *
     * @param url       url to associate with discovered blog.
     * @param feedUrl   feed URL.
     * @param clientId  ID of the client.
     */
    public void discoverAndAssociate(String url, String feedUrl, int clientId)
    {
        BlogLink link = linkDao.findByUrl(url);
        if (link == null ||
            blogDao.findById(link.getBlogId()).getStatus() == Blog.STATUS_INVALID)
        {
            // If we have the link and it points to invalid blog then remove it as we will
            // add it again to a probably valid blog
            if (link != null) linkDao.delete(link);

            Statistics.registerMetaSuggestion();
            discoveryManager.scheduleSuggestedDiscovery(url, feedUrl, clientId);
        }
    }

    /**
     * Sets the values for fields of the Blog specified by Feed URL.
     *
     * @param feedUrl   Feed URL of the blog.
     * @param fields    fields to set in map name:value, where value can be byte[] or
     *                  Vector of byte[].
     * @param clientId  ID of the client.
     *
     * @return null (success) or error message.
     */
    public String setCommunityFields(String feedUrl, Hashtable fields, int clientId)
    {
        if (feedUrl == null) return "Feed URL isn't set";
        if (fields == null) return "Fields aren't set";

        String response = null;

        Blog blog = blogDao.findByDataUrl(feedUrl);
        if (blog == null)
        {
            discoveryManager.scheduleSetCommunityFieldsDiscovery(feedUrl, fields, clientId);
        } else
        {
            setCommunityFields(blog.getId(), fields);
        }

        return response;
    }

    /**
     * Sets community fields one by one to the blog.
     *
     * @param blogId    ID of the blog.
     * @param fields    fields to set.
     */
    public void setCommunityFields(int blogId, Hashtable fields)
    {
        for (Object o : fields.entrySet())
        {
            Map.Entry entry = (Map.Entry)o;
            String fieldName = (String)entry.getKey();

            setCommunityField(blogId, fieldName, entry.getValue());
        }
    }

    /**
     * Sets single- or multi-value field.
     *
     * @param blogId    ID of associated blog.
     * @param name      name of field.
     * @param value     value object (byte[] or Vector of byte[]).
     */
    void setCommunityField(int blogId, String name, Object value)
    {
        if (value instanceof byte[])
        {
            blogCommunityFieldDao.set(blogId, name, StringUtils.fromUTF8((byte[])value));
        } else if (value instanceof Vector)
        {
            Vector vect = (Vector)value;
            byte[][] strings = (byte[][])vect.toArray(new byte[vect.size()][]);

            blogCommunityFieldDao.set(blogId, name, StringUtils.fromUTF8(strings));
        }
    }

    /**
     * Returns fields assigned to the blog.
     *
     * @param feedUrl   Feed URL of the blog.
     *
     * @return fields.
     */
    public Hashtable getCommunityFields(String feedUrl)
    {
        Hashtable fields = null;

        if (feedUrl != null)
        {
            Blog blog = blogDao.findByDataUrl(feedUrl);
            if (blog != null)
            {
                fields = getCommunityFields(blog.getId());
            }
        }

        return fields == null ? EMPTY_MAP : fields;
    }

    /**
     * Returns fields assigned to the blog.
     *
     * @param blogId    ID of the blog.
     *
     * @return fields.
     */
    Hashtable getCommunityFields(int blogId)
    {
        BlogCommunityField[] fields = blogCommunityFieldDao.get(blogId);
        Hashtable fieldsMap = new Hashtable(fields.length);

        for (BlogCommunityField field : fields) addFieldToMap(fieldsMap, field);

        return fieldsMap;
    }

    // Adds field to the map.
    static void addFieldToMap(Hashtable fieldsMap, BlogCommunityField field)
    {
        if (field == null || fieldsMap == null || field.getName() == null ||
            field.getValue() == null) return;

        String name = field.getName();
        Vector values = (Vector)fieldsMap.get(name);

        if (values == null)
        {
            values = new Vector();
            fieldsMap.put(name, values);
        }

        values.add(StringUtils.toUTF8(field.getValue()));
    }

    /**
     * Returns current discovery manager.
     *
     * @return current discovery manager.
     */
    public DiscoveryManager getDiscoveryManager()
    {
        return discoveryManager;
    }
}
