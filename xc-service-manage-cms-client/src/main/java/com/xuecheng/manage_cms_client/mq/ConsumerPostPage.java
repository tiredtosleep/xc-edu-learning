package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 监听MQ，接收页面发布消息
 * @author:cxg
 * @Date:${time}
 */
@Slf4j
@Component
public class ConsumerPostPage {
    @Autowired
    PageService pageService;
    @Autowired
    CmsPageRepository cmsPageRepository;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg) throws IOException {
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        //得到消息中的页面id
        String pageId =(String) map.get("pageId");
        //校验pageId是否存在
        Optional<CmsPage> byId = cmsPageRepository.findById(pageId);
        if (!byId.isPresent()){
            log.error("receive cms post page,cmsPage is null:{}\",msg.toString()");
        }
        //调用service方法将页面从GridFs中下载到服务器
        pageService.savePageToServerPath(pageId);

    }
}
