/**
 * @(#)ImageController.java, 2014骞�鏈�7鏃� 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.web.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.model.ImageEntity;
import org.gallery.persist.ImageDao;
import org.gallery.persist.utils.PageBean;
import org.gallery.web.utils.EmotionParser;
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

	final String thumbnailFormatName = "jpg";

	final String THUMBNAILNAME_SUFFIX = "-thumbnail." + thumbnailFormatName;

	final int THUMBNAIL_SIZE = 150;

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

			String newFilenameBase = UUID.randomUUID().toString();
			String originalFileExtension = originalFilename
					.substring(originalFilename.lastIndexOf("."));
			String newFilename = newFilenameBase + originalFileExtension;
			String storageDirectory = fileUploadDirectory;
			String contentType = mpf.getContentType();

			File newFile = new File(storageDirectory + File.separatorChar
					+ newFilename);
			try {
				mpf.transferTo(newFile);

				BufferedImage thumbnail = Scalr.resize(ImageIO.read(newFile),
						THUMBNAIL_SIZE);
				String thumbnailFilename = newFilenameBase
						+ THUMBNAILNAME_SUFFIX;
				File thumbnailFile = new File(storageDirectory
						+ File.separatorChar + thumbnailFilename);
				ImageIO.write(thumbnail, thumbnailFormatName, thumbnailFile);

				log.info("EmotionParser...");
				// ImageEntity entity =
				// EmotionParser.parse(ImageIO.read(newFile));
				ImageEntity entity = new ImageEntity();
				entity.setName(originalFilename);
				// entity.setThumbnailFilename(thumbnailFilename);
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
		File imageFile = new File(fileUploadDirectory + File.separatorChar
				+ entity.getNewFilename());
		response.setContentType(entity.getContentType());
		response.setContentLength(entity.getSize().intValue());
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
		String thumbnailFilename = entity.getNewFilename().substring(0,
				entity.getNewFilename().lastIndexOf('.'))
				+ THUMBNAILNAME_SUFFIX;
		File imageFile = new File(fileUploadDirectory + File.separatorChar
				+ thumbnailFilename);

		response.setContentType("image" + File.separatorChar
				+ thumbnailFormatName);
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
				entity.getNewFilename().lastIndexOf('.'))
				+ THUMBNAILNAME_SUFFIX;
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

	@RequestMapping(value = "/convert", params = { "id1", "id2" }, method = RequestMethod.GET)
	@ResponseBody
	public Map convert(@RequestParam("id1") long srcId,
			@RequestParam("id2") long destId) {
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
}
