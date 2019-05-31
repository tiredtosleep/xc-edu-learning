package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 页面查询
 * @author:cxg
 * @Date:${time}
 */
@Api(value = "cms页面管理接口", description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    //页面查询(分页参数+响应条件)
    public QueryResponseResult<CourseBase> findList(int page, int size, QueryPageRequest queryPageRequest);

    @ApiOperation("查询站点信息")
    public List<CmsSite> findSite();

    @ApiOperation("查询模板信息")
    public List<CmsTemplate> findTemplate();

    @ApiOperation("添加信息")
    public CmsPageResult add(CmsPage cmsPage);

    @ApiOperation("根据id查询页面信息")
    public CmsPage findById(String id);

    @ApiOperation("修改页面")
    public CmsPageResult edit(String id, CmsPage cmsPage);

    @ApiOperation("删除页面")
    public ResponseResult del(String id);

    @ApiOperation("页面发布")
    public ResponseResult post(String pageId);

    @ApiOperation("保存页面")
    public CmsPageResult save(CmsPage cmsPage);

    @ApiOperation("一键发布")
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);
}

