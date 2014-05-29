/**
 * 
 */
package org.gallery.model;

import org.gallery.model.common.AssetEntityImpl;
import org.gallery.model.common.ContentEntityPlug;

/**
 * @author Dahaka
 * 
 */
public class ProductEntity extends AssetEntityImpl implements ContentEntityPlug {

	String content;

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
}
