package com.hswt.broihier.gravity;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by broihier on 5/11/17.
 */

public class UniverseDrawingView extends View {
    private final String TAG = "UniverseDrawingView";
    private final double T = 0.1;
    private final int T_MILLISECONDS = (int) (T*1000.0);
    private final double EPSILON = 0.000001;
    private Body currentBody;
    private Paint bodyColor;
    private Paint backgroundColor;
    private ArrayList<Body> bodies = new ArrayList<Body>();

    public UniverseDrawingView(Context context) {
        this(context,null);
        Log.d(TAG,"Constructor from activity");
    }
    public UniverseDrawingView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        Log.d(TAG,"Constructor");
        bodyColor = new Paint();
        bodyColor.setColor(0x22ff0000);
        backgroundColor = new Paint();
        backgroundColor.setColor(0xfff8efe0);
        new Thread ( new Runnable() {
            public void run () {
                //ArrayList<double[]> history = new ArrayList<double[]>();
                while (true) {
                    try {
                        Thread.sleep(T_MILLISECONDS);
                        //Log.d(TAG,"tick");
                    } catch (Exception e) {
                        Log.e(TAG,"weird error");
                        break;
                    }
                    if (bodies.size() > 1) {
                        synchronized (bodies) {
                            for (Body body : bodies) {
                                double[] forces = forceOnThisBody(body);
                                //history.add(forces);
                                body.applyForce(forces[0], forces[1], T);
                            }
                        }
                        if (GravityActivity.getGravityActivity() != null) {
                            GravityActivity.getGravityActivity().runOnUiThread(
                                    new Runnable() {
                                        public void run() {
                                            invalidate();
                                        }
                                    });
                        }
                    }

                };
                Log.d(TAG,"finishing runnable");

            }
        }).start();

    }

    public boolean onTouchEvent(MotionEvent event) {
        PointF point = new PointF(event.getX(),event.getY());
        //PointF point = new PointF(event.getX(),bodies.get(0).getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentBody = new Body(point,1.0,0.0,100.0);
                Log.d(TAG,"new body is at "+point.x+ ", "+point.y);
                synchronized (bodies) {
                    bodies.add(currentBody);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentBody != null) {
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                currentBody = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                currentBody = null;
                break;
        }
        return true;
    }
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawPaint(backgroundColor);
        for (Body body : bodies) {
            canvas.drawCircle(body.getX(),body.getY(),(float) body.getRadius(),bodyColor);
        }

    }

    @Override
    public void onSizeChanged(int newX, int newY, int oldX, int oldY) {
        float X = (float) (newX / 2.0);
        float Y = (float) (newY / 2.0);
        Log.d(TAG,"Center of Universe is at "+X+ ", "+Y);
        currentBody = new Body(new PointF(X,Y),1000000.0,0.0,0.0);
        currentBody.setRadius(50.0);
        bodies.add(currentBody);
        invalidate();
    }

    private double[] forceOnThisBody(Body body) {
        double[] returnArray = {0.0, 0.0};
        if (bodies.size() > 1) {
            double totalForceX = 0.0;
            double totalForceY = 0.0;
            for (Body other : bodies) {
                if (other == body) {
                    //Log.d(TAG, "Skipping - this is self");
                    continue;
                }
                double deltaX = body.getX() - other.getX();
                double deltaY = body.getY() - other.getY();
                double distanceSquared = Math.pow(deltaX, 2.0) + Math.pow(deltaY, 2.0);
                double forceMagnitude;
                if (distanceSquared > EPSILON) {
                    forceMagnitude = other.getMass() * body.getMass() / distanceSquared;
                } else {
                    forceMagnitude = EPSILON;
                }
                double theta = Math.atan2(deltaY, deltaX);
                totalForceX -= forceMagnitude * Math.cos(theta);
                totalForceY -= forceMagnitude * Math.sin(theta);

            }
            returnArray[0] = totalForceX;
            returnArray[1] = totalForceY;
        }
        return returnArray;
    }

}
