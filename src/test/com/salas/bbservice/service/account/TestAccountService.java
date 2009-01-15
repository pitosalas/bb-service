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
// $Id: TestAccountService.java,v 1.1.1.1 2006/10/23 13:55:59 alg Exp $
//

package com.salas.bbservice.service.account;

import com.ibatis.dao.client.DaoManager;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.DaoTestHelper;
import com.salas.bbservice.persistence.IUserDao;
import com.salas.bbservice.utils.AbstractTestCase;

/**
 * @see com.salas.bbservice.service.account.AccountService
 */
public class TestAccountService extends AbstractTestCase
{
    private AccountService service;
    private DaoManager daoManager = DaoConfig.getDaoManager();
    private IUserDao dao = (IUserDao)daoManager.getDao(IUserDao.class);

    private static final String EMAIL_1 = "Email";
    private static final String EMAIL_2 = "BadEmail";
    private static final String FULL_NAME_1 = "Name";
    private static final String FULL_NAME_2 = "OtherName";
    private static final String LOCALE_1 = "en_UK";
    private static final String LOCALE_2 = "ru_RU";
    private static final String PASSWORD_1 = "Password";
    private static final String PASSWORD_2 = "OtherPassword";

    /**
     * Creates new test instance.
     */
    public TestAccountService()
    {
        service = AccountService.getInstance();
    }

    /**
     * @see AccountService#registerAccount
     */
    public void testRegisterAccount()
    {
        // remove account if it exists
        DaoTestHelper.cleanByEmail(dao, EMAIL_1);

        // register new account
        try
        {
            service.registerAccount(FULL_NAME_1, EMAIL_1, PASSWORD_1, LOCALE_1, true);
        } catch (AccountRegisteredException e)
        {
            fail();
        }

        try
        {
            // check how's account registered
            User u = (User)dao.findByEmail(EMAIL_1);
            assertEquals(FULL_NAME_1, u.getFullName());
            assertEquals(EMAIL_1, u.getEmail());
            assertEquals(PASSWORD_1, u.getPassword());
            assertEquals(LOCALE_1, u.getLocale());
            assertEquals(true, u.isNotifyOnUpdates());
            assertEquals(false, u.isActivated());

            // check duplicate registration
            try
            {
                service.registerAccount(FULL_NAME_2, EMAIL_1, PASSWORD_2, LOCALE_2, false);
                fail("Exception should be thrown. Account is already registered.");
            } catch (AccountRegisteredException e)
            {
                // Expected behavior
            }
        } finally
        {
            DaoTestHelper.cleanByEmail(dao, EMAIL_1);
        }
    }

    /**
     * @see AccountService#getUserAccount(String)
     */
    public void testGetUserAccount()
    {
        // remove account if it exists
        DaoTestHelper.cleanByEmail(dao, EMAIL_1);

        // register new account
        try
        {
            service.registerAccount(FULL_NAME_1, EMAIL_1, PASSWORD_1, LOCALE_1, true);
        } catch (AccountRegisteredException e)
        {
            fail();
        }

        try
        {
            // check how's data written
            try
            {
                assertNotNull(service.getUserAccount(EMAIL_1));
            } catch (AccountNotRegisteredException e)
            {
                fail();
            }

            // check verification of non-existing account
            try
            {
                service.getUserAccount(EMAIL_2);
                fail("Exception should be thrown. Account is not registered.");
            } catch (AccountNotRegisteredException e)
            {
                // Expected behavior
            }
        } finally
        {
            // cleanup
            DaoTestHelper.cleanByEmail(dao, EMAIL_1);
        }
    }

    /**
     * @see AccountService#getUserAccount(int)
     */
    public void testGetUserAccount2()
    {
        int id = -1;

        DaoTestHelper.cleanByEmail(dao, EMAIL_1);

        try
        {
            // add and check
            try
            {
                id = service.registerAccount(FULL_NAME_1, EMAIL_1, PASSWORD_1, LOCALE_1, true);
                assertNotNull(service.getUserAccount(id));
            } catch (AccountRegisteredException e)
            {
                fail(e.getMessage());
            }

            // delete and check
            try
            {
                service.unregisterAccount(EMAIL_1);
                assertNull(service.getUserAccount(id));
            } catch (AccountNotRegisteredException e)
            {
                fail(e.getMessage());
            }
        } finally
        {
            // cleanup
            DaoTestHelper.cleanByEmail(dao, EMAIL_1);
        }
    }

    /**
     * @see AccountService#unregisterAccount
     */
    public void testUnregisterAccount()
    {
        // remove test account
        DaoTestHelper.cleanByEmail(dao, EMAIL_1);

        // register account
        try
        {
            service.registerAccount(FULL_NAME_1, EMAIL_1, PASSWORD_1, LOCALE_1, true);
        } catch (AccountRegisteredException e)
        {
            fail();
        }

        // check unregistering non-existing account
        try
        {
            try
            {
                service.unregisterAccount(EMAIL_2);
                fail("Exception should be thrown. Account is not registered.");
            } catch (AccountNotRegisteredException e)
            {
                // Expected behavior
            }

            // unregister account
            try
            {
                service.unregisterAccount(EMAIL_1);
            } catch (AccountNotRegisteredException e)
            {
                fail("Account should exist.");
            }

            // unregister non-existing account
            try
            {
                service.unregisterAccount(EMAIL_1);
            } catch (AccountNotRegisteredException e)
            {
                // Expected behavior
            }
        } finally
        {
            // remove test account
            DaoTestHelper.cleanByEmail(dao, EMAIL_1);
        }
    }

    /**
     * @see AccountService#activateAccount
     */
    public void testActivateAccount()
    {
        // remove account if it exists
        DaoTestHelper.cleanByEmail(dao, EMAIL_1);

        // register new account
        int accountId = -1;
        try
        {
            accountId = service.registerAccount(FULL_NAME_1, EMAIL_1, PASSWORD_1, LOCALE_1, true);
        } catch (AccountRegisteredException e)
        {
            fail();
        }

        try
        {
            assertFalse(-1 == accountId);

            // test activation state of newly created account
            try
            {
                assertFalse(service.isActivedAccount(EMAIL_1));
            } catch (AccountNotRegisteredException e)
            {
                fail("Account should be registered.");
            }

            // account can be safely activated several times
            try
            {
                service.activateAccount(EMAIL_1, accountId);
            } catch (AccountNotRegisteredException e)
            {
                fail("Account should be registered.");
            }

            try
            {
                service.activateAccount(EMAIL_1, accountId);
            } catch (AccountNotRegisteredException e)
            {
                fail("Account should be registered.");
            }

            try
            {
                assertTrue(service.isActivedAccount(EMAIL_1));
            } catch (AccountNotRegisteredException e)
            {
                fail("Account should be registered.");
            }

            // test activating account with valid email and invalid id
            try
            {
                service.activateAccount(EMAIL_1, accountId + 1);
            } catch (AccountNotRegisteredException e)
            {
                // Expected behavior
            }

            // test activating account with invalid email and valid id
            try
            {
                service.activateAccount(EMAIL_2, accountId);
            } catch (AccountNotRegisteredException e)
            {
                // Expected behavior
            }

            try
            {
                service.unregisterAccount(EMAIL_1);
            } catch (AccountNotRegisteredException e)
            {
                fail("Account should be registered.");
            }

            try
            {
                service.activateAccount(EMAIL_2, -1);
                fail("Exception should be thrown. Account is not registered.");
            } catch (AccountNotRegisteredException e)
            {
                // Expected behavior
            }

            try
            {
                service.getUserAccount(EMAIL_2);
                fail("Exception should be thrown. Account is not registered.");
            } catch (AccountNotRegisteredException e)
            {
                // Expected behavior
            }
        } finally
        {
            DaoTestHelper.cleanByEmail(dao, EMAIL_1);
        }
    }
}
