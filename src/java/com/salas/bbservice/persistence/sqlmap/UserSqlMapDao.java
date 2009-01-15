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
// $Id: UserSqlMapDao.java,v 1.1.1.1 2006/10/23 13:55:40 alg Exp $
//
package com.salas.bbservice.persistence.sqlmap;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.persistence.IUserDao;
import com.salas.bbservice.utils.Constants;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * SqlMap implementation for User DAO.
 */
public class UserSqlMapDao extends SqlMapDaoTemplate implements IUserDao
{
    /**
     * Creates DAO for User.
     *
     * @param daoManager manager.
     */
    public UserSqlMapDao(DaoManager daoManager)
    {
        super(daoManager);
    }

    /**
     * Adds user to database.
     *
     * @param u user to add.
     */
    public void add(User u)
    {
        insert("User.insert", u);
    }

    /**
     * Removes user from database.
     *
     * @param u user to delete.
     */
    public void delete(User u)
    {
        delete("User.delete", u);
        u.setId(-1);
    }

    /**
     * Deletes all guides associated with this user.
     *
     * @param u user object.
     */
    public void deleteAllGuides(User u)
    {
        delete("User.deleteAllGuides", u);
    }

    /**
     * Updates user information in database.
     *
     * @param u user to update.
     */
    public void update(User u)
    {
        update("User.update", u);
    }

    /**
     * Finds user in database by ID.
     *
     * @param id ID of the user.
     * @return user object o null.
     */
    public User findById(int id)
    {
        return (User)queryForObject("User.findById", new Integer(id));
    }

    /**
     * Finds user in database by e-mail. Email should be unique.
     *
     * @param email email of user.
     * @return user object or null.
     */
    public User findByEmail(String email)
    {
        return (User)queryForObject("User.findByEmail", email);
    }

    /**
     * Returns list of users.
     *
     * @param offset  offset.
     * @param count   maximum number.
     * @param pattern pattern to match against the name or email or <code>NULL</code>.
     *
     * @return list of users.
     */
    public List getUsers(int offset, int count, String pattern)
    {
        Map params = new HashMap();
        params.put("offset", new Integer(offset));
        params.put("count", new Integer(count));
        if (pattern != null) params.put("pattern", "%" + pattern + "%");

        return queryForList("User.getUsers", params);
    }

    /**
     * Returns the list of users registrations taken from database sorted by the registration date
     * from the most recent to older.
     *
     * @param count number of records to return.
     *
     * @return users.
     */
    public List getRecentUsers(int count)
    {
        String clause;
        if (count < 1)
        {
            // returning all users from today.
            clause = "WHERE TO_DAYS(registrationDate) - TO_DAYS(NOW()) = 0";
            clause += " ORDER BY registrationDate DESC";
        } else
        {
            // returning given number of rows
            clause = "ORDER BY registrationDate DESC LIMIT " + count;
        }


        return queryForList("User.getRecentUsers", clause);
    }

    /**
     * Returns total count of registered users.
     *
     * @param pattern pattern to match against the name or email or <code>NULL</code>.
     * 
     * @return total count.
     */
    public int getUsersCount(String pattern)
    {
        Map params = new HashMap();
        if (pattern != null) params.put("pattern", "%" + pattern + "%");

        Integer i = (Integer)queryForObject("User.getUsersCount", params);

        return i == null ? 0 : i.intValue();
    }

    /**
     * Selects users from database.
     *
     * @param aReleaseNotificationsOnly TRUE if only users with <code>notifyOnUpdates</code> set
     *                                  required.
     *
     * @return list of users.
     */
    public User[] select(boolean aReleaseNotificationsOnly)
    {
        String whereClause = aReleaseNotificationsOnly
            ? "WHERE notifyOnUpdates=1"
            : Constants.EMPTY_STRING;

        List list = queryForList("User.select", whereClause);

        return (User[])list.toArray(new User[list.size()]);
    }

    /**
     * Selects users from database.
     *
     * @param group group id. The ID is the position of a group in USER_GROUPS list.
     * @param n     number of users to select. '-1' means all.
     *
     * @return list of users.
     */
    public User[] select(int group, int n)
    {
        String query = USER_QUERIES[group];
        Integer param = n == -1 ? null : new Integer(n);

        List list = queryForList(query, param);
        return (User[])list.toArray(new User[list.size()]);
    }

    /**
     * Registers synchronization attempt of a user.
     *
     * @param u     user.
     * @param store <code>TRUE</code> to register store attempt.
     */
    public void registerSync(User u, boolean store)
    {
        Map args = new HashMap();
        args.put("userId", new Integer(u.getId()));
        args.put("synctime", new Long(System.currentTimeMillis()));
        args.put("store", Boolean.valueOf(store));

        update("User.registerSync", args);
    }
}
