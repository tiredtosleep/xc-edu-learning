package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 课程分类查询
 * @author:cxg
 * @Date:${time}
 */
@RestController
@RequestMapping("/category")
public class CategoryController implements CategoryControllerApi{
    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    @Override
    public CategoryNode findCategory() {
        return categoryService.findCategory();

    }
}
