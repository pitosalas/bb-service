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
// $Id: TestPlansHandler.java,v 1.1 2007/01/24 09:41:31 alg Exp $
//

package com.salas.bbservice.plans;

import junit.framework.TestCase;
import com.salas.bbservice.persistence.IPlansDao;
import com.salas.bbservice.domain.dao.UserPlan;

import java.util.*;

/**
 * When no user registered, password incorrect or the plan is free,
 * the hash is always '0' and means "Unlimited Free Plan".
 */
public class TestPlansHandler extends TestCase
{
    private CustomPlansDao dao;
    private CustomPlansHandler han;

    /**
     * Initializes the handler and DAO.
     *
     * @throws Exception in case of any troubles.
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        dao = new CustomPlansDao();
        han = new CustomPlansHandler();
    }

    /**
     * No user or password is invalid.
     */
    public void testGetHash_no_user()
    {
        dao.users_present = false;
        assertHash(0, 1, 1, false);
    }

    /**
     * Unmetered free plan.
     */
    public void testGetHash_free()
    {
        dao.setPlanState(1, null, false, 0, 0);
        assertHash(0, 1, 1, false);
    }

    /**
     * Paid unlimited.
     */
    public void testGetHash_paid_unlim()
    {
        dao.setPlanState(2, null, false, 5, 3);
        assertHash(0, dao.users_plan_id, dao.plans_serial, false);
    }

    /**
     * Paid limited.
     */
    public void testGetHash_paid_lim()
    {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, 1);

        dao.setPlanState(3, c.getTime(), false, 5, 3);
        assertHash(c.getTimeInMillis(), dao.users_plan_id, dao.plans_serial, false);
    }

    /**
     * Expired paid limited account.
     */
    public void testGetHash_paid_expired()
    {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, -1);

        dao.setPlanState(2, c.getTime(), false, 5, 3);
        assertHash(c.getTimeInMillis(), dao.users_plan_id, dao.plans_serial, false);
    }

    /**
     * Paid trial.
     */
    public void testGetHash_paid_trial()
    {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, 1);

        dao.setPlanState(2, c.getTime(), true, 5, 3);
        assertHash(c.getTimeInMillis(), dao.users_plan_id, dao.plans_serial, true);
    }

    /**
     * Paid trial.
     */
    public void testGetHash_paid_trial_expired()
    {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, -1);

        dao.setPlanState(2, c.getTime(), true, 0, 0);
        assertHash(c.getTimeInMillis(), dao.users_plan_id, dao.plans_serial, true);
    }

    /**
     * Check if the expected hash matches actual.
     *
     * @param expTime       expiration time.
     * @param planId        plan ID.
     * @param planSerial    plan serial.
     * @param isTrial       TRUE if plan is trial.
     */
    private void assertHash(long expTime, int planId, int planSerial, boolean isTrial)
    {
        String hash = Long.toString(expTime) + "-" + planId + "-" + planSerial + "-" + (isTrial ? 1 : 0);
        assertEquals(hash, han.getHash(null, null));
    }

    // ------------------------------------------------------------------------
    // Returning the features list
    // When:
    //  * the user is unregistered,
    //  * the plan has expired,
    //  * the plan is set to free
    // the free plan feature set is returned.
    // ------------------------------------------------------------------------

    public void testGetFeatures_no_user()
    {
        dao.users_present = false;
        assertFeatures(0, 0, 0, false, "1", "2");
    }

    public void testGetFeatures_free()
    {
        dao.setPlanState(1, null, false, 0, 0);
        assertFeatures(0, 0, 0, false, "1", "2");
    }

    public void testGetFeatures_paid_unlimited()
    {
        dao.setPlanState(2, null, false, 5, 3);
        assertFeatures(0, 5, 3, false, "10", "20");
    }

    public void testGetFeatures_paid_limited()
    {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, 1);

        dao.setPlanState(3, c.getTime(), false, 5, 3);
        assertFeatures(c.getTimeInMillis(), 5, 3, false, "100", "200");
    }

    public void testGetFeatures_paid_trial()
    {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, 1);

        dao.setPlanState(3, c.getTime(), true, 5, 3);
        assertFeatures(c.getTimeInMillis(), 5, 3, true, "100", "200");
    }

    /**
     * Checks if correct feature values are provided.
     *
     * @param expTime       expiration time.
     * @param price         price.
     * @param periodMonths  period in months.
     * @param isTrial       trial.
     * @param f1            feature 1 value.
     * @param f2            feature 2 value.
     */
    private void assertFeatures(long expTime, float price, int periodMonths, boolean isTrial, String f1, String f2)
    {
        Map<String, String> map = han.getFeatures("", "");
        assertEquals(6, map.size());
        assertEquals(Long.toString(expTime), map.get("_exp_date"));
        assertEquals(Float.toString(price), map.get("_price"));
        assertEquals(Integer.toString(periodMonths), map.get("_period_months"));
        assertEquals(isTrial ? "1" : "0", map.get("_is_trial"));
        assertEquals(f1, map.get("F1"));
        assertEquals(f2, map.get("F2"));
    }

    // ------------------------------------------------------------------------
    // Helper class
    // ------------------------------------------------------------------------

    /**
     * Custom plans handler we use to use simulated database.
     */
    private class CustomPlansHandler extends PlansHandler
    {
        /**
         * Returns the DAO.
         *
         * @return DAO.
         */
        protected IPlansDao getDAO()
        {
            return dao;
        }
    }

    /**
     * Custom plans DAO we use to simulate database.
     */
    private class CustomPlansDao implements IPlansDao
    {
        private Map<Integer, Map<String, String>> planFeatures = new HashMap<Integer, Map<String, String>>();

        /**
         * Creates a DAO with fixture.
         */
        public CustomPlansDao()
        {
            Map<String, String> features = new HashMap<String, String>();
            features.put("F1", "1");
            features.put("F2", "2");
            planFeatures.put(1, features);

            features = new HashMap<String, String>();
            features.put("F1", "10");
            features.put("F2", "20");
            planFeatures.put(2, features);

            features = new HashMap<String, String>();
            features.put("F1", "100");
            features.put("F2", "200");
            planFeatures.put(3, features);
        }

        /**
         * Returns the plan setup hash.
         *
         * @param email    email of the account.
         * @param password password of the account.
         *
         * @return the hash value.
         */
        public String getHash(String email, String password)
        {
            UserPlan up = getUserPlan(email, password);

            Date expdate = up.getPlan_exp_date();
            long exptime = expdate == null ? 0 : expdate.getTime();

            return exptime +
                    "-" + up.getPlan_id() +
                    "-" + up.getPlan_serial() +
                    "-" + (up.isPlan_is_trial() ? 1 : 0);
        }

        /**
         * Returns info on the plan the user is assigned to.
         *
         * @param email   email of the account.
         * @param pasword password of the account.
         *
         * @return the info on user plan.
         */
        public UserPlan getUserPlan(String email, String pasword)
        {
            UserPlan up = new UserPlan();
            up.setPlan_id(users_plan_id);
            up.setPlan_exp_date(users_plan_exp_date);
            up.setPlan_price(users_plan_price);
            up.setPlan_period_months(users_plan_period_months);
            up.setPlan_is_trial(users_plan_is_trial);
            up.setPlan_serial(plans_serial);
            
            return up;
        }

        /**
         * Returns the map of all features available for the plan.
         *
         * @param planId ID of the plan to learn features for.
         *
         * @return the map.
         */
        public Map<String, String> getFeatures(int planId)
        {
            return planFeatures.get(planId);
        }

        // --------------------------------------------------------------------
        // State
        // --------------------------------------------------------------------

        public boolean  users_present               = true;
        public int      users_plan_id               = 1;
        public Date     users_plan_exp_date         = null;
        public boolean  users_plan_is_trial         = false;
        public float    users_plan_price            = 0;
        public int      users_plan_period_months    = 0;

        public int      plans_serial                = 1;

        public Map      features                    = new HashMap();

        public void setPlanState(int id, Date exp_date, boolean is_trial, float price, int period_months)
        {
            users_plan_id = id;
            users_plan_exp_date = exp_date;
            users_plan_is_trial = is_trial;
            users_plan_price = price;
            users_plan_period_months = period_months;
        }
    }
}
