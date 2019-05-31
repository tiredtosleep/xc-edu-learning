package com.xuecheng.api.course;

import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 课程Course的api
 * @author:cxg
 * @Date:${time}
 */
@Api(value = "课程的管理接口", description = "课程的管理接口，提供课程的增、删、改、查")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);

    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(Teachplan teachplan);

    @ApiOperation("分页查询我的课程")
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("添加课程")
    public AddCourseResult addCourse(CourseBase courseBase);

    @ApiOperation("根据课程id获取课程基础信息")
    public CourseBase getCourseById(String id);

    @ApiOperation("修改课程信息")
    public ResponseResult updateCourse(String id, CourseBase courseBase);

    @ApiOperation("根据课程id查询课程营销信息")
    public CourseMarket getMarket(String courseId);

    @ApiOperation("修改课程营销信息")
    public ResponseResult updateMarket(String courseId, CourseMarket courseMarket);

    @ApiOperation("添加课程图片")
    public ResponseResult addCoursePic(String courseId, String pic);

    @ApiOperation("根据课程id查找课程图片")
    public CoursePic findCoursePicList(String courseId);

    @ApiOperation("根据id删除课程图片")
    public ResponseResult deletePic(String courseId);


    @ApiOperation("课程详情查询")
    public CourseView courseView(@PathVariable("id") String id);


    @ApiOperation("课程预览")
    public CoursePublishResult preview(String id);

    @ApiOperation("课程发布")
    public CoursePublishResult publish(String id);

    @ApiOperation("保存计划和媒资文件的关联")
    public ResponseResult savemedia(TeachplanMedia teachplanMedia);
}
