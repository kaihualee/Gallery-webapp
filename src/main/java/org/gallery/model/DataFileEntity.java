/**
 * @(#)DataFileEntity.java, Mar 13, 2014. 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.model;

import org.gallery.common.LogicEntityImpl;
import org.gallery.common.StatusEntity;


/**
 *
 * @author likaihua
 *
 */
public class DataFileEntity extends LogicEntityImpl implements StatusEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -7911502419247905782L;

    /**
     * the file system's hashkey
     */
    private String key;

    /**
     * fileType
     */
    private String fileType;

    private String md5Code;
    
    /**
     * the size of file
     */
    private Long  size;

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the md5Code
     */
    public String getMd5Code() {
        return md5Code;
    }

    /**
     * @param md5Code
     *            the md5Code to set
     */
    public void setMd5Code(String md5Code) {
        this.md5Code = md5Code;
    }

    /**
     * @return the fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param fileType
     *            the fileType to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    @Override
    public String toString(){
        return "[" + getClass().getSimpleName() + " " + id + " " + status + "]"
            + name + " " + key + " " + fileType + " " + size; 
    }

}
