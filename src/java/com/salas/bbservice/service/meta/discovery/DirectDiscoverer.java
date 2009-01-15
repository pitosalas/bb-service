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
// $Id: DirectDiscoverer.java,v 1.2 2006/10/31 12:48:32 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.Map;

/**
 * Checks if the URL points directly to the XML resources.
 */
public class DirectDiscoverer extends AbstractDiscoverer
{
    private static final int NUM_FIRST_BYTES = 2048;

    /**
     * Sets the properties for discoverer.
     *
     * @param properties properties.
     */
    public void setProperties(Map properties)
    {
    }

    /**
     * Returns the OR'ed list of fields provided by this discoverer.
     *
     * @return list of fields.
     */
    public int getProvidedFields()
    {
        return FIELD_DATA_URL;
    }

    /**
     * Makes a try to discover the URL and update information in the source blog.
     *
     * @param source source blog.
     * @param url    url to discover.
     *
     * @return fields actually discovered.
     *
     * @throws InterruptedIOException in case of interrupted transfer (timeout or whatever).
     */
    public int discover(Blog source, URL url) throws InterruptedIOException
    {
        int discoveredFields = 0;

        try
        {
            if (source.getDataUrl() == null && isRecognizableFormat(url))
            {
                source.setDataUrl(url.toString());
                discoveredFields = FIELD_DATA_URL;
            }
        } catch (InterruptedIOException e)
        {
            throw e;
        } catch (IOException e)
        {
            // All other exceptions (including server return codes != 200) are OK
            discoveredFields = 0;
        }

        return discoveredFields;
    }

    /** Tries to read data behind URL and recognize format. */
    private boolean isRecognizableFormat(URL url)
        throws IOException
    {
        final InputStream in = new BufferedInputStream(urlInputStream(url), NUM_FIRST_BYTES);
        boolean recognizable = false;

        byte[] b = new byte[NUM_FIRST_BYTES];

        int bytesRead = 0;
        while (bytesRead < NUM_FIRST_BYTES)
        {
            int bytes = in.read(b, bytesRead, NUM_FIRST_BYTES - bytesRead);
            if (bytes == -1) break;
            bytesRead += bytes;
        }

        try
        {
            String rootElement = getRootElement(b);
            if (rootElement.startsWith("rss"))
            {
                recognizable = ((rootElement.indexOf("0.91") > 0) ||
                    (rootElement.indexOf("0.92") > 0) ||
                    (rootElement.indexOf("0.93") > 0) ||
                    (rootElement.indexOf("0.94") > 0) ||
                    (rootElement.indexOf("2.0") > 0));
            } else if ((rootElement.indexOf("rdf") >= 0) ||
                (rootElement.indexOf("feed") >= 0))
            {
                recognizable = true;
            }
        } catch (Exception e)
        {
            // Invalid file - non-XML
        }

        return recognizable;
    }

    /**
     * Gets the name of the root element and the attributes (inclusive
     * namespace declarations).
     */
    private static String getRootElement(byte[] b)
    {
        String s = new String(b);
        int startPos = 0;
        int endPos = 0;
        boolean inComment = false;
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == '<' && Character.isLetter(s.charAt(i + 1)) && !inComment)
            {
                startPos = i + 1;
                for (int j = i + 1; j < s.length(); j++)
                {
                    if (s.charAt(j) == '>')
                    {
                        endPos = j;
                        break;
                    }
                }
                break;
            } else if (!inComment && s.charAt(i) == '<' && s.charAt(i + 1) == '!' &&
                s.charAt(i + 2) == '-' && s.charAt(i + 3) == '-')
            {
                inComment = true;
            } else if (inComment && s.charAt(i) == '-' && s.charAt(i + 1) == '-' &&
                s.charAt(i + 2) == '>')
            {
                inComment = false;
            }
        }

        if (startPos >= 0 && endPos >= 0 && endPos > startPos)
        {
            return s.substring(startPos, endPos);
        } else
        {
            throw new IllegalArgumentException("Unable to retrieve root element from " + s);
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "DirectDiscoverer";
    }
}
