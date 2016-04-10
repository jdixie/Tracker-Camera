package com.ninjapiratestudios.trackercamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import org.opencv.core.Point;

import java.util.ArrayList;

/**
 * Created by jjdixie on 1/30/16.
 * The idea here is to create the rectangle overlay that will outline the tracked subject.
 * This may well be temporary after I plug in OpenGL ES
 */
public class Overlay {
    private static boolean draw = false;
    private static boolean ready = false;
    private static Graphic graphic;
    private static ArrayList<Point> blobs = new ArrayList<>();

    public static void setupGraphic(Context c){
        graphic = new Graphic(c);
    }

    public static Graphic getGraphic() { return graphic; }

    public static void toggleDraw() { draw = !draw; }

    public static void toggleReady() { ready = !ready; }

    public static void invalidate() {
            graphic.invalidate();
    }

    public static void clearBlobs(){ blobs.clear(); }

    public static void addBlob(Point p) { blobs.add(p); }

    private static class Graphic extends View {
        private Paint paint = new Paint();

        Graphic(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (draw) {
                super.onDraw(canvas);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.YELLOW);
                paint.setStrokeWidth(10);

                int frameWidth = canvas.getWidth();
                int frameHeight = canvas.getHeight();
                int xRadius = frameHeight / 20;
                int yRadius = frameHeight / 20;

                //draw guide box
                for(int i = 0; i < blobs.size(); i++)
                    canvas.drawRect((float)blobs.get(i).x - xRadius, (float)blobs.get(i).y - yRadius,
                            (float)blobs.get(i).x + xRadius, (float)blobs.get(i).y + yRadius, paint);
            }
        }
    }
}
