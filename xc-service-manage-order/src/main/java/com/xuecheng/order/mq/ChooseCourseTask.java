package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.dao.XcTaskRepository;
import com.xuecheng.order.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description: 定时任务的注解开发
 * @author:cxg
 * @Date:${time}
 */
@Slf4j
@Component
public class ChooseCourseTask {

    @Autowired
    TaskService taskService;
    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * /得到一分钟前的时间，并发送mq消息给xc-service-learning
     */
    @Scheduled(cron = "0/3 * * * * *")//每隔3秒执行
    public void sendChoosecourseTask() {
        //1、得到时间
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(GregorianCalendar.MINUTE, -1);//1分钟之前的时间
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findXcTaskList(time, 100);
        System.out.println(xcTaskList);
        //2、调用service发布消息，将添加选课的任务发送给mq
        for (XcTask xcTask : xcTaskList) {
            if (taskService.getTask(xcTask.getId(),xcTask.getVersion())>0){
                //要发送的交换机，发送消息要带routingKey
                taskService.publish(xcTask,xcTask.getMqExchange(),xcTask.getMqRoutingkey());
            }
        }
    }

    /**
     * 接收xc-service-learning完成选课消息
     * @param xcTask
     */
    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChoosecourseTask(XcTask xcTask) {
        if (xcTask!=null|| StringUtils.isNotEmpty(xcTask.getId())){
            //完成删除xcTask中的消息
            taskService.finishTask(xcTask.getId());
        }
    }
}
