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
    private final int T_MILLISECONDS = (int) (T * 1000.0);
    private final double EPSILON = 0.000001;
    private static int numberOfSatellites = 0;
    private static double starMass = 0.0;
    private Body currentBody;
    private Paint starColor;
    private Paint backgroundColor;
    private Paint satelliteColor;
    private Paint tailColor;
    private ArrayList<Body> bodies = new ArrayList<Body>();
    private ArrayList<Body> projection = new ArrayList<Body>(2000);
    private double previousVelocityX = 0.0;
    private double previousVelocityY = 0.0;

    public UniverseDrawingView(Context context) {
        this(context, null);
        Log.d(TAG, "Constructor from activity");
    }

    public UniverseDrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d(TAG, "Constructor");
        starColor = new Paint();
        starColor.setColor(0xffff0000);
        backgroundColor = new Paint();
        backgroundColor.setColor(0xff000000);
        satelliteColor = new Paint();
        satelliteColor.setColor(0x7f7f7f7f);
        tailColor = new Paint();
        tailColor.setColor(0x7fffffff);
        new Thread(new Runnable() {
            public void run() {
                //ArrayList<double[]> history = new ArrayList<double[]>();
                while (true) {
                    try {
                        Thread.sleep(T_MILLISECONDS);
                        //Log.d(TAG,"tick");
                    } catch (Exception e) {
                        Log.e(TAG, "weird error");
                        break;
                    }
                    if (bodies.size() > 1) {
                        synchronized (bodies) {
                            for (Body body : bodies) {
                                if (collision(body)) {
                                    bodies.get(0).setMass(body.getMass());
                                    starMass = bodies.get(0).getMass();
                                    bodies.remove(body);
                                    numberOfSatellites--;
                                    break;
                                } else {
                                    double[] forces = forceOnThisBody(body);
                                    body.applyForce(forces, T);
                                }
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

                }
                ;
                Log.d(TAG, "finishing runnable");

            }
        }).start();

    }

    public boolean onTouchEvent(MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        //PointF point = new PointF(event.getX(),bodies.get(0).getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentBody = new Body(point, 1.0, 0.0, 0.0, satelliteColor);
                Log.d(TAG, "new body is at " + point.x + ", " + point.y);
                previousVelocityX = 0.0;
                previousVelocityY = 0.0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentBody != null) {
                    double velocityX = -(event.getX() - currentBody.getX()) * 0.7;
                    double velocityY = -(event.getY() - currentBody.getY()) * 0.7;
                    previousVelocityX = alphaBeta(0.1,velocityX,previousVelocityX);
                    previousVelocityY = alphaBeta(0.1,velocityY,previousVelocityY);
                    currentBody.setVelocityX(previousVelocityX);
                    currentBody.setVelocityY(previousVelocityY);
                    synchronized (projection) {
                        projection.removeAll(projection);
                        projection.add(new Body(new PointF((float) currentBody.getX(), (float) currentBody.getY()), currentBody.getMass(), currentBody.getVelocityX(), currentBody.getVelocityY(), satelliteColor));
                        for (int i = 1; i < 500; i++) {
                            Body projRef = projection.get(i - 1);
                            double[] forces = forceOnThisBody(projRef);
                            projRef.applyForce(forces, T);
                            forces = forceOnThisBody(projRef);
                            projRef.applyForce(forces, T);
                            forces = forceOnThisBody(projRef);
                            projRef.applyForce(forces, T);
                            forces = forceOnThisBody(projRef);
                            projRef.applyForce(forces, T);
                            projection.add(new Body(new PointF((float) projRef.getX(), (float) projRef.getY()), projRef.getMass(), projRef.getVelocityX(), projRef.getVelocityY(), satelliteColor));
                        }
                    }
                    if (bodies.size() < 2) {
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentBody != null) {
                    synchronized (bodies) {
                        bodies.add(currentBody);
                        numberOfSatellites++;
                    }
                    projection.removeAll(projection);
                    currentBody = null;
                }
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
        if (bodies.size() <= 0) return;
        synchronized (bodies) {
            for (Body body : bodies) {
                canvas.drawCircle(body.getX(), body.getY(), (float) body.getRadius(), body.getColor());
                for (PointF previousPosition : body.getWhereWasI()) {
                    //canvas.drawCircle(previousPosition.x, previousPosition.y, (float) body.getRadius(), body.getColor());
                    canvas.drawPoint(previousPosition.x, previousPosition.y, tailColor);
                }

            }
        }
        synchronized (projection) {
            for (Body body : projection) {
                canvas.drawCircle(body.getX(), body.getY(), (float) body.getRadius(), body.getColor());
            }
        }

    }

    @Override
    public void onSizeChanged(int newX, int newY, int oldX, int oldY) {
        float X = (float) (newX / 2.0);
        float Y = (float) (newY / 2.0);
        Log.d(TAG, "Center of Universe is at " + X + ", " + Y);
        currentBody = new Body(new PointF(X, Y), 100000.0, 0.0, 0.0, starColor);
        currentBody.setRadius(50.0);
        bodies.add(currentBody);
        starMass = currentBody.getMass();
        invalidate();
    }

    private double[] forceOnThisBody(Body body) {
        double[] returnArray = {0.0, 0.0};
        if (bodies.size() > 0) { //&& body != bodies.get(0)) {
            double totalForceX = 0.0;
            double totalForceY = 0.0;
            synchronized (bodies) {
                for (Body other : bodies) {
                    if (other == body) {
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
            }
            returnArray[0] = totalForceX;
            returnArray[1] = totalForceY;
            //Log.d(TAG,"ForceX="+totalForceX+", ForceY="+totalForceY);
        }
        return returnArray;
    }

    private boolean collision(Body body) {
        if (body == bodies.get(0)) {
            return false;
        }
        Body sunReference = bodies.get(0);
        if (body.getRadius() + sunReference.getRadius() > Math.sqrt(
                Math.pow(body.getX() - sunReference.getX(), 2) + Math.pow(body.getY() - sunReference.getY(), 2))) {
            return true;
        }
        return false;
    }

    private double alphaBeta(double alpha, double newValue, double oldValue) {
        return alpha * newValue + (1.0F-alpha)*oldValue;
    }

    public static int getNumberOfSatellites() {
        return numberOfSatellites;
    }

    public static double getStarMass() {
        return starMass;
    }

}
