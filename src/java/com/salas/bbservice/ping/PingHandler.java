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
// $Id: PingHandler.java,v 1.1.1.1 2006/10/23 13:55:42 alg Exp $
//

package com.salas.bbservice.ping;

import com.salas.bbservice.domain.Installation;
import com.salas.bbservice.domain.User;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.persistence.IInstallationDao;
import com.salas.bbservice.persistence.IUserDao;
import com.salas.bbservice.utils.Constants;
import com.salas.bbservice.utils.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Handler of Ping-requests.
 */
public final class PingHandler
{
    private IInstallationDao    installationDao;
    private IUserDao            userDao;

    /**
     * Hidden singleton constructor.
     */
    public PingHandler()
    {
        installationDao = (IInstallationDao)DaoConfig.getDao(IInstallationDao.class);
        userDao = (IUserDao)DaoConfig.getDao(IUserDao.class);
    }

    /**
     * Ping version 1.
     *
     * @param installationId    installation id of application instance.
     * @param appVersion        version of application.
     * @param runs              number of application runs.
     * @param os                OS name and version.
     * @param javaVersion       JRE version.
     * @param email             user's account email.
     * @param digestedEmail     user's account email digested with password.
     *
     * @return empty string.
     */
    public String ping1(String installationId, String appVersion, int runs,
        String os, String javaVersion, String email, byte[] digestedEmail)
    {
        long instID = StringUtils.parseLong(installationId, -1);
        if (instID != -1)
        {
            ping1(instID, appVersion, runs, os, javaVersion);

            if (!StringUtils.isEmpty(email) && digestedEmail.length != 0)
            {
                User user = userDao.findByEmail(email);

                if (user != null && isCorrectUserAccount(user, digestedEmail))
                {
                    associateUserWithInstallation(user, instID);
                }
            }
        }

        return "";
    }

    /**
     * Verifies that supplied email is registered and when digested with password matches the
     * supplied digested version.
     *
     * @param user          user account.
     * @param digestedEmail email digested with the password on the client side.
     *
     * @return <code>TRUE</code> if email-password on the client side match our database.
     */
    static boolean isCorrectUserAccount(User user, byte[] digestedEmail)
    {
        boolean matches = false;

        try
        {
            byte[] correctDigestedEmail = StringUtils.digestMD5(user.getEmail(), user.getPassword());
            matches = Arrays.equals(correctDigestedEmail, digestedEmail);
        } catch (NoSuchAlgorithmException e)
        {
            // Digesting algorithm isn't implemented -- unable to verify password
        }

        return matches;
    }

    /**
     * Associates user with some installation.
     *
     * @param user              user's account.
     * @param installationId    installation id.
     */
    private void associateUserWithInstallation(User user, long installationId)
    {
        installationDao.associateUserWithInstallation(user.getId(), installationId);
    }

    /**
     * Ping version 1.
     *
     * @param installationId    installation id of application instance.
     * @param appVersion        version of application.
     * @param runs              number of application runs.
     * @param os                OS name and version.
     * @param javaVersion       JRE version.
     *
     * @return empty string.
     */
    public String ping1(String installationId, String appVersion, int runs,
        String os, String javaVersion)
    {
        long instID = StringUtils.parseLong(installationId, -1);
        if (instID != -1) ping1(instID, appVersion, runs, os, javaVersion);

        return Constants.EMPTY_STRING;
    }

    /**
     * Performs PING.
     *
     * @param installationId    installation id of application instance.
     * @param appVersion        version of application.
     * @param runs              number of application runs.
     * @param os                OS name and version.
     * @param javaVersion       JRE version.
     */
    private void ping1(long installationId, String appVersion, int runs, String os,
        String javaVersion)
    {
        // find or register new installation
        Installation i = installationDao.findById(installationId);
        if (i == null)
        {
            i = new Installation(installationId, appVersion, runs, os, javaVersion);
            installationDao.add(i);
        } else
        {
            // update runs count
            i.setRuns(runs);
            installationDao.update(i);
        }

        // register run
        installationDao.registerRun(i);
    }

    /**
     * Simple method to indicate that the service is online.
     *
     * @return "pong".
     */
    public String ping()
    {
        return "pong";
    }
}
