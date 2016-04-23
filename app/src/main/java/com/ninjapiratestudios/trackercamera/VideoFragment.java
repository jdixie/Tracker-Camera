package com.ninjapiratestudios.trackercamera;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * An example full-screen activity that shows and hides the system<br/>
 * UI (i.e.status bar and navigation/system bar) with user interaction.
 */
public class VideoFragment extends Fragment {
    public final static String LOG_TAG = "VIDEO_FRAGMENT";
    private CameraRecorder cameraRecorder;
    private VideoActivity activity;
    private OnVideoAddedListener vAListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(LOG_TAG, "onAttach()");
        vAListener = (OnVideoAddedListener) context;
        activity = (VideoActivity)getActivity();
        // Prepare camera and OpenGL
        initializeCameraRecorder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout preview = new FrameLayout(getActivity());
        LinearLayout buttonLL = new LinearLayout(getActivity());

        // Add CameraPreview and Record Button to FrameLayout
        cameraRecorder.cameraPreviewSetup(preview, activity);
        activity.setRecordButton(new ImageButton(getActivity()));
        ImageButton recordButton = activity.getRecordButton();
        recordButton.setImageResource(R.drawable.record);
        recordButton.setBackgroundColor(Color.TRANSPARENT);
        buttonLL.addView(recordButton);
        buttonLL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        preview.addView(buttonLL);

        recordButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        recordToggle();
                    }
                }
        );

        Log.i(LOG_TAG, "VideoFragment layout constructed.");
        return preview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(activity.isIntentAppExit()) {
            // Allow resources to be released after Gallery app exits
            activity.setIntentAppExit(false);
            Log.i(LOG_TAG, "Reset intentAppExit.");
        }
    }

    /**
     * Releases the Camera resource for other apps.
     */
    @Override
    public void onPause() {
        super.onPause();
        if(!activity.isIntentAppExit()) {
            cameraRecorder.releaseMediaResource();
            cameraRecorder.releaseCameraResource();
            activity.finish();
        }
        Log.i(LOG_TAG, "onPause()");
    }

    /**
     * Initializes the CameraRecorder class that is necessary for video
     * recording and OpenGL functionality.
     */
    private void initializeCameraRecorder() {
            cameraRecorder = CameraRecorder.newInstance((VideoActivity)
                    getActivity());
    }

    private void recordToggle() {
        if (!cameraRecorder.isCameraRecording()) {
            // Toggle button image and display FileNameDialog
            cameraRecorder.displayFileNameDialog();
        } else {
            // Toggle button image and Stop recording
            cameraRecorder.setCameraRecording(false);
            activity.toggleRecordButton();
            cameraRecorder.stopRecording();

            // Refresh file video list
            vAListener.onVideoAdded();
        }
    }

    /**
     * A listener that will be used to communicate with the activity when<br/>
     * a new video is recorded.
     */
    public interface OnVideoAddedListener
    {
        void onVideoAdded();
    }
}