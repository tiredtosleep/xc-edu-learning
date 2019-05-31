package com.xuecheng.order.service;


import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
public class TaskService {
    @Autowired
    XcTaskRepository xcTaskRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;
    /**
     * 查询前n条记录
     *
     * @return
     */
    public List<XcTask> findXcTaskList(Date updateTime, int size) {
        //设置分页参数
        Pageable pageable=new PageRequest(0,size);
        Page<XcTask> xcTasks = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        return xcTasks.getContent();
    }

    /**
     * 发消息
     * @param xcTask
     * @param ex         交换机
     * @param routingKey 路由key
     */
    @Transactional
    public void publish(XcTask xcTask, String ex, String routingKey) {

        Optional<XcTask> byId = xcTaskRepository.findById(xcTask.getId());
        if (byId.isPresent()){
            rabbitTemplate.convertAndSend(ex, routingKey, xcTask);
            //更新任务时间
            XcTask one = byId.get();
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }

    }

    /**
     * updateVersion: 使用乐观锁方式校验任务id和版本号是否匹配，匹配则版本号加1
     * 判断是否成功
     * @param id
     * @param version
     * @return
     */
    @Transactional
    public int getTask(String id,int version){
        int i = xcTaskRepository.updateVersion(id, version);
        return i;
    }
    //完成课程
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> optional = xcTaskRepository.findById(taskId);
        if (optional.isPresent()) {
            //当前任务
            XcTask xcTask = optional.get();
            //历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }
}
