package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Repository
public interface SysDictionaryDao extends MongoRepository<SysDictionary, String> {
    //根据字典分类type查询字典信息
   SysDictionary findBydType(String type);

}
