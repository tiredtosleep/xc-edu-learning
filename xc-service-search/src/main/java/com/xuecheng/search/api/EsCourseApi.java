package com.xuecheng.search.api;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public interface EsCourseApi {
    /**
     * 根据课程计划id查询课程媒资信息
     *
     * @param teachplanId
     * @return
     */
    @GetMapping("/search/course/getmedia/{teachplanId}")
    public TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId);
}
