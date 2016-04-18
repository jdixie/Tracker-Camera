package com.ninjapiratestudios.trackercamera;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Setup extends Activity {
	
	private final String TAG = "BluetoothLog";//To isolate

    private Thread setupPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_setup);

        if(((BT_Application)this.getApplicationContext()).mBluetooth.startBluetooth())
            ((BT_Application)this.getApplicationContext()).mBluetooth.discover_helper();
        else
            alertUser();

        setupPage = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(20000);
                } catch (Exception e) {

                }finally {
                    goTo_videoActivity();
                    //alertUser();
                }
            }
        };
        setupPage.start();

    }

    public void alertUser(){
        PopupDialog dialog = PopupDialog.newInstance();
        dialog.show(getFragmentManager(), PopupDialog.FRAGMENT_TAG);
    }
    public void goTo_videoActivity(){
        if(((BT_Application)this.getApplicationContext()).mBluetooth.isConnected()) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent i = new Intent(Setup.this, VideoActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 2000);
        }else
            alertUser();
    }

/*
    public void getReadPermissions() {
        //check for permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }

    public void getCameraPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    public void getAudioPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
        }
    }
*/

}
