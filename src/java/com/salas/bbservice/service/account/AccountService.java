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
// $Id: AccountService.java,v 1.1.1.1 2006/10/23 13:55:43 alg Exp $
//
package com.salas.bbservice.service.account;

import com.ibatis.dao.client.DaoManager;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.IUserDao;

/**
 * User account service.
 */
public final class AccountService
{
    private static final AccountService INSTANCE = new AccountService();

    private DaoManager daoManager = DaoConfig.getDaoManager();

    private IUserDao userDao;

    /**
     * Singleton hidden constructor.
     */
    private AccountService()
    {
        userDao = (IUserDao)daoManager.getDao(IUserDao.class);
    }

    /**
     * Return service instance.
     *
     * @return instance of the service.
     */
    public static AccountService getInstance()
    {
        return INSTANCE;
    }

    /**
     * Registers new account in database.
     *
     * @param fullName  full user name.
     * @param email     user's email.
     * @param password  user's password.
     * @param locale    user's locale.
     * @param notifyOnUpdates if user allowed to use his email for updates notifications.
     *
     * @return ID of created account.
     *
     * @throws AccountRegisteredException when account with given email is already registered.
     */
    public int registerAccount(String fullName, String email, String password,
                               String locale, boolean notifyOnUpdates)
        throws AccountRegisteredException
    {
        User u = userDao.findByEmail(email);
        if (u != null) throw new AccountRegisteredException(email);

        u = new User(fullName, email, password, locale, notifyOnUpdates);
        userDao.add(u);

        return u.getId();
    }

    /**
     * Removes all information from database about account.
     *
     * @param email     user's email.
     *
     * @throws AccountNotRegisteredException when account with given email is not registered.
     */
    public void unregisterAccount(String email)
        throws AccountNotRegisteredException
    {
        User u = userDao.findByEmail(email);
        if (u == null) throw new AccountNotRegisteredException(email);

        userDao.delete(u);
    }

    /**
     * Activates account.
     *
     * @param email     user's email.
     * @param id        ID of user's account.
     *
     * @throws AccountNotRegisteredException when account with given email is not registered.
     */
    public void activateAccount(String email, int id)
        throws AccountNotRegisteredException
    {
        User u = userDao.findById(id);
        if (u == null || !u.getEmail().equals(email))
        {
            throw new AccountNotRegisteredException(email);
        }

        u.setActivated(true);
        userDao.update(u);
    }

    /**
     * Returns activation state of the account.
     *
     * @param email     user's account.
     *
     * @return TRUE if account is activated.
     *
     * @throws AccountNotRegisteredException when account with given email is not registered.
     */
    public boolean isActivedAccount(String email)
        throws AccountNotRegisteredException
    {
        User u = userDao.findByEmail(email);
        if (u == null) throw new AccountNotRegisteredException(email);

        return u.isActivated();
    }

    /**
     * Looks for account specified by email.
     *
     * @param email     user's email.
     *
     * @return user account.
     *
     * @throws AccountNotRegisteredException when account with given email is not registered.
     */
    public User getUserAccount(String email)
        throws AccountNotRegisteredException
    {
        User u = userDao.findByEmail(email);
        if (u == null) throw new AccountNotRegisteredException(email);

        return u;
    }

    /**
     * Returns user account specified by ID.
     *
     * @param id        user ID.
     *
     * @return user account or null.
     */
    public User getUserAccount(int id)
    {
        return userDao.findById(id);
    }
}
