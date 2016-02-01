package com.ninjapiratestudios.trackercamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by jjdixie on 1/30/16.
 */
public class CamPreview extends SurfaceView implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {

    SurfaceHolder holder;
    Camera camera;
    TextureSurface glTextureSurface;
    Context context;
    SurfaceTexture previewTexture;

    CamPreview(Context c, Camera cam) {
        super(c);
        camera = cam;
        context = c;

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

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            previewTexture = new SurfaceTexture(0);
            //camera.setPreviewDisplay(holder);
            glTextureSurface = new TextureSurface(context, previewTexture, this.getHeight(), this.getWidth());
            camera.setPreviewTexture(previewTexture);
            camera.startPreview();

        } catch (IOException e) {
            //camera preview error
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
