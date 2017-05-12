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
    }

    public boolean onTouchEvent(MotionEvent event) {
        PointF point = new PointF(event.getX(),event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentBody = new Body(point,1.0,0.0,0.0);
                Log.d(TAG,"new body is at "+point.x+ ", "+point.y);
                bodies.add(currentBody);
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

}
