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
// $Id: ValueCountDate.java,v 1.1 2007/01/17 12:51:29 alg Exp $
//

package com.salas.bbservice.domain.dao;

import java.util.*;

/**
 * Simple counter for value with a date.
 */
public class ValueCountDate extends ValueCount
{
    private Date date;

    /**
     * Constructs new value count.
     */
    public ValueCountDate()
    {
    }

    /**
     * Constructs new value count.
     *
     * @param value value.
     * @param count count.
     * @param date date.
     */
    public ValueCountDate(String value, int count, Date date)
    {
        super(value, count);
        this.date = date;
    }

    /**
     * Sets the date field.
     *
     * @param date new date.
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * Returns the date field.
     *
     * @return date.
     */
    public Date getDate()
    {
        return date;
    }
}
