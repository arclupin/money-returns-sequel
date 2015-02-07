package com.ncl.team5.lloydsmockup;

import android.app.Application;

/**
 * Created by benlambert on 07/02/2015.
 */
public class KillApp extends Application {
    private boolean killapp = false;

    public boolean getStatus()
    {
        return killapp;
    }

    public void setStatus(boolean status)
    {
        killapp = status;
    }
}
