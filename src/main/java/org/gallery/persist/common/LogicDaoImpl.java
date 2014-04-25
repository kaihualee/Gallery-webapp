package org.gallery.persist.common;

import java.util.List;

import org.gallery.common.LogicEntityImpl;
import org.gallery.common.Status;
import org.gallery.persist.exception.DaoException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * @author likaihua
 */
public class LogicDaoImpl<T extends LogicEntityImpl> extends BaseDaoImpl<T>
    implements LogicDao<T> {

    @Override
    public void deleteById(Long id) {
        T entity = get(id);
        entity.setStatus(Status.Deleted);
        saveOrUpdate(entity);
    }

    @Override
    public List<T> getAll() {
        Criteria criteria = super.getSession().createCriteria(entityClass);
        criteria.add(Restrictions.ne("status", Status.Deleted));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> getAllByStatus(Status... status) {
        Criteria criteria = super.getSession().createCriteria(entityClass);
        criteria.add(Restrictions.in("status", status));
        List<T> list = criteria.list();
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list;
        }
    }

    @Override
    public T getById(Long id) {
        Criteria criteria = super.getSession().createCriteria(entityClass);
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.or(Restrictions.eq("status", Status.Normal),
            Restrictions.eq("status", Status.Disabled)));
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
    public T getByStatusAndId(Long id, Status... status) {
        Criteria criteria = super.getSession().createCriteria(entityClass);
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.in("status", status));
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
    public List<T> getAllByName(String name) {
        Criteria criteria = super.getSession().createCriteria(entityClass);
        criteria.add(Restrictions.eq("name", name));
        criteria.add(Restrictions.eq("status", Status.Normal));
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

}
