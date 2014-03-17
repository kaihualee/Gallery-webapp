package org.gallery.persist;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.easymock.EasyMock;
import org.gallery.model.DataFileEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

/**
 * @author likaihua
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class DataFileDaoTest {

    @Autowired
    private DataFileDao dataFileDao;

    @Before
    public void init() throws FileNotFoundException {}

    @Test
    public void testSave() {
        String expected_filename = "filename";
        String expected_filetype = "img";
        String expected_key = UUID.randomUUID().toString();
        Long expected_size = 10L;
        DataFileEntity entity = new DataFileEntity();
        entity.setName(expected_filename);
        entity.setKey(expected_key);
        entity.setFileType(expected_filetype);
        entity.setSize(expected_size);
        dataFileDao.save(entity);

        assertNotNull(entity.getId());
        assertNotNull(entity.getKey());
        assertEquals(expected_key, entity.getKey());
        assertNotNull(entity.getFileType());
        assertEquals(expected_filetype, entity.getFileType());
        assertNotNull(entity.getName());
        assertEquals(expected_filename, entity.getName());
        //assertNotNull(entity.getMd5Code());
        System.out.println(entity.toString());
    }

    @Test
    public void testRead() {
        DataFileEntity entity = dataFileDao.get(1L);
        assertNotNull(entity);
        System.out.println(entity.toString());
    }
}
