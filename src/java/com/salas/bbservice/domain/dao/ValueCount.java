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
// $Id: ValueCount.java,v 1.1.1.1 2006/10/23 13:55:36 alg Exp $
//

package com.salas.bbservice.domain.dao;

import java.util.*;

/**
 * Simple counter for value.
 */
public class ValueCount
{
    private String value;
    private int count;

    /**
     * Constructs new value count.
     */
    public ValueCount()
    {
    }

    /**
     * Constructs new value count.
     *
     * @param value value.
     * @param count count.
     */
    public ValueCount(String value, int count)
    {
        this.value = value;
        this.count = count;
    }

    /**
     * Returns value.
     *
     * @return value.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value value.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Returns count.
     *
     * @return count.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Sets count value.
     *
     * @param count new value.
     */
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * Groups rows with the same values and sums their counts.
     *
     * @param src source.
     *
     * @return groupped list.
     */
    public static ValueCount[] groupValues(ValueCount[] src)
    {
        if (src == null || src.length == 0) return src;

        Map values = new HashMap(src.length);
        for (int i = 0; i < src.length; i++)
        {
            ValueCount vc = src[i];
            Integer counts = (Integer)values.get(vc.getValue());

            if (counts == null) counts = new Integer(vc.getCount());
            else counts = new Integer(counts.intValue() + vc.getCount());

            values.put(vc.getValue(), counts);
        }

        // Shortcut which is applicable when nothing has been regrouped
        if (values.size() == src.length) return src;

        // Convert values map to ValueCount array
        ValueCount[] dst = new ValueCount[values.size()];
        int i = 0;
        Set set = values.entrySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();)
        {
            Map.Entry e = (Map.Entry)iterator.next();
            dst[i++] = new ValueCount((String)e.getKey(), ((Integer)e.getValue()).intValue());
        }
        return dst;
    }

    /**
     * Compares two <code>ValueCount</code> objects by counts. If counts are equal then
     * compares them by value. The direction can be changed with <code>descending</code>
     * property, but it affects only counts-related results, meaning that if you have two
     * objects with equal counts they will be compared by value ignoring the descending
     * property. It's implemented this way to support building charts easily where all
     * values with the same counts are always sorted alphabetically.
     */
    public static class CVComparator implements Comparator
    {
        private boolean desc;

        public CVComparator(boolean descending)
        {
            desc = descending;
        }

        public int compare(Object o1, Object o2)
        {
            int vc1 = ((ValueCount)o1).getCount();
            int vc2 = ((ValueCount)o2).getCount();

            int c = (vc1 > vc2)
                ? 1
                : (vc1 == vc2)
                    ? 0
                    : -1;

            if (c == 0)
            {
                c = ((ValueCount)o1).getValue().compareTo(((ValueCount)o2).getValue());
            } else if (desc) c = -c;

            return c;
        }
    }
}
