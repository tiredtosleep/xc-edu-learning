package com.xuecheng.manage_cms.dao;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;


    /**
     * 存文件
     * @throws FileNotFoundException
     */
    @Test
    public void testGrid() throws FileNotFoundException {
        //要上传的文件
        File file = new File("d:/index_banner.ftl");
        //输入流
        FileInputStream inputStram = new FileInputStream(file);
        //上传
        ObjectId objectId = gridFsTemplate.store(inputStram, "4月21日","");
        String s = objectId.toString();
        System.out.println(s);
    }
    /**
     * 存课程信息模板文件
     * @throws FileNotFoundException
     */
    @Test
    public void testGrid1() throws FileNotFoundException {
        //要上传的文件
        File file = new File("e:/course.ftl");
        //输入流
        FileInputStream inputStram = new FileInputStream(file);
        //上传
        ObjectId objectId = gridFsTemplate.store(inputStram, "课程详情模板文件","");
        String s = objectId.toString();
        System.out.println(s);
    }

    /**
     *取文件
     */
    @Test
    public void queryFile() throws IOException {
        //根据文件id查询文件
        GridFSFile id = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5cb969825b7d4a350838ac31")));
        //打开一个下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(id.getObjectId());
        //创建GridFsResource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(id, gridFSDownloadStream);
        //从流中获取数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(s);
    }

}
