package com.ninjapiratestudios.trackercamera;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.hardware.Camera;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;

public class Analyzer{
    private final int MAX_COLORS = 2;
    private boolean isColorSelected = false;
    //Mat used for frame analysis
    private Mat rgba;

    public static boolean wait = false;

    //selected color variables
    private Scalar[] blobColorRgba;
    private Scalar[] blobColorHsv;
    private int numColors;

    //detector is the go between to OpenCV to find blobs/contours
    private ColorBlobDetector[] detector = new ColorBlobDetector[MAX_COLORS];

    //used to show color range of selected color, and color of contour outline
    //TODO: remove in next iteration
    private Mat spectrum;
    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;

    //used to convert android camera preview frame to OpenCV usable image
    CameraAccessFrame cameraAccessFrame;

    //Used for dimensions calculations
    Camera.Parameters params;

    //storage for preview dimensions
    private int frameWidth, frameHeight;
    Point center;

    //booleans for synchronization since I can't sync something happening on the GPU
    private boolean readyForFrame = false;
    private boolean frameAnalyzed = true;
    private boolean stopRecording = false;

    List<MatOfPoint>[] contours = new List[8];
    ArrayList<Point>[] centroids = new ArrayList[8];

    Point lastTrackedCentroid[] = new Point[8];
    int[] locationAsPercent = new int[8];

    // provides a reference to the main activity
    VideoActivity activity;

    AnalyzerThread analyzerThread;

    private final int EPSILON = 10;

    public Analyzer(int w, int h, Camera.Parameters params, VideoActivity activity) {
        frameWidth = w;
        frameHeight = h;
        center = new Point(frameWidth/2.0, frameHeight/2.0);
        rgba = new Mat(frameHeight, frameWidth, CvType.CV_8UC4);
        spectrum = new Mat();
        numColors = 0;
        blobColorHsv = new Scalar[MAX_COLORS];
        blobColorRgba = new Scalar[MAX_COLORS];
        for(int i = 0; i < MAX_COLORS; i++) {
            detector[i] = new ColorBlobDetector();
            blobColorRgba[i] = new Scalar(255);
            blobColorHsv[i] = new Scalar(255);
            centroids[i] = new ArrayList<Point>();
        }
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
        Mat frame = new Mat(frameHeight + (frameHeight / 2), frameWidth, CvType.CV_8UC1);
        cameraAccessFrame = new CameraAccessFrame(frame, frameWidth, frameHeight);
        this.params = params;
        this.activity = activity;

    }

    public void onCameraViewStopped() {
        rgba.release();
    }

    //Load frame as Mat and get contours/blobs
    public void onCameraFrame(byte[] frame) {
        if(readyForFrame) {
            cameraAccessFrame.put(frame);
            rgba = cameraAccessFrame.rgba();
            readyForFrame = false;
        }
    }

    //sync frame preview before run loop begins
    public void onStartRecord() {
        stopRecording = false;
        if(!readyForFrame && frameAnalyzed)
            readyForFrame = true;
        analyzerThread = new AnalyzerThread();
        analyzerThread.start();
        Overlay.toggleDraw();
    }

    //sync thread to end after current analysis if there is one
    public void onStopRecord(){
        stopRecording = true;
        Overlay.toggleDraw();
    }

    private void invalidateOverlay() {
        Overlay.invalidate();
    }

    //analyze frame for contours based on the color(s)
    public void analyze(){
        Date now = new Date();
        frameAnalyzed = false;

        if (isColorSelected) {
            for(int k = 0; k < numColors; k++) {
                //process frame
                detector[k].process(rgba);
                contours[k] = detector[k].getContours();
                Log.i("Contours count", " " + contours[k].size());

                //find centroids for each found contour
                Moments moments;
                Point p;
                centroids[k].clear();
                Overlay.toggleReady();
                Overlay.clearBlobs();

                for (int i = 0; i < contours[k].size(); i++) {
                    moments = Imgproc.moments(contours[k].get(i));
                    p = new Point();

                    p.x = moments.get_m10() / moments.get_m00();
                    p.y = moments.get_m01() / moments.get_m00();
                    Log.i("Centroid", p.x + ", " + p.y);
                    centroids[k].add(p);
                    Overlay.addBlob(p);
                }
                Overlay.toggleReady();

                if (lastTrackedCentroid[k] == null) {
                    Double shortestFromCenter = Double.POSITIVE_INFINITY;
                    for (Point t : centroids[k]) {
                        if (distanceFromCenter(t) < shortestFromCenter) {
                            lastTrackedCentroid[k] = t;
                        }
                    }
                } else {
                    Double shortestFromLastCentroid = Double.POSITIVE_INFINITY;
                    for (Point t : centroids[k]) {
                        if (distanceFromLastTrackedCentroid(k, t) < shortestFromLastCentroid) {
                            lastTrackedCentroid[k] = t;
                        }
                    }
                }


                locationAsPercent[k] = (int) ((lastTrackedCentroid[k].x / (double) frameWidth) * 100d);
                Log.i("Loc", "" + locationAsPercent[k]);
            }

            //find multi-color groupings within 10% of a screen slice
            /*int groupSize = 1;
            ArrayList<Integer> grouping = new ArrayList<Integer>();
            for(int i = 0; i < numColors - 1; i++){
                int tempSize = 1;
                ArrayList<Integer> tempGroup = new ArrayList<Integer>();
                tempGroup.add(i);
                for(int j = i+1; j < numColors; j++){
                    if(Math.abs(locationAsPercent[i] - locationAsPercent[j]) <= 10){ // <--- screen size as %
                        tempSize++;
                        tempGroup.add(j);
                    }
                }
                if(tempSize > groupSize){
                    groupSize = tempSize;
                    grouping = tempGroup;
                }
                else if(tempSize == groupSize && i == 0){
                    grouping = tempGroup;
                }
            }
            if(grouping.size() == 0){
                grouping.add(0);
            }
            for(int i=0; i < grouping.size(); i++){
                Log.i("Group", "includes " + i + " at " + locationAsPercent[i]);
            }
            //find "average x" value
            int avgX = 0;
            for(int i = 0; i < grouping.size(); i++){
                avgX += locationAsPercent[grouping.get(i)];
            }
            avgX = (int)((double)avgX / (double)grouping.size());
            Log.i("AvgX", "" + avgX);*/

            //find multi-color groupings within 10% of a screen slice
            int groupSize = 1;
            //ArrayList<Integer> grouping = new ArrayList<Integer>();
            Group grouping = new Group();
            int minX=100, maxX=0;
            boolean grouped = false;

            for(int color1 = 0; color1 < numColors - 1; color1++){
                int tempSize = 1;
                Group tempGroup = new Group();
                for(int centroid1 = 0; centroid1 < centroids[color1].size(); centroid1++) {
                    int lapj = (int) ((centroids[color1].get(centroid1).x / (double) frameWidth) * 100d);
                    for (int color2 = color1 + 1; color2 < numColors; color2++) {
                        for(int centroid2 = 0; centroid2 < centroids[color2].size(); centroid2++) {
                            int lapm = (int) ((centroids[color2].get(centroid2).x / (double) frameWidth) * 100d);
                            //for the simplistic two color we're implementing, do this, otherwise if I ever grow this to the 8,
                            //do a modified Dijsktra's to create a path no more than 10% from centroid to centroid, and toss it as
                            //a group as soon as it grows too big
                            if (Math.abs(lapm - lapj) <= 10) { // <--- screen size as %
                                tempSize++;
                                //neither color is added yet
                                if(!tempGroup.colorIDs.contains(color1) && !tempGroup.colorIDs.contains(color2)) {
                                    tempGroup.colorIDs.add(color1);
                                    tempGroup.centroidIDs.add(centroid1);
                                    tempGroup.colorIDs.add(color2);
                                    tempGroup.centroidIDs.add(centroid2);
                                }
                                //color1 exists, 2 doesn't - for bigger version
                                /*else if(!tempGroup.colorIDs.contains(color2)){
                                }
                                //color2 exists, 1 doesn't - for bigger version
                                else if (!tempGroup.colorIDs.contains(color1)){
                                }*/
                                //both exist
                                else{
                                    int c1idx = tempGroup.colorIDs.indexOf(color1);
                                    int c2idx = tempGroup.colorIDs.indexOf(color2);
                                    double curDist = distanceSquared(centroids[color1].get(tempGroup.centroidIDs.get(c1idx)),
                                            centroids[color2].get(tempGroup.centroidIDs.get(c2idx)));
                                    double newDist = distanceSquared(centroids[color1].get(centroid1),
                                            centroids[color2].get(centroid2));
                                    if(newDist < curDist){
                                        tempGroup.colorIDs.clear();
                                        tempGroup.centroidIDs.clear();
                                        tempGroup.colorIDs.add(color1);
                                        tempGroup.centroidIDs.add(centroid1);
                                        tempGroup.colorIDs.add(color2);
                                        tempGroup.centroidIDs.add(centroid2);
                                    }
                                }
                            }
                        }
                    }
                }
                if (tempSize > groupSize) {
                    groupSize = tempSize;
                    grouping = tempGroup;
                } else if (tempSize == groupSize && color1 == 0) {
                    grouping = tempGroup;
                }
            }
            if(grouping.centroidIDs.size() > 1){
                grouped = true;
            }
            /*for(int i=0; i < grouping.size(); i++){
                Log.i("Group", "includes " + i + " at " + locationAsPercent[i]);
            }*/

            //find "average x" value
            int avgX = locationAsPercent[0];
            if(grouped) {
                for (int i = 0; i < grouping.centroidIDs.size(); i++) {
                    avgX += centroids[grouping.colorIDs.get(i)].get(grouping.centroidIDs.get(i)).x;
                }
                avgX = (int) ((double) avgX / (double) grouping.colorIDs.size());
            }
            Log.i("AvgX", "" + avgX);

            if (!wait) {
                if (Math.abs(avgX - 50) > EPSILON) {
                    int zoom = params.getZoomRatios().get(params.getZoom()).intValue();
                    Camera.Size sz = params.getPreviewSize();
                    double aspect = (double) sz.width / (double) sz.height;
                    double thetaV = Math.toRadians(params.getVerticalViewAngle());
                    double thetaH = 2d * Math.atan(aspect * Math.tan(thetaV / 2.0));
                    //double thetaH = params.getHorizontalViewAngle();
                    thetaH = (2d * Math.atan(100d * Math.tan(thetaH / 2d) / zoom)) / (Math.PI) * 180d;
                    Date sendingAt = new Date();
                    Log.i("Analyze time", (sendingAt.getTime() - now.getTime() + "ms; ThetaH: " + thetaH));
                    if (avgX > 60) {
                        //activity.turnRight((int) -(Math.abs((avgX / 100d * thetaH) - (.5 * thetaH)) / 2d));
                        Log.i("Spin", "" + (int) -(Math.abs((avgX / 100d * thetaH) - (.5 * thetaH))));
                    } else {
                        //activity.turnLeft((int) (Math.abs((avgX / 100d * thetaH) - (.5 * thetaH)) / 2d));
                        Log.i("Spin", "" + (int) (Math.abs((avgX / 100d * thetaH) - (.5 * thetaH))));
                    }
                    //wait = true;
                /*try {
                    Thread.sleep(1000);
                }
                catch(InterruptedException e){
                }*/
                }
            }
        }




        //get ready for a new frame
        readyForFrame = true;
        frameAnalyzed = true;
    }

    private double distanceFromCenter(Point p){
        return Math.sqrt(Math.abs(center.x * center.x - p.x * p.x) + Math.abs(center.y * center.y - p.y * p.y));
    }

    private double distanceFromLastTrackedCentroid(int centroid, Point p){
        return Math.sqrt(Math.abs(lastTrackedCentroid[centroid].x * lastTrackedCentroid[centroid].x - p.x * p.x) +
                Math.abs(lastTrackedCentroid[centroid].y * lastTrackedCentroid[centroid].y - p.y * p.y));
    }

    private double distanceSquared(Point a, Point b){
        double xDiff = a.x - b.x;
        double yDiff = a.y - b.y;
        return xDiff * xDiff + yDiff * yDiff;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    public void liveCalibrate(){
        //choose point in center of preview
        int x = frameWidth / 2;
        int y = frameHeight / 2;

        //create an 8x8 pixel rectangle to average color at center
        Rect rect = new Rect();

        rect.x = x - 4;
        rect.y = y - 4;

        rect.width = 8;
        rect.height = 8;

        Mat regionRgba = rgba.submat(rect);

        Mat regionHsv = new Mat();
        Imgproc.cvtColor(regionRgba, regionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        //calculate average color of selection
        blobColorHsv[0] = Core.sumElems(regionHsv);
        int pointCount = rect.width*rect.height;
        for (int i = 0; i < blobColorHsv[0].val.length; i++)
            blobColorHsv[0].val[i] /= pointCount;

        blobColorRgba[0] = converScalarHsv2Rgba(blobColorHsv[0]);

        Log.i("Color", "Selected rgba color: (" + blobColorRgba[0].val[0] + ", " + blobColorRgba[0].val[1] +
                ", " + blobColorRgba[0].val[2] + ", " + blobColorRgba[0].val[3] + ")");

        detector[0].setHsvColor(blobColorHsv[0]);

        Imgproc.resize(detector[0].getSpectrum(), spectrum, SPECTRUM_SIZE);

        regionRgba.release();
        regionHsv.release();

        numColors = 1;
        isColorSelected = true;
    }



    //got this class and interface from someone else in order to do the android camera to OpenCV image conversion
    private class CameraAccessFrame implements CameraFrame {
        private Mat mYuvFrameData;
        private Mat mRgba;
        private int mWidth;
        private int mHeight;
        private Bitmap mCachedBitmap;
        private boolean mRgbaConverted;
        private boolean mBitmapConverted;

        @Override
        public Mat gray() {
            return mYuvFrameData.submat(0, mHeight, 0, mWidth);
        }

        @Override
        public Mat rgba() {
            if (!mRgbaConverted) {
                Imgproc.cvtColor(mYuvFrameData, mRgba,
                        Imgproc.COLOR_YUV2BGR_NV12, 4);
                mRgbaConverted = true;
            }
            return mRgba;
        }

        // @Override
        // public Mat yuv() {
        // return mYuvFrameData;
        // }

        @Override
        public synchronized Bitmap toBitmap() {
            if (mBitmapConverted)
                return mCachedBitmap;

            Mat rgba = this.rgba();
            Utils.matToBitmap(rgba, mCachedBitmap);

            mBitmapConverted = true;
            return mCachedBitmap;
        }

        public CameraAccessFrame(Mat Yuv420sp, int width, int height) {
            super();
            mWidth = width;
            mHeight = height;
            mYuvFrameData = Yuv420sp;
            mRgba = new Mat();

            this.mCachedBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
        }

        public synchronized void put(byte[] frame) {
            mYuvFrameData.put(0, 0, frame);
            invalidate();
        }

        public void release() {
            mRgba.release();
            mCachedBitmap.recycle();
        }

        public void invalidate() {
            mRgbaConverted = false;
            mBitmapConverted = false;
        }
    }

    public interface CameraFrame extends CvCameraViewFrame {
        Bitmap toBitmap();

        @Override
        Mat rgba();

        @Override
        Mat gray();
    }

    public class AnalyzerThread extends Thread{
        @Override
        public void run(){
            if (!isColorSelected) {
                //check for color selection in app
                BTApplication bta = (BTApplication)activity.getApplication();
                if(bta.colorsSelected > 0){
                    for(int i = 0; i < bta.colorsSelected; i++){
                        Rect rect = new Rect();

                        rect.x = (int)bta.points[i].x - 4;
                        rect.y = (int)bta.points[i].y - 4;

                        rect.width = 8;
                        rect.height = 8;

                        Mat regionRgba = rgba.submat(rect);

                        Mat regionHsv = new Mat();
                        Imgproc.cvtColor(regionRgba, regionHsv, Imgproc.COLOR_RGB2HSV_FULL);

                        //calculate average color of selection
                        blobColorHsv[i] = Core.sumElems(regionHsv);
                        int pointCount = rect.width*rect.height;
                        for (int j = 0; j < blobColorHsv[i].val.length; j++)
                            blobColorHsv[i].val[j] /= pointCount;

                        blobColorRgba[i] = converScalarHsv2Rgba(blobColorHsv[i]);

                        Log.i("Color", "Selected rgba color: (" + blobColorRgba[i].val[0] + ", " + blobColorRgba[i].val[1] +
                                ", " + blobColorRgba[i].val[2] + ", " + blobColorRgba[i].val[3] + ")");

                        detector[i].setHsvColor(blobColorHsv[i]);

                        Imgproc.resize(detector[i].getSpectrum(), spectrum, SPECTRUM_SIZE);

                        regionRgba.release();
                        regionHsv.release();

                        numColors++;
                    }
                    isColorSelected = true;
                }
                else {
                    while (readyForFrame) {
                        //wait for first frame
                    }
                    liveCalibrate();
                }
            }
            while(!interrupted()) {
                //if a frame is loaded and converted, the analyzer switches to "not ready for frame" so
                //the frame can then be analyzed
                if (!readyForFrame) {
                    analyze();
                }
                if (stopRecording) {
                    readyForFrame = false;
                    frameAnalyzed = true;
                    stopRecording = false;
                    interrupt();
                }
            }
        }
    }
}