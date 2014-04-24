/**
 * @(#)ImageDaoTest.java, 2014年3月17日. 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.persist;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import org.gallery.model.ImageEntity;
import org.gallery.persist.utils.PageBean;
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
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class ImageDaoTest {

	@Autowired
	private ImageDao imageDao;

	String expected_name = "勉励";
	final String expected_contentType = "image/png";
	final Long expected_size = 2048L;
	final String expected_newFilename = UUID.randomUUID().toString();

	@Test
	@Transactional
	public void testSave() throws UnsupportedEncodingException {
		ImageEntity entity = new ImageEntity();
		entity.setName(expected_name);
		entity.setContentType(expected_contentType);
		entity.setSize(expected_size);
		entity.setNewFilename(expected_newFilename);
		imageDao.save(entity);
		System.out.println(entity.toString());
	}

	@Test
	public void testGetPageBean() {
		PageBean pageBean = new PageBean();
		pageBean.setPageNum(1);
		List<ImageEntity> list = imageDao.getAll(pageBean);
		for (ImageEntity entity : list) {
			System.out.println(entity.toString());
		}
	}
}
