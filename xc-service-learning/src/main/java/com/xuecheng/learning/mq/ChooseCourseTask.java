package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.service.LearningService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Component
public class ChooseCourseTask {
    @Autowired
    LearningService learningService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 接收xc-service-manage-order发送的添加选课消息
     * @param xcTask
     */
    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE)
    public void receiveChoosecourseTask(XcTask xcTask) throws ParseException {

        //取出requestBody对象
            String requestBody = xcTask.getRequestBody();
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = (String) map.get("userId");
            String courseId = (String) map.get("courseId");
        //String userId, String courseId, String valid, Date startTime,Date endTime, XcTask xcTask

            String valid = (String) map.get("valid");
/*        //时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            Date startTime = null;
            Date endTime = null;

            if (StringUtils.isNotEmpty((String) map.get("startTime"))) {
                startTime = simpleDateFormat.parse((String) map.get("startTime"));
            }
            if (StringUtils.isNotEmpty((String) map.get("endTime"))){
                endTime= simpleDateFormat.parse((String) map.get("endTime"));
            }*/
            //添加选课
            ResponseResult addcourse = learningService.addcourse(userId, courseId, valid, null, null, xcTask);
            //添加选课消息成功则发送消息
            if (addcourse.isSuccess()){
                rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE,RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY,xcTask);
            }




    }
}
