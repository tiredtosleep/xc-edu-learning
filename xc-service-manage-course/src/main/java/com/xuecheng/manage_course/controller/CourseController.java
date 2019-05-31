package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {

    @Autowired
    CourseService courseService;
    /**
     * 课程计划查询
     * @param courseId
     * @return
     */
    //添加注解PreAuthorize，并指定此方法所需要的权限,当用户据用course_teachplan_list权限是可以访问此方法
    @PreAuthorize("hasAuthority('course_teachplan_list')")
    @GetMapping("/teachplan/list/{courseId}")
    @Override
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }

    /**
     * 课程计划添加
     * @param teachplan
     * @return
     */
    @PreAuthorize("hasAuthority('xc_teachmanager_course_plan')")
    @PostMapping("/teachplan/add")
    @Override
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {

        return courseService.addTeachplan(teachplan);
    }

    /**
     * 分页查询我的课程（course_base和course_pic表）
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    @GetMapping("/coursebase/list/{page}/{size}")
    @Override
    public QueryResponseResult<CourseInfo> findCourseList(
            @PathVariable("page") int page,
            @PathVariable("size") int size,
            CourseListRequest courseListRequest) {
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        //解析jwt拿到用户信息
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        String companyId=userJwt.getCompanyId();
        return courseService.findCourseList(companyId,page,size,courseListRequest);
    }

    /**
     * 添加课程信息（jpa）
     * @param courseBase
     * @return
     */
    @PostMapping("/coursebase/add")
    @Override
    public AddCourseResult addCourse(@RequestBody CourseBase courseBase) {
        return courseService.addCourse(courseBase);
    }

    /**
     * 根据课程id查询课程信息
     * @param courseid
     * @return
     */
    @PreAuthorize("hasAuthority('course_find_list')")
    @GetMapping("/coursebase/get/{courseid}")
    @Override
    public CourseBase getCourseById(@PathVariable("courseid") String courseid) {
        return courseService.findCourseById(courseid);
    }


    /**
     * 根据id修改课程信息
     * @param id
     * @param courseBase
     * @return
     */
    @PutMapping("/coursebase/update/{id}")
    @Override
    public ResponseResult updateCourse(@PathVariable("id") String id,@RequestBody CourseBase courseBase) {
        return courseService.updateCourse(id,courseBase);
    }


    /**
     * 根据课程id查询课程营销信息
     * @param courseId
     * @return
     */
    @PreAuthorize("hasAuthority('xc_teachmanager_course_market')")
    @GetMapping("/coursemarket/get/{courseId}")
    @Override
    public CourseMarket getMarket(@PathVariable("courseId") String courseId) {
        return courseService.getMarket(courseId);
    }

    /**
     * 修改或添加课程营销信息
     * @param id
     * @param courseMarket
     * @return
     */
    @PostMapping("/coursemarket/update/{id}")
    @Override
    public ResponseResult updateMarket(@PathVariable("id")String id, @RequestBody CourseMarket courseMarket) {
        return courseService.updateMarket(id,courseMarket);
    }

    /**
     * 保存图片到mysql中
     * @param courseId
     * @param pic
     * @return
     */
    @PostMapping("/coursepic/add")
    @Override
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        return courseService.addCoursePic(courseId,pic);
    }

    /**
     * 根据id查询存储在mysql中的course_pic
     * @param courseId
     * @return
     */
    @PreAuthorize("hasAuthority('course_pic_list')")
    @GetMapping("/coursepic/list/{courseId}")
    @Override
    public CoursePic findCoursePicList(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePicList(courseId);

    }

    /**
     * 删除图片
     * @param courseId
     * @return
     */
    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deletePic(@RequestParam("courseId")String courseId) {
        return courseService.deletePic(courseId);
    }


    /**
     * 课程详情查询
     * @param id
     * @return
     */
    @GetMapping("/courseview/{id}")
    @Override
    public CourseView courseView(@PathVariable("id") String id) {
        return courseService.getCoruseView(id);
    }

    /**
     * 页面预览
     * @param id
     * @return
     */
    @PostMapping("/preview/{id}")
    @Override
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    /**
     * 课程发布
     * @param id
     * @return
     */
    @PostMapping("/publish/{id}")
    @Override
    public CoursePublishResult publish(@PathVariable("id") String id) {
        return courseService.publish(id);
    }

    /**
     * 选择视频并保存视频
     * @param teachplanMedia
     * @return
     */
    @PostMapping("/savemedia")
    @Override
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemedia(teachplanMedia);
    }


}
