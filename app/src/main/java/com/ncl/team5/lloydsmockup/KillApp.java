package com.ncl.team5.lloydsmockup;

import android.app.Application;

/**
 * Created by benlambert on 07/02/2015.
 */

/* This is an application global variable (why it extends application)
 * that is used to decide when to kill the application. If the app calls stop
 * and the value of KillApp is true, it will cause the user to login again.
 * The actual implementation is simple, just get and set methods.
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
