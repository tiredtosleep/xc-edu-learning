package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 站点
 * @author:cxg
 * @Date:${time}
 */
public interface CmsStieRepository extends MongoRepository<CmsSite,String> {
}
