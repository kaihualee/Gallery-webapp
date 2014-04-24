/**
 * @(#)FileController.java, Mar 4, 2014. 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gallery.model.DataFileEntity;
import org.gallery.persist.DataFileDao;
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
public class FileController {
	private final static Logger log = LoggerFactory
			.getLogger(FileController.class);

	@Autowired
	@Value(value = "#{'${app.filesystem.dir}'}")
	private String fileUploadDirectory;

	@Autowired
	private DataFileDao dataFileDao;

	@RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
	public @ResponseBody
	Map upload(MultipartHttpServletRequest request, HttpServletResponse response) {

		log.info("uploadfile called...");
		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf;
		List<DataFileEntity> list = new LinkedList<>();

		while (itr.hasNext()) {
			mpf = request.getFile(itr.next());
			String originalFilename = mpf.getOriginalFilename();
			log.info("Uploading {}", originalFilename);

			String key = UUID.randomUUID().toString();
			String storageDirectory = fileUploadDirectory;

			File file = new File(storageDirectory + File.separatorChar + key);
			try {

				// Save Images
				mpf.transferTo(file);

				// Save FileEntity
				DataFileEntity dataFileEntity = new DataFileEntity();
				dataFileEntity.setName(mpf.getOriginalFilename());
				dataFileEntity.setKey(key);
				dataFileEntity.setSize(mpf.getSize());
				dataFileEntity.setContentType(mpf.getContentType());
				dataFileDao.save(dataFileEntity);
				list.add(dataFileEntity);
			} catch (IOException e) {
				log.error("Could not upload file " + originalFilename, e);
			}
		}
		Map<String, Object> files = new HashMap<>();
		files.put("files", list);
		return files;
	}

	/**
	 * Download file using filename
	 * 
	 * @param response
	 * @param filename
	 */
	@RequestMapping(value = "/downloadfilebyname/{name:.+}", params = { "attachment" }, method = RequestMethod.GET)
	public void downloadfilebyname(HttpServletResponse response,
			@PathVariable("name") String filename,
			@RequestParam("attachment") boolean attachment) {

		log.info("downloadfilebyname called...");
		String realPath = fileUploadDirectory + File.separator + filename;
		File file = new File(realPath);
		if (attachment)
			try {
				response.setHeader(
						"Content-Disposition",
						"attachment; filename="
								+ java.net.URLEncoder.encode(filename, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				log.error("Could not  encode " + filename, e1);
			}
		try {
			InputStream is = new FileInputStream(file);
			IOUtils.copy(is, response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Could not  download " + filename, e);
		}
	}

	@RequestMapping(value = "/downloadfile/{id}", params = { "attachment" }, method = RequestMethod.GET)
	public void download(HttpServletResponse response,
			@PathVariable("id") Long id,
			@RequestParam("attachment") boolean attachment) {

		log.info("downloadfile called...");
		DataFileEntity entity = dataFileDao.getById(id);
		if (entity == null) {
			return;
		} else {
			String realPath = fileUploadDirectory + File.separatorChar
					+ entity.getKey();
			File file = new File(realPath);
			response.setContentType(entity.getContentType());
			response.setContentLength(entity.getSize().intValue());
			if (attachment)
				try {
					response.setHeader(
							"Content-Disposition",
							"attachment; filename="
									+ java.net.URLEncoder.encode(
											entity.getName(), "UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					log.error("Could not  encode " + entity.getName(), e1);
				}
			try {
				InputStream is = new FileInputStream(file);
				IOUtils.copy(is, response.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Could not  download: " + entity.toString(), e);
			}
		}
	}
}
