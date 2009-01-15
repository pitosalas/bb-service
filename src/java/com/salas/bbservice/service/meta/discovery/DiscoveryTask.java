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
// $Id: DiscoveryTask.java,v 1.5 2008/06/04 06:26:30 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.ibatis.dao.client.DaoManager;
import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.BlogLink;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.IBlogDao;
import com.salas.bbservice.persistence.IBlogLinkDao;
import com.salas.bbservice.stats.Statistics;
import com.salas.bbservice.utils.BucketQueueItem;
import com.salas.bbservice.utils.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Discovery worker thread runs all the time and uses callback
 * for getting new tasks. It asks for a new task right after
 * current one is finished. If new task is not available at the moment
 * it goes to sleep for a while and then asks again. It performs
 * the cycle until the server will go down.
 */
class DiscoveryTask implements Runnable, BucketQueueItem
{
    static final String BAD_BLOG_DATA_URL = "~bad_blog~";

    private static final Object     blogUpdateLock;
    private static final List<String> discoveries;

    private final IBlogDao          blogDao;
    private final IBlogLinkDao      blogLinkDao;
    private final IBlogDiscoverer   discoverer;

    private final String            discoveryURL;
    private final long              clientId;

    static
    {
        discoveries = new ArrayList<String>();
        blogUpdateLock = new Object();
    }

    /**
     * Creates worker thread.
     *
     * @param aBlogDao      DAO for blogs.
     * @param aBlogLinkDao  DAO for blog links.
     * @param aDiscoverer   discoverer to use.
     * @param aURL          URL to discover.
     * @param aClientId     ID of the asking client for bucketing.
     */
    public DiscoveryTask(IBlogDao aBlogDao, IBlogLinkDao aBlogLinkDao,
        IBlogDiscoverer aDiscoverer, String aURL, long aClientId)
    {
        if (StringUtils.isEmpty(aURL)) throw new IllegalArgumentException("aURL can't be NULL or empty");

        clientId = aClientId;

        discoveryURL = aURL;

        blogDao = aBlogDao;
        blogLinkDao = aBlogLinkDao;

        discoverer = aDiscoverer;
    }

    /**
     * Returns the ID of the bucket this item belongs to. It's allowed to return <code>-1</code> when unsure.
     *
     * @return ID of the bucket.
     */
    public long getBucketId()
    {
        return clientId;
    }

    /**
     * Main discovery method.
     */
    public void run()
    {
        if (lock(discoveryURL))
        {
            try
            {
                // Perform the discovery
                Blog discoveredBlog = blogDao.findByDataUrl(discoveryURL);
                if (discoveredBlog == null) discoveredBlog = performNewDiscovery(discoveryURL);

                // Perform any post-discovery tasks
                performPostDiscoveryTasks(discoveredBlog);
            } catch (Throwable e)
            {
                toLog("+++ Caught unexpected exception: " + e.getMessage());
                e.printStackTrace();
            } finally
            {
                unlock(discoveryURL);
            }
        }
    }

    /**
     * The hook for the sub-tasks to perform any tasks over the discovered blog
     * after it was added to the database.
     *
     * @param discoveredBlog blog or <code>NULL</code> if nothing was discovered.
     */
    protected void performPostDiscoveryTasks(Blog discoveredBlog)
    {
        // Nothing by default. Override if necessary.
    }

    /**
     * Locks url so no one will be capable of doing discovery of it until it's unlocked.
     *
     * @param url URL to lock.
     *
     * @return <code>TRUE</code> if locked.
     **/
    private static synchronized boolean lock(String url)
    {
        boolean locked;
        synchronized (discoveries)
        {
            locked = !discoveries.contains(url);
            if (locked) discoveries.add(url);
        }

        return locked;
    }

    /**
     * Unlocks the discovery url.
     *
     * @param url URL to unlock.
     **/
    private static synchronized void unlock(String url)
    {
        synchronized (discoveries)
        {
            discoveries.remove(url);
        }
    }

    /**
     * Makes a try to reconstructUrl URL. It removes references or anchors (#something) and adds
     * trailing slash if there's no path or query parameters. So 'http://www.bbc.com' will become
     * 'http://www.bbs.com/' and 'http://www.bbs.com/index.jsp?something' will remain untouched.
     *
     * @param urlString   origianl url.
     *
     * @return reconstructed URL.
     *
     * @throws MalformedURLException if string passed in is not actually URL.
     */
    static URL reconstructUrl(String urlString) throws MalformedURLException
    {
        URL u = new URL(urlString);

        boolean hasPathOrQuery = false;
        StringBuffer result = new StringBuffer();
        result.append(u.getProtocol());
        result.append(":");

        if (!StringUtils.isEmpty(u.getAuthority()))
        {
            result.append("//");
            result.append(u.getAuthority());
        }

        if (!StringUtils.isEmpty(u.getPath()))
        {
            result.append(u.getPath());
            hasPathOrQuery = true;
        }

        if (u.getQuery() != null)
        {
            result.append('?');
            result.append(u.getQuery());
            hasPathOrQuery = true;
        }

        if (!hasPathOrQuery)
        {
            result.append("/");
        }

        return new URL(result.toString());
    }

    /**
     * Performs the discovery of URL.
     *
     * @param aUrl URL to discover.
     *
     * @return discovered blog or <code>NULL</code>.
     **/
    private Blog performNewDiscovery(String aUrl)
    {
        BlogLink link = getLinkToBlog(aUrl);

        // If link already exists then abort discovery
        if (link == null) return null;

        URL url;
        try
        {
            url = reconstructUrl(aUrl);
        } catch (MalformedURLException e)
        {
            // record bad link
            registerBadLink(link);
            return null;
        }

        int urlHash = url.toString().toLowerCase().hashCode();
        toLog("--- New Task: URL=" + url + " / " + urlHash);

        Blog discoveredBlog = null;
        boolean failed = true;

        // Before we start discovery make a quick test for validity of the link
        if (isValidLink(url))
        {
            discoveredBlog = new Blog();
            discoveredBlog.setStatus(Blog.STATUS_VALID);

            try
            {
                long start = System.currentTimeMillis();
                boolean discovered = performActualDiscovery(discoveredBlog, url);
                long length = System.currentTimeMillis() - start;
                Statistics.registerTimeInDiscovery(length);

                final DaoManager daoManager = DaoConfig.getDaoManager();
                try
                {
                    daoManager.startTransaction();
                    boolean incomplete = discoveredBlog.isIncompleteDiscovery();
                    if (discovered || (incomplete && discoveredBlog.getDataUrl() != null))
                    {
//                        if (discovered) toLog("    discovered " + url);
//                        else toLog("    incompletely discovered " + url);

                        updateDatabase(discoveredBlog, link);
                    } else if (incomplete)
                    {
                        // There's no data discovered and some services failed --
                        // releasing reservation
//                        toLog("    incompletely discovered (no data URL) " + url);

// Replaced incomplete discovery with "undiscovery" because of
// server flooding with continued discovery attempts
// AG 06/04/08
//                        blogLinkDao.delete(link);
                        updateDatabase(null, link);
                    } else
                    {
//                        toLog("    undiscovered " + url);
                        updateDatabase(null, link);
                    }
                    daoManager.commitTransaction();
                    failed = false;
//                    toLog("    updated database for " + url);
                } catch (Exception e)
                {
                    toLog("!!! could not update database: " + e.getMessage());
                    e.printStackTrace();
                } finally
                {
                    daoManager.endTransaction();
                }
            } catch (IOException e)
            {
                toLog("!!! could not discover blog because of comm problems.");
            } finally
            {
                // Delete reservation if discovery failed.
                if (failed) blogLinkDao.delete(link);
            }
        } else
        {
            toLog("--- Quick check has FAILED");
            // link points to something invalid - mark as bad blog
            updateDatabase(null, link);
        }

        return failed ? null : discoveredBlog;
    }

    /**
     * Checks if link is valid.
     *
     * @param url link.
     *
     * @return <code>true</code> if valid.
     */
    static boolean isValidLink(URL url)
    {
        // We don't do extended check with reading of bytes any more
        // because it can happen that site isn't available at the moment,
        // but the data can still be retreived from the services.

        String protocol = url.getProtocol();
        String host = url.getHost();

        // We do not accept:
        // 1. Files URL's
        // 2. URL's in local network or to local host
        return !protocol.equalsIgnoreCase("file") &&
               !host.equalsIgnoreCase("localhost") &&
               !host.equals("127.0.0.1") &&
               !host.startsWith("192.168.");
    }

    private BlogLink getLinkToBlog(String url)
    {
        BlogLink link;

        synchronized (blogUpdateLock)
        {
            link = blogLinkDao.findByUrl(url);
            if (link == null)
            {
                link = new BlogLink(null, url);
                blogLinkDao.add(link);
            } else link = null;
        }

        return link;
    }

    /**
     * Performs the actual discovery of Blog.
     *
     * @param blog  blog structure to fill information of.
     * @param url   URL to discover
     *
     * @return <code>TRUE</code> if something new was discovered.
     *
     * @throws java.io.IOException if there's an exception with discovery.
     **/
    private boolean performActualDiscovery(Blog blog, URL url) throws IOException
    {
        boolean newDiscoveries = false;

        if (blog != null && discoverer.getProvidedFields() > 0)
        {
            int mandatory = IBlogDiscoverer.FIELD_DATA_URL;
            newDiscoveries = (discoverer.discover(blog, url) & mandatory) == mandatory;
        }

        return newDiscoveries;
    }

    /**
     * Updates the database in accordance with discoveries.
     *
     * @param discoveredBlog blog to update the database for.
     * @param aLink          link that was used to discover the blog (added to the database).
     **/
    void updateDatabase(Blog discoveredBlog, BlogLink aLink)
    {
        if (discoveredBlog != null &&
            ((!discoveredBlog.isIncompleteDiscovery() && discoveredBlog.getDataUrl() != null) ||
             discoveredBlog.isIncompleteDiscovery()))
        {
            String dataUrl = discoveredBlog.getDataUrl();
            String htmlUrl = discoveredBlog.getHtmlUrl();
            int dateUHC = BlogLink.url2hashCode(dataUrl);
            int htmlUHC = BlogLink.url2hashCode(htmlUrl);

            // Synchronization is necessary to prevent addition of two similar blog records when
            // two threads are discoverying single blog through different references.
            synchronized (blogUpdateLock)
            {
                // Find out if we already have similar blog
                Integer blogId = findMatchingBlogId(discoveredBlog);

                // If we don't have blog record yet then create it
                if (blogId == null)
                {
//                    toLog("    adding blog: " + discoveredBlog);
                    blogDao.add(discoveredBlog);
                    blogId = discoveredBlog.getId();
                } else discoveredBlog.setId(blogId);

                aLink.setBlogId(blogId);
                blogLinkDao.update(aLink);

                // Create data URL & HTML links if they aren't mentioned in blog link reservation
                int linkUHC = aLink.getUrlHashCode();
                if (dateUHC != 0 && dateUHC != linkUHC) addOrUpdateLink(dataUrl, blogId);
                if (htmlUHC != 0 && htmlUHC != linkUHC) addOrUpdateLink(htmlUrl, blogId);
            }
        } else
        {
            // If nothing was discovered about the link then this link is likely to point in
            // some wrong direction -- register it as invalid
            registerBadLink(aLink);
        }
    }

    // Links URL to the blog with specified ID
    protected void addOrUpdateLink(final String aUrl, Integer aBlogId)
    {
        if (!StringUtils.isEmpty(aUrl))
        {
            BlogLink link = blogLinkDao.findByUrl(aUrl);
            if (link != null)
            {
                link.setBlogId(aBlogId);
                blogLinkDao.update(link);
            } else
            {
                blogLinkDao.add(new BlogLink(aBlogId, aUrl));
//                toLog("   added URL to BlogLinks: " + aUrl);
            }
        }
    }

    /**
     * Finds the ID of blog having data or HTML URL's matching the specified blog.
     *
     * @param blog blog to find a match for.
     *
     * @return ID of the matching blog or <code>NULL</code>.
     **/
    Integer findMatchingBlogId(Blog blog)
    {
        Integer id = null;

        if (blog != null)
        {
            final String dataUrl = blog.getDataUrl();
            final String htmlUrl = blog.getHtmlUrl();
            BlogLink link = null;

            // Try data link first
            if (!StringUtils.isEmpty(dataUrl)) link = filterBadBlogLink(blogLinkDao.findByUrl(dataUrl));

            // Try html link then
            if (link == null && StringUtils.isEmpty(htmlUrl)) link = filterBadBlogLink(blogLinkDao.findByUrl(htmlUrl));

            // Record blog ID if we managed to find any blog which isn't bad
            if (link != null) id = link.getBlogId();
        }

        return id;
    }

    /**
     * If the link points to good blog, pass it through.
     *
     * @param link link to test.
     *
     * @return non-NULL link if it points to good blog.
     */
    protected BlogLink filterBadBlogLink(BlogLink link)
    {
        if (link != null)
        {
            Integer blogId = link.getBlogId();
            if (blogId != null)
            {
                Blog target = blogDao.findById(blogId);
                if (target.getDataUrl().equals(BAD_BLOG_DATA_URL)) link = null;
            } else link = null;
        }
        return link;
    }

    /**
     * Directs a link to specially created blog, incorporating all bad links.
     *
     * @param link registers a link as to pointing to a bad blog.
     **/
    private synchronized void registerBadLink(BlogLink link)
    {
        Blog badBlog = blogDao.findByDataUrl(BAD_BLOG_DATA_URL);

        if (badBlog == null)
        {
            badBlog = new Blog(null, null, null, null, BAD_BLOG_DATA_URL, 0, null, null,
                Blog.STATUS_INVALID, 0);

            // Set last update time to max to avoid it to be taken by updater
            badBlog.setLastUpdateTime(Long.MAX_VALUE);
            badBlog.setIncompleteDiscovery(false);
            blogDao.add(badBlog);
        }

        link.setBlogId(badBlog.getId());
        if (link.getId() > -1)
        {
            blogLinkDao.update(link);
        } else
        {
            synchronized (blogUpdateLock)
            {
                blogLinkDao.add(link);
            }
        }
    }

    private void toLog(String str)
    {
        CompositeDiscoverer.toLog(str);
    }

    /**
     * Compares this object to the other.
     *
     * @param o objec to compare with.
     *
     * @return TRUE if equal.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof DiscoveryTask)) return false;

        final DiscoveryTask dt = (DiscoveryTask)o;

        return discoveryURL.equals(dt.discoveryURL);
    }

    /**
     * Hash code of this task object.
     *
     * @return hash code.
     */
    public int hashCode()
    {
        return discoveryURL.hashCode();
    }
}
