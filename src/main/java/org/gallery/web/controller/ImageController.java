/**
 * @(#)ImageController.java, 2014骞�鏈�7鏃� 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.web.controller;

import static org.bridj.Pointer.allocateDoubles;
import static org.bridj.Pointer.pointerToBytes;
import static org.bridj.Pointer.pointerToCString;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
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
import org.bridj.Pointer;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.common.ThumbnailSize;
import org.gallery.model.ColorThemeEntity;
import org.gallery.model.ImageEntity;
import org.gallery.nativemethod.ImageConvertDllLibrary;
import org.gallery.persist.ImageDao;
import org.gallery.persist.utils.PageBean;
import org.gallery.web.vo.ColorThemeVO;
import org.gallery.web.vo.ImageVO;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
	protected ImageDao imageDao;

	@Autowired
	@Value(value = "#{'${spring.app.filesystem.dir}'}")
	protected String fileUploadDirectory;


	protected final static ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = "/list", method = RequestMethod.GET)
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
			imageVO.setUrl("/picture/" + entity.getId());
			imageVO.setThumbnailUrl("/thumbnail/" + entity.getId());
			imageVO.setDeleteUrl("/delete/" + entity.getId());
			imageVO.setDeleteType("DELETE");
			list.add(imageVO);
			log.info(imageVO.toString());
		}
		log.debug("Returning: {}", list);
		return list;
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody
	Map upload(MultipartHttpServletRequest request, HttpServletResponse response)
			throws IOException {
		log.info("UploadPost called...");
		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf;
		List<ImageVO> list = new LinkedList<>();

		while (itr.hasNext()) {
			mpf = request.getFile(itr.next());
			String originalFilename = mpf.getOriginalFilename();
			if(originalFilename == null || StringUtils.isEmpty(originalFilename)){
				originalFilename = UUID.randomUUID().toString()+".jpg";
			}
			log.info("Uploading {}", originalFilename);

			String newFilenameBase = "-" + UUID.randomUUID().toString();
			String storageDirectory = fileUploadDirectory;
			String contentType = mpf.getContentType();
			if(contentType == null || StringUtils.isEmpty(contentType)){
				contentType = "image/jpeg";
			}
			String newFilename = newFilenameBase;
			InputStream in = null;
			try {
				// Save Images
				// mpf.transferTo(newFile);
				in = new ByteArrayInputStream(mpf.getBytes());
				BufferedImage originalImage = ImageIO.read(in);

				// Save Original Image
				String filenameExtension = originalFilename.substring(
						originalFilename.lastIndexOf(".") + 1,
						originalFilename.length());
				ImageIO.write(originalImage, filenameExtension, new File(
						storageDirectory + File.separatorChar
								+ originalFilename));

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
				imageVO.setUrl("/picture/" + entity.getId());
				imageVO.setThumbnailUrl("/thumbnail/" + entity.getId());
				imageVO.setDeleteUrl("/delete/" + entity.getId());
				imageVO.setEmotionUrl("/emotion/" + entity.getId());
				imageVO.setDeleteType("DELETE");

				list.add(imageVO);
			} catch (IOException e) {
				log.error("Could not upload file " + originalFilename, e);
			} finally {
				in.close();
			}
		}
		Map<String, Object> files = new HashMap<>();
		files.put("files", list);
		return files;
	}

	@RequestMapping(value = "/picture/{id}", method = RequestMethod.GET)
	public void picture(HttpServletResponse response, @PathVariable Long id) {
		ImageEntity entity = imageDao.getById(id);
		String thumbnailFilename = entity.getName();

		File imageFile = new File(fileUploadDirectory + File.separatorChar
				+ thumbnailFilename);

		response.setContentType(entity.getContentType());
		response.setContentLength((int) imageFile.length());
		try {
			InputStream is = new FileInputStream(imageFile);
			IOUtils.copy(is, response.getOutputStream());
			// IOUtils.copy(is,new
			// FileOutputStream("c:\\tmp\\picture-"+thumbnailFilename));
		} catch (IOException e) {
			log.error("Could not show picture " + id, e);
		}
	}

	@RequestMapping(value = "/thumbnail/{id}", params = { "size" }, method = RequestMethod.GET)
	public void thumbnail(HttpServletResponse response, @PathVariable Long id,
			@RequestParam("size") int size) {
		ImageEntity entity = imageDao.getById(id);
		ThumbnailSize thumbnail_Enum = ThumbnailSize.valueOf(size);
		log.debug("size: %d", new Object[] { thumbnail_Enum.getSize() });
		String thumbnailFilename = thumbnail_Enum.getId()
				+ entity.getNewFilename() + "."
				+ thumbnail_Enum.getFormatName();

		File imageFile = new File(fileUploadDirectory + File.separatorChar
				+ thumbnailFilename);

		response.setContentType("image/" + thumbnail_Enum.getFormatName());
		response.setContentLength((int) imageFile.length());
		try {
			InputStream is = new FileInputStream(imageFile);
			IOUtils.copy(is, response.getOutputStream());
			// IOUtils.copy(is, new
			// FileOutputStream("c:\\tmp\\thumbnail-"+thumbnail_Enum.getId()+"."+thumbnail_Enum.getFormatName()));
		} catch (IOException e) {
			log.error("Could not show thumbnail:" + id, e);
		}
	}

	/**
	 * Download file using filename
	 * 
	 * @param response
	 * @param filename
	 */
	@RequestMapping(value = "/download/{name:.+}", params = { "attachment" }, method = RequestMethod.GET)
	public void download(HttpServletResponse response,
			@PathVariable("name") String filename,
			@RequestParam("attachment") boolean attachment) {
		String realPath = fileUploadDirectory + File.separator + filename;
		File imageFile = new File(realPath);
		response.setContentType("image/jpeg");
		response.setContentLength((int) imageFile.length());
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
			InputStream is = new FileInputStream(imageFile);
			IOUtils.copy(is, response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Could not  download " + filename, e);
		}
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	List delete(@PathVariable Long id) {
		ImageEntity entity = imageDao.getById(id);
		String originalFilename = entity.getName();
		String newFilenameBase = entity.getNewFilename();

		// Delete original File
		File file = new File(fileUploadDirectory + File.separatorChar
				+ originalFilename);
		file.delete();

		// Delete thumbnails
		for (ThumbnailSize thumbnail_Enum : ThumbnailSize.values()) {
			file = new File(fileUploadDirectory + File.separatorChar
					+ thumbnail_Enum.getId() + newFilenameBase + "."
					+ thumbnail_Enum.getFormatName());
			file.delete();
		}

		imageDao.deleteById(entity.getId());
		log.debug(entity.toString());
		List<Map<String, Object>> results = new ArrayList<>();
		Map<String, Object> success = new HashMap<>();
		success.put("success", true);
		results.add(success);
		return results;
	}

	@RequestMapping(value = "/emotion/{id}", method = RequestMethod.GET)
	public @ResponseBody
	Map emotion(@PathVariable Long id) {
		ImageEntity entity = imageDao.getById(id);

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

	@RequestMapping(value = "/convert", params = { "id1", "id2", "option",
			"size" }, method = RequestMethod.GET)
	@ResponseBody
	public Map convert(@RequestParam("id1") long srcId,
			@RequestParam("id2") long destId,
			@RequestParam("option") int option, @RequestParam("size") int size) {
		ThumbnailSize thumbnail_Enum = ThumbnailSize.valueOf(size);

		String srcImagePath = fileUploadDirectory + File.separator
				+ thumbnail_Enum.getId()
				+ imageDao.getById(srcId).getNewFilename() + "."
				+ thumbnail_Enum.getFormatName();
		String destImagePath = fileUploadDirectory + File.separator
				+ thumbnail_Enum.getId()
				+ imageDao.getById(destId).getNewFilename() + "."
				+ thumbnail_Enum.getFormatName();

		Pointer<Byte> srcImageName = pointerToCString(srcImagePath);
		Pointer<Byte> destImageName = pointerToCString(destImagePath);
		String resultImageName = UUID.randomUUID().toString()+".jpg";
		Pointer<Byte> outImageName = pointerToCString(fileUploadDirectory+File.separatorChar+resultImageName);
		ImageConvertDllLibrary.convertImage3(srcImageName, destImageName,
				option,outImageName);

		Map<String, String> success = new HashMap<String, String>();
		success.put("filename",resultImageName);
		return success;
	}

	@RequestMapping(value = "/colortheme/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ColorThemeVO colorTheme(@PathVariable("id") Long id)
			throws JsonParseException, JsonMappingException, IOException,
			SQLException {
		ImageEntity entity = imageDao.getById(id);
		ThumbnailSize thumbnail_Enum = ThumbnailSize.SMALL_SIZE;
		String srcImagePath = fileUploadDirectory + File.separator
				+ thumbnail_Enum.getId() + entity.getNewFilename() + "."
				+ thumbnail_Enum.getFormatName();

		Pointer<Byte> srcImageName = pointerToCString(srcImagePath);
		log.debug("Read Image:" + srcImagePath.toString());
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
		ColorThemeEntity colorThemeEntity = new ColorThemeEntity(colorNum, r,
				g, b, percent);
		ColorThemeVO result = new ColorThemeVO(colorThemeEntity);
		return result;
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
