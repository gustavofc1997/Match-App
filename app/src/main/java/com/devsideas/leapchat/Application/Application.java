package com.devsideas.leapchat.Application;

/**
 * Created by Latitude on 06/03/2017.
 */

public class Application extends android.app.Application {
    public static Application sInstance;

    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Application.sInstance=this;
    }
}
