package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 统一异常捕获类
 * @author:cxg
 * @Date:${time}
 */
@ControllerAdvice//控制增强
public class ExceptionCatch {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);


    //使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;

    //使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder =ImmutableMap.builder();



    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception) {
        //记录日志
        LOGGER.error("catch exception : {}",exception.getMessage());
        if (EXCEPTIONS == null) {
            //EXCEPTIONS 构建成功
            EXCEPTIONS= builder.build();
        }
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        final ResponseResult responseResult;
        if (resultCode!=null){
            responseResult = new ResponseResult(resultCode);
        }else {
            responseResult = new ResponseResult(CommonCode.SERVER_ERROR);
        }
        return responseResult;

    }



    //捕获 CustomException异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException) {

        //记录日志
        LOGGER.error("catch exception : {}", customException.getMessage());
        ResultCode resultCode = customException.getResultCode();
        ResponseResult responseResult = new ResponseResult(resultCode);
        return new ResponseResult(resultCode);
    }

    //在这里加入一些基础的异常类型判断:HttpMessageNotReadableException是第三方框架抛出来的，不是自定义
    static {
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }
}
