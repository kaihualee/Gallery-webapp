/**
 * 
 */
package org.gallery.persist;

import static org.bridj.Pointer.allocateDoubles;
import static org.bridj.Pointer.pointerToBytes;
import static org.bridj.Pointer.pointerToCString;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialException;

import static junit.framework.Assert.*;

import org.bridj.Pointer;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.model.ColorThemeEntity;
import org.gallery.web.vo.ColorThemeVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gallery.nativemethod.ImageConvertDllLibrary;

/**
 * @author Dahaka
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class ColorThemeDaoTest {

	@Autowired
	public ColorThemeDao colorThemeDao;

	@Test
	public void testSave() throws JsonGenerationException,
			JsonMappingException, IOException, SerialException, SQLException {
		String sourceFileName = "img/source.jpg";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(sourceFileName);
		File srcFile = new File(url.getFile());

		Pointer<Byte> srcImageName = pointerToCString(srcFile
				.getCanonicalPath());
		System.out.println("Read Image:" + srcImageName.toString());
		// Memory allocated from Java using Pointer.allocateXXX and
		// Pointer.pointerToXXX methods has known valid bounds. Pointers that
		// wrap direct NIO buffers also have known valid bounds that they take
		// from the buffer.
		int colorNum = ImageConvertDllLibrary.getColorNum(srcImageName);
		ByteBuffer rBuf = ByteBuffer.allocateDirect(colorNum);
		ByteBuffer gBuf = ByteBuffer.allocateDirect(colorNum);
		ByteBuffer bBuf = ByteBuffer.allocateDirect(colorNum);
		Pointer<Byte> r = pointerToBytes(rBuf);
		Pointer<Byte> g = pointerToBytes(gBuf);
		Pointer<Byte> b = pointerToBytes(bBuf);
		Pointer<Double> percent = allocateDoubles(colorNum);
		ImageConvertDllLibrary.getColorTheme(srcImageName, r, g, b, percent);

		// Serialize and Deserialize
		ColorThemeEntity entity = new ColorThemeEntity(colorNum, r, g, b,
				percent);
		System.out.println("Entity:" + entity.toString());
		colorThemeDao.save(entity);

		ColorThemeVO vo = new ColorThemeVO(entity);
		System.out.println("VO:" + vo.toString());
		;

	}
}
