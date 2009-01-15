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
// $Id: SynchronizationHandler.java,v 1.1.1.1 2006/10/23 13:55:48 alg Exp $
//
package com.salas.bbservice.service.sync;

import com.salas.bbservice.domain.User;
import com.salas.bbservice.service.account.AccountHandler;
import com.salas.bbservice.utils.Constants;
import com.salas.bbservice.utils.StringUtils;

import java.util.Hashtable;

/**
 * Handles requests to synchronization module.
 */
public final class SynchronizationHandler
{
    private static final Hashtable EMPTY_HASH_TABLE = new Hashtable();

    /**
     * Stores information using specified session and array of bytes in UTF-8 charset.
     *
     * @param sessionId ID of session.
     * @param opml      guides information.
     *
     * @return error message or empty string on success.
     */
    public String storeInUtf8(int sessionId, byte[] opml)
    {
        return store(sessionId, StringUtils.fromUTF8(opml));
    }

    /**
     * Stores information using specified session.
     *
     * @param sessionId ID of session.
     * @param opml      guides information.
     *
     * @return error message or empty string on success.
     */
    public String store(int sessionId, String opml)
    {
        String result = null;

        User u = AccountHandler.getUserBySessionId(sessionId);
        if (u != null)
        {
            try
            {
                SynchronizationService.getInstance().store(u, opml);
            } catch (SynchronizationException e)
            {
                result = e.getMessage();
            }
        } else
        {
            result = "Session doesn't exist.";
        }

        return result == null ? Constants.EMPTY_STRING : result;
    }

    /**
     * Restores guides information using specified session and returns it as array of
     * bytes in UTF-8 charater set.
     *
     * @param sessionId ID of session.
     *
     * @return guides information.
     */
    public byte[] restoreInUtf8(int sessionId)
    {
        return restoreInUtf8(sessionId, 0);
    }

    /**
     * Restores guides information using specified session and returns it as array of
     * bytes in UTF-8 charater set.
     *
     * @param sessionId     ID of session.
     * @param opmlVersion   OPML format version.
     *
     * @return guides information.
     */
    public byte[] restoreInUtf8(int sessionId, int opmlVersion)
    {
        return StringUtils.toUTF8(restore(sessionId, opmlVersion));
    }

    /**
     * Restores guides information using specified session.
     *
     * @param sessionId ID of session.
     *
     * @return guides information.
     */
    public String restore(int sessionId)
    {
        return restore(sessionId, 0);
    }

    /**
     * Restores guides information using specified session.
     *
     * @param sessionId     ID of session.
     * @param opmlVersion   OPML format version (0 -- old, ...)
     *
     * @return guides information.
     */
    public String restore(int sessionId, int opmlVersion)
    {
        String result = null;

        User u = AccountHandler.getUserBySessionId(sessionId);
        if (u != null)
        {
            result = SynchronizationService.getInstance().restore(u, opmlVersion);
        }

        return result == null ? Constants.EMPTY_STRING : result;
    }

    /**
     * Stores preferences taken from the map in the database. Old preferences are removed.
     *
     * @param sessionId     ID of the session.
     * @param preferences   preferences map.
     *
     * @return null.
     */
    public String storePrefs(int sessionId, Hashtable preferences)
    {
        User u = AccountHandler.getUserBySessionId(sessionId);
        if (u != null)
        {
            SynchronizationService.getInstance().storePrefs(u, preferences);
        }

        return Constants.EMPTY_STRING;
    }

    /**
     * Loads preferences associated with session owner from database.
     *
     * @param sessionId     ID of the session.
     *
     * @return preferences map.
     */
    public Hashtable restorePrefs(int sessionId)
    {
        Hashtable prefs = EMPTY_HASH_TABLE;

        User u = AccountHandler.getUserBySessionId(sessionId);
        if (u != null)
        {
            prefs = SynchronizationService.getInstance().restorePrefs(u);
        }

        return prefs;
    }
}
