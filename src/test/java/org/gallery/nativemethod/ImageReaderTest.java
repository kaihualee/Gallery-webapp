package org.gallery.nativemethod;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import org.apache.commons.lang.StringUtils;
import org.gallery.model.common.ThumbnailSize;
import org.imgscalr.Scalr;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ImageReaderTest {

	final String imgfilename = "img-test/050359.tif";

	private ClassLoader loader;
	private String baseFilePath;
	private File file;

	@Before
	public void setUp() throws IOException {
		loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(imgfilename);
		file = new File(url.getFile());
		baseFilePath = file.getParentFile().getCanonicalPath();
	}

	@Test
	public void testSave() throws IOException {

		String[] writerNames = ImageIO.getWriterFormatNames();
		System.out.println(Arrays.toString(writerNames));

		BufferedImage bufferImage = ImageIO.read(file);

		String formartName = imgfilename.substring(
				imgfilename.lastIndexOf('.') + 1, imgfilename.length());
		File out = new File(baseFilePath + File.separatorChar + "result."
				+ formartName);
		System.out.println(out.getAbsolutePath());
		File out2 = new File(baseFilePath + File.separatorChar + "result2."
				+ formartName);
		System.out.println(out2.getAbsolutePath());

		BufferedImage thumbnail = Scalr.resize(bufferImage,
				ThumbnailSize.SMALL_SIZE.getSize());
		BufferedImage thumbnail2 = Scalr.resize(bufferImage,
				ThumbnailSize.MEDIUM_SIZE.getSize());
		ImageIO.write(thumbnail, formartName, out);
		ImageIO.write(thumbnail2, formartName, out2);
	}

	@Test
	public void testResize() throws IOException {
		String[] originalFiles = { "050359.tif"};

		for (String originalFile : originalFiles) {
			String srcPath = baseFilePath + File.separatorChar + originalFile;

			String newFilenameBase = "-" + UUID.randomUUID().toString();
			String fileFormatName = "jpg";

			// Read Original Image
			BufferedImage originalImage = ImageIO.read(new File(srcPath));

			// Need to remove
			String storageDirectory = baseFilePath;

			// 该文件名 formatName fileName
			// Small Thumbnails
			BufferedImage thumbnail_small = Scalr.resize(originalImage,
					ThumbnailSize.SMALL_SIZE.getSize());
			ImageIO.write(thumbnail_small, fileFormatName, new File(
					storageDirectory + File.separatorChar + ThumbnailSize.SMALL_SIZE.getSize()
							+ newFilenameBase + "." + fileFormatName));

			// Medium Thumbnail
			BufferedImage thumbnail_medium = Scalr.resize(originalImage,
					ThumbnailSize.MEDIUM_SIZE.getSize());
			ImageIO.write(thumbnail_medium, fileFormatName, new File(
					storageDirectory + File.separatorChar + ThumbnailSize.MEDIUM_SIZE.getSize()
							+ newFilenameBase + "." + fileFormatName));

			// Big Thumbnails
			BufferedImage thumbnail_big = Scalr.resize(originalImage, ThumbnailSize.BIG_SIZE.getSize());
			compressImg(thumbnail_big, new File(storageDirectory
					+ File.separatorChar + ThumbnailSize.BIG_SIZE.getSize() + newFilenameBase + "."
					+ fileFormatName), 0.6);

		}
	}

	public static boolean compressImg(BufferedImage src, File outfile, double d) {
		Assert.assertNotNull(src);
		FileOutputStream out = null;
		ImageWriter imgWrier;
		ImageWriteParam imgWriteParams;

		// 指定写图片的方式为 jpg
		imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
		imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(
				null);
		// 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
		imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
		// 这里指定压缩的程度，参数qality是取值0~1范围内，
		imgWriteParams.setCompressionQuality((float) d);
		imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
		ColorModel colorModel = ColorModel.getRGBdefault();
		// 指定压缩时使用的色彩模式
		imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(
				colorModel, colorModel.createCompatibleSampleModel(16, 16)));

		try {
			out = new FileOutputStream(outfile);
			imgWrier.reset();
			// 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何
			// OutputStream构造
			imgWrier.setOutput(ImageIO.createImageOutputStream(out));
			// 调用write方法，就可以向输入流写图片
			imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void testCompressPic() {
		if (compressPic(baseFilePath + File.separator + "result.tif",
				baseFilePath + File.separator + "compressed.jpg")) {
			System.out.println("压缩成功！");
		} else {
			System.out.println("压缩失败！");
		}
	}

	public static boolean compressPic(String srcFilePath, String descFilePath) {
		System.out.println(String.format("%s->%s", srcFilePath, descFilePath));
		File file = null;
		BufferedImage src = null;
		FileOutputStream out = null;
		ImageWriter imgWrier;
		ImageWriteParam imgWriteParams;

		// 指定写图片的方式为 jpg
		imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
		imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(
				null);
		// 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
		imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
		// 这里指定压缩的程度，参数qality是取值0~1范围内，
		imgWriteParams.setCompressionQuality((float) 0.6);
		imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
		ColorModel colorModel = ColorModel.getRGBdefault();
		// 指定压缩时使用的色彩模式
		imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(
				colorModel, colorModel.createCompatibleSampleModel(16, 16)));

		try {
			if (StringUtils.isBlank(srcFilePath)) {
				return false;
			} else {
				// Read Image
				file = new File(srcFilePath);
				src = ImageIO.read(file);
				out = new FileOutputStream(descFilePath);

				imgWrier.reset();
				// 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何
				// OutputStream构造
				imgWrier.setOutput(ImageIO.createImageOutputStream(out));
				// 调用write方法，就可以向输入流写图片
				imgWrier.write(null, new IIOImage(src, null, null),
						imgWriteParams);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
