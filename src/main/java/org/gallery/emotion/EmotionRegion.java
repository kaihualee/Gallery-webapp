/**
 * @(#)EmotionRegion.java, 2013年12月15日. 
 * 
 * Copyright 2013 ZJU, Inc. All rights reserved.
 *
 */
package org.gallery.emotion;

/**
 * @author likaihua
 */
public enum EmotionRegion {

    ACTIVITY(-1.7f, 4.0f), WEIGHT(-2.0f, 2.1f), HEAT(-1.2f, 2.2f);

    private double MIN;

    private double MAX;

    private final static float INCREAMENT = 0.1f;

    /**
     * @param min
     * @param max
     */
    EmotionRegion(final double min, final double max) {
        this.MIN = min;
        this.MAX = max;
    }

    /**
     * @return the mIN
     */
    public double getMIN() {
        return MIN;
    }

    /**
     * @param mIN the mIN to set
     */
    public void setMIN(double mIN) {
        MIN = mIN;
    }

    /**
     * @return the mAX
     */
    public double getMAX() {
        return MAX;
    }

    /**
     * @param mAX the mAX to set
     */
    public void setMAX(double mAX) {
        MAX = mAX;
    }

    /**
     * @return the increament
     */
    public static float getIncreament() {
        return INCREAMENT;
    }
}
