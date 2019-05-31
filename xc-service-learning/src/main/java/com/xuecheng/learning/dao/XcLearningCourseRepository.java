package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse ,String> {

    XcLearningCourse findByCourseIdAndUserId(String courseId,String userId);
}
