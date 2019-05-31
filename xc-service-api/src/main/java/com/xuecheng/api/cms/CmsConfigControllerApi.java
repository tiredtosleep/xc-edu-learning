package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */

@Api(value = "cms配置管理接口",description = "cms配置管理接口，提供数据模型的管理，查询接口")
public interface CmsConfigControllerApi {

    @ApiOperation("根据id查询cms配置信息")
    @GetMapping("/getmodel/{id}")
    public CmsConfig getmodel(@PathVariable("id") String id);
}
