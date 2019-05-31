package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaUploadControllerApi;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 视频文件上传
 * @author:cxg
 * @Date:${time}
 */
@RestController
@RequestMapping("/media/upload")
public class MediaUploadController implements MediaUploadControllerApi {
    @Autowired
    MediaUploadService mediaUploadService;

    /**
     * 文件上传前的准备工作
     *
     * @param fileMd5
     * @param fileName
     * @param fileSize 文件大小
     * @param mimetype 文件类型
     * @param fileExt  文件扩展名
     * @return
     */
    @PostMapping("/register")
    @Override
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        return mediaUploadService.register( fileMd5,  fileName,  fileSize,  mimetype,  fileExt);
    }

    /**
     * 检验分块
     * @param fileMd5
     * @param chunk  分块下标
     * @param chunkSize 分块大小
     * @return
     */
    @PostMapping("/checkchunk")
    @Override
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        return mediaUploadService.checkchunk(fileMd5,chunk,chunkSize);
    }

    /**
     * 上传分块
     * @param file
     * @param fileMd5
     * @param chunk 分块下标
     * @return
     */
    @PostMapping("/uploadchunk")
    @Override
    public ResponseResult uploadchunk(MultipartFile file, String fileMd5, Integer chunk) {
        return mediaUploadService.uploadchunk(file,fileMd5,chunk);
    }

    /**
     *
     * 合并文件
     * @param fileMd5
     * @param fileName
     * @param fileSize 文件大小
     * @param mimetype 文件类型
     * @param fileExt  文件扩展名
     * @return
     */
    @PostMapping("/mergechunks")
    @Override
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        return mediaUploadService.mergechunks(fileMd5,fileName,fileSize,mimetype,fileExt);
    }
}
