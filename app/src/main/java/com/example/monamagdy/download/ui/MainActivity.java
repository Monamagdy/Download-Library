package com.example.monamagdy.download.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monamagdy.download.R;
import com.example.monamagdy.download.service.DownloadService;
import com.example.monamagdy.download.utility.DownloadConstants;
import com.example.monamagdy.download.utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.pb_download_progress)
    ProgressBar downloadProgress;

    @BindView(R.id.tv_message)
    TextView textFileSize;

    @BindView(R.id.edt_link)
    EditText downloadLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if(downloadLink.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(),"Please enter your link",Toast.LENGTH_LONG).show();
            else{
                Intent intent = new Intent(this, DownloadService.class);
                intent.putExtra("link_to_download",downloadLink.getText().toString());
                startService(intent);
                textFileSize.setText(R.string.message);
            }
        }
    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadProgressReceiver, new IntentFilter(DownloadConstants.DOWNLOAD_BROADCAST));
        super.onStart();
    }

    @OnClick(R.id.b_download)
    public void onDownloadPressed(){
        if(Utils.verifyStoragePermissions(MainActivity.this)){
            if(downloadLink.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(),"Please enter your link",Toast.LENGTH_LONG).show();
            else{
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra("link_to_download",downloadLink.getText().toString());
            startService(intent);
            textFileSize.setText(R.string.message);
            }
        }
    }

    private final BroadcastReceiver downloadProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int temp = intent.getIntExtra("file_size", 0) / 1024;
            String message = "Your file size is " + String.valueOf(temp) + " KB";
            textFileSize.setText(message);
            downloadProgress.setProgress(intent.getIntExtra(DownloadConstants.PROGRESS_UPDATE_INTENT_NAME, 0));
        }
    };

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadProgressReceiver);
        super.onStop();
    }

}
