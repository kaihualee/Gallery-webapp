package org.gallery.persist.common;

import java.util.List;

import org.gallery.model.common.BaseEntity;
import org.gallery.model.common.PageBean;

public interface BaseDao<T extends BaseEntity> {

    public T get(Long id);

    public List<T> getAll();

    public List<T> getAll(PageBean pageBean);

    public void merge(T entity);

    public void refresh(T entity);

    public void remove(List<T> entityList);

    public void remove(T entity);

    public int removeAll();

    public void removeById(Long id);

    public void save(T entity);

    public void saveOrUpdate(T entity);

    public void update(T entity);

}
