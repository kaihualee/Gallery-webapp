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
import java.net.URL;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.common.Status;
import org.gallery.model.ImageEntity;
import org.gallery.persist.utils.PageBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author likaihua
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
public class ImageDaoTest {

	@Autowired
	private ImageDao imageDao;

	final String jsonfilename = "data-test/imageEntity.json";
	private File file = null;

	@Before
	public void setUp() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(jsonfilename);
		file = new File(url.getFile());
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

	@Test
	@Transactional
	public void testGetPageBean() {
		PageBean pageBean = new PageBean();
		pageBean.setPageNum(1);
		List<ImageEntity> list = imageDao.getAll(pageBean);
		for (ImageEntity entity : list) {
			System.out.println(entity.toString());
		}
	}

	@Test
	@Transactional
	public void testDeleteById() throws JsonParseException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ImageEntity entity = mapper.readValue(file, ImageEntity.class);
		imageDao.save(entity);
		assertNotNull(entity.getId());

		Long id = entity.getId();
		imageDao.deleteById(id);
		ImageEntity result = imageDao.getById(id);
		assertNull(result);
		result = imageDao.get(id);
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals(id, result.getId());
		assertEquals(Status.Deleted, result.getStatus());
		System.out.println(result.toString());
	}
}
