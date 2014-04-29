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

import junit.framework.Assert;

import org.bridj.Pointer;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.model.ColorThemeEntity;
import org.gallery.web.vo.ColorThemeVO;
import org.junit.Before;
import org.junit.Test;

import com.gallery.nativemethod.ImageConvertDllLibrary;

/**
 * @author Dahaka
 * 
 */
public class ImageConvertTest {


	@Before
	public void tearUp() {
		System.out.println(System.getProperty("java.library.path"));
		System.out.println("------------------------------");
	}

	@Test
	public void testConvertImage() throws IOException {
		String sourceFileName = "img/source.jpg";
		String destFileName = "img/match.jpg";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(sourceFileName);
		File srcFile = new File(url.getFile());
		url = loader.getResource(destFileName);
		File destFile = new File(url.getFile());

		Pointer<Byte> srcImageName = pointerToCString(srcFile
				.getCanonicalPath());
		Pointer<Byte> destImageName = pointerToCString(destFile
				.getCanonicalPath());
		String outFileName = srcFile.getParentFile().getPath() + "\\out.jpg";
		Pointer<Byte> outImageName = pointerToCString(outFileName);
		ImageConvertDllLibrary.convertImage3(srcImageName, destImageName, 1,
				outImageName);
	}

	@Test
	public void testGetColorNum() throws IOException {
		String sourceFileName = "img/source.jpg";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(sourceFileName);
		File srcFile = new File(url.getFile());

		Pointer<Byte> srcImageName = pointerToCString(srcFile
				.getCanonicalPath());
		System.out.println("Read Image:" + srcImageName.toString());
		int colorNum = ImageConvertDllLibrary.getColorNum(srcImageName);
		System.out.println(colorNum);
	}

	@Test
	public void testGetColorTheme() throws JsonGenerationException,
			JsonMappingException, IOException, SerialException, SQLException {
		String sourceFileName = "img/source.jpg";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(sourceFileName);
		File file = new File(url.getFile());

		// Read Image
		System.out.println(file.getCanonicalPath());
		System.out.println(file.getCanonicalPath());
		Pointer<Byte> srcImageName = pointerToCString(file.getCanonicalPath());
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
		ColorThemeVO result = new ColorThemeVO(entity);
		System.out.println("Result:" + result.toString());
	}

	@Test
	public void testShift() {
		Byte a = 0x00;
		Byte r = 0x01;
		Byte g = 0x02;
		Byte b = 0x03;
		int rgb = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8)
				| ((b & 0xFF) << 0);
		System.out.printf("%#x", rgb);
	}
}
