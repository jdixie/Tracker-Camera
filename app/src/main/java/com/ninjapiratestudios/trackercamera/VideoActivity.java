package com.ninjapiratestudios.trackercamera;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ninjapiratestudios.trackercamera.ColorSelection.ConfigurationFragment;
import com.ninjapiratestudios.trackercamera.fileSystem.ItemFragment;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class VideoActivity extends FragmentActivity implements
        ItemFragment.OnListFragmentInteractionListener, VideoFragment.OnVideoAddedListener,
        ConfigurationFragment.OnColorsSelectedListener {
    public final static String LOG_TAG = "VIDEO_ACTIVITY";
    private boolean overlayThreadRunning = true;
    private ViewPager mViewPager;
    private boolean intentAppExit; // Prevents release of camera resources
    private ImageButton recordButton;
    private boolean stopImage;
    Thread overlayHelper;
    PowerManager powerManager;
    WakeLock wakeLock;

    public void colorsSelected(ArrayList<Color> color) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init);

        // Setup PagerAdapter for swiping functionality
        PagerAdapter pagerAdapter;
        mViewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);


        ((BTApplication) this.getApplicationContext()).mBluetooth.rotate(360);
        Overlay.setupGraphic(this);
        addContentView(Overlay.getGraphic(),
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
        }

        //every 100 milliseconds update overlay draw
        overlayThreadRunning = true;
        overlayHelper = new OverlayThread();
        overlayHelper.start();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //overlayHelper.interrupt();
        overlayThreadRunning = false;
        overlayHelper = null;
        Log.i(LOG_TAG, "Thread Interrupt called");
        wakeLock.release();
    }

    /**
     * Checks to see if the app has been exited due to an Intent to the Gallery.
     *
     * @return Whether or not the above condition is true.
     */
    public boolean isIntentAppExit() {
        return intentAppExit;
    }

    public void setIntentAppExit(boolean intentAppExit) {
        this.intentAppExit = intentAppExit;
    }

    @Override
    public void onListFragmentInteraction(File f) {
        intentAppExit = true;
        Uri videoUri = Uri.fromFile(f);
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_VIEW);
        galleryIntent.setDataAndType(videoUri, "video/*");
        galleryIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(galleryIntent);
    }

    @Override
    public void onVideoAdded() {
        Log.i("VideoAdded called", "the OnVideoAdded Callback was called");
        ScreenSlidePagerAdapter sSPA = (ScreenSlidePagerAdapter) mViewPager.getAdapter();
        ItemFragment iF = (ItemFragment) sSPA.getItem(1);
        iF.update();
    }

    /**
     * Getter for the Record Button.
     *
     * @return a reference to the Record Button.
     */
    public ImageButton getRecordButton() {
        return recordButton;
    }

    /**
     * Setter for the new Record Button.
     *
     * @param recordButton The new Record Button object.
     */
    public void setRecordButton(ImageButton recordButton) {
        this.recordButton = recordButton;
    }

    /**
     * Toggles setting the stop.png and record.png image types for<br/>
     * the Record button
     */
    public void toggleRecordButton() {
        if (recordButton != null) {
            if (!stopImage) {
                stopImage = true;
                recordButton.setImageResource(R.drawable.stop);
            } else {
                stopImage = false;
                recordButton.setImageResource(R.drawable.record);
            }
        }
    }

    /**
     * A simple pager adapter that represents 2 Fragment objects, in
     * sequence.
     */
    protected class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private ConfigurationFragment cF;
        private VideoFragment vF;
        private ItemFragment iF;

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                cF = new ConfigurationFragment();
                return cF;
            } else if (position == 1) {
                if (vF == null) {
                    vF = new VideoFragment();
                    return vF;
                } else {
                    return vF;
                }
            } else {
                if (iF == null) {
                    iF = new ItemFragment();
                    return iF;
                } else {
                    return iF;
                }
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.configuration).toUpperCase(l);
                case 1:
                    return getString(R.string.record_page).toUpperCase(l);
                case 2:
                    return getString(R.string.sharing).toUpperCase(l);
            }
            return null;
        }
    }


    private class OverlayThread extends Thread {
        @Override
        public void run() {
            while (overlayThreadRunning) {
                //Log.i(LOG_TAG, "Overlay Thread Running!");
                try {

                    synchronized (this) {
                        wait(50);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Overlay.invalidate();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void turnLeft(int amount) {
        ((BTApplication) this.getApplicationContext()).mBluetooth.rotate(amount);
    }

    public void turnRight(int amount) {
        ((BTApplication) this.getApplicationContext()).mBluetooth.rotate(amount);
    }
}