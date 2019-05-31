package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 基础信息、课程营销、课程图片、教学计划
 * @author:cxg
 * @Date:${time}
 */
@Data
@NoArgsConstructor
@ToString
public class CourseView implements Serializable {
    private CourseBase courseBase;
    private CoursePic coursePic;
    private CourseMarket courseMarket;
    private TeachplanNode teachplanNode;
}
