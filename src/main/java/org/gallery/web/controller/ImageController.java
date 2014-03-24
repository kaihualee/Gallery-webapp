/**
 * @(#)ImageController.java, 2014年3月17日. 
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

    final String thumbnail_suffix = "-thumbnail.png";

    private final static ObjectMapper mapper = new ObjectMapper();

    @RequestMapping
    public String index() {
        log.debug("ImageController home");
        return "image/index";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public @ResponseBody
    List list() {
        log.debug("uploadGet called");
        List<ImageEntity> images = imageDao.getAll();
        List<ImageVO> list = new ArrayList<>();
        for (ImageEntity entity: images) {
            ImageVO imageVO = new ImageVO(entity);
            imageVO.setUrl("/action/picture/" + entity.getId());
            imageVO.setThumbnailUrl("/action/thumbnail/" + entity.getId());
            imageVO.setDeleteUrl("/action/delete/" + entity.getId());
            imageVO.setDeleteType("DELETE");
            list.add(imageVO);
        }
        log.debug("Returning: {}", list);
        return list;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    Map upload(MultipartHttpServletRequest request, HttpServletResponse response) {
        log.info("uploadPost called");
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf;
        List<ImageVO> list = new LinkedList<>();

        while (itr.hasNext()) {
            mpf = request.getFile(itr.next());
            log.info("Uploading {}", mpf.getOriginalFilename());

            String newFilenameBase = UUID.randomUUID().toString();
            String originalFileExtension = mpf.getOriginalFilename().substring(
                mpf.getOriginalFilename().lastIndexOf("."));
            String newFilename = newFilenameBase + originalFileExtension;
            String storageDirectory = fileUploadDirectory;
            String contentType = mpf.getContentType();

            File newFile = new File(storageDirectory + "/" + newFilename);
            try {
                mpf.transferTo(newFile);

                BufferedImage thumbnail = Scalr.resize(ImageIO.read(newFile),
                    290);
                String thumbnailFilename = newFilenameBase + thumbnail_suffix;
                File thumbnailFile = new File(storageDirectory + "/"
                    + thumbnailFilename);
                ImageIO.write(thumbnail, "png", thumbnailFile);

                ImageEntity entity = EmotionParser.parse(ImageIO.read(newFile));
                //ImageEntity entity = new ImageEntity();
                entity.setName(mpf.getOriginalFilename());
                // entity.setThumbnailFilename(thumbnailFilename);
                entity.setNewFilename(newFilename);
                entity.setContentType(contentType);
                entity.setSize(mpf.getSize());
                imageDao.save(entity);

                ImageVO imageVO = new ImageVO(entity);
                imageVO.setUrl("/action/picture/" + entity.getId());
                imageVO.setThumbnailUrl("/action/thumbnail/" + entity.getId());
                imageVO.setDeleteUrl("/action/delete/" + entity.getId());
                imageVO.setEmotionUrl("/action/emotion/" + entity.getId());
                imageVO.setDeleteType("DELETE");

                list.add(imageVO);

            } catch (IOException e) {
                log.error("Could not upload file " + mpf.getOriginalFilename(),
                    e);
            }

        }

        Map<String, Object> files = new HashMap<>();
        files.put("files", list);
        return files;
    }

    @RequestMapping(value = "/picture/{id}", method = RequestMethod.GET)
    public void picture(HttpServletResponse response, @PathVariable Long id) {
        ImageEntity entity = imageDao.get(id);
        File imageFile = new File(fileUploadDirectory + "/"
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
            + thumbnail_suffix;
        File imageFile = new File(fileUploadDirectory + "/" + thumbnailFilename);

        response.setContentType(entity.getContentType());
        response.setContentLength(entity.getSize().intValue());
        try {
            InputStream is = new FileInputStream(imageFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch (IOException e) {
            log.error("Could not show thumbnail " + id, e);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    List delete(@PathVariable Long id) {
        ImageEntity entity = imageDao.get(id);
        File imageFile = new File(fileUploadDirectory + "/"
            + entity.getNewFilename());
        imageFile.delete();
        String thumbnailFilename = entity.getNewFilename().substring(0,
            entity.getNewFilename().lastIndexOf('.'))
            + thumbnail_suffix;
        File thumbnailFile = new File(fileUploadDirectory + "/"
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
}
