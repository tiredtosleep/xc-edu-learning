package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public interface XcTaskRepository extends JpaRepository<XcTask,String> {
    //查询某个时间之间的前n条任务
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);

/*
    //自动更新时间（没调用）
    @Modifying
    @Query("update XcTask  t set t.updateTime = :updateTime where t.id= :id")
    public int updateTaskTime(@Param("id") String id, @Param("updateTime")Date updateTime);
*/


    //使用乐观锁方式校验任务id和版本号是否匹配，匹配则版本号加1
    @Modifying
    @Query("update XcTask  t set t.version = :version+1 where t.id= :id and t.version= :version")
    public int updateVersion(@Param("id") String id, @Param("version")int version);

}
