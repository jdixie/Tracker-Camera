package com.ninjapiratestudios.trackercamera;

import android.app.Application;

import org.opencv.core.Point;

/**
 * Created by Jalen on 4/17/2016.
 */
public class BTApplication extends Application {
    private static final int MAX_COLORS = 2;
    public BluetoothComms mBluetooth;
    public static byte[] imageData;
    public static Point[] points;
    public static int colorsSelected = 0;

    @Override
    public void onCreate(){
        super.onCreate();
        mBluetooth = new BluetoothComms(getApplicationContext());
        points = new Point[MAX_COLORS];
        for(int i = 0; i < MAX_COLORS; i++)
            points[i] = new Point();
    }

    public void setColorSelection(Point p){
        if(colorsSelected < MAX_COLORS) {
            points[colorsSelected] = p;
            colorsSelected++;
        }
    }

    public void setImageBytes(byte[] bytes){
        imageData = bytes;
    }
}
