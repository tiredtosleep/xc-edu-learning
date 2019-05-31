package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Api(value = "媒资管理接口", description = "媒资管理接口、提供文件上传、处理等接口")
public interface MediaUploadControllerApi {

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
    @ApiOperation("注册上传文件")
    public ResponseResult register(
            String fileMd5,
            String fileName,
            Long fileSize,
            String mimetype,
            String fileExt);

    /**
     *
     * @param fileMd5
     * @param chunk  分块下标
     * @param chunkSize 分块大小
     * @return
     */
    @ApiOperation("校验文件分块")
    public CheckChunkResult checkchunk(String fileMd5,
                                       Integer chunk,
                                       Integer chunkSize
                                       );

    /**
     *
     * @param file
     * @param fileMd5
     * @param chunk 分块下标
     * @return
     */
    @ApiOperation("上传分块")
    public ResponseResult uploadchunk(MultipartFile file,
                                      String fileMd5,
                                      Integer chunk);

    /**
     *
     *
     * @param fileMd5
     * @param fileName
     * @param fileSize 文件大小
     * @param mimetype 文件类型
     * @param fileExt  文件扩展名
     * @return
     */
    @ApiOperation("合并分块")
    public ResponseResult  mergechunks( String fileMd5,
                                        String fileName,
                                        Long fileSize,
                                        String mimetype,
                                        String fileExt);
}
