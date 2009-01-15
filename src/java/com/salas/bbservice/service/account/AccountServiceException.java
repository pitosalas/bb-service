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
// $Id: AccountServiceException.java,v 1.1.1.1 2006/10/23 13:55:43 alg Exp $
//
package com.salas.bbservice.service.account;

/**
 * Base exception for <code>AccountService</code>.
 */
public class AccountServiceException extends Exception
{
    /**
     * @see Exception#Exception()
     */
    public AccountServiceException()
    {
        super();
    }

    /**
     * @see Exception#Exception(String)
     */
    public AccountServiceException(String message)
    {
        super(message);
    }

    /**
     * @see Exception#Exception(Throwable)
     */
    public AccountServiceException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @see Exception#Exception(String, Throwable)
     */
    public AccountServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
