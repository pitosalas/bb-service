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
// mailto:pitosalas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: SharedTagsQueryParam.java,v 1.1.1.1 2006/10/23 13:55:36 alg Exp $
//

package com.salas.bbservice.domain.dao;

/**
 * User Shared Tags query parameter.
 */
public class SharedTagsQueryParam
{
    private String  url;
    private long    userId;

    /**
     * Creates holder.
     *
     * @param aUrl      URL.
     * @param aUserId   user ID.
     */
    public SharedTagsQueryParam(String aUrl, long aUserId)
    {
        url = aUrl;
        userId = aUserId;
    }

    /**
     * Returns URL.
     *
     * @return url.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Returns user ID.
     *
     * @return user ID.
     */
    public long getUserId()
    {
        return userId;
    }
}
