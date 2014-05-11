/**
 * 
 */
package org.gallery.persist;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.gallery.model.AccountEntity;
import org.gallery.utils.AbstractSpringDbUnitELTemplateTestCaseJUnit44;
import org.gallery.utils.DataSets;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Dahaka
 * 
 */
public class AccountDaoTest extends AbstractSpringDbUnitELTemplateTestCaseJUnit44 {

	private AccountDao accountDao;

	@Test
	@DataSets(setUpDataSet = "data-test/empty.xml", contextLocation = "applicationContext-test.xml")
	public void testSave() {
		accountDao = getApplicationContext().getBean(AccountDao.class);
		AccountEntity account = new AccountEntity();
		String name = "likaihua" + UUID.randomUUID().toString();
		String email = "likaihua@163.com";
		String password = DigestUtils.shaHex("password");
		account.setName(name);
		account.setEmail(email);
		account.setPassword(password);
		account.setBirthday(new Date());
		accountDao.save(account);
		AccountEntity entity = accountDao.getByName(name);
		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(name, entity.getName());
		System.out.println(entity.toString());
	}
}
