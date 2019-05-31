package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 页面
 * @author:cxg
 * @Date:${time}
 */
public interface  CmsPageRepository extends MongoRepository<CmsPage,String> {
    //根据页面名称、站点Id、页面webpath查询
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName, String site, String pageWebpPath);
}
