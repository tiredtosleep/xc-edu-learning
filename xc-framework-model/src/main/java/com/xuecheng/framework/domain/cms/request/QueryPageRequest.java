package com.xuecheng.framework.domain.cms.request;

import lombok.Data;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 接收页面查询的查询条件
 * @author:cxg
 * @Date:${time}
 */
@Data
public class QueryPageRequest {
    	//站点id
        private String siteId;
        //页面ID
        private  String pageId;
        //页面名称
        private String pageName;
        //别名
        private String pageAliase;
        //模版id
       private String templateId;
}
