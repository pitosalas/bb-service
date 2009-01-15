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
// $Id: TechnoratiServiceClient.java,v 1.2 2006/10/31 12:48:32 alg Exp $
//

package com.salas.bbservice.service.meta.discovery.technorati;

import com.salas.bbservice.service.meta.discovery.AbstractDiscoverer;
import sun.security.action.GetPropertyAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.AccessController;

/**
 * Technorati service frontend.
 */
public class TechnoratiServiceClient
{
    private static final String SERVICE_URL = "http://api.technorati.com/bloginfo";

    private String key;
    private TechnoratiParser technoratiParser;
    private String enc;

    /**
     * Creates Technorati service client.
     *
     * @param key your Technorati registration key.
     */
    public TechnoratiServiceClient(String key)
    {
        this.key = key;
        technoratiParser = new TechnoratiParser();

        enc = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
    }

    /**
     * Sets Technorati key.
     *
     * @param aKey key.
     */
    public void setKey(String aKey)
    {
        key = aKey;
    }

    /**
     * Reads and parses outline from Technorati service.
     *
     * @param blogUrl URL of desired blog.
     *
     * @return outline.
     *
     * @throws TechnoratiServiceException if errors occured during processing.
     */
    public TechnoratiResponse getResponse(String blogUrl) throws TechnoratiServiceException
    {
        String xml = getXmlFromServer(blogUrl);

        try
        {
            return technoratiParser.parse(xml);
        } catch (TechnoratiParserException e)
        {
            throw new TechnoratiServiceException("Outline parsing error:\n---\n" + xml +
                "\n---", e);
        }
    }

    /**
     * Reads outline information in textual form from server.
     *
     * @param blogUrl URL of desired blog.
     *
     * @return outline xml packet from Technorati server.
     *
     * @throws TechnoratiServiceException if request URL is not valid (technical); or if unable to
     *                                    read data from URL (possibly offline).
     */
    private String getXmlFromServer(String blogUrl) throws TechnoratiServiceException
    {
        String encodedURL = encode(blogUrl);
        final String urlString = SERVICE_URL + "?key=" + key + "&url=" + encodedURL + "&rnd=" + Math.random();

        final URL url;
        try
        {
            url = new URL(urlString);
        } catch (MalformedURLException e)
        {
            throw new TechnoratiServiceException("Invalid URL. :" + urlString, e);
        }

        StringBuffer xmlBuf = new StringBuffer();
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(AbstractDiscoverer.urlInputStream(url)));
            String line;
            while ((line = reader.readLine()) != null)
            {
                xmlBuf.append(line);
            }
        } catch (IOException e)
        {
            throw new TechnoratiServiceException("Could not read stream from URL.", e);
        }

        return xmlBuf.toString();
    }

    /**
     * Encode value for URL.
     *
     * @param str string.
     *
     * @return URL-encoded string.
     */
    private String encode(String str)
    {
        String result = null;

        try
        {
            result = URLEncoder.encode(str, enc);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return result;
    }
}