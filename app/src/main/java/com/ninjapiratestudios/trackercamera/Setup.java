package com.ninjapiratestudios.trackercamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class Setup extends Activity {
	
	private final String TAG = "BluetoothLog";//To isolate
    long start, elapsed;
    Thread setupThread;
    Context context;
   //BT_Application application = ((BT_Application)this.getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_setup);
        context = this.getApplicationContext();

        if(((BT_Application)this.getApplicationContext()).mBluetooth.startBluetooth())
            ((BT_Application)this.getApplicationContext()).mBluetooth.discover_helper();
        else
            alertUser();

        setupThread = new Thread() {
            @Override
            public void run() {
                start = System.currentTimeMillis();
                elapsed = 0;
                while(!((BT_Application)context).mBluetooth.isConnected() && elapsed < 20){
                    elapsed = (System.currentTimeMillis() - start)/1000;
                    Log.i(TAG, "Time: " + elapsed);
                }
                goToVideoActivity();
            }
        };
        setupThread.start();
    }

    public void alertUser(){
        PopupDialog dialog = PopupDialog.newBluetoothAlertDialog();
        dialog.show(getFragmentManager(), PopupDialog.FRAGMENT_TAG);
    }
    public void goToVideoActivity(){
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
