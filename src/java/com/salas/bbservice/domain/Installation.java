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
// $Id: Installation.java,v 1.1.1.1 2006/10/23 13:55:33 alg Exp $
//

package com.salas.bbservice.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Installation object. Holds information about single installation.
 */
public class Installation
{
    private long id;
    private Date installationDate;
    private String version;
    private int runs;
    private String os;
    private String javaVersion;

    /**
     * Creates installation object.
     */
    public Installation()
    {
    }

    /**
     * Creates installation object.
     *
     * @param id            installation ID.
     * @param version       version of application.
     * @param runs          number of runs.
     * @param os            OS and its version.
     * @param javaVersion   version of JRE.
     */
    public Installation(long id, String version, int runs, String os, String javaVersion)
    {
        this.id = id;
        this.version = version;
        this.runs = runs;
        this.os = os;
        this.javaVersion = javaVersion;

        // reset milliseconds
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.MILLISECOND, 0);
        installationDate = cal.getTime();
    }

    /**
     * Returns identity.
     *
     * @return identity.
     */
    public long getId()
    {
        return id;
    }

    /**
     * Sets identity.
     *
     * @param id identity.
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Returns date of installation.
     *
     * @return installation date.
     */
    public Date getInstallationDate()
    {
        return installationDate;
    }

    /**
     * Sets date of installation.
     *
     * @param installationDate installation date.
     */
    public void setInstallationDate(Date installationDate)
    {
        this.installationDate = installationDate;
    }

    /**
     * Returns application version.
     *
     * @return application version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets application version.
     *
     * @param version application version.
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * Returns number of runs.
     *
     * @return number of runs.
     */
    public int getRuns()
    {
        return runs;
    }

    /**
     * Sets number of runs.
     *
     * @param runs number of runs.
     */
    public void setRuns(int runs)
    {
        this.runs = runs;
    }

    /**
     * Returns OS and its version.
     *
     * @return OS and its version.
     */
    public String getOs()
    {
        return os;
    }

    /**
     * Sets OS and its version.
     *
     * @param os OS and its version.
     */
    public void setOs(String os)
    {
        this.os = os;
    }

    /**
     * Returns the version of JRE.
     *
     * @return version of JRE.
     */
    public String getJavaVersion()
    {
        return javaVersion;
    }

    /**
     * Sets the version of JRE.
     *
     * @param javaVersion   version of JRE.
     */
    public void setJavaVersion(String javaVersion)
    {
        this.javaVersion = javaVersion;
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
        if (!(o instanceof Installation)) return false;

        final Installation installation = (Installation)o;

        if (runs != installation.runs) return false;
        if (installationDate != null ? !installationDate.equals(installation.installationDate)
            : installation.installationDate != null) return false;
        if (javaVersion != null ? !javaVersion.equals(installation.javaVersion)
            : installation.javaVersion != null) return false;
        if (os != null ? !os.equals(installation.os) : installation.os != null) return false;
        if (version != null ? !version.equals(installation.version)
            : installation.version != null) return false;

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
        return (installationDate != null ? installationDate.hashCode() : 0);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "Installation: id=" + id +
            ", version=" + (version != null ? version : "null") +
            ", installationDate=" + (installationDate != null
                ? installationDate.toString() : "null") +
            ", runs=" + runs +
            ", os=" + (os != null ? os : "null") +
            ", javaVersion=" + (javaVersion != null ? javaVersion : "null");
    }
}
