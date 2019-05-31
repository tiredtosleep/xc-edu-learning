package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 自定义异常 类型
 * @author:cxg
 * @Date:${time} RuntimeException对代码没有侵入性
 */
public class CustomException extends RuntimeException {
    //错误代码
    ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
    public ResultCode getResultCode(){
        return this.resultCode;
    }

}
