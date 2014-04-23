/**
 * @(#)ImageController.java, 2014骞�鏈�7鏃� 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.web.controller;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.common.ThumbnailSize;
import org.gallery.model.ImageEntity;
import org.gallery.persist.ImageDao;
import org.gallery.persist.utils.PageBean;
import org.gallery.web.vo.ImageVO;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @author likaihua
 */
@Controller
@RequestMapping
public class ImageController {
	private static final Logger log = LoggerFactory
			.getLogger(ImageController.class);

	@Autowired
	private ImageDao imageDao;

	@Autowired
	@Value(value = "#{'${app.filesystem.dir}'}")
	private String fileUploadDirectory;

	@Autowired
	@Value(value = "#{'${cmd}'}")
	private String cmd;

	final String BASE_URL = "../action";

	private final static ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public @ResponseBody
	List list(@RequestParam("pageNum") int pageNum) {
		log.debug("uploadGet called");
		PageBean pageBean = new PageBean();
		pageBean.setPageNum(pageNum);
		List<ImageEntity> images = imageDao.getAll(pageBean);
		List<ImageVO> list = new ArrayList<>();
		for (ImageEntity entity : images) {
			ImageVO imageVO = new ImageVO(entity);
			imageVO.setId(entity.getId());
			imageVO.setUrl(BASE_URL + "/picture/" + entity.getId());
			imageVO.setThumbnailUrl(BASE_URL + "/thumbnail/" + entity.getId());
			imageVO.setDeleteUrl(BASE_URL + "/delete/" + entity.getId());
			imageVO.setDeleteType("DELETE");

			list.add(imageVO);
			log.info(imageVO.toString());
		}
		log.debug("Returning: {}", list);
		return list;
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody
	Map upload(MultipartHttpServletRequest request, HttpServletResponse response) {
		log.info("UploadPost called...");
		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf;
		List<ImageVO> list = new LinkedList<>();

		while (itr.hasNext()) {
			mpf = request.getFile(itr.next());
			String originalFilename = mpf.getOriginalFilename();
			log.info("Uploading {}", originalFilename);

			String newFilenameBase = "-" + UUID.randomUUID().toString();
			String storageDirectory = fileUploadDirectory;
			String contentType = mpf.getContentType();
			String newFilename = newFilenameBase;

			try {
				// Save Images
				// mpf.transferTo(newFile);
				BufferedImage originalImage = ImageIO
						.read(new ByteArrayInputStream(mpf.getBytes()));

				// Small Thumbnails
				BufferedImage thumbnail_small = Scalr.resize(originalImage,
						ThumbnailSize.SMALL_SIZE.getSize());
				compressImg(thumbnail_small,
						new File(storageDirectory + File.separatorChar
								+ ThumbnailSize.SMALL_SIZE.getId()
								+ newFilenameBase + "."
								+ ThumbnailSize.SMALL_SIZE.getFormatName()),
						ThumbnailSize.SMALL_SIZE.getCompressionQuality());

				// Medium Thumbnail
				BufferedImage thumbnail_medium = Scalr.resize(originalImage,
						ThumbnailSize.MEDIUM_SIZE.getSize());
				compressImg(thumbnail_medium,
						new File(storageDirectory + File.separatorChar
								+ ThumbnailSize.MEDIUM_SIZE.getId()
								+ newFilenameBase + "."
								+ ThumbnailSize.MEDIUM_SIZE.getFormatName()),
						ThumbnailSize.MEDIUM_SIZE.getCompressionQuality());

				// Big Thumbnails
				BufferedImage thumbnail_big = Scalr.resize(originalImage,
						ThumbnailSize.BIG_SIZE.getSize());
				compressImg(thumbnail_big,
						new File(storageDirectory + File.separatorChar
								+ ThumbnailSize.BIG_SIZE.getId()
								+ newFilenameBase + "."
								+ ThumbnailSize.BIG_SIZE.getFormatName()),
						ThumbnailSize.BIG_SIZE.getCompressionQuality());

				log.info("EmotionParser...");
				// ImageEntity entity =
				// EmotionParser.parse(ImageIO.read(newFile));
				ImageEntity entity = new ImageEntity();
				entity.setName(originalFilename);
				entity.setNewFilename(newFilename);
				entity.setContentType(contentType);
				entity.setSize(mpf.getSize());
				imageDao.save(entity);

				ImageVO imageVO = new ImageVO(entity);
				imageVO.setId(entity.getId());
				imageVO.setUrl(BASE_URL + "/picture/" + entity.getId());
				imageVO.setThumbnailUrl(BASE_URL + "/thumbnail/"
						+ entity.getId());
				imageVO.setDeleteUrl(BASE_URL + "/delete/" + entity.getId());
				imageVO.setEmotionUrl(BASE_URL + "/emotion/" + entity.getId());
				imageVO.setDeleteType("DELETE");

				list.add(imageVO);

			} catch (IOException e) {
				log.error("Could not upload file " + originalFilename, e);
			}
		}
		Map<String, Object> files = new HashMap<>();
		files.put("files", list);
		return files;
	}

	@RequestMapping(value = "/picture/{id}", method = RequestMethod.GET)
	public void picture(HttpServletResponse response, @PathVariable Long id) {
		ImageEntity entity = imageDao.get(id);
		ThumbnailSize THUMBNAL_ENUM = ThumbnailSize.MEDIUM_SIZE;
		String thumbnailFilename = THUMBNAL_ENUM.getId()
				+ entity.getNewFilename() + "." + THUMBNAL_ENUM.getFormatName();

		File imageFile = new File(fileUploadDirectory + File.separatorChar
				+ thumbnailFilename);

		response.setContentType("image/" + THUMBNAL_ENUM.getFormatName());
		response.setContentLength((int) imageFile.length());
		try {
			InputStream is = new FileInputStream(imageFile);
			IOUtils.copy(is, response.getOutputStream());
		} catch (IOException e) {
			log.error("Could not show picture " + id, e);
		}
	}

	@RequestMapping(value = "/thumbnail/{id}", method = RequestMethod.GET)
	public void thumbnail(HttpServletResponse response, @PathVariable Long id) {
		ImageEntity entity = imageDao.get(id);
		ThumbnailSize THUMBNAL_ENUM = ThumbnailSize.SMALL_SIZE;
		String thumbnailFilename = THUMBNAL_ENUM.getId()
				+ entity.getNewFilename() + "." + THUMBNAL_ENUM.getFormatName();

		File imageFile = new File(fileUploadDirectory + File.separatorChar
				+ thumbnailFilename);

		response.setContentType("image/" + THUMBNAL_ENUM.getFormatName());
		response.setContentLength((int) imageFile.length());
		try {
			InputStream is = new FileInputStream(imageFile);
			IOUtils.copy(is, response.getOutputStream());
		} catch (IOException e) {
			log.error("Could not show thumbnail:" + id, e);
		}
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	List delete(@PathVariable Long id) {
		ImageEntity entity = imageDao.get(id);
		File imageFile = new File(fileUploadDirectory + File.separatorChar
				+ entity.getNewFilename());
		imageFile.delete();

		String thumbnailFilename = entity.getNewFilename().substring(0,
				entity.getNewFilename().lastIndexOf('.'));
		File thumbnailFile = new File(fileUploadDirectory + File.separatorChar
				+ thumbnailFilename);
		thumbnailFile.delete();
		imageDao.deleteById(entity.getId());
		List<Map<String, Object>> results = new ArrayList<>();
		Map<String, Object> success = new HashMap<>();
		success.put("success", true);
		results.add(success);
		return results;
	}

	@RequestMapping(value = "/emotion/{id}", method = RequestMethod.GET)
	public @ResponseBody
	Map emotion(@PathVariable Long id) {
		ImageEntity entity = imageDao.get(id);

		Map<String, Object> results = new HashMap<>();
		float[] heats = null;
		float[] activities = null;
		float[] weights = null;
		try {
			heats = mapper.readValue(entity.getHeats().getBinaryStream(),
					float[].class);
			activities = mapper.readValue(entity.getActivities()
					.getBinaryStream(), float[].class);
			weights = mapper.readValue(entity.getWeights().getBinaryStream(),
					float[].class);
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results.put("activities", activities);
		results.put("weights", weights);
		results.put("heats", heats);
		log.debug(Arrays.toString(activities));
		return results;
	}

	@RequestMapping(value = "/convert", params = { "id1", "id2", "option" }, method = RequestMethod.GET)
	@ResponseBody
	public Map convert(@RequestParam("id1") long srcId,
			@RequestParam("id2") long destId, @RequestParam("option") int option) {
		String srcImagePath = fileUploadDirectory + File.separator
				+ imageDao.get(srcId).getNewFilename();
		String destImagePath = fileUploadDirectory + File.separator
				+ imageDao.get(destId).getNewFilename();
		String fileName = imageDao.get(srcId).getNewFilename();
		String fileFormatName = fileName.substring(
				fileName.lastIndexOf(".") + 1, fileName.length());
		String cmdLine = "";
		try {
			cmdLine = cmd + " " + srcImagePath + " " + destImagePath;
			log.info("cmdLine:" + cmdLine);
			java.lang.Process process = Runtime.getRuntime().exec(cmdLine);
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("conver?id1=" + srcId + "id2=" + destId, e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.error("exec " + cmdLine + " failed!", e);
			e.printStackTrace();
		}
		Map<String, String> success = new HashMap<String, String>();
		success.put("filename", "converted." + fileFormatName);
		return success;
	}

	@RequestMapping(value = "/download/{name:.+}", method = RequestMethod.GET)
	public void download(HttpServletResponse response,
			@PathVariable("name") String name) {
		String realPath = fileUploadDirectory + File.separator + name;
		File imageFile = new File(realPath);
		response.setContentType("image/png");
		response.setContentLength((int) imageFile.length());
		try {
			InputStream is = new FileInputStream(imageFile);
			IOUtils.copy(is, response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Could not  download " + name, e);
		}
	}

	public static boolean compressImg(BufferedImage src, File outfile, double d) {
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
}
