package com.example.mkarimi.icu;

import android.app.Application;

import net.gotev.uploadservice.UploadService;

/**
 * Created by mkarimi on 11/20/17.
 */

public class Initializer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
       // UploadService.NAMESPACE = "com.example.mkarimi.icu";
    }
}
