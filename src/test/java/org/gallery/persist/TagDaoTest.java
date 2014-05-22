/**
 * 
 */
package org.gallery.persist;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dbunit.dataset.DataSetException;
import org.gallery.model.AccountEntity;
import org.gallery.model.TagEntity;
import org.gallery.utils.AbstractSpringDbUnitELTemplateTestCaseJUnit44;
import org.gallery.utils.DataSets;
import org.junit.Test;

/**
 * @author Dahaka
 * 
 */
public class TagDaoTest extends AbstractSpringDbUnitELTemplateTestCaseJUnit44 {

	private TagDao tagDao;
	private AccountDao accountDao;

	@Test
	@DataSets(setUpDataSet = "data-test/tag-database.xml", contextLocation = "applicationContext-test.xml")
	public void testSave() throws DataSetException, FileNotFoundException,
			SQLException, IOException {

		tagDao = getApplicationContext().getBean(TagDao.class);
		accountDao = getApplicationContext().getBean(AccountDao.class);
		AccountEntity owner = accountDao.getById(1L);

		TagEntity tag1 = new TagEntity();
		String name = "tag1";
		tag1.setName(name);
		tag1.setOwner(owner);
		tagDao.save(tag1);
		System.out.println(tag1.toString());

		name = "tag2";
		TagEntity tag2 = new TagEntity();
		tag2.setName(name);
		tag2.setOwner(owner);
		tagDao.save(tag2);
		System.out.println(tag2.toString());

		name = "tag3";
		TagEntity tag3 = new TagEntity();
		tag3.setName(name);
		tag3.setOwner(owner);
		tagDao.save(tag3);
		System.out.println(tag3.toString());

		Set<TagEntity> list = new HashSet<TagEntity>();
		list.add(tag3);
		list.add(tag2);
		tag1.setChildren(list);
		tagDao.saveOrUpdate(tag1);

		// Read roo tags
		List<TagEntity> rootTags = tagDao.getRootTags(owner.getId());
		System.out.println(Arrays.toString(rootTags.toArray()));
		saveDataSet("tag-dataset.xml");

	}
}
