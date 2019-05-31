package com.xuecheng.ucenter;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.ucenter.dao.UserMapper;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {

    @Autowired
    UserMapper userMapper;

    @Autowired
    XcMenuMapper xcMenuMapper;
    @Test
    public void testCreateJwt(){
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId("49");
        System.out.println(xcMenus);
    }

}
