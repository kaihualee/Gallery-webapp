/**
 * 
 */
package org.gallery.common;

import java.util.List;
import java.util.Set;

/**
 * @author Dahaka
 * 
 */
public interface MultilevelEntity<T> {

	public Set<T> getChildren();

	public void setChildren(Set<T> list);
}
