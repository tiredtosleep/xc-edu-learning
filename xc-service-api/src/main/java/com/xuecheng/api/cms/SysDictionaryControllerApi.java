package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Api(value = "课程的管理接口", description = "数据字典查询接口")
public interface SysDictionaryControllerApi {
    @ApiOperation(value="数据字典查询接口")
    public SysDictionary getByType(String type);
}
