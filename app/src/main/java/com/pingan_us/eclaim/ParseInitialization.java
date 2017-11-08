package com.pingan_us.eclaim;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by yshui on 11/7/17.
 */

public class ParseInitialization extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, getResources().getString(R.string.back4app_app_id), getResources().getString(R.string.back4app_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
