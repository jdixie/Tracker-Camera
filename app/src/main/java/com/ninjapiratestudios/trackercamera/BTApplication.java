package com.ninjapiratestudios.trackercamera;

import android.app.Application;

/**
 * Created by Jalen on 4/17/2016.
 */
public class BTApplication extends Application {
    public BluetoothComms mBluetooth;

    @Override
    public void onCreate(){
        super.onCreate();
        mBluetooth = new BluetoothComms(getApplicationContext());
    }
}
