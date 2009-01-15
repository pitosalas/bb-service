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
// $Id: ReportsService.java,v 1.1.1.1 2006/10/23 13:55:48 alg Exp $
//

package com.salas.bbservice.service.reports;

import com.salas.bbservice.persistence.IClientErrorDao;
import com.salas.bbservice.persistence.IFeedbackMessageDao;
import com.salas.bbservice.persistence.DaoConfig;
import com.salas.bbservice.domain.ClientError;
import com.salas.bbservice.domain.FeedbackMessage;
import com.ibatis.dao.client.DaoManager;

import java.io.UnsupportedEncodingException;

/**
 * Service serving reports requests.
 */
public class ReportsService
{
    private static ReportsService   instance;

    private IClientErrorDao     clientErrorDao;
    private IFeedbackMessageDao feedbackMessageDao;

    /**
     * Hidden singleton constructor.
     */
    private ReportsService()
    {
        DaoManager manager = DaoConfig.getDaoManager();
        clientErrorDao = (IClientErrorDao)manager.getDao(IClientErrorDao.class);
        feedbackMessageDao = (IFeedbackMessageDao)manager.getDao(IFeedbackMessageDao.class);
    }

    /**
     * Returns instance of service.
     *
     * @return instance of service.
     */
    public synchronized static ReportsService getInstance()
    {
        if (instance == null) instance = new ReportsService();

        return instance;
    }

    /**
     * Records client error.
     *
     * @param message   message.
     * @param details   details.
     * @param version   application version.
     */
    public void recordClientError(String message, String details, String version)
    {
        ClientError ce = new ClientError(message, details, version);
        clientErrorDao.add(ce);
    }

    /**
     * Records feedback message.
     *
     * @param message   message.
     */
    public void recordFeedbackMessage(String message)
    {
        FeedbackMessage fm = new FeedbackMessage(message);
        feedbackMessageDao.add(fm);
    }
}
