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
// $Id: MetaDataCleaner.java,v 1.1.1.1 2006/10/23 13:55:44 alg Exp $
//

package com.salas.bbservice.service.meta;

import com.salas.bbservice.persistence.IBlogDao;
import com.salas.bbservice.persistence.DaoConfig;
import com.ibatis.dao.client.DaoManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cleaner of meta information.
 */
class MetaDataCleaner
{
    private static final Logger LOG = Logger.getLogger(MetaDataCleaner.class.getName());
    private static final long STARTUP_DELAY = 1000;
    private static final long MILLIS_IN_MINUTE = 60000;

    private Timer       timer;
    private long        lifeSpan;
    private int         minBlogsInDatabase;

    /**
     * Creates cleaner task.
     *
     * @param cleanupPeriod         period of cleanup in minutes.
     * @param aLifeSpan             maximum time in minutes blog can live without being accessed.
     * @param aMinBlogsInDatabase   minimum number of blogs to leave after cleanup.
     */
    public MetaDataCleaner(long cleanupPeriod, long aLifeSpan, int aMinBlogsInDatabase)
    {
        timer = new Timer(true);
        lifeSpan = aLifeSpan * MILLIS_IN_MINUTE;  // to milliseconds
        minBlogsInDatabase = aMinBlogsInDatabase;

        long period = cleanupPeriod * MILLIS_IN_MINUTE;
        timer.scheduleAtFixedRate(new CleanerTask(), STARTUP_DELAY, period);

        System.out.println("Blogs cleanup period = " + cleanupPeriod + " minutes");
        System.out.println("Blog life span = " + aLifeSpan + " minutes");
    }

    /**
     * Terminates cleanup.
     */
    public void terminate()
    {
        timer.cancel();
    }

    // ---------------------------------------------------------------------------------------------
    // Cleaner task
    // ---------------------------------------------------------------------------------------------

    /**
     * Task for cleaning database.
     */
    private class CleanerTask extends TimerTask
    {
        public void run()
        {
            LOG.info("Blogs cleanup");
            try
            {
                IBlogDao blogDao = (IBlogDao)DaoConfig.getDao(IBlogDao.class);
                blogDao.deleteOld(lifeSpan, minBlogsInDatabase);
            } catch (Exception e)
            {
                LOG.log(Level.SEVERE, "Could not clean blog records.", e);
            }
        }
    }
}
