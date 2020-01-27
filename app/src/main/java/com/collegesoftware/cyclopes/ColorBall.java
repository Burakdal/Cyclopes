package com.collegesoftware.cyclopes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class ColorBall {

    private static final String TAG = "ColorBall";
    private Bitmap bitmap;
    private Point point;
    int id;
    static int count = 0;

    public ColorBall(Point point) {
        this.id = count++;



        bitmap = createBitmap();


        Log.d(TAG,"id: "+id);

        this.point = point;
    }

    public int getWidthOfBall() {
        return bitmap.getWidth();
    }

    public int getHeightOfBall() {
        return bitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return point.x;
    }

    public int getY() {
        return point.y;
    }

    public int getID() {
        return id;
    }

    public void setX(int x) {
        point.x = x;
    }

    public void setY(int y) {
        point.y = y;
    }
    private Bitmap createBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(
                30, // Width
                30, // Height
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);

        // Initialize a new Paint instance to draw the Circle
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);

        int radius = Math.min(canvas.getWidth(),canvas.getHeight()/2);
        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getHeight() / 2, // cy
                radius, // Radius
                paint // Paint
        );

        return bitmap;
    }
}
