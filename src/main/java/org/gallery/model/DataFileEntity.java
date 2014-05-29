/**
 * @(#)DataFileEntity.java, Mar 13, 2014. 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.model;

import org.gallery.model.common.LogicEntityImpl;
import org.gallery.model.common.StatusEntityPlug;

/**
 * Name(基类),contentType,key,Size
 * 
 * @author likaihua
 * 
 */
public class DataFileEntity extends LogicEntityImpl implements StatusEntityPlug {

	/**
	 * the file system's hashkey
	 */
	private String key;

	/**
	 * contentType
	 */
	private String contentType;

	/**
	 * the size of file
	 */
	private Long size;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return the size
	 */
	public Long getSize() {
		return size;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return super.toString() + ";" + key + " " + contentType + " " + size
				+ "Bytes";
	}
}
