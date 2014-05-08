/**
 * 
 */
package org.gallery.persist.impl;

import java.util.List;

import org.gallery.common.Status;
import org.gallery.model.AccountEntity;
import org.gallery.persist.AccountDao;
import org.gallery.persist.common.LogicDaoImpl;
import org.gallery.persist.utils.PageBean;
import org.springframework.stereotype.Repository;

/**
 * @author Dahaka
 * 
 */
@Repository
public class AccountDaoImpl extends LogicDaoImpl<AccountEntity> implements
		AccountDao {

}
