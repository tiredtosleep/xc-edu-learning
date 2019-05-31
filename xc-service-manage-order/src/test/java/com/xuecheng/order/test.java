package com.xuecheng.order;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.dao.XcTaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class test {
    @Autowired
    XcTaskRepository xcTaskRepository;
    @Test
    public void test(){
        HashMap<String, String> map = new HashMap<>();
        //"49","courseId":"4028e581617f945f01617f9dabc40000"}
        map.put("userId","49");
        map.put("courseId","4028e581617f945f01617f9dabc40000");
        String jsonString = JSON.toJSONString(map);
        XcTask xcTask = new XcTask();
        xcTask.setRequestBody(jsonString);
        xcTaskRepository.save(xcTask);
    }

}

