// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2006 by R. Pito Salas
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
// $Id $
//

package com.salas.bbservice.domain.dao;

import java.util.*;

/**
 * Simple holder for label, count and percentage.
 */
public class LabelCountPercentage
{
    private String  label;
    private int     count;
    private double  percentage;

    /**
     * Creates empty holder.
     */
    public LabelCountPercentage()
    {
    }

    /**
     * Creates initialized holder.
     *
     * @param label         label.
     * @param count         count.
     * @param percentage    percentage.
     */
    public LabelCountPercentage(String label, int count, double percentage)
    {
        this.label = label;
        this.count = count;
        this.percentage = percentage;
    }

    /**
     * Returns the label.
     *
     * @return label.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Sets new label.
     *
     * @param label label.
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Returns the count.
     *
     * @return count.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Sets new count.
     *
     * @param count count.
     */
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * Returns percentage.
     *
     * @return percentage.
     */
    public double getPercentage()
    {
        return percentage;
    }

    /**
     * Sets new percentage.
     *
     * @param percentage percentage.
     */
    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    /**
     * Takes source report data (unique values and counters) and prepares consolidated report.
     * The report counts number of value with equal counts. It also shows
     * percentage for each of the resulting entries.
     *
     * @param src source report data.
     *
     * @return consolidated data sorted by decreasing count.
     */
    public static LabelCountPercentage[] consolidate(ValueCount[] src)
    {
        if (src == null) return null;
        if (src.length == 0) return new LabelCountPercentage[0];

        Map countsToCounts = new HashMap(src.length);

        for (int i = 0; i < src.length; i++)
        {
            ValueCount vc = src[i];
            int cnt = vc.getCount();
            Integer key = new Integer(cnt);

            LabelCountPercentage lcp = (LabelCountPercentage)countsToCounts.get(key);
            if (lcp == null)
            {
                lcp = new LabelCountPercentage(Integer.toString(cnt), 1, 100d / src.length);
            } else
            {
                int ncnt = lcp.getCount() + 1;
                lcp.setCount(ncnt);
                lcp.setPercentage(100d * ncnt / src.length);
            }

            countsToCounts.put(key, lcp);
        }

        // Get results, sort them, and return
        Collection lcpv = countsToCounts.values();
        LabelCountPercentage[] lcps = (LabelCountPercentage[])lcpv.toArray(new LabelCountPercentage[lcpv.size()]);
        Arrays.sort(lcps, new LCPComparator(true));

        return lcps;
    }

    /**
     * Compares two <code>LabelCountPercentage</code> objects by counts. If counts are equal then
     * compares them by value. The direction can be changed with <code>descending</code>
     * property, but it affects only counts-related results, meaning that if you have two
     * objects with equal counts they will be compared by value ignoring the descending
     * property. It's implemented this way to support building charts easily where all
     * values with the same counts are always sorted alphabetically.
     */
    public static class LCPComparator implements Comparator
    {
        private boolean desc;

        public LCPComparator(boolean descending)
        {
            desc = descending;
        }

        public int compare(Object o1, Object o2)
        {
            int lcp1 = ((LabelCountPercentage)o1).getCount();
            int lcp2 = ((LabelCountPercentage)o2).getCount();

            int c = (lcp1 > lcp2)
                ? 1
                : (lcp1 == lcp2)
                    ? 0
                    : -1;

            if (c == 0)
            {
                c = ((LabelCountPercentage)o1).getLabel().compareTo(((LabelCountPercentage)o2).getLabel());
            } else if (desc) c = -c;

            return c;
        }
    }
}
