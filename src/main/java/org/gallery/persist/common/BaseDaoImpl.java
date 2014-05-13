package org.gallery.persist.common;

import java.util.Date;
import java.util.List;

import org.gallery.model.common.BaseEntity;
import org.gallery.persist.utils.GenericsUtils;
import org.gallery.persist.utils.PageBean;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;



public abstract class BaseDaoImpl<T extends BaseEntity> extends
        HibernateDaoSupport implements BaseDao<T> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseDaoImpl() {
        entityClass = GenericsUtils.getSuperClassGenricType(getClass());
    }

    @Override
    public T get(Long id) {
        return (T) getHibernateTemplate().get(entityClass, id);
    }

    @Override
    public List<T> getAll() {
        return getHibernateTemplate().loadAll(entityClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> getAll(PageBean pageBean) {
        Criteria criteria = super.getSession().createCriteria(entityClass);
        criteria.setFirstResult(pageBean.getFirstResult());
        criteria.setMaxResults(pageBean.getMaxResults());
        return (List<T>) criteria.list();
    }

    @Override
    public void merge(T entity) {
        getHibernateTemplate().merge(entity);
    }

    @Override
    public void refresh(T entity) {
    	getHibernateTemplate().refresh(entity);
    }

    @Override
    public void remove(List<T> entityList) {
        for(T entity : entityList) {
            remove(entity);
        }
    }

    @Override
    public void remove(T entity) {
        getHibernateTemplate().delete(entity);
        logger.debug("remove class={}", entity);
    }

    @Override
    public int removeAll() {
        String hql = "delete from " + entityClass.getSimpleName();
        Query query = getSession().createQuery(hql);
        return query.executeUpdate();
    }

    @Override
    public void removeById(Long id) {
        remove(get(id));
        logger.debug("remove class={}, id={}", entityClass, id);
    }

    protected int removeByProperty(String propertyName,
            Object propertyValue) {
        String hql = "delete from " + entityClass.getSimpleName() + " where "
                + propertyName + " =:propertyValue";
        Query query = getSession().createQuery(hql);
        if (propertyValue instanceof Long) {
            query.setLong("propertyValue", (Long) propertyValue);
        } else if (propertyValue instanceof Integer) {
            query.setInteger("propertyValue", (Integer) propertyValue);
        } else if (propertyValue instanceof String) {
            query.setString("propertyValue", (String) propertyValue);
        } else if (propertyValue instanceof Date) {
            query.setDate("propertyValue", (Date) propertyValue);
        }

        return query.executeUpdate();
    }
    
    @Override
    public void save(T entity) {
        logger.debug("save class={}", entity);
        getHibernateTemplate().save(entity);
    }
    
    @Override
    public void saveOrUpdate(T entity) {
        if (entity.getId() == null) {
            save(entity);
        } else {
            update(entity);
        }
    }
    
    @Override
    public void update(T entity) {
        logger.debug("update class={}", entity);
        entity.setModifyTime(new Date());
        getHibernateTemplate().update(entity);
    }

}
