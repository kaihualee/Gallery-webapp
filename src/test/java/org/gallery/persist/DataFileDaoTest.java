package org.gallery.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.UUID;

import org.gallery.model.DataFileEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
        String expected_filename = "myfilename.jpg";
        String expected_filetype = "image/jpg";
        String expected_key = UUID.randomUUID().toString();
        Long expected_size = 1024L;
        DataFileEntity entity = new DataFileEntity();
        entity.setName(expected_filename);
        entity.setKey(expected_key);
        entity.setContentType(expected_filetype);
        entity.setSize(expected_size);
        dataFileDao.save(entity);

        assertNotNull(entity.getId());
        assertNotNull(entity.getKey());
        assertEquals(expected_key, entity.getKey());
        assertNotNull(entity.getContentType());
        assertEquals(expected_filetype, entity.getContentType());
        assertNotNull(entity.getName());
        assertEquals(expected_filename, entity.getName());
        //assertNotNull(entity.getMd5Code());
        System.out.println(entity.toString());
    }

    public void testRead() {
        DataFileEntity entity = dataFileDao.get(1L);
        assertNotNull(entity);
        System.out.println(entity.toString());
    }
}
