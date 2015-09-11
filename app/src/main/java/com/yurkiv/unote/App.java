package com.yurkiv.unote;

import android.app.Application;

import jonathanfinerty.once.Once;

/**
 * Created by yurkiv on 10.09.2015.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Once.initialise(this);
    }
}
