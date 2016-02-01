package com.ninjapiratestudios.trackercamera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by jjdixie on 1/30/16.
 */
public class CamPreview extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder holder;
    Camera camera;

    CamPreview(Context context, Camera c) {
        super(context);
        camera = c;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //deal with orientation changes. Lock to landscape?
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //release in activity
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            //camera preview error
        }
    }
}
