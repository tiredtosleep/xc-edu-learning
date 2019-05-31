package com.xuecheng.learning.client;

import com.xuecheng.search.api.EsCourseApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@FeignClient("xc-search-service")
public interface EsCourseClient extends EsCourseApi {
}
