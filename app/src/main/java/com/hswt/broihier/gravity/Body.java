package com.hswt.broihier.gravity;

import android.graphics.PointF;

/**
 * Created by broihier on 5/11/17.
 */

public class Body {
    private PointF currentLocation;
    private double radius;
    private double mass;
    private double velocityX;
    private double velocityY;

    public Body(PointF location, double mass, double velocityX, double velocityY) {
        this.currentLocation = location;
        this.mass = mass;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.radius = 10;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public float getX() {
        return currentLocation.x;
    }

    public float getY() {
        return currentLocation.y;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getMass() {
        return mass;
    }
    public void setMass(double massDelta) {
        this.mass += massDelta;
    }
    public void applyForce(double[] forces, double deltaT){
        // f = ma
        // a = f/m
        double aX = forces[0]/mass;
        double aY = forces[1]/mass;
        velocityX += aX * deltaT;
        velocityY += aY * deltaT;
        currentLocation.x += (float) (velocityX*deltaT + 0.5 * aX * deltaT * deltaT);
        currentLocation.y += (float) (velocityY*deltaT + 0.5 * aY * deltaT * deltaT);
    }

}
