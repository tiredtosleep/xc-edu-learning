package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 操作mongodb
 * @author:cxg
 * @Date:${time}
 */
public interface CmsStieRepository extends MongoRepository<CmsSite,String> {
}
