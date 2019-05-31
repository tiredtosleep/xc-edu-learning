package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsStieRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class PageService {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsStieRepository cmsStieRepository;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 页面列表分页查询
     * @param page 当前页码，页码从1开始计数
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    public QueryResponseResult<CourseBase> findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest==null){
           queryPageRequest = new QueryPageRequest();
        }
        //1.自定义条件查询
        //1.1构建条件匹配器
        ExampleMatcher exampleMatcher=ExampleMatcher.matching().withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //1.2条件对象
        CmsPage cmsPage = new CmsPage();
        //1.3设置条件值
        //1.3.1 设置站点id
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //1.3.2 设置模板id
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //1.3.3 设置页面别名
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //定义example
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);

        //分页参数
        if (page<=0){
            page=1;
        }
        page=page-1;
        if (size<=0) {
            size=10;
        }
        Pageable pageable=PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);//实现自定义条件和分页查询

        QueryResult queryResult = new QueryResult();//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        queryResult.setList(all.getContent());
        QueryResponseResult<CourseBase> queryResponseResult = new QueryResponseResult<CourseBase>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    public List<CmsSite> findSite() {
        List<CmsSite> all = cmsStieRepository.findAll();
        return all;

    }

    public CmsPageResult add(CmsPage cmsPage) {


        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage all = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (all!=null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        if (all==null){
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL,null);

    }

    public List<CmsTemplate> findTemplate() {
        List<CmsTemplate> all = cmsTemplateRepository.findAll();
        return all;
    }

    public CmsPage findById(String id) {
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    //更新修改
    public CmsPageResult edit(@PathVariable("id") String id,@RequestBody CmsPage cmsPage) {
        //根据id查询页面信息
        CmsPage byId = findById(id);
        if (byId!=null){
            CmsPage cmsPage1 = new CmsPage();
            cmsPage1.setSiteId(cmsPage.getSiteId());
            cmsPage1.setPageId(cmsPage.getPageId());
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
            cmsPage1.setPageName(cmsPage.getPageName());
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            cmsPage1.setDataUrl(cmsPage.getDataUrl());
            cmsPage1.setPageType(cmsPage.getPageType());
            cmsPage1.setPageCreateTime(cmsPage.getPageCreateTime());
            CmsPage save = cmsPageRepository.save(cmsPage1);
            if (save!=null ){
                return new CmsPageResult(CommonCode.SUCCESS,save);
            }

        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    public ResponseResult delete(String id) {
        CmsPage byId = findById(id);
        if (byId!=null){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
    /**
     * 页面静态化
     * 2、静态化程序获取页面的DataUrl
     3、静态化程序远程请求DataUrl获取数据模型。
     4、静态化程序获取页面的模板信息
     5、执行页面静态化
     */
    public String getPageHtml(String pageId){
        //获取模型数据
        Map modelById = getModelById(pageId);
        if(modelById==null){
            //数据模型找不到
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        //取模板的内容
        String templateByPageId = getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(templateByPageId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //页面静态化
        String html = generateHtml(templateByPageId, modelById);
        return html;
    }

    //页面静态化
    private String generateHtml(String templateContent,Map model){
        //创建配置类
        Configuration configuration=new Configuration(Configuration.getVersion());
        //创建模板加速器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        //向configuration配置模板加速器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template template = configuration.getTemplate("template", "utf-8");
            //调用api进行静态化
            String s = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 被调用的函数
     * 获取模板信息
     * @param pageId
     * @return
     */
    private String getTemplateByPageId(String pageId){
        //取出页面信息
        CmsPage byId = findById(pageId);
        if (byId==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String templateId = byId.getTemplateId();
        if (StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()){
            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从GridFs中取模板文件内容
            //根据文件id查询文件
            GridFSFile id = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开一个下载流
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(id.getObjectId());
            //创建GridFsResource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(id, gridFSDownloadStream);
            //从流中获取数据
            try {
                String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return s;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 被调用的函数
     * 获取数据模型
     * @param pageId
     * @return
     */
    private Map getModelById(String pageId){
        //取出页面信息
        CmsPage byId = findById(pageId);
        if (byId==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出dataUrl
        String dataUrl = byId.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //远程调用
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    /**
     * 执行页面的发布
     * @param pageId
     * @return
     */
    public ResponseResult postPage(String pageId) {
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将页面静态化页面存储到GridFs中
        CmsPage cmsPage = this.saveHtml(pageId, pageHtml);
        //向MQ发送消息
        sendPostPage(pageId);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //被调用的函数，将静态文件上传到GridFs中,并且修改cmsPage中htmlFileId
    private CmsPage saveHtml(String pageId, String htmlContent) {
        ObjectId objectId=null;
        //得到页面信息
        CmsPage cmsPage = findById(pageId);
        if (cmsPage==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //将html文件内容转成输入流
        try {
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件内容保存到GridFs
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将html文件id更新到cmsPage中
        cmsPage.setHtmlFileId(objectId.toString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }
    //被调用，发消息
    private void sendPostPage(String pageId){
        CmsPage cmsPage = findById(pageId);
        if (cmsPage==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //创建消息对象
        Map<String,String> map=new HashMap<>();
        map.put("pageId",pageId);
        //将map转成json字符串
        String s = JSON.toJSONString(map);
        //发送消息，得到站点id
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,cmsPage.getSiteId(),s);

    }

    //有则更新，没有就保存
    public CmsPageResult save(CmsPage cmsPage) {
        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage all = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (all!=null){
            //调用更新
           return  edit(all.getPageId(), cmsPage);
        }
        //调用add方法
        return add(cmsPage);
    }


    /**
     * 一键发布
     * @param cmsPage
     * @return
     */
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //将页面信息存储到cms_page集合中
        CmsPageResult save = this.save(cmsPage);
        if (!save.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //得到页面id
        CmsPage getCmsPage = save.getCmsPage();
        String pageId = getCmsPage.getPageId();
        //执行页面发布（先静态化，保存gridFs，向MQ发消息）
        ResponseResult post = this.postPage(pageId);
        if (!post.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //页面Url= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
        //Url=站点域名+站点web路径+页面web路径+页面名称
        String siteId = getCmsPage.getSiteId();
        CmsSite cmsSite = this.findCmsSitebyId(siteId);
        String pageUrl=cmsSite.getSiteDomain()+cmsSite.getSiteWebPath()+getCmsPage.getPageWebPath()+getCmsPage.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }
    //根据siteId查询站点信息
    public CmsSite findCmsSitebyId(String siteId){
        Optional<CmsSite> byId = cmsStieRepository.findById(siteId);
        if (byId.isPresent()){
            CmsSite cmsSite = byId.get();
            return cmsSite;
        }
        return null;
    }
}
