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
// $Id: TestAccountHandler.java,v 1.1.1.1 2006/10/23 13:55:59 alg Exp $
//

package com.salas.bbservice.service.account;

import com.ibatis.dao.client.DaoManager;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.DaoTestHelper;
import com.salas.bbservice.persistence.IUserDao;
import com.salas.bbservice.utils.AbstractTestCase;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see AccountHandler
 */
public class TestAccountHandler extends AbstractTestCase
{
    private static final String FULL_NAME = "Name3";
    private static final String EMAIL_1 = "Email3";
    private static final String EMAIL_2 = "BadEmail3";
    private static final String PASSWORD_1 = "Password3";
    private static final String PASSWORD_2 = "BadPassword3";
    private static final String LOCALE_1 = "en_UK";
    private static final String LOCALE_2 = "ru_RU";

    private DaoManager daoManager = DaoConfig.getDaoManager();
    private IUserDao dao = (IUserDao)daoManager.getDao(IUserDao.class);
    private AccountHandler handler = new AccountHandler();

    /**
     * @see com.salas.bbservice.service.account.AccountHandler#registerAccount
     */
    public void testRegisterAccount()
    {
        Logger.getLogger(AccountHandler.class.getName()).setLevel(Level.SEVERE);
        
        DaoTestHelper.cleanByEmail(dao, EMAIL_1);
        DaoTestHelper.cleanByEmail(dao, EMAIL_2);

        try
        {
            // check registration with invalid email. Account should be unregistered.
            assertFalse("".equals(handler.registerAccount(FULL_NAME,
                    EMAIL_2, PASSWORD_1, LOCALE_1, true)));

            try
            {
                AccountService.getInstance().isActivedAccount(EMAIL_2);
                fail("After email error account should be unregistered.");
            } catch (AccountNotRegisteredException e)
            {
                // Expected behavior
            }

            // check registration and duplicate
            assertEquals("", handler.registerAccount(FULL_NAME,
                    EMAIL_1, PASSWORD_1, LOCALE_1, true, false));

            assertFalse("".equals(handler.registerAccount(FULL_NAME,
                    EMAIL_1, PASSWORD_1, LOCALE_2, true, false)));

            // call AccountService to unregister account
            try
            {
                AccountService.getInstance().unregisterAccount(EMAIL_1);
            } catch (AccountNotRegisteredException e)
            {
                e.printStackTrace();
                fail();
            }
        } finally
        {
            DaoTestHelper.cleanByEmail(dao, EMAIL_1);
            DaoTestHelper.cleanByEmail(dao, EMAIL_2);
        }
    }

    /**
     * @see com.salas.bbservice.service.account.AccountHandler#getSessionId
     */
    public void testGetSessionId()
    {
        User u = null;

        DaoTestHelper.cleanByEmail(dao, EMAIL_1);

        // register account
        assertEquals("", handler.registerAccount(FULL_NAME,
                EMAIL_1, PASSWORD_1, LOCALE_1, true, false));

        try
        {
            // not activated
            assertEquals(-1, handler.getSessionId(EMAIL_1, PASSWORD_1));

            // activated
            u = dao.findByEmail(EMAIL_1);
            u.setActivated(true);
            dao.update(u);

            assertEquals(u.getId(), handler.getSessionId(EMAIL_1, PASSWORD_1));
            assertEquals(-2, handler.getSessionId(EMAIL_1, PASSWORD_2));

            // delete and check not existing
            dao.delete(u);

            assertEquals(-3, handler.getSessionId(EMAIL_1, PASSWORD_1));
        } finally
        {
            // cleanup
            DaoTestHelper.cleanByEmail(dao, EMAIL_1);
        }
    }

    /**
     * @see com.salas.bbservice.service.account.AccountHandler#getUserBySessionId
     */
    public void testGetUserBySessionId()
    {
        User u = null;

        DaoTestHelper.cleanByEmail(dao, EMAIL_1);

        // register account
        assertEquals("", handler.registerAccount(FULL_NAME,
                EMAIL_1, PASSWORD_1, LOCALE_1, true, false));

        try
        {
            // activate account
            u = dao.findByEmail(EMAIL_1);
            u.setActivated(true);
            dao.update(u);

            // get session id
            int id = handler.getSessionId(EMAIL_1, PASSWORD_1);
            User u2 = AccountHandler.getUserBySessionId(id);

            assertEquals(u, u2);
        } finally
        {
            // cleanup
            DaoTestHelper.cleanByEmail(dao, EMAIL_1);
        }
    }
}
