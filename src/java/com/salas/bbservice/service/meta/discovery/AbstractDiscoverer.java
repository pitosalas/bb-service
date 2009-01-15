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
// $Id: AbstractDiscoverer.java,v 1.2 2007/12/11 12:09:33 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.utils.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Abstract discoverer with some tools.
 */
public abstract class AbstractDiscoverer implements IBlogDiscoverer
{
    /**
     * Returns the input stream.
     *
     * @param url   URL to open stream for.
     *
     * @return input stream.
     *
     * @throws IOException if connection or stream opening fails.
     */
    public static InputStream urlInputStream(URL url)
        throws IOException
    {
        URLConnection con = urlConnection(url);
        return con == null ? null : con.getInputStream();
    }

    /**
     * Returns the connection.
     *
     * @param url   URL to open connection for.
     *
     * @return connection.
     *
     * @throws IOException if connection opening fails.
     */
    public static URLConnection urlConnection(URL url)
        throws IOException
    {
        URLConnection con = null;

        if (url != null)
        {
            con = url.openConnection();
            con.setConnectTimeout(Configuration.getConnectTimeout());
            con.setReadTimeout(Configuration.getReadTimeout());
        }

        return con;
    }
}
