package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi{

    @Autowired
    PageService pageService;

    /**
          * 页面列表分页查询
          * @param page 当前页码，页码从1开始计数
          * @param size 每页记录数
          * @param queryPageRequest 查询条件
          * @return 页面列表
          */
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult<CourseBase> findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
/*
        //空数据
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        ArrayList<CmsPage> list = new ArrayList<>();
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试页面");
        list.add(cmsPage);
        queryResult.setList(list);
        queryResult.setTotal(1);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
*/

        return pageService.findList(page,size,queryPageRequest);
    }

    /**
     * 获取站点cms_site
     * @return
     */
    @Override
    @GetMapping("/list/site")
    public List<CmsSite> findSite(){
        return pageService.findSite();

    }

    /**
     * 添加页面
     * @param cmsPage
     * @return
     */
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }


    /**
     * 获取站点模板cms_template
     * @return
     */
    @Override
    @GetMapping("/template")
    public List<CmsTemplate> findTemplate(){
        return pageService.findTemplate();
    }

    /**
     * 根据id查询网页信息
     * @param id
     * @return
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.findById(id);
    }

    /**
     * 修改页面
     * @param id
     * @param cmsPage
     * @return
     */
    @Override
    @PutMapping("/edit/{id}")//这里使用put方法，http 方法中put表示更新
    public CmsPageResult edit(@PathVariable("id") String id,@RequestBody CmsPage cmsPage) {
        return pageService.edit(id,cmsPage);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    @DeleteMapping("/del/{id}")
    public ResponseResult del(@PathVariable("id") String id) {
        return pageService.delete(id);
    }

    /**
     * 执行页面的发布
     * @param pageId
     * @return
     */
    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return pageService.postPage(pageId);
    }

    /**
     * CMS 添加页面接口
     * @param cmsPage
     * @return
     */
    @PostMapping("/save")
    @Override
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return pageService.save(cmsPage);
    }

    /**
     * 一键发布
     * @param cmsPage
     * @return
     */
    @PostMapping("/postPageQuick")
    @Override
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        return pageService.postPageQuick(cmsPage);
    }

}
