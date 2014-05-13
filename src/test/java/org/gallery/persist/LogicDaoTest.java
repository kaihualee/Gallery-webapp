/**
 * 
 */
package org.gallery.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.gallery.model.ImageEntity;
import org.gallery.model.common.PageBean;
import org.gallery.model.common.Status;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * @author Dahaka
 * 
 */
public class LogicDaoTest {

	private ImageDao imageDao;

	@Before
	public void setUp() throws DatabaseUnitException, SQLException, IOException {
		ApplicationContext ctx = new FileSystemXmlApplicationContext(
				"classpath:applicationContext-test.xml");
		Connection connection = DataSourceUtils.getConnection((DataSource) ctx
				.getBean("dataSource"));
		DatabaseConnection dbunitConnection = new DatabaseConnection(
				connection, null);
		IDataSet expectedDataSet = getDataSet("data-test/full-database.xml");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitConnection,
				expectedDataSet);
		imageDao = ctx.getBean(ImageDao.class);
	}

	@Test
	public void testGetById() {
		Long id = 1L;
		ImageEntity entity = imageDao.getById(id);
		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(Status.Normal, entity.getStatus());
		System.out.println(entity.toString());
		id = 2L;
		entity = imageDao.getById(id);
		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(Status.Disabled, entity.getStatus());
		System.out.println(entity.toString());
		id = 3L;
		entity = imageDao.getById(id);
		assertNull(entity);
	}

	@Test
	public void testGetByStatusAndId() {
		Status[] status = new Status[] { Status.Normal, Status.Deleted };
		Long id = 1L;
		ImageEntity entity = imageDao.getByStatusAndId(id, status);
		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(Status.Normal, entity.getStatus());

		id = 2L;
		entity = imageDao.getByStatusAndId(id, status);
		assertNull(entity);

		id = 3L;
		entity = imageDao.getByStatusAndId(id, status);
		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(Status.Deleted, entity.getStatus());

	}

	@Test
	public void testGetAllByStatus() {
		PageBean pageBean = new PageBean();
		int pageNum = 1;
		pageBean.setPageNum(pageNum);
		Status[] status = new Status[] { Status.Normal, Status.Deleted };
		List<ImageEntity> list = imageDao.getAllByStatus(pageBean, status);
		for (ImageEntity entity : list) {
			System.out.println(entity.toString());
		}
	}

	@Test
	public void testGetByName() {
		String name = "lkhsrc8small.jpg";
		ImageEntity entity = imageDao.getByName(name);
		assertNotNull(entity);
		assertEquals(new Long(1), entity.getId());
		System.out.println(entity.toString());
	}

	@Test
	public void testDeleteById() {
		Long id = 1L;
		ImageEntity entity = imageDao.getById(id);
		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(Status.Normal, entity.getStatus());
		System.out.println(entity.toString());

		imageDao.deleteById(id);
		entity = imageDao.getById(id);
		assertNull(entity);

	}

	@Test
	public void testGetAll() {
		PageBean pageBean = new PageBean();
		pageBean.setPageNum(1);
		List<ImageEntity> list = imageDao.getAll(pageBean);
		for (ImageEntity entity : list) {
			System.out.println(entity.toString());
		}
	}

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
		assertNotNull(inputStream);
		Reader reader = new InputStreamReader(inputStream);
		return new FlatXmlDataSet(reader);
	}
}
