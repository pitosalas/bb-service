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
// $Id: StringUtils.java,v 1.1.1.1 2006/10/23 13:55:52 alg Exp $
//

package com.salas.bbservice.utils;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.net.URLEncoder;

/**
 * Various string utilities.
 */
public final class StringUtils
{
    private static final Logger LOG = Logger.getLogger(StringUtils.class.getName());

    private static final Pattern PAT_DOUBLE_QUOTE = Pattern.compile("\"");
    private static final Pattern PATTERN_KEYWORDS =
        Pattern.compile("\\s*((\\\"([^\\\"]*)\\\"|([^\\s\\\"]+))\\s*)");
    private static final Pattern PATTERN_KEYWORDS_VERTICAL =
        Pattern.compile("\\s*(([^\\|]+)\\s*)");

    /**
     * Hidden utility class constructor.
     */
    private StringUtils()
    {
    }

    /**
     * Converts any string into the value. If string is NULL or invalid number, the
     * default will be returned.
     *
     * @param str   string to convert.
     * @param def   default value.
     *
     * @return value.
     */
    public static int toInt(String str, int def)
    {
        int value = def;

        try
        {
            if (str != null) value = Integer.parseInt(str);
        } catch (NumberFormatException e)
        {
            // It's ok, default will be returned.
        }

        return value;
    }

    /**
     * Converts array of bytes in UTF-8 encoding to appropriate string. If encoding
     * isn't supported then the array will be converted into string using default
     * encoding and record will be put in log with severe priority.
     *
     * @param string    bytes forming string.
     *
     * @return resulting string.
     */
    public static String fromUTF8(byte[] string)
    {
        if (string == null) return null;

        String str;

        try
        {
            str = new String(string, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            LOG.severe("UTF-8 isn't supported.");
            str = new String(string);
        }

        return str;
    }

    /**
     * Converts array of byte arrays in UTF-8 encoding to array of strings. The notes are
     * the same as for <code>fromUTF8(String)</code> method.
     *
     * @param strings   array of byte arrays to decode.
     *
     * @return resulting array of strings.
     */
    public static String[] fromUTF8(byte[][] strings)
    {
        if (strings == null) return null;

        String[] strs = new String[strings.length];
        for (int i = 0; i < strings.length; i++)
        {
            byte[] string = strings[i];
            strs[i] = fromUTF8(string);
        }

        return strs;
    }

    /**
     * Converts string into array of bytes in UTF-8 encoding. If UTF-8 encoding isn't supported
     * then the tring is converted into bytes in default encoding and record is put in log
     * with severe priority.
     *
     * @param string    string to convert.
     *
     * @return resulting array of bytes.
     */
    public static byte[] toUTF8(String string)
    {
        if (string == null) return null;

        byte[] result;

        try
        {
            result = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            LOG.severe("UTF-8 isn't supported");
            result = string.getBytes();
        }

        return result;
    }

    /**
     * Escapes string for comma-delimetered format.
     *
     * @param str   source string.
     *
     * @return resulting string.
     */
    public static String escapeForCDL(String str)
    {
        if (str.indexOf('"') != -1)
        {
            str = PAT_DOUBLE_QUOTE.matcher(str).replaceAll("\\\\\"");
        }

        return str;
    }

    /**
     * Escapes known chars and replaces CR's with &lt;br&gt;.
     *
     * @param str string.
     *
     * @return result.
     */
    public static String escape(String str)
    {
        if (str == null) return null;

        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("'", "&apos;");
        str = str.replaceAll("\"", "&quot;");
        str = str.replaceAll("\n", "<br>");

        return str;
    }

    /**
     * Splits the string into pieces using the delimiter setting. Empty strings
     * aren't put in the output list.
     *
     * @param string    source string.
     * @param delimiter delimiter.
     *
     * @return list of tokens.
     */
    public static String[] split(String string, String delimiter)
    {
        List tokens = new ArrayList();

        if (string != null && delimiter != null)
        {
            StringTokenizer tok = new StringTokenizer(string, delimiter);
            while (tok.hasMoreTokens())
            {
                String token = tok.nextToken();
                if (token.trim().length() > 0) tokens.add(token);
            }
        }

        return (String[])tokens.toArray(new String[tokens.size()]);
    }

    /**
     * Converts list of keywords separated by whitespace. Each keyword can actually
     * contain several words if enclosed in double quotes.
     *
     * @param keywords keywords string.
     *
     * @return list of keywords or <code>NULL</code> if <code>keywords</code>
     *         were <code>NULL</code>.
     */
    public static String[] keywordsToArray(String keywords)
    {
        if (keywords == null) return null;

        Pattern pattern = (keywords.indexOf('|') != -1)
            ? PATTERN_KEYWORDS_VERTICAL : PATTERN_KEYWORDS;

        Matcher mat = pattern.matcher(keywords);
        List matches = new ArrayList();
        while (mat.find())
        {
            String keyword = null;
            if (mat.groupCount() > 3) keyword = mat.group(3);
            if (keyword == null) keyword = mat.group(2);
            matches.add(keyword);
        }

        return (String[])matches.toArray(new String[matches.size()]);
    }

    /**
     * Joins the string from the list with each other by placing delimiter between them.
     *
     * @param strings   string to join.
     * @param delimiter delimiter to put in.
     *
     * @return joined copy.
     */
    public static String join(String[] strings, String delimiter)
    {
        String result = null;

        if (strings != null && strings.length > 0 && delimiter != null)
        {
            StringBuffer buf = new StringBuffer(strings[0]);
            for (int i = 1; i < strings.length; i++)
            {
                buf.append(delimiter).append(strings[i]);
            }

            result = buf.toString();
        }

        return result;
    }

    /**
     * Returns <code>TRUE</code> if string is <code>NULL</code> or empty.
     *
     * @param str string.
     *
     * @return <code>TRUE</code> if string is <code>NULL</code> or empty.
     */
    public static boolean isEmpty(String str)
    {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Digests the buffer with key using MD5 algorithm.
     *
     * @param buffer    buffer.
     * @param key       key is secret key (password or something else which isn't
     *                  going to be passed over network).
     *
     * @return digested buffer.
     *
     * @throws java.security.NoSuchAlgorithmException if there's no MD5 algorithm implemetation.
     */
    public static byte[] digestMD5(String buffer, String key)
        throws NoSuchAlgorithmException
    {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(buffer.getBytes());
        return md5.digest(key.getBytes());
    }

    /**
     * Parses long and return value or specified default if string is not a number or empty.
     *
     * @param longString    long string to parse.
     * @param longDefault   long default value.
     *
     * @return value or default value.
     */
    public static long parseLong(String longString, long longDefault)
    {
        long longNumber = longDefault;

        if (longString != null)
        {
            try
            {
                longNumber = Long.parseLong(longString);
            } catch (NumberFormatException e)
            {
                // Invalid number format
            }
        }

        return longNumber;
    }

    /**
     * Encodes string to be put in URL.
     *
     * <p>Example:</p>
     * <pre>
     *  input string: 'a &?b'
     *  output string: 'a+%26%3Fb'
     * </pre>
     *
     * @param str string to encode.
     *
     * @return encoded string or NULL if source was NULL.
     */
    public static String encodeForURL(String str)
    {
        if (str == null) return null;

        try
        {
            str = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("UTF-8 encoding isn't supported!", e);
        }

        return str;
    }

    /**
     * Replaces all occurances of target string with replacement in the str line.
     *
     * @param str           line to do replacements in.
     * @param target        target string to replace.
     * @param replacement   replacement string.
     *
     * @return the result.
     */
    public static String replaceStrings(String str, String target, String replacement)
    {
        if (target == null || replacement == null || str == null || target.equals(replacement)) return str;

        int len = target.length();
        int max = str.length();

        StringBuffer buf = new StringBuffer(str.length());
        int pos = 0;
        int index = str.indexOf(target, pos);
        while (pos < max && index != -1)
        {
            buf.append(str.substring(pos, index)).append(replacement);
            pos = index + len;
            index = str.indexOf(target, pos);
        }
        buf.append(str.substring(pos));

        return buf.toString();
    }
}
