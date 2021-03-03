package com.example.scutapp;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication MyApplicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplicationInstance = this;
    }

    public static MyApplication getMyApplicationInstance() {
        return MyApplicationInstance;
    }
}
