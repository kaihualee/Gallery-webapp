/**
 * 
 */
package org.gallery.utils;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * @author Dahaka
 * 
 */
public abstract class AbstractDbUnitTest {
	
	protected static ApplicationContext ctx;
	
	protected static DatabaseConnection dbunitConnection;
	@Before
	public void setUp() throws DatabaseUnitException, SQLException, IOException {
		ctx = new FileSystemXmlApplicationContext(
				"classpath:applicationContext-test.xml");
		Connection connection = DataSourceUtils.getConnection((DataSource) ctx
				.getBean("dataSource"));
		dbunitConnection = new DatabaseConnection(
				connection, null);
		IDataSet expectedDataSet = getDataSet("data-test/full-database.xml");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitConnection,
				expectedDataSet);
	}

	/**
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws DataSetException
	 */
	protected IDataSet getDataSet(String filename) throws IOException,
			DataSetException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = loader.getResourceAsStream(filename);
		assertNotNull(inputStream);
		Reader reader = new InputStreamReader(inputStream);
		return new FlatXmlDataSet(reader);
	}
	
	protected void saveDataSet(){
		
	}
	
	
}