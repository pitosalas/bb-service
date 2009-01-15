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
// $Id: ValueDouble.java,v 1.1.1.1 2006/10/23 13:55:36 alg Exp $
//

package com.salas.bbservice.domain.dao;

import java.util.Comparator;

/**
 * Simple value with double.
 */
public class ValueDouble
{
    private String value;
    private double count;

    /**
     * Constructs new value count.
     */
    public ValueDouble()
    {
    }

    /**
     * Constructs new value count.
     *
     * @param value value.
     * @param count count.
     */
    public ValueDouble(String value, double count)
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
    public double getCount()
    {
        return count;
    }

    /**
     * Sets count value.
     *
     * @param count new value.
     */
    public void setCount(double count)
    {
        this.count = count;
    }
}
