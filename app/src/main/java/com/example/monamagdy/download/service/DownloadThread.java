package com.example.monamagdy.download.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.monamagdy.download.R;
import com.example.monamagdy.download.utility.DownloadConstants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by monamagdy on 9/20/17.
 */

public class DownloadThread extends AsyncTask<Void, Integer, Void> {

    private int lenghtOfFile;
    private Context context;
    private float percentageDownloaded;
    private String fileLink;

    public DownloadThread(Context context, String fileLink) {
        this.context = context;
        this.fileLink = fileLink;
    }

    @Override
    protected Void doInBackground(Void... params) {

        int count;
        try {
            URL url = new URL(fileLink);
            Log.d("file link",fileLink);
            URLConnection connection = url.openConnection();
            connection.connect();

            lenghtOfFile = connection.getContentLength();

            publishProgress(lenghtOfFile);

            Intent intent = new Intent(DownloadConstants.DOWNLOAD_BROADCAST);
            intent.putExtra("file_size", lenghtOfFile);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            OutputStream output = new FileOutputStream(dir + DownloadConstants.DOWNLOADED_FILENAME);
            byte data[] = new byte[1024];

            float total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                percentageDownloaded = (total / lenghtOfFile) * 100;
                intent.putExtra(DownloadConstants.PROGRESS_UPDATE_INTENT_NAME, Math.round(percentageDownloaded));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                output.write(data, 0, count);
            }

            output.flush();// flushing output
            output.close();// closing streams
            input.close();

        } catch (Exception e) {
            Toast.makeText(context,"Please check your link",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        this.percentageDownloaded = values[0];
        notificationDisplay(values[0]);
        super.onProgressUpdate(values);
    }

    public void notificationDisplay(final int x) {
        final int id = 1;
        final NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context);
        notifyBuilder.setContentTitle("Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                notifyBuilder.setProgress(100, x, false);
                notifyManager.notify(id, notifyBuilder.build());
                while (getStatus().equals(Status.RUNNING));
                notifyBuilder.setContentText("Download complete").setProgress(0, 0, false);
                notifyManager.notify(id, notifyBuilder.build());
            }
        }
        ).start();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ((DownloadService) context).stopSelf();
        super.onPostExecute(aVoid);
    }

}


