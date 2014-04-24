package org.gallery.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.gallery.common.ThumbnailSize;
import org.gallery.web.controller.ImageController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:spring-servlet.xml" })
public class ImageControllerTest {

	@Autowired
	private ImageController controller;

	@Test
	public void testConvert() {
		ThumbnailSize Thumbnail_Enum = ThumbnailSize.MEDIUM_SIZE;
		int srcId = 5;
		int destId = 6;
		int option = 1;
		controller.convert(srcId, destId, option, Thumbnail_Enum.getId());
	}

	@Test
	public void testThumbnail() throws FileNotFoundException {
		for(ThumbnailSize Thumbnail_Enum : ThumbnailSize.values()){
			Long id = 6L;
			//controller.thumbnail(null, id, Thumbnail_Enum.getId());
		}
	}
	
	@Test
	public void testPicure(){
		Long id = 12L;
		//controller.picture(null, id);
	}

}
