package org.gallery.persist.common;

import java.util.List;

import org.gallery.common.LogicEntityImpl;
import org.gallery.common.Status;
import org.gallery.persist.utils.PageBean;

/**
 * @author likaihua
 */
public interface LogicDao<T extends LogicEntityImpl> extends BaseDao<T> {

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
	public T getByName(String name);

	/**
	 * 获取所有列表 默认Normal,Disable
	 */
	public List<T> getAll();

	/**
	 * @param name
	 * @return
	 */
	public List<T> getAllByName(String name);

	/**
	 * 根据状态集查询列表
	 * 
	 * @param status
	 * @return
	 */
	public List<T> getAllByStatus(Status... status);

	/**
	 * @param pageBean
	 * @param status
	 * @return
	 */
	public List<T> getAllByStatus(PageBean pageBean, Status... status);

	/**
	 * 删除某个实体 默认 逻辑删除
	 */
	public void deleteById(Long id);

}
