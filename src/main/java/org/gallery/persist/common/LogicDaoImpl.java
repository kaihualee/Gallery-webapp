package org.gallery.persist.common;

import java.util.List;

import org.gallery.model.common.LogicEntityImpl;
import org.gallery.model.common.Status;
import org.gallery.persist.exception.DaoException;
import org.gallery.persist.utils.PageBean;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * @author likaihua
 */
public class LogicDaoImpl<T extends LogicEntityImpl> extends BaseDaoImpl<T>
		implements LogicDao<T> {

	protected Criteria getCritera() {
		Criteria criteria = super.getSession().createCriteria(entityClass);
		criteria.add(Restrictions.ne("status", Status.Deleted));
		return criteria;
	}

	protected Criteria getCritera(Status... status) {
		Criteria criteria = super.getSession().createCriteria(entityClass);
		criteria.add(Restrictions.in("status", status));
		return criteria;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getById(Long id) {
		Criteria criteria = getCritera();
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

	@SuppressWarnings("unchecked")
	@Override
	public T getByStatusAndId(Long id, Status... status) {
		Criteria criteria = getCritera(status);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll() {
		Criteria criteria = getCritera();
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll(PageBean pageBean) {
		Criteria criteria = getCritera();
		criteria.setFirstResult(pageBean.getFirstResult());
		criteria.setMaxResults(pageBean.getMaxResults());
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAllByStatus(Status... status) {
		Criteria criteria = getCritera(status);
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAllByStatus(PageBean pageBean, Status... status) {
		Criteria criteria = getCritera(status);
		criteria.setFirstResult(pageBean.getFirstResult());
		criteria.setMaxResults(pageBean.getMaxResults());
		return criteria.list();
	}

	@Override
	public T getByName(String name) {
		List<T> list = getAllByName(name);
		if (list == null || list.isEmpty()) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new DaoException("Select list not only one.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAllByName(String name) {
		Criteria criteria = getCritera();
		criteria.add(Restrictions.eq("name", name));
		return criteria.list();

	}

	@Override
	public void deleteById(Long id) {
		T entity = get(id);
		entity.setStatus(Status.Deleted);
		saveOrUpdate(entity);
	}

}
