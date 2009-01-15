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
// $Id: DatesRange.java,v 1.1.1.1 2006/10/23 13:55:35 alg Exp $
//

package com.salas.bbservice.domain.dao;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date range holder object.
 */
public class DatesRange
{
    private static final MessageFormat FORMAT =
        new MessageFormat("{0,number,####}-{1,number,##}-{2,number,##}");
    private static final SimpleDateFormat SIMPLE_FORMAT =
        new SimpleDateFormat("yyyy-MM-dd");

    private String start;
    private String end;

    /**
     * Creates date range holder.
     *
     * @param aStart    start date.
     * @param aEnd      end date.
     *
     * @see #createDate(int, int, int)
     */
    public DatesRange(String aStart, String aEnd)
    {
        start = aStart;
        end = aEnd;
    }

    /**
     * Creates dates range from two dates.
     *
     * @param aStart    start date.
     * @param aEnd      end date.
     */
    public DatesRange(Date aStart, Date aEnd)
    {
        this(SIMPLE_FORMAT.format(aStart), SIMPLE_FORMAT.format(aEnd));
    }

    /**
     * Returns start date.
     *
     * @return start date.
     */
    public String getStart()
    {
        return start;
    }

    /**
     * Returns end date.
     *
     * @return end date.
     */
    public String getEnd()
    {
        return end;
    }

    /**
     * Creates date string from components.
     *
     * @param year  year.
     * @param month month.
     * @param day   day.
     *
     * @return date string.
     */
    public static String createDate(int year, int month, int day)
    {
        return FORMAT.format(new Object[] { new Integer(year), new Integer(month),
            new Integer(day) });
    }
}
