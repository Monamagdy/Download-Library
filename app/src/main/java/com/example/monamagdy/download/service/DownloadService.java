package com.example.monamagdy.download.service;

/**
 * Created by monamagdy on 9/20/17.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {

    /**
     * interface for clients that bind
     */
    IBinder mBinder;

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("SERVICE_START" , ":)");
        new DownloadThread(this).execute();

        return START_STICKY;
    }
    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}