package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;

    /**
     * 创建索引库
     *
     * @throws IOException
     */
    @Test
    public void testCreateIndex() throws IOException {
        //创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course_test1");
        //设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", 1)
                .put("number_of_replicas", 0));
        //设置映射
        createIndexRequest.mapping("doc", MAPPING_JSON2, XContentType.JSON);
        //创建索引操作客户端
        IndicesClient indices = client.indices();
        //创建响应对象
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        //得到响应结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    /**
     * 删除索引库
     *
     * @throws IOException
     */
    @Test
    public void testDeleteIndex() throws IOException {
        //删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course_test1");
        //删除索引
        AcknowledgedResponse delete = client.indices().delete(deleteIndexRequest);
        //删除索引响应结果
        boolean acknowledged = delete.isAcknowledged();
        System.out.println(acknowledged);
    }


    /**
     * 添加文档
     *
     * @throws IOException
     */
    @Test
    public void testAddDoc() throws IOException {
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        //索引请求对象
        IndexRequest indexRequest = new IndexRequest("xc_course_test1", "doc");
        //指定索引文档内容
        indexRequest.source(jsonMap);
        //索引响应对象
        IndexResponse indexResponse = client.index(indexRequest);
        //获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);
    }

    /**
     * 查询文档
     *
     * @throws IOException
     */
    @Test
    public void getDoc() throws IOException {
        GetRequest getRequest = new GetRequest(
                "xc_course_test1",
                "doc",
                "uJYtAWoBnMRh5klKgrkb");
        GetResponse getResponse = client.get(getRequest);
        boolean exists = getResponse.isExists();
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println(exists);
        System.out.println(sourceAsMap);
    }


    /**
     * 根据id删除文档
     * @throws IOException
     */
    @Test
    public void testDelDoc() throws IOException {
        //删除文档id
        String id = "uJYtAWoBnMRh5klKgrkb";
        //删除索引请求对象
        DeleteRequest deleteRequest = new DeleteRequest("xc_course_test1", "doc", id);
        //响应对象
        DeleteResponse deleteResponse = client.delete(deleteRequest);
        //获取响应结果
        DocWriteResponse.Result result = deleteResponse.getResult();
        System.out.println(result);
    }


    private static final String MAPPING_JSON1 = "{\n" +
            "    \"properties\":{\n" +
            "        \"description\":{\n" +
            "            \"type\":\"text\",\n" +
            "            \"analyzer\":\"ik_max_word\",\n" +
            "            \"search_analyzer\":\"ik_smart\"\n" +
            "        },\n" +
            "        \"name\":{\n" +
            "            \"type\":\"text\",\n" +
            "            \"analyzer\":\"ik_max_word\",\n" +
            "            \"search_analyzer\":\"ik_smart\"\n" +
            "        },\n" +
            "        \"price\":{\n" +
            "            \"type\":\"float\"\n" +
            "        },\n" +
            "        \"studymodel\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"timestamp\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy‐MM‐dd HH:mm:ss||yyyy‐MM‐dd||epoch_millis\"\n" +
            "        }\n" +
            "    }\n" +
            "}";
    private static final String MAPPING_JSON2 = "{\n" +
            "    \"properties\":{\n" +
            "        \"description\":{\n" +
            "            \"type\":\"text\",\n" +
            "            \"analyzer\":\"ik_max_word\",\n" +
            "            \"search_analyzer\":\"ik_smart\"\n" +
            "        },\n" +
            "        \"name\":{\n" +
            "            \"type\":\"text\",\n" +
            "            \"analyzer\":\"ik_max_word\",\n" +
            "            \"search_analyzer\":\"ik_smart\"\n" +
            "        },\n" +
            "        \"price\":{\n" +
            "            \"type\":\"float\"\n" +
            "        },\n" +
            "        \"studymodel\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        }\n" +
            "    }\n" +
            "}";








    /**
     * 添加文档
     *
     * @throws IOException
     */
    @Test
    public void testAddDoc2() throws IOException {
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        //索引请求对象
        IndexRequest indexRequest = new IndexRequest("xc_course", "doc");
        //指定索引文档内容
        indexRequest.source(jsonMap);
        //索引响应对象
        IndexResponse indexResponse = client.index(indexRequest);
        //获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);
    }
}