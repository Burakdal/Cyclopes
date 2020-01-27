package com.collegesoftware.cyclopes;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;




public class DrawView extends View {


    private static final String TAG="DrawView";

    Point point1, point3;
    Point point2, point4;

    private IPoints mInterface;

    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    int groupId = -1;
    public ArrayList<ColorBall> colorballs = new ArrayList<ColorBall>();
    // array that holds the balls
    private int balID = 0;
    // variable to know what ball is being dragged
    Paint paint;
    Canvas canvas;


    public DrawView(Context context) {
        super(context);
        mInterface=(IPoints)context;
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
        // setting the start point for the balls
        point1 = new Point();
        point1.x = 0;
        point1.y = 0;

        point2 = new Point();
        point2.x = 0;
        point2.y = 120;

        point3 = new Point();
        point3.x = 250;
        point3.y = 120;

        point4 = new Point();
        point4.x = 250;
        point4.y = 0;

        // declare each ball with the ColorBall class
        colorballs.add(new ColorBall( point1));
        colorballs.add(new ColorBall(point2));
        colorballs.add(new ColorBall(point3));
        colorballs.add(new ColorBall(point4));

    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        mInterface=(IPoints)context;

        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
        // setting the start point for the balls
        point1 = new Point();
        point1.x = 0;
        point1.y = 0;

        point2 = new Point();
        point2.x = 0;
        point2.y = 120;

        point3 = new Point();
        point3.x = 250;
        point3.y = 120;

        point4 = new Point();
        point4.x = 250;
        point4.y = 0;

        // declare each ball with the ColorBall class
        colorballs.add(new ColorBall(point1));
        colorballs.add(new ColorBall(point2));
        colorballs.add(new ColorBall(point3));
        colorballs.add(new ColorBall(point4));

    }

    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas) {
        //if you want another background color
//        Color.parseColor("#55FFFFFF")
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        // mPaint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5);
        canvas.drawPaint(paint);

        paint.setColor(Color.parseColor("#75C0C0C0"));
        Log.d(TAG,"size of colorballs "+colorballs.size());
        if (groupId == 1) {
            canvas.drawRect(point1.x + colorballs.get(0).getWidthOfBall() / 2,
                    point3.y + colorballs.get(2).getWidthOfBall() / 2, point3.x
                            + colorballs.get(2).getWidthOfBall() / 2, point1.y
                            + colorballs.get(0).getWidthOfBall() / 2, paint);
        } else {
            canvas.drawRect(point2.x + colorballs.get(1).getWidthOfBall() / 2,
                    point4.y + colorballs.get(3).getWidthOfBall() / 2, point4.x
                            + colorballs.get(3).getWidthOfBall() / 2, point2.y
                            + colorballs.get(1).getWidthOfBall() / 2, paint);
        }


        // draw the balls on the canvas
        for (ColorBall ball : colorballs) {
            canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                    new Paint());

        }
    }

    // events when touching the screen
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        int X = (int) event.getX();
        Log.d(TAG,"X: "+X);
        int Y = (int) event.getY();
        Log.d(TAG,"Y: "+Y);


        switch (eventaction) {

            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                // a ball
                balID = -1;
                groupId = -1;
                for (ColorBall ball : colorballs) {
                    // check if inside the bounds of the ball (circle)
                    // get the center for the ball
                    Log.d(TAG,"Id : " + ball.getID());
                    Log.d(TAG,"getX : " + ball.getX() + " getY() : " + ball.getY());
                    int centerX = ball.getX() + ball.getWidthOfBall();
                    int centerY = ball.getY() + ball.getHeightOfBall();
                    paint.setColor(Color.CYAN);
                    // calculate the radius from the touch to the center of the ball
                    double radCircle = Math
                            .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                    * (centerY - Y)));

                    Log.d(TAG,"X : " + X + " Y : " + Y + " centerX : " + centerX
                            + " CenterY : " + centerY + " radCircle : " + radCircle);

                    if (radCircle < ball.getWidthOfBall()) {

                        balID = ball.getID();
                        Log.d(TAG,"Selected ball : " + balID);
                        if (balID == 1 || balID == 3) {
                            groupId = 2;
                            canvas.drawRect(point1.x, point3.y, point3.x, point1.y,
                                    paint);
                        } else {
                            groupId = 1;
                            canvas.drawRect(point2.x, point4.y, point4.x, point2.y,
                                    paint);
                        }
                        invalidate();
                        break;
                    }
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_MOVE: // touch drag with the ball
                // move the balls the same as the finger
                Log.d(TAG,"height :"+getHeight());
                Log.d(TAG,"width :"+getWidth());
                if (X>0 && Y>0 && X<getWidth()-30 && Y<getHeight()-30){
                    if (balID > -1 && balID<4) {
                        Log.d(TAG,"Moving Ball : " + balID);
                        if(balID==3){
                            if (X>colorballs.get(0).getX()+50 && Y<colorballs.get(2).getY()-50 ){
                                colorballs.get(balID).setX(X);
                                colorballs.get(balID).setY(Y);
                            }
                        }else if (balID==2){
                            if (X>colorballs.get(1).getX()+50 && Y>colorballs.get(3).getY()+50){
                                colorballs.get(balID).setX(X);
                                colorballs.get(balID).setY(Y);
                            }
                        }else if(balID==1){
                            if (X<colorballs.get(2).getX()-50 && Y>colorballs.get(0).getY()+50){
                                colorballs.get(balID).setX(X);
                                colorballs.get(balID).setY(Y);
                            }

                        }else if(balID==0){
                            if (X<colorballs.get(3).getX()-50 && Y<colorballs.get(1).getY()-50){
                                colorballs.get(balID).setX(X);
                                colorballs.get(balID).setY(Y);
                            }
                        }


                        paint.setColor(Color.CYAN);

                        if (groupId == 1) {
                            colorballs.get(1).setX(colorballs.get(0).getX());
                            colorballs.get(1).setY(colorballs.get(2).getY());
                            colorballs.get(3).setX(colorballs.get(2).getX());
                            colorballs.get(3).setY(colorballs.get(0).getY());
                            canvas.drawRect(point1.x, point3.y, point3.x, point1.y,paint);
                        }else{
                            colorballs.get(0).setX(colorballs.get(1).getX());
                            colorballs.get(0).setY(colorballs.get(3).getY());
                            colorballs.get(2).setX(colorballs.get(3).getX());
                            colorballs.get(2).setY(colorballs.get(1).getY());
                            canvas.drawRect(point2.x, point4.y, point4.x, point2.y,paint);
                        }

                        invalidate();
                    }
                }


                break;

            case MotionEvent.ACTION_UP:
                // touch drop - just do things here after dropping
                mInterface.getPoints(point1,point2,point3,point4);
                break;
        }
        // redraw the canvas
        invalidate();
        return true;

    }




}
