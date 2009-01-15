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
// $Id: IBlogDiscoverer.java,v 1.1.1.1 2006/10/23 13:55:46 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Discoverer performs implementation-specific moves to get necessary data.
 * <p/>
 * Today we have a very limited set of informational fields about the blog.
 * Each discoverer is capable of finding some or all of them. In order to
 * optimize operation each of discoverers should report what information it
 * can provide. Underlying engine can use this information to decide whether
 * the call of this discoverer is necessary or the information provided by
 * it is already known.
 */
public interface IBlogDiscoverer
{
    /** HTML URL field. */
    int FIELD_HTML_URL      = 1;

    /** Data URL field. */
    int FIELD_DATA_URL      = 2;

    /** Inbound links field. */
    int FIELD_INBOUND_LINKS = 4;

    /** Category field. */
    int FIELD_CATEGORY      = 8;

    /** Location field. */
    int FIELD_LOCATION      = 16;

    /** Title of the blog. */
    int FIELD_TITLE         = 32;

    /** Author of the blog. */
    int FIELD_AUTHOR        = 64;

    /** Description of the blog. */
    int FIELD_DESCRIPTION   = 128;

    /**
     * Sets the properties for discoverer.
     *
     * @param properties properties.
     */
    void setProperties(Map properties);

    /**
     * Returns the OR'ed list of fields provided by this discoverer.
     *
     * @return list of fields.
     */
    int getProvidedFields();

    /**
     * Makes a try to discover the URL and update information in the source blog.
     *
     * @param source    source blog.
     * @param url       url to discover.
     *
     * @return fields actually discovered.
     *
     * @throws IOException in case of any communication problems.
     */
    int discover(Blog source, URL url) throws IOException;
}
