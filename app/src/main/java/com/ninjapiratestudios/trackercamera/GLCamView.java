package com.ninjapiratestudios.trackercamera;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jjdixie on 2/1/16.
 */
public class GLCamView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {

    GLRenderer renderer;

    GLCamView(Context c){
        super(c);
        renderer = new GLRenderer(this);
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
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

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture st){
        //updateSurfaceTexture = true;
        requestRender();
    }

    public class GLRenderer implements GLSurfaceView.Renderer {

        GLCamView glCamView;
        Camera camera;
        private SurfaceTexture surfaceTexture;

        float[] transformMatrix;
        float[] viewMatrix;
        float[] projectionMatrix;

        private short indices[] = { 0, 1, 2, 2, 1, 3 };
        private ShortBuffer indexBuffer;
        private FloatBuffer vertexBuffer;
        private FloatBuffer textureBuffer;
        private float rectCoords[] = { -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f,  1.0f, 0.0f,
                1.0f,  1.0f, 0.0f};
        private float texCoords[] = { 0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f};

        //handles
        int[] textureHandle = new int[1];
        private int vertexShaderHandle;
        private int fragmentShaderHandle;
        private int shaderProgram;

        boolean updateSurfaceTexture;

        GLRenderer(GLCamView glcv){
            glCamView = glcv;

            //ready index, vertex, and texture buffers
            ByteBuffer ib = ByteBuffer.allocateDirect(indices. length * 2);
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

        public void release(){
            surfaceTexture.release();
            camera.stopPreview();
            GLES20.glDeleteTextures(1, textureHandle, 0);
        }

        @Override
        public void onSurfaceCreated ( GL10 unused, EGLConfig config ) {
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
            GLES20.glGenTextures(1, textureHandle, 0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle[0]);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            surfaceTexture = new SurfaceTexture(textureHandle[0]);
            surfaceTexture.setOnFrameAvailableListener(glCamView);

            camera = Camera.open();
            try {
                camera.setPreviewTexture(surfaceTexture);
            } catch(IOException e){
                throw new RuntimeException("Error setting camera preview to texture.");
            }

            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            GLES20.glDisable(GLES20.GL_BLEND);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            loadShaders();
        }

        @Override
        public void onSurfaceChanged(GL10 unused, int width, int height) {
            GLES20.glViewport(0, 0, width, height);

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

            Camera.Size previewSize = camera.getParameters().getPreviewSize();
            Camera.Parameters param = camera.getParameters();
            param.setPictureSize(previewSize.width, previewSize.height);
            param.set("orientation", "landscape");
            camera.setParameters(param);
            camera.startPreview();
        }

        @Override
        public void onDrawFrame(GL10 unused){
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            float[] textureMatrix = new float[16];
            float[] modelMatrix = { 1.0f, 0.0f, 0.0f, 0.0f,
                                        0.0f, 1.0f, 0.0f, 0.0f,
                                        0.0f, 0.0f, 1.0f, 0.0f,
                                        0.0f, 0.0f, 0.0f, 1.0f};

            Matrix.multiplyMM(transformMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(transformMatrix, 0, projectionMatrix, 0, transformMatrix, 0);
        /*synchronized(this){
            if(updateSurfaceTexture){
                surfaceTexture.updateTexImage();
                updateSurfaceTexture = false;
            }
        }*/

            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(textureMatrix);

            GLES20.glUseProgram(shaderProgram);

            int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "position");
            int textureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "inputTextureCoordinate");
            int textureMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "textureMatrix");
            int transformMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "MVPMatrix");
            int textureHandleUI = GLES20.glGetUniformLocation(shaderProgram, "videoFrame");

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle[0]);
            GLES20.glUniform1i(textureHandleUI, 0);

            GLES20.glUniformMatrix4fv(textureMatrixHandle, 1, false, textureMatrix, 0);
            GLES20.glUniformMatrix4fv(transformMatrixHandle, 1, false, transformMatrix, 0);

            vertexBuffer.position(0);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            GLES20.glEnableVertexAttribArray(positionHandle);

            textureBuffer.position(0);
            GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
            GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

            indexBuffer.position(0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
            //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
            GLES20.glUseProgram(0);
        }

        private void loadShaders(){
            AssetManager assetManager = glCamView.getContext().getAssets();
            String vertexShaderCode = "";
            String fragmentShaderCode = "";

            try {
                InputStream fis = assetManager.open("DirectDisplayShader.fsh");
                InputStreamReader fisr = new InputStreamReader(fis);
                BufferedReader fbr = new BufferedReader(fisr);
                StringBuilder fsb = new StringBuilder();
                String next;
                while((next = fbr.readLine()) != null){
                    fsb.append(next);
                    fsb.append('\n');
                }
                fragmentShaderCode = fsb.toString();

                InputStream vis = assetManager.open("DirectDisplayShader.vsh");
                InputStreamReader visr = new InputStreamReader(vis);
                BufferedReader vbr = new BufferedReader(visr);
                StringBuilder vsb = new StringBuilder();
                while((next = vbr.readLine()) != null){
                    vsb.append(next);
                    vsb.append('\n');
                }
                vertexShaderCode = vsb.toString();
            }
            catch(IOException e){
                Log.d("Shader loading error: ", e.getMessage());
                return;
            }
            int[] status = new int[1];

            vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            GLES20.glShaderSource(vertexShaderHandle, vertexShaderCode);
            GLES20.glCompileShader(vertexShaderHandle);
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, status, 0);
            if(status[0] == GLES20.GL_FALSE){
                Log.d("Shader","Vertex Shader: " + GLES20.glGetShaderInfoLog(vertexShaderHandle));
            }

            fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShaderCode);
            GLES20.glCompileShader(fragmentShaderHandle);
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, status, 0);
            if(status[0] == GLES20.GL_FALSE){
                Log.d("Shader","Fragment Shader: " + GLES20.glGetShaderInfoLog(fragmentShaderHandle));
            }

            shaderProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(shaderProgram, vertexShaderHandle);
            GLES20.glAttachShader(shaderProgram, fragmentShaderHandle);
            GLES20.glLinkProgram(shaderProgram);

            GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS, status, 0);
            if (status[0] != GLES20.GL_TRUE) {
                String error = GLES20.glGetProgramInfoLog(shaderProgram);
                //throw new RuntimeException("Shader program compilation failure: " + error);
            }
        }

    }

}

