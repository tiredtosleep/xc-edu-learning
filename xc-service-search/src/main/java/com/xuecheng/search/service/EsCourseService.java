package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class EsCourseService {

    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.course.source_field}")
    private String source_field;

    @Value("${xuecheng.media.index}")
    private String media_index;
    @Value("${xuecheng.media.type}")
    private String media_type;
    @Value("${xuecheng.media.source_field}")
    private String media_source_field;






    @Autowired
    RestHighLevelClient restHighLevelClient;

    //课程搜索
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        if(courseSearchParam == null){
            courseSearchParam = new CourseSearchParam();
        }
        //1、创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //2、设置搜索类型
        searchRequest.types(type);

        //3、构建SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //过虑源字段
        String[] source_field_array = source_field.split(",");
        searchSourceBuilder.fetchSource(source_field_array,new String[]{});
        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //4、搜索条件
        //4.1 根据关键字搜索
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%")
                    //提升另个字段的Boost值
                    .field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        //4.2 通过过滤器
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
            //4.2.1 根据一级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }

        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
            //4.2.2 根据二级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }

        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            //4.2.3 根据难度等级
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }

        //设置boolQueryBuilder到searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);

        //7.设置分页
        if(page<=0){
            page=1;
        }
        if (size<=0){
            size=12;
        }
        int from=(page-1)*size;
        searchSourceBuilder.from(from);//起始下标
        searchSourceBuilder.size(size);//每页总数

        //8、设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        //设置高亮
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);


        searchRequest.source(searchSourceBuilder);

        //5 创建结果集
        QueryResult<CoursePub> queryResult = new QueryResult();
        List<CoursePub> list = new ArrayList<>();
        try {
            //5.1 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //5.2 获取响应结果（得到搜索结果，接下来下面对数据处理）
            SearchHits hits = searchResponse.getHits();
            //匹配的总记录数
            long totalHits = hits.totalHits;
            queryResult.setTotal(totalHits);
            SearchHit[] searchHits = hits.getHits();
            //6 处理搜索结果的数据
            for(SearchHit hit:searchHits){
                CoursePub coursePub = new CoursePub();
                //6.1 源文档
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取到id
                String id = (String) sourceAsMap.get("id");
                coursePub.setId(id);
                //6.2 取出name
                String name = (String) sourceAsMap.get("name");
                //取出高亮
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(highlightFields!=null){
                    //高亮name字段
                    HighlightField highlightFieldName = highlightFields.get("name");
                    if (highlightFieldName!=null){
                        Text[] fragments = highlightFieldName.fragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for (Text fragment : fragments) {
                            stringBuffer.append(fragment);
                        }
                        name=stringBuffer.toString();
                    }

                }
                coursePub.setName(name);

                //6.3 图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //6.4 价格
                Double price = null;
                try {
                    if(sourceAsMap.get("price")!=null ){
                        price = (Double) sourceAsMap.get("price");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                //6.5 旧价格
                Double price_old = null;
                try {
                    if(sourceAsMap.get("price_old")!=null ){
                        price_old = (Double) sourceAsMap.get("price_old");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                //将coursePub对象放入list
                list.add(coursePub);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        queryResult.setList(list);
        QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);

        return queryResponseResult;
    }

    //使用ES的客户端向Es请求查询索引信息
    public Map<String, CoursePub> getall(String id) {
        //1、创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //2、设置搜索类型
       searchRequest.types(type);
        //3、构建SearchSourceBuilder：设置对源文档的搜索方法
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //设置使用termQuery
        searchSourceBuilder.query(QueryBuilders.termQuery("id",id));
        searchRequest.source(searchSourceBuilder);
        //最终要返回的课程信息
        CoursePub coursePub = new CoursePub();
        Map<String,CoursePub> map = new HashMap<>();
        try {

            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            //取出匹配记录
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                //课程id
                String courseId = (String) sourceAsMap.get("id");
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                map.put(courseId,coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    //根据多个课程计划查询课程媒资
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
        //1、创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(media_index);
        //2、设置搜索类型
        searchRequest.types(media_type);
        //3、构建SearchSourceBuilder：设置对源文档的搜索方法
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termsQuery根据多个id查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));

        //过虑源字段
        String[] include = media_source_field.split(",");
        //前面是包括，后面是排除那些字段
        searchSourceBuilder.fetchSource(include,new String[]{});
        searchRequest.source(searchSourceBuilder);

        List<TeachplanMediaPub> list=new ArrayList<>();
        long total=0;
        try {
            //执行搜索
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            total = hits.getTotalHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit searchHit : searchHits) {
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                //取源数据
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");

                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                list.add(teachplanMediaPub);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(list);
        queryResult.setTotal(total);
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<TeachplanMediaPub>(CommonCode.SUCCESS,queryResult);


        return queryResponseResult;
    }
}
