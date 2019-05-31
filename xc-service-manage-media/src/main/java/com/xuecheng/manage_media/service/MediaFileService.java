package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 媒体管理
 * @author:cxg
 * @Date:${time}
 */
@Service
public class MediaFileService {

    @Autowired
    MediaFileRepository mediaFileRepository;
    /**
     * 查询视频文件列表
     *
     * @param page
     * @param size
     * @param queryMediaFileRequest
     * @return
     */
    public QueryResponseResult<MediaFile> finList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        if (queryMediaFileRequest==null){
            queryMediaFileRequest=new QueryMediaFileRequest();
        }
        //条件
        MediaFile mediaFile = new MediaFile();
        //1 文件的原始名称
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }

        //2 条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                //contains模糊
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())
                //contains模糊
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                //exact精确
                .withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());

        //3 定义example条件对象
        Example<MediaFile> example = Example.of(mediaFile, matcher);
        //4 分页对象
        //分页参数
        if (page<=0){
            page=1;
        }
        page=page-1;
        if (size<=0) {
            size=10;
        }
        Pageable pageable= PageRequest.of(page,size);
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        QueryResult<MediaFile> queryResult = new QueryResult<>();
        //总数据
        queryResult.setTotal(all.getTotalElements());
        //数据列表
        queryResult.setList(all.getContent());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }
}
