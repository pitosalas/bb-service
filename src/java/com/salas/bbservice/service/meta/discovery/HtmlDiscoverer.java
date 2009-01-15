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
// $Id: HtmlDiscoverer.java,v 1.4 2008/11/20 08:17:30 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Discoverer that looks through HTML file header for links to the RSS's.
 */
public class HtmlDiscoverer extends AbstractDiscoverer
{
    private static final Pattern PATTERN_FILTER_LINKS_1 =
        Pattern.compile("<link[^>]+rel=['\"]alternate['\"]\\s+[^>]*>",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_FILTER_LINKS_2 =
        Pattern.compile("\\s+type=['\"]application/(atom|rss)\\+xml['\"]",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_FILTER_LINKS_3 =
        Pattern.compile("href=['\"]([^'\"]+)['\"]");

    static final Pattern PATTERN_FILTER_LINKS_4 =
        Pattern.compile("<a\\s+[^>]*href\\s*=\\s*['\"]([^'\"]+\\.(rdf|rss|atom|xml))['\"][^>]*>",
            Pattern.CASE_INSENSITIVE);
    
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
    public int discover(Blog source, URL url)
        throws InterruptedIOException
    {
        if (source.getDataUrl() != null) return 0;

        int discoveredFields = 0;

        try
        {
            String head = getHeadData(url);

            if (head != null)
            {
                String dataUrl = discover(head);
                dataUrl = convertToAbsolute(url, dataUrl);

                if (dataUrl != null)
                {
                    source.setDataUrl(dataUrl);
                    discoveredFields = FIELD_DATA_URL;
                } else
                {
                    String newURLS = detectHTMLRedirectionURL(head);

                    // If new URL is found, redirect
                    if (newURLS != null)
                    {
                        URL newURL = new URL(url, newURLS);
                        if (!newURL.equals(url)) discoveredFields = discover(source, newURL);
                    }
                }
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

    /**
     * Attempts to find a redirection URL in the head.
     *
     * @param head head.
     *
     * @return URL string or <code>NULL</code> if not found.
     */
    static String detectHTMLRedirectionURL(String head)
    {
        String newURLS = null;

        // See if we deal with internal HTML redirection
        String c = "[^>]*(content\\s*=\\s*['\"][0-9]+;url=([^\"']+)['\"])[^>]*";
        Pattern pat1 = Pattern.compile("<meta\\s+" + c + "http-equiv\\s*=\\s*['\"]refresh['\"]", Pattern.CASE_INSENSITIVE);
        Pattern pat2 = Pattern.compile("<meta\\s+[^>]*http-equiv\\s*=\\s*['\"]refresh['\"]" + c, Pattern.CASE_INSENSITIVE);
//        String content = "[^>]*(content\\s*=\\s*['\"]([0-9]+;)*url=([^\"'])['\"])?[^>]*";
//        Pattern pat = Pattern.compile("<meta\\s+" + content +
//            "http-equiv\\s*=\\s*['\"]refresh['\"]" + content,
//            Pattern.CASE_INSENSITIVE);

        Pattern[] p = new Pattern[] { pat1, pat2 };
        for (Pattern pattern : p)
        {
            Matcher mat = pattern.matcher(head);
            while (newURLS == null && mat.find())
            {
                newURLS = mat.group(2);
                if (newURLS == null) newURLS = mat.group(6);
            }

            if (newURLS != null) break;
        }

        return newURLS;
    }

    /**
     * Returns a head data of the resource behind the URL.
     *
     * @param url URL.
     *
     * @return head data or <code>NULL</code> if resource type doesn't match "text/html".
     *
     * @throws IOException I/O error.
     */
    protected String getHeadData(URL url) throws IOException
    {
        String head = null;

        // Limit content to text/html only
        URLConnection connection = urlConnection(url);
        String contentType = connection.getContentType();
        if (contentType != null && contentType.toLowerCase().startsWith("text/html"))
        {
            head = new String(readHeadData(connection));
        }

        return head;
    }

    /**
     * Converts link from any to absolute.
     *
     * @param aUrl      base URL.
     * @param aDataUrl  link.
     *
     * @return absolute URL.
     */
    static String convertToAbsolute(URL aUrl, String aDataUrl)
    {
        URL url = null;

        if (aDataUrl != null && aUrl != null)
        {
            try
            {
                url = new URL(aUrl, aDataUrl);
            } catch (MalformedURLException e)
            {
                // It happens sometimes :)
            }
        }

        return url == null ? null : url.toExternalForm();
    }

    /**
     * Finds and returns (if found) data URL from the head of HTML file.
     *
     * @param head head of HTML file.
     *
     * @return data URL or null.
     */
    String discover(String head)
    {
        Matcher m = PATTERN_FILTER_LINKS_1.matcher(head);
        String dataUrl = null;
        
        // First phase is to look at <LINK ...> in the header
        while (dataUrl == null && m.find())
        {
            String link = m.group();
            if (PATTERN_FILTER_LINKS_2.matcher(link).find())
            {
                Matcher m3 = PATTERN_FILTER_LINKS_3.matcher(link);
                if (m3.find()) dataUrl = m3.group(1);
            }
        }

        // Second phase is to look for .rss, .rdf, .atom, .xml extensions in href's
        if (dataUrl == null)
        {
            m = PATTERN_FILTER_LINKS_4.matcher(head);
            if (m.find())
            {
                dataUrl = m.group(1);
            }
        }

        return dataUrl;
    }

    /**
     * Reads data from the head of stream behind the URL.
     *
     * @param connection connection to use for reading.
     *
     * @return head bytes.
     *
     * @throws IOException in case of communication problems.
     */
    protected byte[] readHeadData(URLConnection connection)
        throws IOException
    {
        byte[] b = new byte[0];
        final InputStream in = new BufferedInputStream(connection.getInputStream());

        int dest = 0;
        byte[] buf = new byte[1024];
        boolean finished = false;

        while (!finished)
        {
            int read = in.read(buf, 0, 1024);

            if (read != -1)
            {
                byte[] newBuf = new byte[b.length + read];

                // append read data to the buffer
                System.arraycopy(b, 0, newBuf, 0, b.length);
                System.arraycopy(buf, 0, newBuf, dest, read);
                b = newBuf;
                dest += read;
            } else finished = true;
        }

        return b;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "HtmlDiscoverer";
    }
}
