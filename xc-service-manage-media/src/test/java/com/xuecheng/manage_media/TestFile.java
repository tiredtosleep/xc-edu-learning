package com.xuecheng.manage_media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestFile {
    //测试文件分块
    @Test
    public void testChunk() throws IOException {
        //源文件
        File file = new File("G:\\Java-webspace\\xc_edu\\develop\\video\\11.mp4");
        //块目录
        String chunkFileFolder = "G:\\Java-webspace\\xc_edu\\develop\\video\\chunks\\";
        //定义块文件大小
        long chunkFileSize = 1 * 1024 * 1024;
        //块数
        long chunkFileSizeNum = (long) Math.ceil(file.length() * 1.0 / chunkFileSize);
        //创建读文件的对象
        RandomAccessFile raf_read = new RandomAccessFile(file, "r");

        //缓存区
        byte[] b = new byte[1024];
        for (int i=0;i<chunkFileSizeNum;i++){
            //块文件
            File chunkFile = new File(chunkFileFolder + i);
            //创建块写对象
            RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
            int len=-1;
            while ((len = raf_read.read(b)) != -1) {
                //开始写文件
                raf_write.write(b, 0, len);
                if (chunkFile.length()>=chunkFileSize){
                    break;
                }
            }
            raf_write.close();
        }
        raf_read.close();


    }

    //测试文件合并
    @Test
    public void testMergeFile() throws IOException {
        //块文件目录
        File chunkFileFolder = new File("G:\\Java-webspace\\xc_edu\\develop\\video\\chunks\\");
        //块文件列表
        File[] files = chunkFileFolder.listFiles();
        //将块文件排序，按名称排序。升序
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            //定义排序规则
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                    //降序
                    return 1;
                }
                //升序
                return -1;
            }
        });

        //合并文件路径和名字
        File mergeFile = new File("G:\\Java-webspace\\xc_edu\\develop\\video\\new.mp4");
        //创建新文件
        boolean newFile = mergeFile.createNewFile();

        //创建读写对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");

        //缓存区
        byte[] b = new byte[1024];
        for (File chunkFile : fileList) {
            //创建读块文件
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
            int len=-1;
            while ((len = raf_read.read(b)) != -1) {
                //开始写文件
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();


    }

}
