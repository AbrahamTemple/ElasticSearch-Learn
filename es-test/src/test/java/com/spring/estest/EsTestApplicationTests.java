package com.spring.estest;

import com.alibaba.fastjson.JSON;
import com.spring.estest.entity.Users;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class EsTestApplicationTests {

    @Resource
    @Qualifier("restHighLevelClient")
    RestHighLevelClient client;

    @Test /* 创建一个空索引 */
    void testCreateIndex() throws IOException {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("test1");
        //获得索引响应
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        //输出结果
        System.out.println(response);
    }

    @Test /* 索引是否存在 */
    void testIndexExist() throws IOException {
        //获得索引响应
        GetIndexRequest request = new GetIndexRequest("test1");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        //输出结果
        System.out.println(exists);
    }

    @Test /* 删除索引 */
    void testDeleteIndex() throws IOException {
        //获得索引响应
        DeleteIndexRequest request = new DeleteIndexRequest("test1");
        AcknowledgedResponse deleted = client.indices().delete(request, RequestOptions.DEFAULT);
        //输出删除结果
        System.out.println(deleted.isAcknowledged());
    }

    @Test /* 插入文档数据 */
    void testInsert() throws IOException {
        //请求目标索引
        IndexRequest request = new IndexRequest("test1");
        //设置连接超时时限
        request.timeout(TimeValue.timeValueSeconds(1));
        //创建文档名（默认是20位由英文数字组成的名）
        request.id("1");
        Users users = new Users("普洛意",28,"女");
        //将数据对象放入这个请求当中的文档去
        request.source(JSON.toJSONString(users), XContentType.JSON);
        //获取响应结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        //请求结束状态
        System.out.println(response.status());

    }

    @Test /* 文档是否存在 */
    void testDocumentExist() throws IOException {
        //获取文档响应
        GetRequest request = new GetRequest("test1","1");
        //不获取_source返回的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        //显式指定将返回的存储字段（_source中的字段）
        request.storedFields("_none_");
        boolean exists = client.exists(request,RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test /* 查询索引 */
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("test1","1");
        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
        //获取数据的json字符串
        System.out.println(documentFields.getSourceAsString());
        //获取包括_source所有值的json字符串
        System.out.println(documentFields);
    }

    @Test /* 更新数据 */
    void testUpdate() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("test1","1");
        updateRequest.timeout("1s");
        Users users = new Users("小意",27,"女");
        updateRequest.doc(JSON.toJSONString(users),XContentType.JSON);
        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    @Test /* 删除文档 */
    void testDelete() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("test1","1");
        deleteRequest.timeout("1s");
        DeleteResponse deleted = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleted.status());
    }

    @Test /* 批量插入文档 */
    void testBulkInsert() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<Users> usersList = new ArrayList<>();
        usersList.add(new Users("卖萌",23,"男"));
        usersList.add(new Users("达记",20,"男"));
        usersList.add(new Users("鸭哥",24,"男"));
        usersList.add(new Users("狗博",21,"男"));
        usersList.add(new Users("小意",28,"女"));

        for (int i = 0; i < usersList.size(); i++) {
            bulkRequest.add(new IndexRequest("test1")
                    .id(i+1+"") //文档是字符串
                    .source(JSON.toJSONString(usersList.get(i)),XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());
    }

    @Test /* 复杂查询 */
    void testQuerySelect() throws IOException {
        SearchRequest searchRequest = new SearchRequest("test1");
        //复杂条件搜索器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /*
            QueryBuilders：复杂条件构建器
            term：精确查询 ; match：分词查询
        */
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "意");
        //把构建好的复杂条件放到这个搜索器内
        sourceBuilder.query(matchQueryBuilder);
        /* 附加高亮 */
        HighlightBuilder highlightBuilder = new HighlightBuilder(); //高亮构建器
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(false); //显示多个高亮
        highlightBuilder.preTags("<p style='color:red'>");
        highlightBuilder.postTags("</p>");
        //将构建好的高亮词放置搜索器内
        sourceBuilder.highlighter(highlightBuilder);
        /*
            附加分页
            第一页：sourceBuilder.from(1);
            两条数据：sourceBuilder.size(2);
        */
        sourceBuilder.timeout(new TimeValue(30, TimeUnit.SECONDS));
        //把构建好的搜索条件放在请求里
        searchRequest.source(sourceBuilder);
        //响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //输出Json字符串数据结果
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        //拆分hits中的数据至数组
        SearchHit[] hits = searchResponse.getHits().getHits();
        //递归每一条文档以Map格式输出（附带高亮显示）
        for (SearchHit hit : hits) {
            //获取高亮集合
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //在高亮集合中获取字段为name的高亮词
            HighlightField name = highlightFields.get("name");
            //获取原集合
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //如果高亮词存在
            if(name!=null){
                //得到高亮词碎片
                Text[] fragments = name.fragments();
                System.out.println(fragments.length);
                String highLightName = "";
                for (Text fragment : fragments) {
                    //组建高亮词碎片
                    highLightName += fragment;
                }
                //更新到原集合对应的词
                sourceAsMap.put("name", highLightName);
            } System.out.println(sourceAsMap);
        }
    }
}
