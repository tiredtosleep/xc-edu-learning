package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class CmsConfigService {

    @Autowired
    CmsConfigRepository cmsConfigRepository;

    public CmsConfig getmodel(String id) {
        Optional<CmsConfig> byId = cmsConfigRepository.findById(id);
        if (byId.isPresent()){
            CmsConfig cmsConfig = byId.get();
            return cmsConfig;
        }
        return null;
    }
}
