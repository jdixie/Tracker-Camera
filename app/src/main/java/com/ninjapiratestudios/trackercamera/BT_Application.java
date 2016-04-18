package com.ninjapiratestudios.trackercamera;

import android.app.Application;

/**
 * Created by Jalen on 4/17/2016.
 */
public class BT_Application extends Application {
    public Bluetooth_Comms mBluetooth;

    @Override
    public void onCreate(){
        super.onCreate();
        mBluetooth = new Bluetooth_Comms(getApplicationContext());
    }
}
