/**
 * 
 */
package org.gallery.nativemethod;

import static org.bridj.Pointer.allocateDoubles;
import static org.bridj.Pointer.pointerToBytes;
import static org.bridj.Pointer.pointerToCString;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.rowset.serial.SerialException;

import org.bridj.Pointer;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.model.ColorThemeEntity;
import org.gallery.nativemethod.ImageConvertDllLibrary;
import org.gallery.web.vo.ColorThemeVO;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dahaka
 * 
 */
public class ImageConvertTest {

	private final static ObjectMapper mapper = new ObjectMapper();

	final String sourceFileName = "img-test/source.jpg";
	final String destFileName = "img-test/match.jpg";

	final String specialSourceFilename = "img-test/050359.tif";
	final String specialMatchFilename = "img-test/src10small.jpg";

	@Before
	public void setUp() {
		System.out.println(System.getProperty("java.library.path"));
		System.out.println("-----------------------------------------------");
	}

	@Test
	public void testConvertImage() throws IOException {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(sourceFileName);
		File srcFile = new File(url.getFile());
		url = loader.getResource(destFileName);
		File destFile = new File(url.getFile());

		Pointer<Byte> srcImageName = pointerToCString(srcFile
				.getCanonicalPath());
		Pointer<Byte> destImageName = pointerToCString(destFile
				.getCanonicalPath());
		String resultname = UUID.randomUUID().toString() + ".jpg";
		String outFileName = srcFile.getParentFile().getPath()
				+ File.separatorChar + resultname;
		Pointer<Byte> outImageName = pointerToCString(outFileName);
		int option = 1;
		ImageConvertDllLibrary.convertImage3(srcImageName, destImageName,
				option, outImageName);
		System.out.println("Save Image: " + outFileName);
	}

	@Test
	public void testGetColorNum() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(sourceFileName);
		File srcFile = new File(url.getFile());

		Pointer<Byte> srcImageName = pointerToCString(srcFile
				.getCanonicalPath());
		System.out.println("Read Image:" + srcFile.getCanonicalPath());
		int colorNum = ImageConvertDllLibrary.getColorNum(srcImageName);
		System.out.println("ColorNum: " + colorNum);
	}

	@Test
	public void testGetColorTheme() throws JsonGenerationException,
			JsonMappingException, IOException, SerialException, SQLException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(sourceFileName);
		File file = new File(url.getFile());

		// Read Image
		System.out.println("Read Image:" + file.getCanonicalPath());
		Pointer<Byte> srcImageName = pointerToCString(file.getCanonicalPath());
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
		System.out.println(mapper.writeValueAsString(result));
		System.out.println(entity.toString());
		System.out.println(result.toString());
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
