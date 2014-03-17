/**
 * @(#)FileController.java, Mar 4, 2014. 
 * 
 * Copyright 2014 WAWADIMU, Inc. All rights reserved.
 * WAWADIMU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.gallery.web.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
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
@RequestMapping("/controller")
public class FileController {

//    LinkedList<FileMeta> files = new LinkedList<FileMeta>();
//
//
//    private final static Logger log = LoggerFactory
//        .getLogger(FileController.class);
//
//
//    /***************************************************
//     * URL: /rest/controller/upload upload(): receives files
//     * 
//     * @param request
//     *            : MultipartHttpServletRequest auto passed
//     * @param response
//     *            : HttpServletResponse auto passed
//     * @return LinkedList<FileMeta> as json format
//     * @throws IOException
//     ****************************************************/
//    @RequestMapping(value = "/upload", method = RequestMethod.POST)
//    public @ResponseBody
//    LinkedList<FileMeta> upload(MultipartHttpServletRequest request,
//        HttpServletResponse response) throws IOException {
//
//        //1. build an iterator
//        Iterator<String> itr = request.getFileNames();
//        MultipartFile mpf = null;
//
//        //2. get each file
//        while (itr.hasNext()) {
//
//            //2.1 get next MultipartFile
//            mpf = request.getFile(itr.next());
//            System.out.println(mpf.getOriginalFilename() + " uploaded! "
//                + files.size());
//
//            //2.2 if files > 10 remove the first from the list
//            if (files.size() >= 10)
//                files.pop();
//
//            //2.3 create new fileMeta
//            fileMeta = new FileMeta();
//            fileMeta.setFileName(mpf.getOriginalFilename());
//            fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
//            fileMeta.setFileType(mpf.getContentType());
//            fileMeta.setBytes(mpf.getBytes());
//
//            //2.5 create new fileData
//            fileData = new FileData();
//            fileData.setFileName(mpf.getOriginalFilename());
//            fileData.setLength((int) mpf.getSize());
//            fileData.setFileType(mpf.getContentType());
//            fileData.setInputStream(mpf.getInputStream());
//            fileData.setKey(UUID.randomUUID().toString().replace("-", ""));
//
//            //2.6  save entity
//            ImageEntity entity = imageManager.save(fileData);
//
//            log.info("save image success: id :{}!",
//                new Object[] { entity.getId() });
//            log.info("--------Uploading Completed!-------------");
//        }
//        // result will be like this
//        // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"},...]
//        //2.4 add to files
//        fileMeta.setKey(fileData.getKey());
//        files.add(fileMeta);
//        return files;
//    }
//
//    /***************************************************
//     * URL: /rest/controller/get/{value} get(): get file as an attachment
//     * 
//     * @param response
//     *            : passed by the server
//     * @param value
//     *            : value from the URL
//     * @return void
//     ****************************************************/
//    @RequestMapping(value = "/get/{value}", method = RequestMethod.GET)
//    public void get(HttpServletResponse response, @PathVariable String value) {
//        FileMeta getFile = files.get(Integer.parseInt(value));
//        try {
//            response.setContentType(getFile.getFileType());
//            response.setHeader("Content-disposition", "attachment; filename=\""
//                + getFile.getFileName() + "\"");
//            FileCopyUtils.copy(getFile.getBytes(), response.getOutputStream());
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    @RequestMapping(value = "/get-key/{key}", method = RequestMethod.GET)
//    public void get(HttpServletRequest requet, HttpServletResponse response,
//        @PathVariable("key") String key) {
//
//        FileData fileData = dataFileDao.getDataByKey(key);
//        if (fileData == null) {
//            log.info("invalid key :{}" + key);
//        }
//
//        response.setContentType(fileData.getFileType());
//        response.setHeader("Content-disposition", "attachment; filename=\""
//            + fileData.getKey() + "\"");
//        try {
//            FileCopyUtils.copy(fileData.getInputStream(),
//                response.getOutputStream());
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        log.info("file's key{} has been downloaded.", new Object[] { key });
//    }

}
