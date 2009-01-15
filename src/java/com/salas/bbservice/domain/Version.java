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
// $Id: Version.java,v 1.1.1.1 2006/10/23 13:55:35 alg Exp $
//

package com.salas.bbservice.domain;

/**
 * Released version.
 */
public class Version
{
    private int     id = -1;
    private String  version;
    private long    releaseTime;
    private boolean production;

    /**
     * Creates empty version object.
     */
    public Version()
    {
    }

    /**
     * Creates version object.
     *
     * @param aVersion      name of version ("1.2", "2.0" ...).
     * @param aReleaseTime  time of version release.
     * @param aProduction   <code>TRUE</code> if it's production version.
     */
    public Version(String aVersion, long aReleaseTime, boolean aProduction)
    {
        version = aVersion;
        releaseTime = aReleaseTime;
        production = aProduction;
    }

    /**
     * Returns version ID.
     *
     * @return version ID.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the ID of version.
     *
     * @param aId ID of version.
     */
    public void setId(int aId)
    {
        id = aId;
    }

    /**
     * Returns version name.
     *
     * @return version name.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets new version name.
     *
     * @param aVersion version name.
     */
    public void setVersion(String aVersion)
    {
        version = aVersion;
    }

    /**
     * Returns release time.
     *
     * @return release time.
     */
    public long getReleaseTime()
    {
        return releaseTime;
    }

    /**
     * Sets release time.
     *
     * @param aReleaseTime release time.
     */
    public void setReleaseTime(long aReleaseTime)
    {
        releaseTime = aReleaseTime;
    }

    /**
     * Returns <code>TRUE</code> if it is production version.
     *
     * @return <code>TRUE</code> if it is production version.
     */
    public boolean isProduction()
    {
        return production;
    }

    /**
     * Sets production version flag.
     *
     * @param aProduction <code>TRUE</code> if it is production version.
     */
    public void setProduction(boolean aProduction)
    {
        production = aProduction;
    }

    /**
     * Returns <code>TRUE</code> if objects are equal to each other.
     *
     * @param o other object.
     *
     * @return <code>TRUE</code> if objects are equal to each other.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Version version1 = (Version)o;

        if (releaseTime != version1.releaseTime) return false;
        if (!version.equals(version1.version)) return false;
        if (production != version1.production) return false;

        return true;
    }

    /**
     * Calculates hash code of the object.
     *
     * @return hash code.
     */
    public int hashCode()
    {
        return (int)(releaseTime ^ (releaseTime >>> 32));
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "Version: id=" + id + ", version=" + version + ", production=" + production +
            ",releaseTime=" + releaseTime;
    }
}
