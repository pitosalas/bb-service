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
// $Id: User.java,v 1.1.1.1 2006/10/23 13:55:33 alg Exp $
//
package com.salas.bbservice.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * User object. Holds all necessary information about user.
 */
public class User
{
    private int     id = -1;
    private String  fullName;
    private String  email;
    private String  password;
    private boolean isActivated;
    private Date    registrationDate;
    private String  locale;
    private boolean notifyOnUpdates;

    private Date    lastSyncTime;

    /**
     * Constructs new user.
     */
    public User()
    {
    }

    /**
     * Constructs new user with given properties.
     *
     * @param fullName          full user name.
     * @param email             e-mail address.
     * @param password          password.
     * @param locale            user locale (i.e. en_US).
     * @param notifyOnUpdates   flag, showing can the e-mail be used for update notifications.
     */
    public User(String fullName, String email, String password, String locale,
                boolean notifyOnUpdates)
    {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.isActivated = false;
        this.locale = locale;
        this.notifyOnUpdates = notifyOnUpdates;

        // reset milliseconds
        setRegistrationDate(new Date());
    }

    /**
     * Returns identificator of user.
     *
     * @return ID.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets identificator of user.
     *
     * @param id    identificator of user.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Returns full user name.
     *
     * @return full user name.
     */
    public String getFullName()
    {
        return fullName;
    }

    /**
     * Sets full user name.
     *
     * @param fullName  full user name.
     */
    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    /**
     * Returns e-mail address.
     *
     * @return e-mail address.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets e-mail address.
     *
     * @param email     e-mail address.
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Returns password.
     *
     * @return password.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password password.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Returns value of flag showing if this user account is activated.
     *
     * @return TRUE if activated.
     */
    public boolean isActivated()
    {
        return isActivated;
    }

    /**
     * Sets activation state of this account.
     *
     * @param activated TRUE if activated.
     */
    public void setActivated(boolean activated)
    {
        isActivated = activated;
    }

    /**
     * Returns date of registration.
     *
     * @return registration date.
     */
    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    /**
     * Sets date of registration.
     *
     * @param registrationDate registration date.
     */
    public void setRegistrationDate(Date registrationDate)
    {
        this.registrationDate = resetMillis(registrationDate);
    }

    /**
     * Resets milliseconds.
     *
     * @param date date.
     *
     * @return trimmed time.
     */
    private static Date resetMillis(Date date)
    {
        if (date != null)
        {
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.set(Calendar.MILLISECOND, 0);
            date = cal.getTime();
        }
        
        return date;
    }

    /**
     * Returns TRUE when user allowed to use his e-mail for update notifications.
     *
     * @return TRUE when it's possible to use e-mail for update notifications.
     */
    public boolean isNotifyOnUpdates()
    {
        return notifyOnUpdates;
    }

    /**
     * Sets the value of notification usage flag.
     *
     * @param notifyOnUpdates   TRUE if user allowed to use account for notifications.
     */
    public void setNotifyOnUpdates(boolean notifyOnUpdates)
    {
        this.notifyOnUpdates = notifyOnUpdates;
    }

    /**
     * Returns locale of user account.
     *
     * @return locale.
     */
    public String getLocale()
    {
        return locale;
    }

    /**
     * Sets locale of user account.
     *
     * @param locale locale.
     */
    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    /**
     * Returns the time of last sync repored by the client itself.
     *
     * @return the time of last sync.
     */
    public Date getLastSyncTime()
    {
        return lastSyncTime;
    }

    /**
     * Sets the time of last sync reported by the client.
     *
     * @param time the time of last sync.
     */
    public void setLastSyncTime(Date time)
    {
        lastSyncTime = resetMillis(time);
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
        if (!(o instanceof User)) return false;

        final User user = (User)o;

        if (notifyOnUpdates != user.notifyOnUpdates) return false;
        if (isActivated != user.isActivated) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;

        if (fullName != null ? !fullName.equals(user.fullName) : user.fullName != null)
            return false;

        if (locale != null ? !locale.equals(user.locale) : user.locale != null) return false;

        if (password != null ? !password.equals(user.password) : user.password != null)
            return false;

        if (registrationDate != null ? !registrationDate.equals(user.registrationDate)
                : user.registrationDate != null) return false;

        return !(lastSyncTime != null ? !lastSyncTime.equals(user.lastSyncTime)
            : user.lastSyncTime != null);
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
        return (email != null ? email.hashCode() : 0);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "User: id=" + id +
            ", fullName=" + (fullName != null ? fullName : "null") +
            ", email=" + (email != null ? email : "null") +
            ", password=" + (password != null ? password : "null") +
            ", isActivated=" + isActivated +
            ", registrationDate=" + (registrationDate != null
                ? registrationDate.toString() : "null") +
            ", lastSyncTime=" + lastSyncTime;
    }
}
