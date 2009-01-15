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
// $Id: ReportsHandler.java,v 1.1.1.1 2006/10/23 13:55:47 alg Exp $
//

package com.salas.bbservice.service.reports;

import com.salas.bbservice.utils.Constants;
import com.salas.bbservice.utils.StringUtils;

/**
 * Handler of calls to reports service.
 */
public final class ReportsHandler
{
    /**
     * Registers error reported by client.
     *
     * @param message   error message.
     * @param details   details.
     * @param version   application version.
     *
     * @return empty string.
     */
    public String clientError(String message, String details, String version)
    {
        ReportsService.getInstance().recordClientError(message, details, version);

        return Constants.EMPTY_STRING;
    }

    /**
     * Saves feedback message.
     *
     * @param message   message with feedback.
     *
     * @return empty string.
     */
    public String feedbackMessage(byte[] message)
    {
        String msg = StringUtils.fromUTF8(message);

        ReportsService.getInstance().recordFeedbackMessage(msg);

        return Constants.EMPTY_STRING;
    }
}
