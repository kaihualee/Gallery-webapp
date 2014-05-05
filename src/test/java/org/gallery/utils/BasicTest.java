/**
 * 
 */
package org.gallery.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.gallery.model.ImageEntity;
import org.gallery.persist.ImageDao;
import org.gallery.web.vo.ImageVO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * @author Dahaka
 * 
 */
public class BasicTest {

	@Test
	public void testJson() throws JsonParseException, JsonMappingException,
			IOException, DatabaseUnitException, SQLException {
		ObjectMapper mapper = new ObjectMapper();
		String json = "{\"files\":[{\"url\":\"/picture/109\",\"thumbnailUrl\":\"/thumbnail/109\",\"deleteType\":\"DELETE\",\"deleteUrl\":\"/delete/109\",\"emotionUrl\":\"/emotion/109\",\"name\":\"\",\"id\":109,\"size\":44170}]}";
		Map<String, List<ImageVO>> result = mapper.readValue(json,
				new TypeReference<Map<String, List<ImageVO>>>() {
				});
		List<ImageVO> list = result.get("files");
		ImageVO vo = list.get(0);
		Assert.assertNotNull(vo);
		Assert.assertNotNull(vo.getId());
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
	public void testsetupDatabase() throws DatabaseUnitException, SQLException,
			IOException {
		ApplicationContext ctx = new FileSystemXmlApplicationContext(
				"classpath:applicationContext-test.xml");
		Connection connection = DataSourceUtils.getConnection((DataSource) ctx
				.getBean("dataSource"));
		DatabaseConnection dbunitConnection = new DatabaseConnection(
				connection, null);

		IDataSet expectedDataSet = getDataSet("data-test/full-database.xml");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitConnection, expectedDataSet);
		IDataSet actualDataSet = dbunitConnection.createDataSet();
		// Assertion.assertEquals(setupDataSet, actualDataSet);
		//Assertion.assertEqualsIgnoreCols(expectedDataSet, actualDataSet, "image", new String[]{"id"});
		
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

	/**
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws DataSetException
	 */
	private IDataSet getDataSet(String filename) throws IOException,
			DataSetException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = loader.getResourceAsStream(filename);
		Assert.assertNotNull(inputStream);
		Reader reader = new InputStreamReader(inputStream);
		return new FlatXmlDataSet(reader);
	}
}
