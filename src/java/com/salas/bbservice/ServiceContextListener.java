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

package com.salas.bbservice;

import com.salas.bbpmod.PMod;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

/**
 * Service context listener prepares and shuts down the application upon
 * deployment and undeployment.
 */
public class ServiceContextListener implements ServletContextListener
{
    private PMod pmod = new PMod();

    /**
     * Invoked when the application starts.
     *
     * @param event event.
     */
    public void contextInitialized(ServletContextEvent event)
    {
        pmod.init();
    }

    /**
     * Invoked when the application terminates.
     *
     * @param event event.
     */
    public void contextDestroyed(ServletContextEvent event)
    {
        pmod.uninit();
    }
}
