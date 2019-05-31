package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class CourseService {

    //常量
    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;


    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMarketRepository courseMarketRepository;
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    CoursePubRepository coursePubRepository;

    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;
    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    //课程计划查询
    public TeachplanNode findTeachplanList(String courseId) {
       return     teachplanMapper.selectList(courseId);
    }

    /**
     * 添加课程计划
     * @param teachplan
     * @return
     */
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        //1、teachplan，teachplan.getCourseid(),teachplan.getPname()为空
        if (teachplan==null||
                StringUtils.isEmpty(teachplan.getCourseid())||
                StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        //课程计划id
        String courseid = teachplan.getCourseid();
        //页面传入课程parentId
        String parentid = teachplan.getParentid();
        //2、如果parentid为空则自动添加parentid
        if (StringUtils.isEmpty(parentid)){
            parentid = getTeachplanRoot(courseid);
        }
        //拿到父节点的grade
        Optional<Teachplan> byId = teachplanRepository.findById(parentid);
        Teachplan parentNode = byId.get();
        String grade = parentNode.getGrade();

        //新节点
        Teachplan teachplanNew = new Teachplan();
        //将页面提交的teachplan信息拷贝到teachplanNew对象中
        BeanUtils.copyProperties(teachplan,teachplanNew);
        teachplanNew.setParentid(parentid);
        teachplanNew.setCourseid(courseid);
        if (grade.equals("1")){
            teachplanNew.setGrade("2");
        }else {
            teachplanNew.setGrade("3");
        }
        teachplanRepository.save(teachplanNew);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查询课程的根节点啊，如果查询不到要自动添加
    private String getTeachplanRoot(String courseId){
        Optional<CourseBase> byId = courseBaseRepository.findById(courseId);
        if (!byId.isPresent()){

            return null;
        }
        CourseBase courseBase = byId.get();

        //查询课程的根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList==null||teachplanList.size()<0){
            //为空则添加根节点
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        return teachplanList.get(0).getId();
    }

    public QueryResponseResult<CourseInfo> findCourseList(String companyId,int page, int size, CourseListRequest courseListRequest) {
        if(courseListRequest==null){
            courseListRequest=new CourseListRequest();
        }
        //将公司id参数传入dao中
        courseListRequest.setCompanyId(companyId);
        if (page<0){
            page=0;
        }
        if (size<=0){
            size=20;
        }
        //设置分页参数
        PageHelper.startPage(page,size);
        //查询课程
        Page<CourseInfo> courseList = courseMapper.findCourseList(courseListRequest);
        //获取到总数
        long total = courseList.getTotal();
        //获得查询列表
        List<CourseInfo> result = courseList.getResult();
        //查询结果集
        QueryResult<CourseInfo> courseInfoQueryResult = new QueryResult<>();
        courseInfoQueryResult.setList(courseList);
        courseInfoQueryResult.setTotal(total);
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS,courseInfoQueryResult);
    }

    public AddCourseResult addCourse(CourseBase courseBase) {
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }

    public CourseBase findCourseById(String courseid) {
        Optional<CourseBase> byId = courseBaseRepository.findById(courseid);
        if (byId.isPresent()){
            CourseBase courseBase = byId.get();
            return courseBase;
        }
        return null;
    }

    @Transactional
    public ResponseResult updateCourse(String id, CourseBase courseBase) {
        CourseBase courseById = this.findCourseById(id);
        if (courseById==null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        courseById.setName(courseBase.getName());
        courseById.setMt(courseBase.getMt());
        courseById.setSt(courseBase.getSt());
        courseById.setGrade(courseBase.getGrade());
        courseById.setStudymodel(courseBase.getStudymodel());
        courseById.setUsers(courseBase.getUsers());
        courseById.setDescription(courseBase.getDescription());
        CourseBase save = courseBaseRepository.save(courseById);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CourseMarket getMarket(String courseId) {
        Optional<CourseMarket> byId = courseMarketRepository.findById(courseId);
        if (byId.isPresent()){
            CourseMarket courseMarket = byId.get();
            return courseMarket;
        }
        return null;
    }


    //更新营销信息，如果没有就新增信息，数据存在就跟新信息
    @Transactional
    public ResponseResult updateMarket(String id, CourseMarket courseMarket) {
        CourseMarket market = this.getMarket(id);
        if (market != null) {
            market.setCharge(courseMarket.getCharge());
            market.setQq(courseMarket.getQq());
            market.setEndTime(courseMarket.getEndTime());
            market.setStartTime(courseMarket.getStartTime());
            market.setPrice(courseMarket.getPrice());
            market.setValid(courseMarket.getValid());
            courseMarketRepository.save(market);

        }else {
            market = new CourseMarket();
            BeanUtils.copyProperties(courseMarket,market);
            courseMarketRepository.save(market);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //添加图片
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        CoursePic coursePic =null;
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if (byId.isPresent()){
            coursePic=byId.get();
        }
        if (coursePic==null){
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CoursePic findCoursePicList(String courseId) {
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if (byId.isPresent()){
            CoursePic coursePic = byId.get();
            return coursePic;
        }
        return null;
    }

    public ResponseResult deletePic(String courseId) {
        CoursePic coursePicList = this.findCoursePicList(courseId);
        if (coursePicList==null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
       coursePicRepository.deleteById(courseId);
        return new  ResponseResult(CommonCode.SUCCESS);
    }

    public CourseView getCoruseView(String id) {
        CourseView courseView = new CourseView();
        //课程基本信息
        CourseBase courseBase = this.findCourseById(id);
        courseView.setCourseBase(courseBase);
        //获取营销信息
        CourseMarket market = this.getMarket(id);
        courseView.setCourseMarket(market);
        //查询课程图片
        CoursePic coursePicList = this.findCoursePicList(id);
        courseView.setCoursePic(coursePicList);
        //课程计划
        TeachplanNode teachplanList = this.findTeachplanList(id);
        courseView.setTeachplanNode(teachplanList);
        return courseView;
    }

    //页面预览
    public CoursePublishResult preview(String id) {
        //查询课程信息
        CourseBase courseBase = this.findCourseById(id);

        CmsPage cmsPage = new CmsPage();
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageCreateTime(new Date());
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBase.getName());
        //保存页面
        CmsPageResult save = cmsPageClient.save(cmsPage);
        if (!save.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //得到id
        String pageId = save.getCmsPage().getPageId();
        String pageUrl =previewUrl+pageId;
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    //课程发布
    @Transactional
    public CoursePublishResult publish(String id) {
        //准备页面信息
        CourseBase courseBase = this.findCourseById(id);

        CmsPage cmsPage = new CmsPage();
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageCreateTime(new Date());
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBase.getName());
        //1、调用cms一键发布接口将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }


        //2、更改课程发布状态"已发布"
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        if (save==null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //3、课程索引
        //先创建CoursePub对象
        CoursePub coursePub = this.createCoursePub(id);
        //将coursePub保存到数据库
        CoursePub coursePubNew = this.saveCoursePub(id, coursePub);
        if (coursePubNew==null){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //4、课程缓存




        //得到发布的url地址
        String pageUrl=cmsPostPageResult.getPreviewUrl();

        //向teachplanMediaPub中保存到数据库,保存课程计划媒资信息到待索引表
        this.saveTeachplanMediaPub(id);

        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    //向teachplanMediaPub中保存到数据库
    private void saveTeachplanMediaPub(String courseId){
        //先删除teachplanMediaPub删
        teachplanMediaPubRepository.deleteByCourseId(courseId);
        //从teachplanMeida查询
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        //将teachplanMediaList数据放到teachplanMediaPubs中
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            //添加事件戳
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubs.add(teachplanMediaPub);
        }
        //将teachplanMediaPubs保存
        teachplanMediaPubRepository.saveAll(teachplanMediaPubs);
    }



    //创建coursePub，将courseBase，coursePic，course_market，teachplanNode合成一张表
    private CoursePub createCoursePub(String courseId){
        CoursePub coursePub = new CoursePub();
        //1、获取课程信息
        CourseBase courseBase = this.findCourseById(courseId);
        //将courseBase属性拷贝到CoursePub中
        BeanUtils.copyProperties(courseBase,coursePub);

        //2、图片信息
        CoursePic coursePic = this.findCoursePicList(courseId);
        BeanUtils.copyProperties(coursePic,coursePic);
        //3、营销信息
        CourseMarket market = this.getMarket(courseId);
        BeanUtils.copyProperties(market,coursePub);

        TeachplanNode teachplanNode = this.findTeachplanList(courseId);
        String jsonString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(jsonString);

        return coursePub;

    }

    //将coursePub保存到数据库中
    private CoursePub saveCoursePub(String courseId, CoursePub coursePub) {
        CoursePub coursePubNew=null;
        //查询数据库中是否存在
        Optional<CoursePub> byId = coursePubRepository.findById(courseId);
        if (byId.isPresent()) {
            coursePubNew = byId.get();
        }else {
            coursePubNew=new CoursePub();
        }
        //将coursePub拷贝到coursePubNew
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(courseId);
        //时间戳logstash使用
        coursePubNew.setTimestamp(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String data = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(data);

        coursePubRepository.save(coursePubNew);
        return coursePubNew;

    }

    /**
     * 选择视频并保存视频到teachplan_media中
     *
     * @param teachplanMedia
     * @return
     */
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia==null&&StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<Teachplan> byId = teachplanRepository.findById(teachplanMedia.getTeachplanId());
        Teachplan teachplan=null;
        if (byId.isPresent()){
             teachplan = byId.get();
        }
        String grade = teachplan.getGrade();
        if (!grade.equals("3") && grade == null) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIS_TEACHPLAN_GRADEERROR);
        }
        TeachplanMedia one=null;
        Optional<TeachplanMedia> optional = teachplanMediaRepository.findById(teachplanMedia.getTeachplanId());
        if (optional.isPresent()){
            one = optional.get();
        }else {
            one=new TeachplanMedia();
        }
        one.setCourseId(teachplanMedia.getCourseId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setTeachplanId(teachplanMedia.getTeachplanId());
        teachplanMediaRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
