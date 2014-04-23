package org.gallery.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.junit.Test;

public class ImageReaderTest {

	@Test
	public  void testSave() throws IOException{
		String formartName="tif";
		String[] writerNames = ImageIO.getWriterFormatNames();
		System.out.println(Arrays.toString(writerNames));
		String filePath="C:\\Users\\Dahaka\\workspace\\ImageConvertDll\\ImageConvertDll\\Release";
		String fileName="retrive.tif";
		BufferedImage bufferImage=ImageIO.read(new File(filePath+File.separatorChar+ fileName));
		
		File out = new File(filePath+File.separatorChar+"result."+formartName);
		System.out.println(out.getAbsolutePath());
		
		int width= 1000;
		int height=1000;
		BufferedImage thumbnail =  Scalr.resize(bufferImage,290);
		ImageIO.write(thumbnail,formartName,out );
	}
	
	@Test
	public void testDecode() throws UnsupportedEncodingException{
		//Unicode
		//byte[] bytes = "\\xE9\\x9D\\xA2\\xE6\\x96\\x99";
		//System.out.println(new String(bytes,"UTF-8"));
		
	}
}
