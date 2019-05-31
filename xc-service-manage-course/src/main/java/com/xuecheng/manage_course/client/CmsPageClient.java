package com.xuecheng.manage_course.client;

import com.xuecheng.manage_cms.api.CmsPageAPI;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 远程调用
 * @author:cxg
 * @Date:${time}
 */
@FeignClient("xc-service-manage-cms")
public interface CmsPageClient  extends CmsPageAPI {
}
