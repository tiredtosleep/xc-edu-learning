package com.xuecheng.auth.client;

import com.xuecheng.ucenter.api.UcenterApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@FeignClient("xc-service-ucenter")
public interface UcenterClient extends UcenterApi{
}
