/**
 * 
 */
package org.gallery.persist.common;

import java.util.List;

import org.gallery.model.AccountEntity;
import org.gallery.model.common.AssetEntityImpl;
import org.gallery.model.common.PageBean;
import org.gallery.model.common.Status;
import org.gallery.persist.exception.DaoException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * @author Dahaka
 * 
 */
public class AssetDaoImpl<T extends AssetEntityImpl> extends
		LogicDaoImpl<T> implements AssetDao<T> {

	protected Criteria getCritera(Long ownerId) {
		Criteria criteria = super.getCritera();
		AccountEntity owner = new AccountEntity();
		owner.setId(ownerId);
		criteria.add(Restrictions.eq("owner", owner));
		return criteria;
	}

	protected Criteria getCritera(Long ownerId, Status... status) {
		Criteria criteria = super.getCritera(status);
		AccountEntity owner = new AccountEntity();
		owner.setId(ownerId);
		criteria.add(Restrictions.eq("owner", owner));
		return criteria;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getById(Long ownerId, Long id) {
		Criteria criteria = getCritera(ownerId);
		criteria.add(Restrictions.eq("id", id));
		List<T> list = criteria.list();
		if (list == null || list.isEmpty()) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new DaoException("Select list not only one.");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getByStatusAndId(Long ownerId, Long id, Status... status) {
		Criteria criteria = getCritera(ownerId, status);
		criteria.add(Restrictions.eq("id", id));
		List<T> list = criteria.list();
		if (list == null || list.isEmpty()) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new DaoException("Select list not only one.");
		}
	}

	@Override
	public T getByName(Long ownerId, String name) {
		List<T> list = getAllByName(name);
		if (list == null || list.isEmpty()) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new DaoException("Select list not only one.");
		}
	}

	@Override
	public List<T> getAll(Long ownerId) {
		Criteria criteria = getCritera(ownerId);
		return criteria.list();
	}

	@Override
	public List<T> getAllByName(Long ownerId, String name) {
		Criteria criteria = getCritera(ownerId);
		criteria.add(Restrictions.eq("name", name));
		return criteria.list();
	}

	@Override
	public List<T> getAllByStatus(Long ownerId, Status... status) {
		Criteria criteria = getCritera(ownerId, status);
		return criteria.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gallery.persist.common.AbstractAssetDao#getAllByStatus(java.lang.
	 * Long, org.gallery.persist.utils.PageBean, org.gallery.common.Status[])
	 */
	@Override
	public List<T> getAllByStatus(Long ownerId, PageBean pageBean,
			Status... status) {
		Criteria criteria = getCritera(ownerId, status);
		criteria.setFirstResult(pageBean.getFirstResult());
		criteria.setMaxResults(pageBean.getMaxResults());
		return criteria.list();
	}

	@Override
	public void deleteById(Long ownerId, Long id) {
		T entity = getById(ownerId, id);
		if (entity == null) {
			throw new DaoException("not a valid id or no auth!");
		}
		entity.setStatus(Status.Deleted);
		saveOrUpdate(entity);
	}

}
