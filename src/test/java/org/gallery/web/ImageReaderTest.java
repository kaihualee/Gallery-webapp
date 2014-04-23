package org.gallery.web;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import org.apache.commons.lang.StringUtils;
import org.imgscalr.Scalr;
import org.junit.Test;

public class ImageReaderTest {

	private final String filePath="C:\\Users\\Dahaka\\workspace\\ImageConvertDll\\ImageConvertDll\\Release";
	
	private final int SMALL_SIZE=150;
	private final int MEDIA_SIZE=1024;
	private final int BIG_SIZE=2048;
	
	@Test
	public  void testSave() throws IOException{
		String formartName="tif";
		String[] writerNames = ImageIO.getWriterFormatNames();
		System.out.println(Arrays.toString(writerNames));
		String fileName="retrive.tif";
		BufferedImage bufferImage=ImageIO.read(new File(filePath+File.separatorChar+ fileName));
		
		File out = new File(filePath+File.separatorChar+"result."+formartName);
		System.out.println(out.getAbsolutePath());
		File out2 = new File(filePath+File.separatorChar+"result2."+formartName);
		System.out.println(out2.getAbsolutePath());
		
		int width= 1000;
		int height=1000;
		BufferedImage thumbnail =  Scalr.resize(bufferImage,1024);
		BufferedImage thumbnail2 =  Scalr.resize(bufferImage,2000);
		ImageIO.write(thumbnail,formartName,out );
		ImageIO.write(thumbnail2,formartName,out2 );
	}
	
	@Test
	public void testDecode() throws UnsupportedEncodingException{
		//Unicode
		//byte[] bytes = "\\xE9\\x9D\\xA2\\xE6\\x96\\x99";
		//System.out.println(new String(bytes,"UTF-8"));
		
	}
	
	@Test
    public  void testCompressPic()  
    {  
        if(compressPic(filePath+File.separator+"result.tif", filePath+File.separator+"compressed.jpg"))  
        {  
            System.out.println("压缩成功！");   
        }  
        else  
        {  
            System.out.println("压缩失败！");   
        }  
    }  
      
  
    public static boolean compressPic(String srcFilePath, String descFilePath)  
    {  
    	System.out.println(String.format("%s->%s", srcFilePath, descFilePath));
        File file = null;  
        BufferedImage src = null;  
        FileOutputStream out = null;  
        ImageWriter imgWrier;  
        ImageWriteParam imgWriteParams;  
  
        // 指定写图片的方式为 jpg  
        imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();  
        imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);  
        // 要使用压缩，必须指定压缩方式为MODE_EXPLICIT  
        imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);  
        // 这里指定压缩的程度，参数qality是取值0~1范围内，  
        imgWriteParams.setCompressionQuality((float)0.6);  
        imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);  
        ColorModel colorModel = ColorModel.getRGBdefault();  
        // 指定压缩时使用的色彩模式  
        imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel  
                .createCompatibleSampleModel(16, 16)));  
  
        try  
        {  
            if(StringUtils.isBlank(srcFilePath))  
            {  
                return false;  
            }  
            else  
            {  
            	//Read Image
                file = new File(srcFilePath);  
                src = ImageIO.read(file);  
                out = new FileOutputStream(descFilePath);  
  
                imgWrier.reset();  
                // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何 OutputStream构造  
                imgWrier.setOutput(ImageIO.createImageOutputStream(out));  
                // 调用write方法，就可以向输入流写图片  
                imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);  
                out.flush();  
                out.close();  
            }  
        }  
        catch(Exception e)  
        {  
            e.printStackTrace();  
            return false;  
        }  
        return true;  
    }  
}
