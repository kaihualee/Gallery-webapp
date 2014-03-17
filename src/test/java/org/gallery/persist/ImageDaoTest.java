/**
 * @(#)ImageDaoTest.java, 2014年3月17日. 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.persist;

import java.util.UUID;

import org.gallery.model.ImageEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    
    final String expected_name = "jdei";
    final String expected_contentType = "png";
    final Long expected_size =17148484L;
    final String expected_newFilename = UUID.randomUUID().toString();
    
    @Test
    public void testSave(){
        ImageEntity entity = new ImageEntity();
        entity.setName(expected_name);
        entity.setContentType(expected_contentType);
        entity.setSize(expected_size);
        entity.setNewFilename(expected_newFilename);
        imageDao.save(entity);
        System.out.println(entity.toString());
    }
}
