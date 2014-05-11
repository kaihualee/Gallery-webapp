/**
 * 
 */
package org.gallery.persist.impl;

import org.gallery.model.AccountEntity;
import org.gallery.persist.AccountDao;
import org.gallery.persist.common.LogicDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * @author Dahaka
 * 
 */
@Repository
public class AccountDaoImpl extends LogicDaoImpl<AccountEntity> implements
		AccountDao {

}
