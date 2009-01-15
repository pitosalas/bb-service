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
// $Id: AutoDisablingDiscovererWrapper.java,v 1.1.1.1 2006/10/23 13:55:44 alg Exp $
//

package com.salas.bbservice.service.meta.discovery;

import com.salas.bbservice.domain.Blog;

import java.net.URL;
import java.io.IOException;
import java.util.Map;

/**
 * Wrapper for discoverer disabling it for some time in case sub-discoverer
 * reports too many consequential IOExceptions.
 */
public class AutoDisablingDiscovererWrapper implements IBlogDiscoverer
{
    // Default period - 10 minutes
    private static final long DEFAULT_DISABLING_PERIOD = 600000;

    private static final int DEFAULT_MAX_FAILURES = 20;

    private IBlogDiscoverer subDiscoverer;
    private long            disablingPeriod;
    private int             maxFailures;

    private long            timeOfDisabling;
    private int             failures;

    /**
     * Automatic disabling discoverer. Decorates another discoverer with defined capabilities.
     *
     * @param aSubDiscoverer sub-discoverer.
     */
    public AutoDisablingDiscovererWrapper(IBlogDiscoverer aSubDiscoverer)
    {
        subDiscoverer = aSubDiscoverer;
        disablingPeriod = DEFAULT_DISABLING_PERIOD;
        maxFailures = DEFAULT_MAX_FAILURES;

        timeOfDisabling = -1;
        failures = 0;
    }

    /**
     * Sets the properties for discoverer.
     *
     * @param properties properties.
     */
    public void setProperties(Map properties)
    {
    }

    /**
     * Returns the OR'ed list of fields provided by this discoverer.
     *
     * @return list of fields.
     */
    public int getProvidedFields()
    {
        return subDiscoverer == null ? 0 : subDiscoverer.getProvidedFields();
    }

    /**
     * Makes a try to discover the URL and update information in the source blog.
     *
     * @param source source blog.
     * @param url    url to discover.
     *
     * @return fields actually discovered.
     *
     * @throws java.io.IOException in case of any communication problems.
     */
    public int discover(Blog source, URL url) throws IOException
    {
        int discoveredFields = 0;
        if (isEnabled())
        {
            try
            {
                discoveredFields = subDiscoverer == null ? 0 : subDiscoverer.discover(source, url);
                resetFailures();
            } catch (IOException e)
            {
                countFailure();
                throw e;
            }
        } else
        {
            throw new IOException("Disabled temporarily.");
        }

        return discoveredFields;
    }

    private void resetFailures()
    {
        synchronized (this)
        {
            failures = 0;
        }
    }

    private void countFailure()
    {
        synchronized (this)
        {
            failures++;
            if (failures > maxFailures)
            {
                timeOfDisabling = System.currentTimeMillis();
            }
        }
    }

    private boolean isEnabled()
    {
        boolean enabled = false;

        synchronized (this)
        {
            if (timeOfDisabling == -1)
            {
                enabled = true;
            } else if (System.currentTimeMillis() - timeOfDisabling > disablingPeriod)
            {
                timeOfDisabling = -1;
                failures = 0;
                enabled = true;
            }
        }

        return enabled;
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return subDiscoverer == null ? super.toString() : subDiscoverer.toString() +
            " [fused - " + failures + "/" + maxFailures + "]";
    }
}
