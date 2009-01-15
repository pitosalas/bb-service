// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2007 by R. Pito Salas
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
// $Id: SuggestedDiscoveryTask.java,v 1.1 2007/06/06 10:40:20 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.persistence.IBlogDao;
import com.salas.bbservice.persistence.IBlogLinkDao;

/** Task for regular discovery and final association of blog with the additional URL. */
public class SuggestedDiscoveryTask extends DiscoveryTask
{
    private final String associationURL;

    /**
     * Creates worker thread.
     *
     * @param aBlogDao     DAO for blogs.
     * @param aBlogLinkDao DAO for blog links.
     * @param aDiscoverer  discoverer to use.
     * @param aURL         URL to discover.
     * @param aClientId    ID of the asking client for bucketing.
     * @param aAssociationURL additional association URL.
     */
    public SuggestedDiscoveryTask(IBlogDao aBlogDao, IBlogLinkDao aBlogLinkDao,
        IBlogDiscoverer aDiscoverer, String aURL, long aClientId, String aAssociationURL)
    {
        super(aBlogDao, aBlogLinkDao, aDiscoverer, aURL, aClientId);

        associationURL = aAssociationURL;
    }

    @Override
    protected void performPostDiscoveryTasks(Blog discoveredBlog)
    {
        addOrUpdateLink(associationURL, discoveredBlog.getId());
    }
}
