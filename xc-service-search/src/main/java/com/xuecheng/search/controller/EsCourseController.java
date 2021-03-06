package com.xuecheng.search.controller;

import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {
    @Autowired
    EsCourseService esCourseService;

    /**
     * 课程综合搜索
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    @Override
    @GetMapping(value="/list/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") int page,@PathVariable("size") int size, CourseSearchParam courseSearchParam) {
        return esCourseService.list(page,size,courseSearchParam);
    }

    /**
     * 根据课程id查询课程信息,从搜索服务中查询
     * @param id
     * @return
     */
    @GetMapping("/getall/{id}")
    @Override
    public Map<String, CoursePub> getall(@PathVariable("id") String id) {
        return esCourseService.getall(id);
    }

    /**
     * 根据课程计划id查询课程媒资信息
     *
     * @param teachplanId
     * @return
     */
    @GetMapping("/getmedia/{teachplanId}")
    @Override
    public TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId) {
        //将一个id加入数据中，传递给service方法中
        String[] teachplanIds=new String[]{teachplanId};
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = esCourseService.getmedia(teachplanIds);

        QueryResult queryResult = queryResponseResult.getQueryResult();
        if(queryResult != null){
            List<TeachplanMediaPub> list = queryResult.getList();
            if(list !=null && list.size()>0){
                return list.get(0);
            }
        }
        return new TeachplanMediaPub();
    }
}
