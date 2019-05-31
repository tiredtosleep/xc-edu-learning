package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
@Slf4j
public class FileSystemService {
    @Autowired
    private FastFileStorageClient fileStorageClient;
    @Autowired
    private FileSystemRepository fileSystemRepository;
    //定义文件类型
    private static final List<String> ALLOW_TYPES = Arrays.asList("image/png", "image/jpeg","image/bmp");


    /**
     * 文件上传后将信息存储在mongodb中
     * @param file
     * @param filetag
     * @param businesskey
     * @param metadata
     * @return
     */
    public UploadFileResult upload(MultipartFile file, String filetag, String businesskey, String metadata) {
        if (file==null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL );
        }
        //上传文件,并作为文件id
        String fileid = upload_fastDFS(file);
        //创建file对象
        FileSystem fileSystem = new FileSystem();
        //文件id
        fileSystem.setFileId(fileid);
        //文件在fastdfs中的路径
        fileSystem.setFilePath(fileid);
        //业务标识
        fileSystem.setBusinesskey(businesskey);
        //标签
        fileSystem.setFiletag(filetag);
        //元数据
        if (StringUtils.isEmpty(metadata)){
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        }
        //文件名字
        fileSystem.setFileName(file.getOriginalFilename());
        //大小
        fileSystem.setFileSize(file.getSize());
        //类型
        fileSystem.setFileType(file.getContentType());
        fileSystemRepository.save(fileSystem);

        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    //上传文件到FastFDFS
    public String upload_fastDFS(MultipartFile file){

        try {
            //校验文件类型
            String contentType = file.getContentType();
            if (!ALLOW_TYPES.contains(contentType)){
                ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_TYPE);
            }
            //校验文件内容
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read==null){
                ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_CONTENT);
            }
            // 2、将图片上传到FastDFS
            // 2.1、获取文件后缀名
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            // 2.2、图片上传
            StorePath storePath = fileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            //返回路径
            return  storePath.getFullPath();
            //返回路径
        } catch (IOException e) {
            log.error("上传文件失败",e);
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
            return null;

        }

    }
   /* public void deletePic(){

    }*/
}
