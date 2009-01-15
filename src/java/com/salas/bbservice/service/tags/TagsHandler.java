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
// $Id: TagsHandler.java,v 1.1 2006/10/23 14:24:44 alg Exp $
//

package com.salas.bbservice.service.tags;

import com.ibatis.dao.client.DaoManager;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.domain.UserSharedTags;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.IUserSharedTagsDao;
import com.salas.bbservice.service.account.AccountHandler;
import com.salas.bbservice.utils.StringUtils;

import java.util.Vector;
import java.util.List;

/**
 * Tags service handler.
 */
public class TagsHandler
{
    static final String ERR_NO_SESSION = "Session doesn't exist.";

    private IUserSharedTagsDao userPreferenceDao;

    /**
     * Tags/untags some link. If link is required to be tagged then all parameters are
     * mandatory. If untagged then leave <code>tags</code> and <code>description</code> empty.
     *
     * @param sessionId     session of user requesting the operation.
     * @param url           link to tag/update/untag.
     * @param feed          <code>TRUE</code> if link corresponds to XML feed.
     * @param tags          space-delimited list of quoted tags.
     * @param description   description of link.
     *
     * @return error or empty string on success.
     */
    public String tag(int sessionId, byte[] url, boolean feed, byte[] tags, byte[] description)
    {
        return tag(sessionId, url, feed, tags, description, null);
    }

    /**
     * Tags/untags some link. If link is required to be tagged then all parameters are
     * mandatory. If untagged then leave <code>tags</code> and <code>description</code> empty.
     *
     * @param sessionId     session of user requesting the operation.
     * @param url           link to tag/update/untag.
     * @param feed          <code>TRUE</code> if link corresponds to XML feed.
     * @param tags          space-delimited list of quoted tags.
     * @param description   description of link.
     * @param extended      extended description.
     *
     * @return error or empty string on success.
     */
    public String tag(int sessionId, byte[] url, boolean feed, byte[] tags,
        byte[] description, byte[] extended)
    {
        String result = "";

        int userId = getUserID(sessionId);

        if (userId != -1)
        {
            IUserSharedTagsDao dao = getDAO();
            dao.tagLink(new UserSharedTags(userId, StringUtils.fromUTF8(url), feed,
                StringUtils.fromUTF8(tags), StringUtils.fromUTF8(description),
                StringUtils.fromUTF8(extended)));
        } else
        {
            result = ERR_NO_SESSION;
        }

        return result;
    }

    /**
     * Returns list of strings containing the lists of tags. Tags lists are the space-delimiteed
     * lists of quoted tags.
     *
     * @param sessionId session ID or <code>-1</code> for all tags from each user.
     * @param url       link to get tags for.
     *
     * @return list of tags.
     */
    public Vector getTags(int sessionId, byte[] url)
    {
        Vector tags = new Vector();

        int userId = sessionId == -1 ? -2 : getUserID(sessionId);

        if (userId != -1)
        {
            IUserSharedTagsDao dao = getDAO();
            List tagsList = dao.getLinkTags(StringUtils.fromUTF8(url), userId);

            for (int i = 0; i < tagsList.size(); i++)
            {
                String tagsItem = (String)tagsList.get(i);
                tags.add(StringUtils.toUTF8(tagsItem));
            }
        }

        return tags;
    }

    /**
     * Returns ID of user owning the session or <code>-1</code> if session not exists.
     *
     * @param sessionId session ID.
     *
     * @return user ID or <code>-1</code> if session is missing.
     */
    int getUserID(int sessionId)
    {
        User u = AccountHandler.getUserBySessionId(sessionId);
        return u == null ? -1 : u.getId();
    }

    /**
     * Returns DAO for operations over tags.
     *
     * @return dao.
     */
    private synchronized IUserSharedTagsDao getDAO()
    {
        if (userPreferenceDao == null)
        {
            DaoManager manager = DaoConfig.getDaoManager();
            userPreferenceDao = (IUserSharedTagsDao)manager.getDao(IUserSharedTagsDao.class);
        }

        return userPreferenceDao;
    }
}
