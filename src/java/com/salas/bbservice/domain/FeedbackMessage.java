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
// $Id: FeedbackMessage.java,v 1.1.1.1 2006/10/23 13:55:33 alg Exp $
//

package com.salas.bbservice.domain;



/**
 * Feedback message reported.
 */
public class FeedbackMessage
{
    private int     id;
    private long    time;
    private String  message;

    /**
     * Creates empty feedback message.
     */
    public FeedbackMessage()
    {
        this(null);
    }

    /**
     * Creates the feedback message.
     *
     * @param aMessage  message.
     */
    public FeedbackMessage(String aMessage)
    {
        id = -1;
        message = aMessage;
        time = System.currentTimeMillis();
    }

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
     * Sets new ID.
     *
     * @param aId ID.
     */
    public void setId(int aId)
    {
        id = aId;
    }

    /**
     * Returns time of message.
     *
     * @return time of message.
     */
    public long getTime()
    {
        return time;
    }

    /**
     * Sets time of message.
     *
     * @param aTime time.
     */
    public void setTime(long aTime)
    {
        time = aTime;
    }

    /**
     * Returns message.
     *
     * @return message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param aMessage message.
     */
    public void setMessage(String aMessage)
    {
        message = aMessage;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare.
     *
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FeedbackMessage)) return false;

        final FeedbackMessage clientError = (FeedbackMessage)o;

        if (message != null ? !message.equals(clientError.message) : clientError.message != null)
            return false;

        if (time != clientError.time) return false;

        return true;
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
     *
     * @return  a hash code value for this object.
     */
    public int hashCode()
    {
        return (int)(time ^ (time >>> 32));
    }

    /**
     * Returns string representation.
     *
     * @return string representation.
     */
    public String toString()
    {
        return "ClientError: id=" + id +
            ", message=" + message +
            ", time=" + time;
    }
}
