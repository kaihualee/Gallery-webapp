/**
 * 
 */
package org.gallery.persist;

import java.util.List;

import org.gallery.model.TagEntity;
import org.gallery.persist.common.AssetDao;

/**
 * @author Dahaka
 *
 */
public interface TagDao extends AssetDao<TagEntity> {

	public List<TagEntity> getRootTags(Long ownerId);
}
