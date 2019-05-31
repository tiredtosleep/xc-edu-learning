package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {

    @Autowired
    AuthService authService;
    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;
    /**
     * 登入
     *
     * @param loginRequest
     * @return
     */
    @PostMapping("/userlogin")
    @Override
    public LoginResult login(LoginRequest loginRequest) {

        String password = loginRequest.getPassword();
        String username = loginRequest.getUsername();
        //申请令牌
        AuthToken authToken =  authService.login(username,password,clientId,clientSecret);

        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //将令牌存储到cookie
        this.saveCookie(access_token);

        return new LoginResult(CommonCode.SUCCESS,access_token);
    }

    //将令牌存储到cookie
    private void saveCookie(String token){

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        //设置成uid名字存储
        CookieUtil.addCookie(response,cookieDomain,"/","uid",token,cookieMaxAge,false);

    }
    //删除cookie，有效期设置为0
    private void delCookie(String token){

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        //设置成uid名字存储
        CookieUtil.addCookie(response,cookieDomain,"/","uid",token,0,false);

    }

    /**
     * 退出
     * 1、取到cookie中的token
     * 2、删除redis中的token
     * 3、清除 cookie
     * @return
     */
    @PostMapping("/userlogout")
    @Override
    public ResponseResult logout() {
        //取出令牌
        String access_token = this.getTokenFormCookie();
        //删除redis中的token
        authService.delToken(access_token);

        //清除cookie
        this.delCookie(access_token);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取用户令牌
     * 步骤：
     * 先到浏览器中拿到cookie
     * 然后通过cookie拿到access_token
     * 通过access_token向redis中取到令牌集合
     * @return
     */
    @GetMapping("/userjwt")
    @Override
    public JwtResult userjwt() {
        //取出cookie中用户身份令牌
        String tokenFormCookie = this.getTokenFormCookie();
        if (tokenFormCookie==null){
            return new JwtResult(CommonCode.FAIL,null);
        }

        //拿到身份令牌从redis中查询jwt令牌
        AuthToken userToken = authService.getUserToken(tokenFormCookie);
        if (userToken!=null){
            String jwt_token = userToken.getJwt_token();
            //将jwt令牌返回给用户
            return new JwtResult(CommonCode.SUCCESS,jwt_token);
        }


        return null;
    }

    //取出cookie中的身份令牌
    private String getTokenFormCookie(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if (map!=null&&map.get("uid")!=null){
            String uid = map.get("uid");
            return uid;
        }
        return null;
    }
}
