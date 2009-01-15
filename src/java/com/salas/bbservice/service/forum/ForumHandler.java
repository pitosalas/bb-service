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
// $Id: ForumHandler.java,v 1.1.1.1 2006/10/23 13:55:43 alg Exp $
//

package com.salas.bbservice.service.forum;

import com.salas.bbservice.domain.Forum;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.IForumDao;
import com.salas.bbservice.utils.StringUtils;
import com.salas.bbservice.utils.Configuration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

/**
 * Forum service handler.
 */
public class ForumHandler
{
    private final String forumBaseURL;

    /**
     * Creates forum handler..
     */
    public ForumHandler()
    {
        forumBaseURL = Configuration.getBaseForumURL();
    }

    /**
     * Returns the list of forums available for posting. The map contains
     * Forum-ID to Forum-Name mapping.
     *
     * @return list of available forums.
     */
    public Hashtable getForums()
    {
        IForumDao forumDao = getForumDao();

        Hashtable forums = new Hashtable();
        List forumsList = forumDao.listForums();
        for (int i = 0; i < forumsList.size(); i++)
        {
            Forum forum = (Forum)forumsList.get(i);
            forums.put(Integer.toString(forum.getId()), forum.getName());
        }

        return forums;
    }

    /**
     * Posts a message to forum.
     *
     * @param aFullName  full name of the user.
     * @param anEmail     email address of the user (optional).
     * @param aForumId   ID of the target forum.
     * @param aSubject   subject of the post.
     * @param aMessage   text of the post.
     *
     * @return error message or empty string if everything is OK.
     */
    public String post(byte[] aFullName, byte[] anEmail, int aForumId,
        byte[] aSubject, byte[] aMessage)
    {
        String fullName = StringUtils.fromUTF8(aFullName);
        String email = StringUtils.fromUTF8(anEmail);
        String subject = StringUtils.fromUTF8(aSubject);
        String message = StringUtils.fromUTF8(aMessage);

        String error = validate(fullName, email, aForumId, subject, message);
        if (error == null)
        {
            try
            {
                post(fullName, email, aForumId, subject, message);
            } catch (IOException e)
            {
                e.printStackTrace();
                error = "We were unable to send your request to the forum.";
            }
        }

        return error == null ? "" : error;
    }

    /**
     * Performs data validation before commiting into database.
     */
    private String validate(String aFullName, String aEmail, int aForumId, String aSubject,
        String aMessage)
    {
        String error = null;

        if (StringUtils.isEmpty(aFullName))
        {
            error = "The name should be specified.";
        } else if (aEmail != null && !aEmail.trim().matches("[^@]+@[^\\.]+\\.[^\\.]+"))
        {
            error = "Invalid e-mail address specified. The address can be omitted.";
        } else if (StringUtils.isEmpty(aSubject))
        {
            error = "The subject should be specified.";
        } else if (StringUtils.isEmpty(aMessage))
        {
            error = "The message cannot be empty.";
        } else
        {
            IForumDao forumDao = getForumDao();
            if (!forumDao.forumExists(aForumId))
            {
                error = "The selected forum does no longer exist.";
            }
        }

        return error;
    }

    /**
     * Returns forum DAO.
     *
     * @return DAO.
     */
    private IForumDao getForumDao()
    {
        return (IForumDao)DaoConfig.getDao(IForumDao.class);
    }

    /**
     * Posts a new topic to the forum.
     *
     * @param aFullName full name of the author.
     * @param aEmail    optional email address of the author.
     * @param aForumId  ID of the forum to post topic to.
     * @param aSubject  subject of the post.
     * @param aMessage  message of the post.
     */
    private void post(String aFullName, String aEmail, int aForumId,
        String aSubject, String aMessage)
        throws IOException
    {
        PostMethod post = new PostMethod(forumBaseURL + "/post.php?action=post&fid=" + aForumId);
        post.addParameter("form_sent", "1");
        post.addParameter("form_user", "Guest");
        post.addParameter("req_username", "[BB] " + aFullName.trim());
        post.addParameter("email", StringUtils.isEmpty(aEmail) ? "" : aEmail.trim());
        post.addParameter("req_subject", aSubject.trim());
        post.addParameter("req_message", aMessage.trim());

        HttpClient client = new HttpClient();
        int status = client.executeMethod(post);

        if (status >= 400) throw new IOException("Failed to publish topic.");
    }
}
