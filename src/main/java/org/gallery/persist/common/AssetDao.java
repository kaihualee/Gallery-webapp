/**
 * 
 */
package org.gallery.persist.common;

import java.util.List;

import org.gallery.model.common.AssetEntityImpl;
import org.gallery.model.common.PageBean;
import org.gallery.model.common.Status;

/**
 * @author Dahaka
 * 
 */
public interface AssetDao<T extends AssetEntityImpl> extends
		LogicDao<T> {

	/**
	 * @param ownerId
	 * @param id
	 * @return
	 */
	public T getById(Long ownerId, Long id);

	/**
	 * @param ownerId
	 * @param id
	 * @param status
	 * @return
	 */
	public T getByStatusAndId(Long ownerId, Long id, Status... status);

	/**
	 * @param ownerId
	 * @return
	 */
	public T getByName(Long ownerId, String name);

	/**
	 * @param ownerId
	 * @return
	 */
	public List<T> getAll(Long ownerId);

	/**
	 * @param ownerId
	 * @param name
	 * @return
	 */
	public List<T> getAllByName(Long ownerId, String name);

	/**
	 * @param ownerId
	 * @param status
	 * @return
	 */
	public List<T> getAllByStatus(Long ownerId, Status... status);

	/**
	 * @param ownerId
	 * @param pageBean
	 * @param status
	 * @return
	 */
	public List<T> getAllByStatus(Long ownerId, PageBean pageBean,
			Status... status);

	/**
	 * @param ownerId
	 * @param id
	 */
	public void deleteById(Long ownerId, Long id);

}
