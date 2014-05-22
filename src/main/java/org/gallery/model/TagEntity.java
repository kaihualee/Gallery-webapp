/**
 * 
 */
package org.gallery.model;

import java.util.Set;

import org.gallery.model.common.AssetEntityImpl;
import org.gallery.model.common.MultilevelEntityPlug;

/**
 * @author Dahaka
 * 
 */
@SuppressWarnings("serial")
public class TagEntity extends AssetEntityImpl implements
		MultilevelEntityPlug<TagEntity> {


	public Set<TagEntity> children;

	/**
	 * @return the children
	 */
	public Set<TagEntity> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(Set<TagEntity> children) {
		this.children = children;
	}

	public String toString() {
		return super.toString();
	}
}
