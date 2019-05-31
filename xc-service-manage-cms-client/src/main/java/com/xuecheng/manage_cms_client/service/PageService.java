package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsStieRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Slf4j
@Service
public class PageService {


    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsStieRepository cmsStieRepository;

    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    GridFsTemplate gridFsTemplate;
    /**
     * 将页面html保存到页面物理路径
     * @param pageId
     */
    public void savePageToServerPath(String pageId) throws IOException {
        //1.调用getCmsPageById()获取cmsPage
        CmsPage cmsPage = this.getCmsPageById(pageId);

        //1.1.得到html的文件id，从cmsPage中获取htmlFileId内容
        String htmlFileId = cmsPage.getHtmlFileId();

        //1.2.从gridFs中查询html文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if (inputStream==null){
            log.error("getFileById inputStream is null",htmlFileId);
            return;
        }
        //2、将html文件保存到服务器物理路径
        //2.1.得到站点id，siteId
        String siteId = cmsPage.getSiteId();
        //2.2.得到站点信息
        CmsSite cmsSite = this.getCmsSiteById(siteId);
        //2.3.站点物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //2.4.页面物理路径：页面物理路径=站点物理路径+页面物理路径+页面名称
        String pagePath=sitePhysicalPath+cmsPage.getPagePhysicalPath()+cmsPage.getPageName();
        //2.5.将文件保存到服务器上
        //输出流
        FileOutputStream fileOutputStream = new FileOutputStream(new File(pagePath));
        int copy = IOUtils.copy(inputStream, fileOutputStream);
        //关闭输出流和输入流
        fileOutputStream.close();
        inputStream.close();
    }

    /**
     * 次函数（被savePageToServerPath调用）
     * 取文件
     * @param fileId
     * @return
     * @throws IOException
     */
    public InputStream getFileById(String fileId) throws IOException {
        //根据文件id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建GridFsResource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        return gridFsResource.getInputStream();
    }


    /**
     * 次函数（被savePageToServerPath调用）
     * 根据siteId查询站点信息cmsSite
     * @param siteId
     * @return
     */
    public CmsSite getCmsSiteById(String siteId){
        Optional<CmsSite> byId = cmsStieRepository.findById(siteId);
        if (byId.isPresent()){
            CmsSite cmsSite = byId.get();
            return cmsSite;
        }
        return null;
    }

    /**
     * 次函数（被savePageToServerPath调用）
     * 根据pageId查询站点信息cmsSite
     * @param pageId
     * @return
     */
    public CmsPage getCmsPageById(String pageId){
        Optional<CmsPage> byId = cmsPageRepository.findById(pageId);
        if (byId.isPresent()){
            CmsPage cmsPage=byId.get();
            return cmsPage;
        }
        return null;
    }
}
