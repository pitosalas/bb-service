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
// $Id: UserPlan.java,v 1.2 2007/01/25 16:58:45 alg Exp $
//

package com.salas.bbservice.domain.dao;

import java.util.Date;

/**
 * The holder for the user-plan information.
 */
public class UserPlan
{
    private int     plan_id;
    private int     plan_serial;
    private String  plan_name;
    private float   plan_price;
    private int     plan_period_months;
    private Date    plan_exp_date;
    private boolean plan_is_trial;

    public UserPlan()
    {
    }

    public int getPlan_id()
    {
        return plan_id;
    }

    public void setPlan_id(int plan_id)
    {
        this.plan_id = plan_id;
    }

    public String getPlan_name()
    {
        return plan_name;
    }

    public void setPlan_name(String plan_name)
    {
        this.plan_name = plan_name;
    }

    public float getPlan_price()
    {
        return plan_price;
    }

    public void setPlan_price(float plan_price)
    {
        this.plan_price = plan_price;
    }

    public int getPlan_period_months()
    {
        return plan_period_months;
    }

    public void setPlan_period_months(int plan_period_months)
    {
        this.plan_period_months = plan_period_months;
    }

    public Date getPlan_exp_date()
    {
        return plan_exp_date;
    }

    public void setPlan_exp_date(Date plan_exp_date)
    {
        this.plan_exp_date = plan_exp_date;
    }

    public boolean isPlan_is_trial()
    {
        return plan_is_trial;
    }

    public void setPlan_is_trial(boolean plan_is_trial)
    {
        this.plan_is_trial = plan_is_trial;
    }

    public int getPlan_serial()
    {
        return plan_serial;
    }

    public void setPlan_serial(int plan_serial)
    {
        this.plan_serial = plan_serial;
    }
}
