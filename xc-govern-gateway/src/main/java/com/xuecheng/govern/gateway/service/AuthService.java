package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class AuthService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //从头取出jwt令牌
    public String getJwtFromHeader(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)){
            return null;
        }
        //Authorization的格式是    Bearer +jwt
        if (!authorization.startsWith("Bearer ")){
            return null;
        }
        //从第七位开始取
        return authorization.substring(7);
    }

    //从cookie中取token
    public String getTokenFromCookie(HttpServletRequest request){
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        String access_token = map.get("uid");
        if (StringUtils.isEmpty(access_token)){
            return null;
        }
        return access_token;
    }

    //从redis中取出jwt过期时间
    public long getExpire(String access_token){
        String key="user_token:"+access_token;
        Long expire = stringRedisTemplate.getExpire(key);

        return expire;
    }
}
