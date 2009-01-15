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
// $Id: AccountHandler.java,v 1.2 2007/03/02 13:16:46 alg Exp $
//
package com.salas.bbservice.service.account;

import com.salas.bbservice.domain.User;
import com.salas.bbservice.service.mail.MailService;
import com.salas.bbservice.service.mail.MailServiceException;
import com.salas.bbservice.utils.Constants;
import com.salas.bbservice.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XML-RPC handler of user account related requests.
 */
public final class AccountHandler
{
    private static final Logger LOG = Logger.getLogger(AccountHandler.class.getName());

    // getSessionId error codes
    private static final int ERROR_ACCOUNT_NOT_ACTIVATED = -1;
    private static final int ERROR_PASSWORD_INCORRECT = -2;
    private static final int ERROR_ACCOUNT_NOT_REGISTERED = -3;

    /**
     * Registers new users account.
     *
     * @param fullName  full user name.
     * @param email     user's email.
     * @param password  user's password.
     * @param locale    user's locale.
     * @param notifyOnUpdates if user allowed to use his email for updates notifications.
     *
     * @return error message or NULL if successful.
     */
    public String registerAccount(String fullName, String email, String password, String locale,
                                  boolean notifyOnUpdates)
    {
        return registerAccount(fullName, email, password, locale, notifyOnUpdates, true);
    }

    /**
     * Registers new users account.
     *
     * @param fullName  full user name.
     * @param email     user's email.
     * @param password  user's password.
     * @param locale    user's locale.
     * @param notifyOnUpdates if user allowed to use his email for updates notifications.
     * @param sendMail  TRUE to send activation mail.
     *
     * @return error message or NULL if successful.
     */
    String registerAccount(String fullName, String email, String password, String locale,
                           boolean notifyOnUpdates, boolean sendMail)
    {
        String message = null;
        boolean registeredNew = false;
        int accountId = -1;

        try
        {
            accountId = AccountService.getInstance().registerAccount(fullName, email, password,
                    locale, notifyOnUpdates);

            registeredNew = true;
        } catch (AccountRegisteredException e)
        {
            message = "Account is already registered.";
        }

        // If account was registered successfully and we need to send email then do so.
        if (registeredNew && sendMail)
        {
            boolean sentMail = false;
            try
            {
                MailService.getInstance().sendMessage(email, "BlogBridge Registration",
                        getActivationMailText(fullName, email, accountId));
                sentMail = true;
            } catch (MailServiceException e)
            {
                LOG.log(Level.INFO, "Unable to send mail to new account. Unregistering... " +
                        "(fullName=" + fullName + ", email=" + email + ")", e);

                message = e.getMessage();
            } catch (RuntimeException e)
            {
                LOG.log(Level.SEVERE, "Runtime exception during sending new account letter. " +
                        "Unregistering... ", e);

                message = "Sorry, registration error encountered.";
            }

            // If mail was not sent unregister this account as it will never be activated.
            if (!sentMail)
            {
                try
                {
                    AccountService.getInstance().unregisterAccount(email);
                } catch (AccountNotRegisteredException e)
                {
                    LOG.log(Level.SEVERE, "Unable to unregister failed registration.", e);
                }
            }
        }

        return message == null ? Constants.EMPTY_STRING : message;
    }

    /**
     * Returns text of activation email.
     *
     * @param fullName  full name of user.
     * @param email     user's email.
     * @param accountId id of user's account.
     *
     * @return the text.
     */
    private String getActivationMailText(String fullName, String email, int accountId)
    {
        String enc = System.getProperty("file.encoding");
        String encEmail;
        String message = null;

        try
        {
            encEmail = URLEncoder.encode(email, enc);
            message = "Dear " + fullName + ",\n" +
                "\n" +
                "Yes! You've successfully signed up to use BlogBridge's FREE Service, \n" +
                "actually one of the things people love most about BlogBridge. Once \n" +
                "you confirm this email your account will be ready for you to use. \n" +
                "\n" +
                "Just click on this link and your account will be activated: \n" +
                "\n" +
                "http://www.blogbridge.com/signup/?activate=" + encEmail + "&accountid=" + accountId + "\n" +
                "\n" +
                "\n" +
                "BlogBridge service does lots of useful things. Most importantly it\n" +
                "will store your subscriptions on our server, so that you can use \n" +
                "BlogBridge on multiple computers and always keep things in synch.\n" +
                "\n" +
                "As you learn more about BlogBridge you will see all the other\n" +
                "things the service does for you: It will publish your reading\n" +
                "lists, discover new feeds, help you with tagging, and lots more!\n" +
                "\n" +
                "\n" +
                "Using the Service is easy (and FREE!) once you click the activation link, \n" +
                "all you have to do is to choose Synchronize Now (on the BlogBridge\n" +
                "Toolbar) from time to time, and everything is automagic. If you want\n" +
                "more control, take a look at the Tools menu.\n" +
                "\n" +
                "Thank you for using BlogBridge. \n" +
                "\n" +
                "The BlogBridge Team";

        } catch (UnsupportedEncodingException e)
        {
            LOG.severe(e.getMessage());
        }

        return message;
    }

    /**
     * Activates account.
     *
     * @param email user's email.
     * @param idStr account ID.
     *
     * @return error message or empty string in case of successful activation.
     */
    public String activateAccount(String email, String idStr)
    {
        String message = null;

        // All necessary data provided
        if (StringUtils.isEmpty(email))
        {
            message = "Email is not specified.";
        } else if (StringUtils.isEmpty(idStr))
        {
            message = "Account ID is not specified.";
        } else
        {
            int id = -1;
            try
            {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e)
            {
                message = "Incorrect format of account ID.";
            }

            // If ID is in correct format try to activate account
            if (id > -1)
            {
                message = activateAccount(email, id);
            }
        }

        return message == null ? Constants.EMPTY_STRING : message;
    }

    /**
     * Activates account.
     *
     * @param email user's email.
     * @param id    account ID.
     *
     * @return error message or empty string in case of successful activation.
     */
    private String activateAccount(String email, int id)
    {
        String message = null;

        try
        {
            AccountService.getInstance().activateAccount(email, id);
        } catch (AccountNotRegisteredException e)
        {
            message = "This account cannot be activated because it does not exist.";
        }

        return message == null ? Constants.EMPTY_STRING : message;
    }

    /**
     * Returnes ID of authenticated session.
     *
     * Note: For simplicity sake ID is equal to User ID by now.
     *
     * @param email     user's email.
     * @param password  user's password.
     *
     * @return return ID of session or negative value in case of error:
     *         <ul>
     *           <li>-1 - account isn't activated.</li>
     *           <li>-2 - incorrect login and/or password.</li>
     *           <li>-3 - account doesn't exists.</li>
     *         </ul>
     */
    public int getSessionId(String email, String password)
    {
        int id;

        try
        {
            User u = AccountService.getInstance().getUserAccount(email);
            if (!u.isActivated())
            {
                id = ERROR_ACCOUNT_NOT_ACTIVATED;
            } else if (!u.getPassword().equals(password))
            {
                id = ERROR_PASSWORD_INCORRECT;
            } else
            {
                id = u.getId();
            }
        } catch (AccountNotRegisteredException e)
        {
            id = ERROR_ACCOUNT_NOT_REGISTERED;
        }

        return id;
    }

    /**
     * Requests resending password to the specified account.
     *
     * @param email email of registered account.
     *
     * @return error message or empty string.
     */
    public String requestPasswordResending(String email)
    {
        String msg = null;

        try
        {
            User u = AccountService.getInstance().getUserAccount(email);
            MailService.getInstance().sendMessage(email, "BlogBridge password request",
                "Dear " + u.getFullName() + ",\n" +
                "\n" +
                "According to request we are sending the password, used during registration\n" +
                "of your account on BlogBridge server service.\n" +
                "\n" +
                "You password is '" + u.getPassword() + "'.\n" +
                "\n" +
                "Thank you,\n" +
                "BlogBridge Team");
        } catch (AccountNotRegisteredException e)
        {
            msg = "Account by this E-mail is not registered.";
        } catch (MailServiceException e)
        {
            msg = "Server could not send message to a given E-mail address.";
        }

        return msg == null ? Constants.EMPTY_STRING : msg;
    }

    /**
     * Returns User object associated with session ID.
     *
     * @param id        session ID.
     *
     * @return user object or null if there's no such session.
     */
    public static User getUserBySessionId(int id)
    {
        return AccountService.getInstance().getUserAccount(id);
    }
}
