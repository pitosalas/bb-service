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
// $Id: IUserDao.java,v 1.1.1.1 2006/10/23 13:55:37 alg Exp $
//
package com.salas.bbservice.persistence;

import com.salas.bbservice.domain.User;

import java.util.List;

/**
 * User util interface.
 */
public interface IUserDao
{
    // Every registered user
    String GROUP_EVERYONE       = "Everyone";
    String QUERY_EVERYONE       = "User.selectEveryone";

    // Every registered user with activated account
    String GROUP_ACTIVATED      = "Activated Accounts";
    String QUERY_ACTIVATED      = "User.selectActivated";

    // Every registered user subscribed to updates
    String GROUP_SUBSCRIBED     = "Subscribed to Updates";
    String QUERY_SUBSCRIBED     = "User.selectSubscribed";

    // Top N users of synchronization
    String GROUP_TOP_SYNC       = "Top N Synchronizers";
    String QUERY_TOP_SYNC       = "User.selectTopSync";

    // Top N runners of the application
    String GROUP_TOP_RUNNERS    = "Top N Runners";
    String QUERY_TOP_RUNNERS    = "User.selectTopRunners";

    // Top N readers (having maximum number of feeds)
    String GROUP_TOP_READERS    = "Top N Subscribers";
    String QUERY_TOP_READERS    = "User.selectTopReaders";

    // User groups
    String[] USER_GROUPS = {
        GROUP_EVERYONE, GROUP_ACTIVATED, GROUP_SUBSCRIBED,
        GROUP_TOP_SYNC, GROUP_TOP_RUNNERS, GROUP_TOP_READERS
    };

    // User queries corresponding to groups
    String[] USER_QUERIES = {
        QUERY_EVERYONE, QUERY_ACTIVATED, QUERY_SUBSCRIBED,
        QUERY_TOP_SYNC, QUERY_TOP_RUNNERS, QUERY_TOP_READERS
    };

    /**
     * Adds user to database.
     *
     * @param u user to add.
     */
    void add(User u);

    /**
     * Removes user from database.
     *
     * @param u user to delete.
     */
    void delete(User u);

    /**
     * Deletes all guides associated with this user.
     *
     * @param u user object.
     */
    void deleteAllGuides(User u);

    /**
     * Updates user information in database.
     *
     * @param u user to update.
     */
    void update(User u);

    /**
     * Finds user in database by ID.
     *
     * @param id ID of the user.
     *
     * @return user object or null.
     */
    User findById(int id);

    /**
     * Finds user in database by e-mail. Email should be unique.
     *
     * @param email email of user.
     *
     * @return user object or null.
     */
    User findByEmail(String email);

    /**
     * Returns list of users.
     *
     * @param offset    offset.
     * @param count     maximum number.
     * @param pattern   pattern to match against the name or email or <code>NULL</code>.
     *
     * @return list of users.
     */
    List getUsers(int offset, int count, String pattern);

    /**
     * Returns the list of users registrations taken from database sorted
     * by the registration date from the most recent to older.
     *
     * @param count number of records to return.
     *
     * @return users.
     */
    List getRecentUsers(int count);

    /**
     * Returns total count of registered users.
     *
     * @param pattern pattern to match against the name or email or <code>NULL</code>.
     *
     * @return total count.
     */
    int getUsersCount(String pattern);

    /**
     * Selects users from database.
     *
     * @param aReleaseNotificationsOnly TRUE if only users with <code>notifyOnUpdates</code> set
     *                                  required.
     *
     * @return list of users.
     */
    User[] select(boolean aReleaseNotificationsOnly);

    /**
     * Selects users from database.
     *
     * @param group group id. The ID is the position of a group in USER_GROUPS list.
     * @param n     number of users to select. '-1' means all.
     *
     * @return list of users.
     */
    User[] select(int group, int n);

    /**
     * Registers synchronization attempt of a user.
     *
     * @param u     user.
     * @param store <code>TRUE</code> to register store attempt.
     */
    void registerSync(User u, boolean store);
}
