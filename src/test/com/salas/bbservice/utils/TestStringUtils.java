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
// $Id: TestStringUtils.java,v 1.1.1.1 2006/10/23 13:56:04 alg Exp $
//

package com.salas.bbservice.utils;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * This suite contains tests for <code>StringUtils</code> unit.
 */
public class TestStringUtils extends TestCase
{
    /**
     * Tests space-delimeted quoted keywords list.
     */
    public void testKeywordsToArraySpaces()
    {
        String[] result;

        result = StringUtils.keywordsToArray("a b c");
        assertTrue(dump(result), Arrays.equals(new String[] { "a", "b", "c" }, result));

        result = StringUtils.keywordsToArray("a \"b c\"");
        assertTrue(dump(result), Arrays.equals(new String[] { "a", "b c" }, result));

        result = StringUtils.keywordsToArray("\"a b\" c");
        assertTrue(dump(result), Arrays.equals(new String[] { "a b", "c" }, result));

        result = StringUtils.keywordsToArray(" \"a b c\"");
        assertTrue(dump(result), Arrays.equals(new String[] { "a b c" }, result));
    }

    /**
     * Tests '|'-delimeted quoted keywords list.
     */
    public void testKeywordsToArrayVertical()
    {
        String[] result;

        result = StringUtils.keywordsToArray("a|b|c");
        assertTrue(dump(result), Arrays.equals(new String[] { "a", "b", "c" }, result));

        result = StringUtils.keywordsToArray("a|b c");
        assertTrue(dump(result), Arrays.equals(new String[] { "a", "b c" }, result));

        result = StringUtils.keywordsToArray("a b|c");
        assertTrue(dump(result), Arrays.equals(new String[] { "a b", "c" }, result));
    }

    /**
     * Tests escaping of the double-quotes.
     */
    public void testEscapeForCDL()
    {
        assertEquals("\\\"", StringUtils.escapeForCDL("\""));
    }

    private String dump(String[] array)
    {
        StringBuffer buf = new StringBuffer();

        if (array.length > 0)
        {
            buf.append("'").append(array[0]).append("'");
            for (int i = 1; i < array.length; i++)
            {
                String s = array[i];
                buf.append(", '").append(s).append("'");
            }
        }

        return buf.toString();
    }

    public void testReplaceStrings()
    {
        // Null-string
        assertNull(StringUtils.replaceStrings(null, "a", "b"));

        // Null-target and null-replacement
        assertEquals("a", StringUtils.replaceStrings("a", null, "b"));
        assertEquals("a", StringUtils.replaceStrings("a", "a", null));

        // Empty string
        assertEquals("", StringUtils.replaceStrings("", "a", "b"));
        assertEquals("", StringUtils.replaceStrings("", "aa", "b"));
        assertEquals("", StringUtils.replaceStrings("", "a", "bb"));

        // No replacement
        assertEquals("a", StringUtils.replaceStrings("a", "b", "c"));

        // Replacements
        assertEquals("b", StringUtils.replaceStrings("a", "a", "b"));
        assertEquals("bb", StringUtils.replaceStrings("a", "a", "bb"));
        assertEquals("ba", StringUtils.replaceStrings("aaa", "aa", "b"));
        assertEquals(" b", StringUtils.replaceStrings(" aa", "aa", "b"));
        assertEquals(" b b c ", StringUtils.replaceStrings(" aa aa c ", "aa", "b"));
    }
}
