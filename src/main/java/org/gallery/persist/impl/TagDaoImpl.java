/**
 * 
 */
package org.gallery.persist.impl;

import java.util.List;

import org.gallery.model.TagEntity;
import org.gallery.persist.TagDao;
import org.gallery.persist.common.AssetDaoImpl;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * @author Dahaka
 * 
 */
@Repository
public class TagDaoImpl extends AssetDaoImpl<TagEntity> implements TagDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<TagEntity> getRootTags(Long ownerId) {
		Criteria critera = getCritera(ownerId);
		critera.createAlias("children", "children").add(
				Restrictions.isNull("children"));
		return critera.list();
	}

}
