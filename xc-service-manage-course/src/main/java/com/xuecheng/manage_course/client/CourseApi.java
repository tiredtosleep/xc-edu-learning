package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.course.ext.CourseView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public interface CourseApi {
    @GetMapping("/course/courseview/{id}")
    public CourseView courseView(@PathVariable("id") String id);

}
