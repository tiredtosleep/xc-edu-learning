package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Api(value = "用户中心", description = "用户中心管理")
public interface UcenterControllerApi {

    @ApiOperation("根据用户账号")
    public XcUserExt getUserext(String username);
}
