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
// $Id: MailService.java,v 1.1.1.1 2006/10/23 13:55:43 alg Exp $
//

package com.salas.bbservice.service.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * Mail service for operations with mail.
 */
public final class MailService
{
    private static final Logger LOG = Logger.getLogger(MailService.class.getName());

    private static MailService instance;
    private static Session session;

    /**
     * Hidden singleton constructor.
     */
    private MailService()
    {
        try
        {
            Properties props = getMailProperties();
            session = Session.getDefaultInstance(props);
        } catch (Exception e)
        {
            LOG.log(Level.SEVERE, "Unable to initialize mail session.", e);
        }
    }

    /**
     * Loads and returns mail properties.
     *
     * @return properties.
     *
     * @throws IOException in case of any IO exception.
     */
    public static Properties getMailProperties()
        throws IOException
    {
        Properties props = new Properties();

        props.load(MailService.class.getClassLoader().getResourceAsStream("mail.properties"));

        return props;
    }

    /**
     * Returns initialized instance of the service.
     *
     * @return instance.
     */
    public static synchronized MailService getInstance()
    {
        if (instance == null) instance = new MailService();
        return instance;
    }

    /**
     * Sends email from the name of the server to the specified address.
     *
     * @param to        address of receiver.
     * @param subject   subject of the letter.
     * @param text      text of the letter.
     *
     * @throws MailServiceException when information is incorrect.
     */
    public void sendMessage(String to, String subject, String text)
            throws MailServiceException
    {
        if (session == null)
        {
            throw new MailServiceException("Mail service isn't initialized correctly.");
        }

        Message msg = new MimeMessage(session);

        try
        {
            InternetAddress[] toAddress = InternetAddress.parse(to, false);
            InternetAddress fromAddress = InternetAddress.getLocalAddress(session);

            if (toAddress.length == 0)
            {
                throw new MailServiceException("Address is not valid: " + to);
            }

            msg.setFrom(fromAddress);
            msg.setRecipient(Message.RecipientType.TO, toAddress[0]);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(text);

            Transport.send(msg);
        } catch (MessagingException e)
        {
            throw new MailServiceException("Unable to send message: " + e.getMessage(), e);
        }
    }

    /**
     * Sends email from the name of the server to the specified address.
     *
     * @param aSession  mail session to use.
     * @param from      from address.
     * @param reAddress return address.
     * @param to        address of receiver.
     * @param subject   subject of the letter.
     * @param text      text of the letter.
     *
     * @throws MailServiceException when information is incorrect.
     */
    public void sendMessage(Session aSession, String from, String reAddress,
                            String to, String subject, String text)
            throws MailServiceException
    {
        Message msg = new MimeMessage(aSession);

        try
        {
            InternetAddress[] toAddress = InternetAddress.parse(to, false);
            InternetAddress[] fromAddress = InternetAddress.parse(from, false);
            InternetAddress[] replyToAddress = InternetAddress.parse(reAddress, false);

            msg.setFrom(fromAddress[0]);
            msg.setReplyTo(replyToAddress);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(text);
            msg.setRecipient(Message.RecipientType.TO, toAddress[0]);
            Transport.send(msg);
        } catch (MessagingException e)
        {
            throw new MailServiceException("Unable to send message: " + e.getMessage(), e);
        }
    }
}
