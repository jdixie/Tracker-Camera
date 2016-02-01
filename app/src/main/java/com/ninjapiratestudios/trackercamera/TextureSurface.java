package com.ninjapiratestudios.trackercamera;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by jjdixie on 1/31/16.
 */
public class TextureSurface implements Runnable, SurfaceTexture.OnFrameAvailableListener{
    private static final int EGL_OPENGL_ES2_BIT = 4;
    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    protected final SurfaceTexture texture;
    private SurfaceTexture videoTexture;
    private EGL10 egl;
    private EGLDisplay eglDisplay;
    private EGLContext eglContext;
    private EGLSurface eglSurface;
    float[] transformMatrix;

    private static short indices[] = { 0, 1, 2, 2, 1, 3 };
    private ShortBuffer indexBuffer;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private float rectCoords[] = { -1.0f, -1.0f, 0.0f,
                                    1.0f, -1.0f, 0.0f,
                                   -1.0f,  1.0f, 0.0f,
                                    1.0f,  1.0f, 0.0f};
    private float texCoords[] = { 0.0f, 1.0f, 0.0f, 1.0f,
                                  0.0f, 0.0f, 0.0f, 1.0f,
                                  1.0f, 0.0f, 0.0f, 1.0f,
                                  1.0f, 1.0f, 0.0f, 1.0f };

    //handles
    int[] textureHandle = new int[1];
    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private int shaderProgram;

    private int height;
    private int width;
    private int videoHeight;
    private int videoWidth;
    private boolean running;
    private boolean frameReady;
    private boolean adjustViewport;

    Context context;

    public TextureSurface(Context c, SurfaceTexture t, int w, int h)
    {
        context = c;
        texture = t;
        width = w;
        height = h;

        //transform matrix will be needed for the video display
        transformMatrix = new float[16];

        //start the thread
        running = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run()
    {
        initializeOpenGL();

        while (running)
        {
            long loopStart = System.currentTimeMillis();

            draw();
            egl.eglSwapBuffers(eglDisplay, eglSurface);

            long waitDelta = 16 - (System.currentTimeMillis() - loopStart);
            if (waitDelta > 0)
            {
                try
                {
                    Thread.sleep(waitDelta);
                }
                catch (InterruptedException e) {
                    continue;
                }
            }
        }

        releaseOpenGL();
    }

    //call to start and onResume if I remember correctly
    public void initializeOpenGL(){
        egl = (EGL10) EGLContext.getEGL();
        eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        int[] version = new int[2];
        egl.eglInitialize(eglDisplay, version);

        EGLConfig eglConfig = chooseEglConfig();
        eglContext = createContext(egl, eglDisplay, eglConfig);

        eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, texture, null);

        if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE)
        {
            throw new RuntimeException("OpenGL initialization error: " + GLUtils.getEGLErrorString(egl.eglGetError()));
        }

        if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext))
        {
            throw new RuntimeException("OpenGL initialization error: " + GLUtils.getEGLErrorString(egl.eglGetError()));
        }

        loadShaders();

        //ready index, vertex, and texture buffers
        ByteBuffer ib = ByteBuffer.allocateDirect(indices. length * 2);
        ib.order(ByteOrder.nativeOrder());
        indexBuffer = ib.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        ByteBuffer bb = ByteBuffer.allocateDirect(rectCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(texCoords);
        vertexBuffer.position(0);

        ByteBuffer texturebb = ByteBuffer.allocateDirect(texCoords.length * 4);
        texturebb.order(ByteOrder.nativeOrder());

        textureBuffer = texturebb.asFloatBuffer();
        textureBuffer.put(texCoords);
        textureBuffer.position(0);

        // Generate the actual texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(1, textureHandle, 0);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle[0]);

        videoTexture = new SurfaceTexture(textureHandle[0]);
        videoTexture.setOnFrameAvailableListener(this);
    }

    public void releaseOpenGL(){
        egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(eglDisplay, eglSurface);
        egl.eglDestroyContext(eglDisplay, eglContext);
        egl.eglTerminate(eglDisplay);
    }

    private void loadShaders(){
        AssetManager assetManager = context.getAssets();
        String vertexShaderCode = "";
        String fragmentShaderCode = "";

        try {
            InputStream is = assetManager.open("ThresholdShader.fsh");
            fragmentShaderCode = is.toString();
            is = assetManager.open("ThresholdShader.vsh");
            vertexShaderCode = is.toString();
        }
        catch(IOException e){
            Log.d("Shader loading error: ", e.getMessage());
            return;
        }


        vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShaderHandle, vertexShaderCode);
        GLES20.glCompileShader(vertexShaderHandle);

        fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShaderHandle, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShaderHandle);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShaderHandle);
        GLES20.glAttachShader(shaderProgram, fragmentShaderHandle);
        GLES20.glLinkProgram(shaderProgram);

        int[] status = new int[1];
        GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            String error = GLES20.glGetProgramInfoLog(shaderProgram);
        }
    }

    public void onPause()
    {
        running = false;
        releaseOpenGL();
    }

    public void onResume(){
        initializeOpenGL();
        running = true;
    }

    private EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig)
    {
        int[] attribList = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
        return egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attribList);
    }

    private EGLConfig chooseEglConfig()
    {
        int[] configsCount = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        int[] configSpec = getConfig();

        if (!egl.eglChooseConfig(eglDisplay, configSpec, configs, 1, configsCount))
        {
            throw new IllegalArgumentException("Failed to choose config: " + GLUtils.getEGLErrorString(egl.eglGetError()));
        }
        else if (configsCount[0] > 0)
        {
            return configs[0];
        }

        return null;
    }

    private int[] getConfig()
    {
        return new int[] {
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE
        };
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        running = false;
    }

    public void draw(){
        synchronized (this)
        {
            if (frameReady)
            {
                videoTexture.updateTexImage();
                videoTexture.getTransformMatrix(transformMatrix);
                frameReady = false;
            }
            else
            {
                return;
            }

        }

        if (adjustViewport)
            adjustViewport();

        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Draw texture
        GLES20.glUseProgram(shaderProgram);
        int textureParamHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "vTexCoordinate");
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        int textureTranformHandle = GLES20.glGetUniformLocation(shaderProgram, "textureTransform");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 4 * 3, vertexBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE0, textureHandle[0]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(textureParamHandle, 0);

        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, 4, GLES20.GL_FLOAT, false, 0, textureBuffer);

        GLES20.glUniformMatrix4fv(textureTranformHandle, 1, false, transformMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);

    }

    //Let us know we have a frame to analyze and draw
    @Override
    public void onFrameAvailable(SurfaceTexture st)
    {
        synchronized (this)
        {
            frameReady = true;
        }
    }

    public SurfaceTexture getVideoTexture()
    {
        return videoTexture;
    }

    private void adjustViewport()
    {
        float surfaceAspect = height / (float)width;
        float videoAspect = videoHeight / (float)videoWidth;

        if (surfaceAspect > videoAspect)
        {
            float heightRatio = height / (float)videoHeight;
            int newWidth = (int)(width * heightRatio);
            int xOffset = (newWidth - width) / 2;
            GLES20.glViewport(-xOffset, 0, newWidth, height);
        }
        else
        {
            float widthRatio = width / (float)videoWidth;
            int newHeight = (int)(height * widthRatio);
            int yOffset = (newHeight - height) / 2;
            GLES20.glViewport(0, -yOffset, width, newHeight);
        }

        adjustViewport = false;
    }

    public void setVideoSize(int w, int h)
    {
        videoWidth = w;
        videoHeight = h;
        adjustViewport = true;
    }
}
