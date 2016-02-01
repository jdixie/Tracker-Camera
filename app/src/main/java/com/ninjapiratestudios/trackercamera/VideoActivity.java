package com.ninjapiratestudios.trackercamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class VideoActivity extends AppCompatActivity{

    Camera camera;
    CamPreview camPreview;
    MediaRecorder mediaRecorder;
    Overlay overlay;
    boolean recordingActive;

    //buttons
    Button recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);

        captureCamera();
        recordingActive = false;
        camPreview = new CamPreview(this, camera);
        try {
            camera.setPreviewDisplay(camPreview.getHolder());
        }
        catch(IOException e)
        {

        }
        overlay = new Overlay(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(camPreview);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


        //add the overlay to the content view
        addContentView(overlay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        //buttons
        recordButton = (Button)findViewById(R.id.button_record);
        recordButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view){
                        recordToggle();
                    }
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private void captureCamera() {
        boolean camp = checkCameraHardware(this);
        camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {

        }
    }

    private void recordToggle(){
        //not working - fix later
        /*if(!recordingActive) {
            //the following line can be adjusted once we have implemented video quality options in a stored config
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            //use google's method of filenaming
            mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
            //mediaRecorder.setPreviewDisplay(camPreview.getHolder().getSurface());
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                recordingActive = true;
            } catch (IOException e) {
                Log.d("mediaRecorder start", "Failed to start recorder");
            }
        }
        else{
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            recordingActive = false;
        }*/
    }


    //from google:
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            //for now, show error eventually
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}