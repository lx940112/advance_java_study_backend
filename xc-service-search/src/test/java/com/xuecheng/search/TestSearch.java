package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;

    /**
     * 搜索type下的全部记录
     *
     * @throws IOException
     */
    @Test
    public void testSearchAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        printSearchResult(searchResponse);
    }


    /**
     * 分页查询
     *
     * @throws IOException
     */
    @Test
    public void testSearchByPage() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //分页查询，设置起始下标，从0开始
        searchSourceBuilder.from(0);
        //每页显示个数
        searchSourceBuilder.size(10);
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        printSearchResult(searchResponse);
    }

    /**
     * Term Query为精确查询
     * 在搜索时会整体匹配关键字，不再将关键字分词。
     *
     * @throws IOException
     */
    @Test
    public void testTermQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name", "spring"));
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        printSearchResult(searchResponse);
    }

    /**
     * 根据多个id值匹配
     *
     * @throws IOException
     */
    @Test
    public void testQueryById() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] split = new String[]{"1", "2"};
        List<String> idList = Arrays.asList(split);
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id", idList));
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        printSearchResult(searchResponse);
    }


    /**
     * Match Query为精确查询
     * 先将搜索字符串分词，再使用各各词条从索引中搜索
     *
     * @throws IOException
     */
    @Test
    public void testMatchQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        //匹配关键字
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring开发").operator(Operator.OR));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        printSearchResult(searchResponse);
    }

    @Test
    public void testMatchQuery2() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        //匹配关键字
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("description", "前台页面开发框架 架构").minimumShouldMatch("80%");//设置匹配占比
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        printSearchResult(searchResponse);
    }


    /**
     * multiQuery，一次可以匹配多个字段。
     * 匹配多个字段时可以提升字段的boost（权重）来提高得分
     *
     * @throws IOException
     */
    @Test
    public void testMultiQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        //匹配关键字
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description").minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name", 10);//提升boost
        searchSourceBuilder.query(multiMatchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        printSearchResult(searchResponse);
    }

    /**
     * BooleanQuery查询，实现将多个查询组合起来。
     * - must：文档必须匹配must所包括的查询条件，相当于 “AND”
     * - should：文档应该匹配should所包括的查询条件其中的一个或多个，相当于 "OR"
     * - must_not：文档不能匹配must_not所包括的该查询条件，相当于“NOT”
     *
     * @throws IOException
     */
    @Test
    public void testBooleanQuery() throws IOException {
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        //创建搜索源配置对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(new String[]{"name", "pic", "studymodel"}, new String[]{});
        //multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "name", "description").minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name", 10);
        //TermQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        //设置布尔查询对象
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);//设置搜索源配置
        SearchResponse searchResponse = client.search(searchRequest);
        printSearchResult(searchResponse);
    }

    /**
     * 布尔查询使用过虑器
     *
     * @throws IOException
     */
    @Test
    public void testFilter() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "description"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        //匹配关键字
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description");
        //设置匹配占比
        multiMatchQueryBuilder.minimumShouldMatch("50%");
        //提升另个字段的Boost值
        multiMatchQueryBuilder.field("name", 10);
        searchSourceBuilder.query(multiMatchQueryBuilder);
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(searchSourceBuilder.query());
        //过虑
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel", "201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        //设置布尔查询对象
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);//设置搜索源配置
        SearchResponse searchResponse = client.search(searchRequest);
        printSearchResult(searchResponse);
    }

    /**
     * 排序
     *
     * @throws IOException
     */
    @Test
    public void testSort() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "description"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //过虑
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        //排序
        searchSourceBuilder.sort(new FieldSortBuilder("studymodel").order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));
        SearchResponse searchResponse = client.search(searchRequest);
        printSearchResult(searchResponse);
    }


    /**
     * 高亮显示
     *
     * @throws IOException
     */
    @Test
    public void testHighlight() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "description"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        //匹配关键字
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发", "name", "description");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(searchSourceBuilder.query());
        //过虑
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        //排序
        searchSourceBuilder.sort(new FieldSortBuilder("studymodel").order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));
        //高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");//设置前缀
        highlightBuilder.postTags("</tag>");//设置后缀
        // 设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        // highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        searchSourceBuilder.highlighter(highlightBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //名称
            String name = (String) sourceAsMap.get("name");
            //取出高亮字段内容
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null) {
                    Text[] fragments = nameField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text str : fragments) {
                        stringBuffer.append(str.string());
                    }
                    name = stringBuffer.toString();
                }
            }
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }


    /**
     * 打印查询结果
     *
     * @param searchResponse
     */
    private void printSearchResult(SearchResponse searchResponse) {
        SearchHits hits = searchResponse.getHits();
        System.err.println(hits.getTotalHits());
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }


}