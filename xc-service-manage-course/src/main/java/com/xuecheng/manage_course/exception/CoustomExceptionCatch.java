package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 课程自定义类型
 * @author:cxg
 * @Date:${time}
 */
@ControllerAdvice//控制增强
public class CoustomExceptionCatch extends ExceptionCatch {
    //AccessDeniedException抛出问题
    static {
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }
}
