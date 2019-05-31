package com.xuecheng.manage_course.controller;

import com.xuecheng.api.cms.SysDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RestController
@RequestMapping("/sys")
public class SysDictionaryController implements SysDictionaryControllerApi {

    /**
     * 根据字典分类查询字典信息
     */
    @Autowired
    SysDictionaryService sysDictionaryService;


    @Override
    @GetMapping("/dictionary/{type}")
    public SysDictionary getByType(@PathVariable("type") String type) {
        return sysDictionaryService.findByType(type);
    }
}
