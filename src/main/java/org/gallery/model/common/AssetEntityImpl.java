/**
 * 
 */
package org.gallery.model.common;

import org.gallery.model.AccountEntity;

/**
 * @author Dahaka
 * 
 */
public class AssetEntityImpl extends LogicEntityImpl {

	private AccountEntity owner;

	/**
	 * @return the owner
	 */
	public AccountEntity getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(AccountEntity owner) {
		this.owner = owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gallery.common.LogicEntityImpl#toString()
	 */
	public String toString() {
		return "[" + super.toString() + owner.toString() + "]";
	}

}
