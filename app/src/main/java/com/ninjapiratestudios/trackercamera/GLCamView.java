package com.ninjapiratestudios.trackercamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jjdixie on 2/1/16.
 */
public class GLCamView extends GLSurfaceView {

    GLRenderer renderer;

    GLCamView(Context c){
        super(c);
        renderer = new GLRenderer(this);
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        renderer.release();
        super.surfaceDestroyed(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        super.surfaceChanged(holder, format, w, h);
    }
}
