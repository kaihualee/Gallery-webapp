package org.gallery.persist.common;

import java.util.List;

import org.gallery.common.LogicEntityImpl;
import org.gallery.common.Status;

/**
 * @author likaihua
 */
public interface LogicDao<T extends LogicEntityImpl> extends BaseDao<T> {

    /**
     * 删除某个实体 默认 逻辑删除
     */
    public void deleteById(Long id);

    /**
     * 获取所有列表 默认Normal,Disable
     */
    public List<T> getAll();

    /**
     * 根据状态集查询列表
     * 
     * @param status
     * @return
     */
    public List<T> getAllByStatus(Status... status);

    /**
     * 根据ID查找实体 默认Normal, Disable
     * 
     * @param id
     * @return
     */
    public T getById(Long id);

    /**
     * 根据id,状态集查询实体
     * 
     * @param status
     * @return
     */
    public T getByStatusAndId(Long id, Status... status);

    /**
     * @param name
     * @return
     */
    public List<T> getAllByName(String name);

    /**
     * @param name
     * @return
     */
    public T getByName(String name);

}
