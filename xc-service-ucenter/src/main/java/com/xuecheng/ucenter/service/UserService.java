package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.domain.ucenter.response.UcenterCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class UserService {
    @Autowired
    XcCompanyUserRepository xcCompanyUserRepository;
    @Autowired
    XcUserRepository xcUserRepository;
    @Autowired
    XcMenuMapper xcMenuMapper;

    public XcUserExt getUserext(String username) {

        //根据账号查询用户信息
        XcUser xcUser = this.findByUsername(username);
        if (xcUser==null){
            return null;
        }
        //用户id
        String id = xcUser.getId();
        //根据id查询拥有所拥有的权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(id);
        //根据用户id查询公司所属id
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findXcCompanyUserByUserId(id);
        //取到公司id
        String companyId=null;
        if (xcCompanyUser==null){
            ExceptionCast.cast(UcenterCode.UCENTER_COMPANYID_NULL);
        }
        companyId=xcCompanyUser.getCompanyId();
        XcUserExt xcUserExt = new XcUserExt();
        //将xcUser拷贝到xcUserExt中
        BeanUtils.copyProperties(xcUser,xcUserExt);
        xcUserExt.setCompanyId(companyId);
        //设置权限
        xcUserExt.setPermissions(xcMenus);
        return xcUserExt;
    }
    private XcUser findByUsername(String username){
        return xcUserRepository.findXcUserByUsername(username);
    }
}
