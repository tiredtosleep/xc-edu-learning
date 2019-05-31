package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
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
@Api(value = "文件管理接口", description = "文件管理接口，提供增删改查")
public interface FileSystemControllerApi {
    @ApiOperation("图片管理")
    public UploadFileResult upload(MultipartFile multipartFile,String filetag,String businesskey,String metadata);
}
