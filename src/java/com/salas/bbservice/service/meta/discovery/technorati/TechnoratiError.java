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
// $Id: TechnoratiError.java,v 1.2 2008/02/19 21:30:12 alg Exp $
//

package com.salas.bbservice.service.meta.discovery.technorati;

/**
 * Special kind of exception indicating error from technorati.
 */
public class TechnoratiError implements TechnoratiResponse
{
    private String  message;
    private boolean temporary;

    /**
     * Creates error class.
     *
     * @param aMessage message.
     */
    public TechnoratiError(String aMessage)
    {
        this(aMessage, evalTemporary(aMessage));
    }

    /**
     * Creates error class.
     *
     * @param message message.
     * @param temporary <code>TRUE</code> if the error is temporary.
     */
    public TechnoratiError(String message, boolean temporary)
    {
        this.message = message;
        this.temporary = temporary;
    }

    /**
     * Evaluates the value of <code>temporary</code> flag by message.
     *
     * @param aMessage message.
     *
     * @return value of flag.
     */
    static boolean evalTemporary(String aMessage)
    {
        return aMessage != null &&
            (aMessage.indexOf("daily allotment") == -1) &&
            (aMessage.indexOf("Invalid request") == -1) &&
            (aMessage.indexOf("Invalid URL.") == -1);
    }

    /**
     * Returns message.
     *
     * @return message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Returns TRUE if error is temporary and may not appear after immediate retry.
     *
     * @return TRUE if error is temporary and may not appear after immediate retry.
     */
    public boolean isTemporary()
    {
        return temporary;
    }
}
