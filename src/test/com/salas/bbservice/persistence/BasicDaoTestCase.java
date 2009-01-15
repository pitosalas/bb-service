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
// $Id: BasicDaoTestCase.java,v 1.2 2007/09/06 10:48:34 alg Exp $
//

package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.Blog;
import com.salas.bbservice.domain.UserGuide;
import com.salas.bbservice.utils.AbstractTestCase;

/**
 * Basic super-class for DAO tests.
 */
public abstract class BasicDaoTestCase extends AbstractTestCase
{
    protected IUserDao                  userDao;
    protected IChannelDao               channelDao;
    protected IUserGuideDao             userGuideDao;
    protected IUserChannelDao           userChannelDao;
    protected IUserQueryFeedDao         userQueryFeedDao;
    protected IUserSearchFeedDao        userSearchFeedDao;
    protected IUserPreferenceDao        userPreferenceDao;
    protected IUserSharedTagsDao        userSharedTagsDao;
    protected IUserReadingListDao       userReadingListDao;
    protected IInstallationDao          installationDao;
    protected IStatsDao                 statsDao;
    protected IBlogDao                  blogDao;
    protected IBlogLinkDao              blogLinkDao;
    protected IBlogCommunityFieldDao    blogCommunityFieldDao;
    protected IClientErrorDao           clientErrorDao;
    protected IFeedbackMessageDao       feedbackMessageDao;
    protected IVersionsDao              versionsDao;
    private static int blogCounter = 0;

    protected void setUp() throws Exception
    {
        channelDao = (IChannelDao)DaoConfig.getDao(IChannelDao.class);

        userDao = (IUserDao)DaoConfig.getDao(IUserDao.class);
        userGuideDao = (IUserGuideDao)DaoConfig.getDao(IUserGuideDao.class);
        userChannelDao = (IUserChannelDao)DaoConfig.getDao(IUserChannelDao.class);
        userQueryFeedDao = (IUserQueryFeedDao)DaoConfig.getDao(IUserQueryFeedDao.class);
        userSearchFeedDao = (IUserSearchFeedDao)DaoConfig.getDao(IUserSearchFeedDao.class);
        userPreferenceDao = (IUserPreferenceDao)DaoConfig.getDao(IUserPreferenceDao.class);
        userSharedTagsDao = (IUserSharedTagsDao)DaoConfig.getDao(IUserSharedTagsDao.class);
        userReadingListDao = (IUserReadingListDao)DaoConfig.getDao(IUserReadingListDao.class);

        installationDao = (IInstallationDao)DaoConfig.getDao(IInstallationDao.class);
        statsDao = (IStatsDao)DaoConfig.getDao(IStatsDao.class);

        blogDao = (IBlogDao)DaoConfig.getDao(IBlogDao.class);
        blogLinkDao = (IBlogLinkDao)DaoConfig.getDao(IBlogLinkDao.class);
        blogCommunityFieldDao =
            (IBlogCommunityFieldDao)DaoConfig.getDao(IBlogCommunityFieldDao.class);

        clientErrorDao = (IClientErrorDao)DaoConfig.getDao(IClientErrorDao.class);
        feedbackMessageDao = (IFeedbackMessageDao)DaoConfig.getDao(IFeedbackMessageDao.class);

        versionsDao = (IVersionsDao)DaoConfig.getDao(IVersionsDao.class);
    }

    protected UserGuide guide1()
    {
        return new UserGuide(-1, "A", "B", 1, false, null, null, false, 1, false, false);
    }

    protected UserGuide guide2()
    {
        return new UserGuide(-1, "B", "B", 2, false, null, null, false, 2, false, false);
    }

    protected UserGuide guide3()
    {
        return new UserGuide(1, "A", "B", 2, true, "A", "B", true, 3, true, true);
    }

    /**
     * Adds a blog with last update time.
     *
     * @param lastUpdateTime time.
     *
     * @return blog.
     */
    public Blog addBlog(long lastUpdateTime)
    {
        Blog b = new Blog("A", "A", "A", "A", Integer.toString(blogCounter++), 1, "A", "A", Blog.STATUS_VALID, 1);
        b.setLastUpdateTime(lastUpdateTime);
        blogDao.add(b);

        return b;
    }
}
