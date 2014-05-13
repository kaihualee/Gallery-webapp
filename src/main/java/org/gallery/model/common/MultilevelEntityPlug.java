/**
 * 
 */
package org.gallery.model.common;

import java.util.List;
import java.util.Set;

/**
 * @author Dahaka
 * 
 */
public interface MultilevelEntityPlug<T> {

	public Set<T> getChildren();

	public void setChildren(Set<T> list);
}
