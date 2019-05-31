package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResultCode;


/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 定义异常抛出类
 * @author:cxg
 * @Date:${time}
 */
public class ExceptionCast {
    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }
}
