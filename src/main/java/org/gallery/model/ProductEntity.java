/**
 * 
 */
package org.gallery.model;

import org.gallery.common.AssetEntityImpl;
import org.gallery.common.ContentEntity;

/**
 * @author Dahaka
 * 
 */
public class ProductEntity extends AssetEntityImpl implements ContentEntity {

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
