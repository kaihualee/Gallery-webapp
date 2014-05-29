/**
 * 
 */
package org.gallery.basic;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.easymock.EasyMock;
import org.gallery.model.AccountEntity;
import org.gallery.model.ImageEntity;
import org.gallery.model.common.PageBean;
import org.gallery.persist.AccountDao;
import org.gallery.persist.ImageDao;
import org.gallery.utils.AbstractSpringDbUnitELTemplateTestCaseJUnit44;
import org.gallery.utils.BeanUtils;
import org.gallery.utils.DataSets;
import org.gallery.web.controller.ImageController;
import org.gallery.web.vo.ImageVO;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * @author Dahaka
 * 
 */
public class BasicTest extends AbstractSpringDbUnitELTemplateTestCaseJUnit44 {

	@Test
	@DataSets(setUpDataSet = "data-test/empty.xml", contextLocation = "applicationContext-test.xml")
	public void testReflect() throws NoSuchFieldException {

		ImageDao dao = EasyMock.createMock(ImageDao.class);
		ImageEntity entity = new ImageEntity();
		entity.setId(1L);
		entity.setName(UUID.randomUUID().toString());
		entity.setContentType("contetyep");
		entity.setNewFilename(UUID.randomUUID().toString());
		entity.setSize(12L);
		List<ImageEntity> list = new ArrayList<ImageEntity>();
		list.add(entity);
		
		
		expect(dao.getAll((PageBean) EasyMock.anyObject())).andReturn(list);
		EasyMock.replay(dao);
		
		ImageController controller = new ImageController();
		BeanUtils.forceSetProperty(controller, "imageDao", dao);
		List<ImageVO> result = controller.list(1);
		System.out.println(result.get(0).getUrl());
		
		EasyMock.verify(dao);
	}

	@Test
	@DataSets(setUpDataSet = "data-test/empty.xml", contextLocation = "applicationContext-test.xml")
	public void testDatabaseXml() throws DataSetException,
			FileNotFoundException, SQLException, IOException {
		saveDataSet("save-database.xml");
	}

	@Test
	@DataSets(setUpDataSet = "data-test/full-database-el.xml", contextLocation = "applicationContext-test.xml")
	public void testDbUnitEL() throws Exception {
		final AccountDao accountDao = ctx.getBean(AccountDao.class);
		AccountEntity account = new AccountEntity();
		String name = "myname";
		String email = "xxx@163.com";
		String password = "pwd";
		account.setName(name);
		account.setEmail(email);
		account.setPassword(password);
		account.setBirthday(new Date());
		accountDao.save(account);
		AccountEntity entity = accountDao.get(1L);
		System.out.println(entity.toString());

		ImageDao imageDao = getApplicationContext().getBean(ImageDao.class);
		PageBean pageBean = new PageBean();
		pageBean.setPageNum(1);
		List<ImageEntity> list = imageDao.getAll(pageBean);
		System.out.println(Arrays.toString(list.toArray()));

	}

	@Test
	@DataSets(setUpDataSet = "data-test/empty.xml")
	public void testCommonCodec() {
		String str = "abc";
		System.out.println(DigestUtils.md5Hex(str));

		System.out.println(DigestUtils.shaHex(str));

		// 加密
		byte[] b = Base64.encodeBase64(str.getBytes(), true);
		System.out.println("ecode:" + new String(b));

		// 解密
		byte[] br = Base64.decodeBase64(b);
		System.out.println("decode:" + new String(br));

	}

	public void testAccount() {

		AccountEntity account = new AccountEntity();
		String name = "myname";
		String email = "xxx@163.com";
		String password = "pwd";
		account.setName(name);
		account.setEmail(email);
		account.setPassword(password);
		account.setBirthday(new Date());
		ApplicationContext ctx = new FileSystemXmlApplicationContext(
				"classpath:applicationContext-test.xml");
		AccountDao accountDao = ctx.getBean(AccountDao.class);
		accountDao.save(account);
		assertNotNull(account);
		assertNotNull(account.getId());
		System.out.println(account.toString());
		AccountEntity account2 = new AccountEntity();
		account2.setId(1L);
		account2.setName(name);
		account2.setEmail("email");
		account2.setPassword(password);
		account2.setBirthday(new Date());
		accountDao.update(account2);

		AccountEntity entity = accountDao.get(1L);
		System.out.println(entity.toString());
	}

	public void testReplacementDataSet() throws Exception {
		ApplicationContext ctx = new FileSystemXmlApplicationContext(
				"classpath:applicationContext-test.xml");
		Connection connection = DataSourceUtils.getConnection((DataSource) ctx
				.getBean("dataSource"));
		DatabaseConnection dbunitConnection = new DatabaseConnection(
				connection, null);

		IDataSet actualDataSet = getDataSet("data-test/full-database.xml");
		actualDataSet = getReplaceDataSet(actualDataSet, 10);
		// DatabaseOperation.CLEAN_INSERT.execute(dbunitConnection,
		// actualDataSet);

		FlatXmlDataSet.write(actualDataSet, new FileOutputStream(
				"replacement-dataset.xml"));

	}

	protected IDataSet getReplaceDataSet(IDataSet originalDataSet, int id) {
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(
				originalDataSet);
		replacementDataSet.addReplacementObject("[ID]", id);
		return replacementDataSet;
	}

	public void testJson() throws JsonParseException, JsonMappingException,
			IOException, DatabaseUnitException, SQLException {
		ObjectMapper mapper = new ObjectMapper();
		String json = "{\"files\":[{\"url\":\"/picture/109\",\"thumbnailUrl\":\"/thumbnail/109\",\"deleteType\":\"DELETE\",\"deleteUrl\":\"/delete/109\",\"emotionUrl\":\"/emotion/109\",\"name\":\"\",\"id\":109,\"size\":44170}]}";
		Map<String, List<ImageVO>> result = mapper.readValue(json,
				new TypeReference<Map<String, List<ImageVO>>>() {
				});
		List<ImageVO> list = result.get("files");
		ImageVO vo = list.get(0);
		assertNotNull(vo);
		assertNotNull(vo.getId());
		System.out.println(vo.getId());
		System.out.println(vo.getThumbnailUrl());

		// DBunit Test
		ApplicationContext ctx = new FileSystemXmlApplicationContext(
				"classpath:applicationContext-test.xml");
		Connection connection = DataSourceUtils.getConnection((DataSource) ctx
				.getBean("dataSource"));
		DatabaseConnection dbunitConnection = new DatabaseConnection(
				connection, null);
		saveDataSet(dbunitConnection, "full-database.xml");

	}

	@Test
	@DataSets(setUpDataSet = "data-test/empty.xml")
	public void testsetupDatabase() throws Exception {
		ApplicationContext ctx = new FileSystemXmlApplicationContext(
				"classpath:applicationContext-test.xml");
		Connection connection = DataSourceUtils.getConnection((DataSource) ctx
				.getBean("dataSource"));
		DatabaseConnection dbunitConnection = new DatabaseConnection(
				connection, null);

		IDataSet expectedDataSet = getDataSet("data-test/full-database.xml");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitConnection,
				expectedDataSet);
		IDataSet actualDataSet = dbunitConnection.createDataSet();
		// Assertion.assertEquals(setupDataSet, actualDataSet);
		// Assertion.assertEqualsIgnoreCols(expectedDataSet, actualDataSet,
		// "image", new String[]{"id"});

		ImageDao imageDao = ctx.getBean(ImageDao.class);
		ImageEntity entity = imageDao.get(1L);
		System.out.println(entity.toString());

		// 1st method
		actualDataSet = dbunitConnection
				.createDataSet(new String[] { "image" });

		// filter 2nd method
		IDataSet filterDataSet = new FilteredDataSet(new String[] { "image" },
				dbunitConnection.createDataSet());

		// Filter dataset 3nd method
		QueryDataSet queryDataSet = new QueryDataSet(dbunitConnection);
		queryDataSet.addTable("image", "select * from image limit 10");
		FlatXmlDataSet.write(queryDataSet, new FileOutputStream(
				"query-dataset.xml"));
	}

	/**
	 * @param dbunitConnection
	 * @throws SQLException
	 * @throws DataSetException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void saveDataSet(DatabaseConnection dbunitConnection,
			String filename) throws SQLException, DataSetException,
			FileNotFoundException, IOException {
		IDataSet actualDataSet = dbunitConnection.createDataSet();
		ITableFilter filter = new DatabaseSequenceFilter(dbunitConnection);
		FilteredDataSet filteredDataSet = new FilteredDataSet(filter,
				actualDataSet);
		FileOutputStream xmlStream = new FileOutputStream(filename);
		FlatXmlDataSet.write(filteredDataSet, xmlStream);
	}

	// Set<String> keys = result.keySet();
	// StringBuffer str = new StringBuffer();
	// for (Iterator<String> it = keys.iterator(); it.hasNext();) {
	// str.append(result.get(it.next()).toString() + "\t");
	// }
	// System.out.println(str);
	// using mockobject
	// accountDao = easyMock.createMock(AccountDao.class) ;
	// accountService = new AccountServiceImpl(accountDao);
	//
	// Account account = new Account(TEST_ACCOUNT_NO, 100);
	// accountDao.findAccount(TEST_ACCOUNT_NO);
	// easyMock.expectLastCall().andReturn(account);
	// account.setBalance(150);
	// accountDao.updateAccount(account);
	// easyMock.replay();
	// accountService.deposit(TEST_ACCOUNT_NO, 50);
	// easyMock.verify();

}
