/**
 * @(#)Images.java, 2013年12月8日. 
 * 
 * Copyright 2013 ZJU, Inc. All rights reserved.
 *
 */
package org.gallery.model;

import java.sql.Blob;

import org.gallery.common.LogicEntityImpl;
import org.gallery.common.StatusEntity;

/**
 * @author likaihua
 */
public class ImageEntity extends LogicEntityImpl implements StatusEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -5865240409295652857L;

   
    //Emotion fields
    private Blob weights;

    private Blob heats;

    private Blob activities;

    private float globala;

    private float globalw;

    private float globalh;

    private float global_awh;

    //Image Field
    private String newFilename;

    private String contentType;

    private Long size;

    /**
     * @return the newFilename
     */
    public String getNewFilename() {
        return newFilename;
    }

    /**
     * @param newFilename
     *            the newFilename to set
     */
    public void setNewFilename(String newFilename) {
        this.newFilename = newFilename;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType
     *            the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the global_awh
     */
    public float getGlobal_awh() {
        return global_awh;
    }

    /**
     * @param global_awh
     *            the global_awh to set
     */
    public void setGlobal_awh(float global_awh) {
        this.global_awh = global_awh;
    }

    /**
     * @return the weights
     */
    public Blob getWeights() {
        return weights;
    }

    /**
     * @param weights
     *            the weights to set
     */
    public void setWeights(Blob weights) {
        this.weights = weights;
    }

    /**
     * @return the heats
     */
    public Blob getHeats() {
        return heats;
    }

    /**
     * @param heats
     *            the heats to set
     */
    public void setHeats(Blob heats) {
        this.heats = heats;
    }

    /**
     * @return the activities
     */
    public Blob getActivities() {
        return activities;
    }

    /**
     * @param activities
     *            the activities to set
     */
    public void setActivities(Blob activities) {
        this.activities = activities;
    }

    /**
     * @return the globala
     */
    public float getGlobala() {
        return globala;
    }

    /**
     * @param globala
     *            the globala to set
     */
    public void setGlobala(float globala) {
        this.globala = globala;
    }

    /**
     * @return the globalw
     */
    public float getGlobalw() {
        return globalw;
    }

    /**
     * @param globalw
     *            the globalw to set
     */
    public void setGlobalw(float globalw) {
        this.globalw = globalw;
    }

    /**
     * @return the globalh
     */
    public float getGlobalh() {
        return globalh;
    }

    /**
     * @param globalh
     *            the globalh to set
     */
    public void setGlobalh(float globalh) {
        this.globalh = globalh;
    }

    @Override
    public String toString() {
        return "Image{" + "id=" + id+ ", name=" + name + ", newFilename="
            + newFilename + ", contentType=" + contentType + ", size=" + size
            + '}';
    }
}
