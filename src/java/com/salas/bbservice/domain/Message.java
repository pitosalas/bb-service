// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2007 by R. Pito Salas
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
// $Id: Message.java,v 1.2 2007/07/04 09:39:08 alg Exp $
//

package com.salas.bbservice.domain;

import com.salas.bbservice.utils.StringUtils;

import java.util.Hashtable;

/**
 * Message data holder.
 */
public class Message
{
    private int     id;
    private int     type;
    private int     priority;
    private String  title;
    private String  text;
    private String  version;

    /**
     * Returns ID.
     *
     * @return ID.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets ID.
     *
     * @param id ID.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Returns the type.
     *
     * @return type.
     */
    public int getType()
    {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type type.
     */
    public void setType(int type)
    {
        this.type = type;
    }

    /**
     * Returns the priority.
     *
     * @return priority.
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * Sets priority.
     *
     * @param priority priority.
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    /**
     * Returns the title.
     *
     * @return title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the text.
     *
     * @return text.
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text.
     *
     * @param text text.
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Returns the version.
     *
     * @return version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version version.
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * Converts a message into the table.
     *
     * @return hash table with properties.
     */
    public Hashtable toHashtable()
    {
        Hashtable<String, Object> t = new Hashtable<String, Object>();
        t.put("id", getId());
        t.put("priority", getPriority());
        t.put("type", getType());
        t.put("title", StringUtils.toUTF8(getTitle()));
        t.put("text", StringUtils.toUTF8(getText()));
        t.put("version", getVersion());

        return t;
    }
}
