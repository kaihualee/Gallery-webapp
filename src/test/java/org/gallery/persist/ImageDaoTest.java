/**
 * @(#)ImageDaoTest.java, 2014年3月17日. 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author likaihua
 * 
 */

public class ImageDaoTest {

	private ImageDao imageDao;

	final String jsonfilename = "data-test/imageEntity.json";
	private File file = null;

	@Before
	public void setUp() throws DatabaseUnitException, SQLException, IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(jsonfilename);
		file = new File(url.getFile());

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
	public void testJson() throws JsonParseException, JsonMappingException,
			IOException {
		ObjectMapper mapper = new ObjectMapper();
		ImageEntity entity = mapper.readValue(file, ImageEntity.class);
		System.out.println(entity.toString());
	}

	@Test
	@Transactional
	public void testSave() throws JsonParseException, JsonMappingException,
			IOException {
		ObjectMapper mapper = new ObjectMapper();
		ImageEntity entity = mapper.readValue(file, ImageEntity.class);
		imageDao.save(entity);
		assertNotNull(entity.getId());
		System.out.println(entity.toString());
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

	// @Test
	// @Transactional
	// public void testDeleteById() throws JsonParseException,
	// JsonMappingException, IOException {
	// ObjectMapper mapper = new ObjectMapper();
	// ImageEntity entity = mapper.readValue(file, ImageEntity.class);
	// imageDao.save(entity);
	// assertNotNull(entity.getId());
	//
	// Long id = entity.getId();
	// imageDao.deleteById(id);
	// ImageEntity result = imageDao.getById(id);
	// assertNull(result);
	// result = imageDao.get(id);
	// assertNotNull(result);
	// assertNotNull(result.getId());
	// assertEquals(id, result.getId());
	// assertEquals(Status.Deleted, result.getStatus());
	// System.out.println(result.toString());
	// }
}
