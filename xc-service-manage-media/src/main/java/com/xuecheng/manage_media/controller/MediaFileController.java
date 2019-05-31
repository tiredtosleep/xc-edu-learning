package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaFileControllerApi;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 媒体管理
 * @author:cxg
 * @Date:${time}
 */
@RestController
@RequestMapping("/media/file")
public class MediaFileController implements MediaFileControllerApi {
    @Autowired
    MediaFileService mediaFileService;


    /**
     * 查询视频文件列表
     * @param page
     * @param size
     * @param queryMediaFileRequest
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    @Override
    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        return mediaFileService.finList(page,size,queryMediaFileRequest);
    }
}
