package com.ninjapiratestudios.trackercamera;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jjdixie on 2/1/16.
 */
public class GLCamView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {

    GLRenderer renderer;
    public static final int REGULAR_VIEW = 0;
    public static final int TRACKING_VIEW = 1;
    int viewType = 0;
    int screenWidth;
    int screenHeight;
    float xRatio, yRatio;
    boolean setColorChoice = false;
    boolean updateSurfaceTexture = false;
    boolean colorSet = false;
    float xChoice, yChoice;
    float[] colorSelected = new float[4];
    float threshold = 0.2f;

    GLCamView(Context c) {
        super(c);
        renderer = new GLRenderer(this);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    //toggle tracking on and off
    public void setViewType(int vt) {
        if (colorSet && vt == TRACKING_VIEW)
            viewType = vt;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        renderer.release();
        super.surfaceDestroyed(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
        screenWidth = w;
        screenHeight = h;
    }

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture st) {
        updateSurfaceTexture = true;
        requestRender();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP && setColorChoice == false) {
            //x, y are in terms of width x height of the screen with 0,0 the top left
            float x = e.getX();
            float y = e.getY();

            System.out.println("Touch at: " + x + ". " + y);
            xChoice = x;
            yChoice = y;
            setColorChoice = true;
        }

        return true;
    }

    public class GLRenderer implements GLSurfaceView.Renderer {

        GLCamView glCamView;
        Camera camera;
        private SurfaceTexture surfaceTexture;
        Camera.Size previewSize;

        float[] transformMatrix;
        float[] viewMatrix;
        float[] projectionMatrix;

        private short indices[] = {0, 1, 2, 2, 1, 3};
        private ShortBuffer indexBuffer;
        private FloatBuffer vertexBuffer;
        private FloatBuffer textureBuffer;
        private float rectCoords[] = {-1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f};
        private float texCoords[] = {0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f};

        //handles
        int[] textureHandle = new int[1];
        private int vertexShaderHandle;
        private int fragmentShaderHandle;
        private int regularViewShaderProgram;
        private int positionVertexShaderHandle;
        private int positionFragmentShaderHandle;
        private int trackingViewShaderProgram;
        private int computeShaderHandle;
        private int luminanceShaderProgram;

        private int[] computeBuffers;
        IntBuffer lumVal;
        //private int[] lumVal = new int[256];

        private int[] trackColor = new int[4];

        GLRenderer(GLCamView glcv) {
            glCamView = glcv;

            //ready index, vertex, and texture buffers
            ByteBuffer ib = ByteBuffer.allocateDirect(indices.length * 2);
            ib.order(ByteOrder.nativeOrder());
            indexBuffer = ib.asShortBuffer();
            indexBuffer.put(indices);
            indexBuffer.position(0);

            ByteBuffer bb = ByteBuffer.allocateDirect(rectCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());

            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(rectCoords);
            vertexBuffer.position(0);

            ByteBuffer texturebb = ByteBuffer.allocateDirect(texCoords.length * 4);
            texturebb.order(ByteOrder.nativeOrder());

            textureBuffer = texturebb.asFloatBuffer();
            textureBuffer.put(texCoords);
            textureBuffer.position(0);
        }

        public void release() {
            surfaceTexture.release();
            camera.stopPreview();
            GLES31.glDeleteTextures(1, textureHandle, 0);
        }

        @Override
        public void onSurfaceCreated(GL10 unused, EGLConfig config) {
            // Position the eye behind the origin.
            final float eyeX = 0.0f;
            final float eyeY = 0.0f;
            final float eyeZ = 1.5f;

            // We are looking toward the distance
            final float lookX = 0.0f;
            final float lookY = 0.0f;
            final float lookZ = -5.0f;

            // Set our up vector. This is where our head would be pointing were we holding the camera.
            final float upX = 0.0f;
            final float upY = 1.0f;
            final float upZ = 0.0f;

            transformMatrix = new float[16];
            viewMatrix = new float[16];
            // Set the view matrix. This matrix can be said to represent the camera position.
            // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
            // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
            Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

            textureHandle = new int[1];
            GLES31.glGenTextures(1, textureHandle, 0);
            GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle[0]);
            GLES31.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES31.GL_TEXTURE_WRAP_S, GLES31.GL_CLAMP_TO_EDGE);
            GLES31.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES31.GL_TEXTURE_WRAP_T, GLES31.GL_CLAMP_TO_EDGE);
            GLES31.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES31.GL_TEXTURE_MIN_FILTER, GLES31.GL_NEAREST);
            GLES31.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES31.GL_TEXTURE_MAG_FILTER, GLES31.GL_NEAREST);
            surfaceTexture = new SurfaceTexture(textureHandle[0]);
            surfaceTexture.setOnFrameAvailableListener(glCamView);

            camera = Camera.open();
            try {
                camera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                throw new RuntimeException("Error setting camera preview to texture.");
            }

            GLES31.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);
            GLES31.glDisable(GLES31.GL_CULL_FACE);
            GLES31.glDisable(GLES31.GL_BLEND);
            GLES31.glDisable(GLES31.GL_DEPTH_TEST);
            loadShaders();
        }

        @Override
        public void onSurfaceChanged(GL10 unused, int width, int height) {
            GLES31.glViewport(0, 0, width, height);

            // Create a new perspective projection matrix. The height will stay the same
            // while the width will vary as per aspect ratio.
            final float ratio = (float) width / height;
            final float left = -ratio;
            final float right = ratio;
            final float bottom = -1.0f;
            final float top = 1.0f;
            final float near = 1.0f;
            final float far = 10.0f;

            projectionMatrix = new float[16];
            Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);

            previewSize = camera.getParameters().getPreviewSize();
            Camera.Parameters param = camera.getParameters();
            param.setPictureSize(previewSize.width, previewSize.height);
            param.set("orientation", "landscape");
            camera.setParameters(param);
            camera.startPreview();
            xRatio = previewSize.width / screenWidth;
            yRatio = previewSize.height / screenHeight;
        }

        @Override
        public void onDrawFrame(GL10 unused){
            GLES31.glClear(GLES31.GL_DEPTH_BUFFER_BIT | GLES31.GL_COLOR_BUFFER_BIT);

            float[] textureMatrix = new float[16];
            float[] modelMatrix = {1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f};

            Matrix.multiplyMM(transformMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(transformMatrix, 0, projectionMatrix, 0, transformMatrix, 0);


            synchronized(this){
                if(updateSurfaceTexture){
                    surfaceTexture.updateTexImage();
                    updateSurfaceTexture = false;
                }
            }
            computeLuminance();

            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(textureMatrix);

            if(viewType == REGULAR_VIEW || setColorChoice){
                GLES31.glUseProgram(regularViewShaderProgram);

                int positionHandle = GLES31.glGetAttribLocation(regularViewShaderProgram, "position");
                int textureCoordinateHandle = GLES31.glGetAttribLocation(regularViewShaderProgram, "inputTextureCoordinate");
                int textureMatrixHandle = GLES31.glGetUniformLocation(regularViewShaderProgram, "textureMatrix");
                int transformMatrixHandle = GLES31.glGetUniformLocation(regularViewShaderProgram, "MVPMatrix");
                int textureHandleUI = GLES31.glGetUniformLocation(regularViewShaderProgram, "videoFrame");

                GLES31.glActiveTexture(GLES31.GL_TEXTURE0);
                GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle[0]);
                GLES31.glUniform1i(textureHandleUI, 0);

                GLES31.glUniformMatrix4fv(textureMatrixHandle, 1, false, textureMatrix, 0);
                GLES31.glUniformMatrix4fv(transformMatrixHandle, 1, false, transformMatrix, 0);

                vertexBuffer.position(0);
                GLES31.glVertexAttribPointer(positionHandle, 3, GLES31.GL_FLOAT, false, 0, vertexBuffer);
                GLES31.glEnableVertexAttribArray(positionHandle);

                textureBuffer.position(0);
                GLES31.glVertexAttribPointer(textureCoordinateHandle, 2, GLES31.GL_FLOAT, false, 0, textureBuffer);
                GLES31.glEnableVertexAttribArray(textureCoordinateHandle);

                indexBuffer.position(0);
                GLES31.glDrawElements(GLES31.GL_TRIANGLES, indices.length, GLES31.GL_UNSIGNED_SHORT, indexBuffer);
                GLES31.glDisableVertexAttribArray(positionHandle);
                GLES31.glDisableVertexAttribArray(textureCoordinateHandle);
                GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
                GLES31.glUseProgram(0);
            }
            else{//tracking view for now
                GLES31.glUseProgram(trackingViewShaderProgram);

                int positionHandle = GLES31.glGetAttribLocation(trackingViewShaderProgram, "position");
                int textureCoordinateHandle = GLES31.glGetAttribLocation(trackingViewShaderProgram, "inputTextureCoordinate");
                int textureMatrixHandle = GLES31.glGetUniformLocation(trackingViewShaderProgram, "textureMatrix");
                int textureHandleUI = GLES31.glGetUniformLocation(trackingViewShaderProgram, "videoFrame");
                int inputColorHandle = GLES31.glGetUniformLocation(trackingViewShaderProgram, "inputColor");
                int thresholdHandle = GLES31.glGetUniformLocation(trackingViewShaderProgram, "threshold");

                GLES31.glActiveTexture(GLES31.GL_TEXTURE0);
                GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle[0]);
                GLES31.glUniform1i(textureHandleUI, 0);

                GLES31.glUniform4f(inputColorHandle, colorSelected[0], colorSelected[1], colorSelected[2], colorSelected[3]);
                GLES31.glUniform1f(thresholdHandle, threshold);

                GLES31.glUniformMatrix4fv(textureMatrixHandle, 1, false, textureMatrix, 0);

                vertexBuffer.position(0);
                GLES31.glVertexAttribPointer(positionHandle, 3, GLES31.GL_FLOAT, false, 0, vertexBuffer);
                GLES31.glEnableVertexAttribArray(positionHandle);

                textureBuffer.position(0);
                GLES31.glVertexAttribPointer(textureCoordinateHandle, 2, GLES31.GL_FLOAT, false, 0, textureBuffer);
                GLES31.glEnableVertexAttribArray(textureCoordinateHandle);

                indexBuffer.position(0);
                GLES31.glDrawElements(GLES31.GL_TRIANGLES, indices.length, GLES31.GL_UNSIGNED_SHORT, indexBuffer);
                GLES31.glDisableVertexAttribArray(positionHandle);
                GLES31.glDisableVertexAttribArray(textureCoordinateHandle);
                GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
                GLES31.glUseProgram(0);
            }

            if(setColorChoice){
                setColor();
            }

        }

        private void loadShaders() {
            AssetManager assetManager = glCamView.getContext().getAssets();
            String vertexShaderCode = "";
            String fragmentShaderCode = "";
            String computeShaderCode = "";

            //load regular shader program
            try {
                InputStream fis = assetManager.open("DirectDisplayShader.fsh");
                InputStreamReader fisr = new InputStreamReader(fis);
                BufferedReader fbr = new BufferedReader(fisr);
                StringBuilder fsb = new StringBuilder();
                String next;
                while ((next = fbr.readLine()) != null) {
                    fsb.append(next);
                    fsb.append('\n');
                }
                fragmentShaderCode = fsb.toString();

                InputStream vis = assetManager.open("DirectDisplayShader.vsh");
                InputStreamReader visr = new InputStreamReader(vis);
                BufferedReader vbr = new BufferedReader(visr);
                StringBuilder vsb = new StringBuilder();
                while ((next = vbr.readLine()) != null) {
                    vsb.append(next);
                    vsb.append('\n');
                }
                vertexShaderCode = vsb.toString();
            } catch (IOException e) {
                Log.d("Shader loading error: ", e.getMessage());
                return;
            }
            int[] status = new int[1];

            vertexShaderHandle = GLES31.glCreateShader(GLES31.GL_VERTEX_SHADER);
            GLES31.glShaderSource(vertexShaderHandle, vertexShaderCode);
            GLES31.glCompileShader(vertexShaderHandle);
            GLES31.glGetShaderiv(vertexShaderHandle, GLES31.GL_COMPILE_STATUS, status, 0);
            if(status[0] == GLES31.GL_FALSE){
                Log.d("Shader","Vertex Shader: " + GLES31.glGetShaderInfoLog(vertexShaderHandle));
            }

            fragmentShaderHandle = GLES31.glCreateShader(GLES31.GL_FRAGMENT_SHADER);
            GLES31.glShaderSource(fragmentShaderHandle, fragmentShaderCode);
            GLES31.glCompileShader(fragmentShaderHandle);
            GLES31.glGetShaderiv(fragmentShaderHandle, GLES31.GL_COMPILE_STATUS, status, 0);
            if(status[0] == GLES31.GL_FALSE){
                Log.d("Shader","Fragment Shader: " + GLES31.glGetShaderInfoLog(fragmentShaderHandle));
            }

            regularViewShaderProgram = GLES31.glCreateProgram();
            GLES31.glAttachShader(regularViewShaderProgram, vertexShaderHandle);
            GLES31.glAttachShader(regularViewShaderProgram, fragmentShaderHandle);
            GLES31.glLinkProgram(regularViewShaderProgram);

            GLES31.glGetProgramiv(regularViewShaderProgram, GLES31.GL_LINK_STATUS, status, 0);
            if (status[0] != GLES31.GL_TRUE) {
                String error = GLES31.glGetProgramInfoLog(regularViewShaderProgram);
                //throw new RuntimeException("Shader program compilation failure: " + error);
            }

            //load tracking shader program
            try {
                InputStream fis = assetManager.open("ThresholdShader2.fsh");
                InputStreamReader fisr = new InputStreamReader(fis);
                BufferedReader fbr = new BufferedReader(fisr);
                StringBuilder fsb = new StringBuilder();
                String next;
                while ((next = fbr.readLine()) != null) {
                    fsb.append(next);
                    fsb.append('\n');
                }
                fragmentShaderCode = fsb.toString();

                InputStream vis = assetManager.open("ThresholdShader.vsh");
                InputStreamReader visr = new InputStreamReader(vis);
                BufferedReader vbr = new BufferedReader(visr);
                StringBuilder vsb = new StringBuilder();
                while ((next = vbr.readLine()) != null) {
                    vsb.append(next);
                    vsb.append('\n');
                }
                vertexShaderCode = vsb.toString();
            } catch (IOException e) {
                Log.d("Shader loading error: ", e.getMessage());
                return;
            }
            positionVertexShaderHandle = GLES31.glCreateShader(GLES31.GL_VERTEX_SHADER);
            GLES31.glShaderSource(positionVertexShaderHandle, vertexShaderCode);
            GLES31.glCompileShader(positionVertexShaderHandle);
            GLES31.glGetShaderiv(positionVertexShaderHandle, GLES31.GL_COMPILE_STATUS, status, 0);
            if(status[0] == GLES31.GL_FALSE){
                Log.d("Shader","Vertex Shader: " + GLES31.glGetShaderInfoLog(positionVertexShaderHandle));
            }

            positionFragmentShaderHandle = GLES31.glCreateShader(GLES31.GL_FRAGMENT_SHADER);
            GLES31.glShaderSource(positionFragmentShaderHandle, fragmentShaderCode);
            GLES31.glCompileShader(positionFragmentShaderHandle);
            GLES31.glGetShaderiv(positionFragmentShaderHandle, GLES31.GL_COMPILE_STATUS, status, 0);
            if(status[0] == GLES31.GL_FALSE){
                Log.d("Shader","Fragment Shader: " + GLES31.glGetShaderInfoLog(positionFragmentShaderHandle));
            }

            trackingViewShaderProgram = GLES31.glCreateProgram();
            GLES31.glAttachShader(trackingViewShaderProgram, positionVertexShaderHandle);
            GLES31.glAttachShader(trackingViewShaderProgram, positionFragmentShaderHandle);
            GLES31.glLinkProgram(trackingViewShaderProgram);

            GLES31.glGetProgramiv(trackingViewShaderProgram, GLES31.GL_LINK_STATUS, status, 0);
            if (status[0] != GLES31.GL_TRUE) {
                String error = GLES31.glGetProgramInfoLog(trackingViewShaderProgram);
                //throw new RuntimeException("Shader program compilation failure: " + error);
            }

            //load compute shader program
            try {
                InputStream cis = assetManager.open("LuminanceShader.csh");
                InputStreamReader cisr = new InputStreamReader(cis);
                BufferedReader cbr = new BufferedReader(cisr);
                StringBuilder csb = new StringBuilder();
                String next;
                while((next = cbr.readLine()) != null){
                    csb.append(next);
                    csb.append('\n');
                }
                computeShaderCode = csb.toString();
            }
            catch(IOException e){
                Log.d("Shader loading error: ", e.getMessage());
                return;
            }
            status = new int[1];

            computeShaderHandle = GLES31.glCreateShader(GLES31.GL_COMPUTE_SHADER);
            GLES31.glShaderSource(computeShaderHandle, computeShaderCode);
            GLES31.glCompileShader(computeShaderHandle);
            GLES31.glGetShaderiv(computeShaderHandle, GLES31.GL_COMPILE_STATUS, status, 0);
            if(status[0] == GLES31.GL_FALSE){
                Log.d("Shader","Compute Shader: " + GLES31.glGetShaderInfoLog(computeShaderHandle));
            }

            luminanceShaderProgram = GLES31.glCreateProgram();
            GLES31.glAttachShader(luminanceShaderProgram, computeShaderHandle);
            GLES31.glLinkProgram(luminanceShaderProgram);

            GLES31.glGetProgramiv(luminanceShaderProgram, GLES31.GL_LINK_STATUS, status, 0);
            if (status[0] != GLES31.GL_TRUE) {
                String error = GLES31.glGetProgramInfoLog(regularViewShaderProgram);
                Log.d("Shader","Compute Program: " + error);
            }

            computeBuffers = new int[1];
            ByteBuffer buffer = ByteBuffer.allocateDirect(256*4);
            buffer.order(ByteOrder.nativeOrder());
            lumVal = buffer.asIntBuffer();
            GLES31.glGenBuffers(1, computeBuffers, 0);
            GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER, computeBuffers[0]);
            GLES31.glBufferData(GLES31.GL_SHADER_STORAGE_BUFFER, 256 * 4, lumVal, GLES31.GL_DYNAMIC_COPY);
            GLES31.glBindBuffer(GLES31.GL_SHADER_STORAGE_BUFFER, 0);
        }

        private void setColor() {
            float colPointX = xChoice * xRatio;
            float colPointY = yChoice * yRatio;
            int pixel = (int) (xChoice * xRatio * yChoice * yRatio);
            System.out.println("Cam texture: " + previewSize.width + ", " + previewSize.height);

            ByteBuffer buff = ByteBuffer.allocateDirect(4);
            buff.order(ByteOrder.nativeOrder());
            GLES31.glReadPixels((int) xChoice, (int) yChoice, 1, 1, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                    buff);
            colorSelected[0] = (buff.get(0) & 0xff) / 255.0f;
            colorSelected[1] = (buff.get(1) & 0xff) / 255.0f;
            colorSelected[2] = (buff.get(2) & 0xff) / 255.0f;
            colorSelected[3] = (buff.get(3) & 0xff) / 255.0f;
            setColorChoice = false;
            colorSet = true;
            setViewType(TRACKING_VIEW);
            System.out.println("Color: " + (colorSelected[0] * 255.0) + ", " + (colorSelected[1] * 255.0) + ", " +
                    (colorSelected[2] * 255.0) + ", " + (colorSelected[3] * 255.0));
            System.out.println("Color: " + colorSelected[0] + ", " + colorSelected[1] + ", " +
                    colorSelected[2] + ", " + colorSelected[3]);
        }

        private void computeLuminance(){
            GLES31.glUseProgram(luminanceShaderProgram);
            GLES31.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, 0, computeBuffers[0]);
            GLES31.glActiveTexture(GLES31.GL_TEXTURE0);
            GLES31.glBindImageTexture(0, textureHandle[0], 0, false, 0, GLES31.GL_READ_ONLY, GLES31.GL_R32F);


            GLES31.glDispatchCompute(previewSize.width, previewSize.height, 1);
            //wait for compute to finish
            GLES31.glMemoryBarrier(GLES31.GL_SHADER_STORAGE_BARRIER_BIT);

            GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
            GLES31.glUnmapBuffer(GLES31.GL_SHADER_STORAGE_BUFFER);

            //int[] array = lumVal.array();
            for(int i = 0; i < 256; i++)
                Log.d("Luminance array " + i + ": ", "" + lumVal.get(i));
        }

    }

}

