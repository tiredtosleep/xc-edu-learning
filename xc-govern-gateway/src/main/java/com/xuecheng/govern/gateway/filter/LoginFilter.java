package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 身份验证过滤器
 * @author:cxg
 * @Date:${time}
 */
@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    AuthService authService;

    // filterType：返回字符串代表过滤器的类型，如下 pre：请求在被路由之前
    //执行 routing：在路由请求时调用 post：在routing和errror过滤器之后调用 error：处理请求时发生错误调用
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    //filterOrder：此方法返回整型数值，通过此数值来定义过滤器的执行顺序，数字越小优先级越高。
    @Override
    public int filterOrder() {
        return 0;
    }

    //shouldFilter：返回一个Boolean值，判断该过滤器是否需要执行。返回true表示要执行此过虑器，否则不执
    //行。
    @Override
    public boolean shouldFilter() {
        return true;
    }


    /**
     * 过滤器的业务逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //得到request
        HttpServletRequest request = requestContext.getRequest();
        //得到response
        HttpServletResponse response = requestContext.getResponse();
       //1、取cookie中身份令牌
        String access_token = authService.getTokenFromCookie(request);
        if (StringUtils.isEmpty(access_token)){
            this.access_denied();
        }
        //2、从header中取jwt
        String jwt = authService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(jwt)){
            this.access_denied();
        }
        //3、从redis中取出过期时间
        long expire = authService.getExpire(access_token);
        if (expire<0){
            this.access_denied();
        }
        return null;
    }

    //拒绝访问代码
    private void access_denied() {
        //上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //拒绝访问
        currentContext.setSendZuulResponse(false);
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String jsonString = JSON.toJSONString(responseResult);
        currentContext.setResponseBody(jsonString);
        //设置状态码
        currentContext.setResponseStatusCode(200);
        HttpServletResponse response = currentContext.getResponse();
        response.setContentType("application/json;charset=utf-8");
    }

}
