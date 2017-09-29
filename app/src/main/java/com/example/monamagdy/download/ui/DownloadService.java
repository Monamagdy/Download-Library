package com.example.monamagdy.download.service;

/**
 * Created by monamagdy on 9/20/17.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadService extends Service {

    /**
     * interface for clients that bind
     */
    IBinder mBinder;
    public String fileLink;
    /**
     * The service is starting, due to a call to startService()
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       fileLink = intent.getExtras().get("link_to_download").toString();
        new DownloadThread(this,fileLink).execute();

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