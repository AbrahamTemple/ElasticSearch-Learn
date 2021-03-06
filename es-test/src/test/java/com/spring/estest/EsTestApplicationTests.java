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

    @Test /* ????????????????????? */
    void testCreateIndex() throws IOException {
        //??????????????????
        CreateIndexRequest request = new CreateIndexRequest("test1");
        //??????????????????
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        //????????????
        System.out.println(response);
    }

    @Test /* ?????????????????? */
    void testIndexExist() throws IOException {
        //??????????????????
        GetIndexRequest request = new GetIndexRequest("test1");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        //????????????
        System.out.println(exists);
    }

    @Test /* ???????????? */
    void testDeleteIndex() throws IOException {
        //??????????????????
        DeleteIndexRequest request = new DeleteIndexRequest("test1");
        AcknowledgedResponse deleted = client.indices().delete(request, RequestOptions.DEFAULT);
        //??????????????????
        System.out.println(deleted.isAcknowledged());
    }

    @Test /* ?????????????????? */
    void testInsert() throws IOException {
        //??????????????????
        IndexRequest request = new IndexRequest("test1");
        //????????????????????????
        request.timeout(TimeValue.timeValueSeconds(1));
        //???????????????????????????20?????????????????????????????????
        request.id("1");
        Users users = new Users("?????????",28,"???");
        //???????????????????????????????????????????????????
        request.source(JSON.toJSONString(users), XContentType.JSON);
        //??????????????????
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        //??????????????????
        System.out.println(response.status());

    }

    @Test /* ?????????????????? */
    void testDocumentExist() throws IOException {
        //??????????????????
        GetRequest request = new GetRequest("test1","1");
        //?????????_source??????????????????
        request.fetchSourceContext(new FetchSourceContext(false));
        //???????????????????????????????????????_source???????????????
        request.storedFields("_none_");
        boolean exists = client.exists(request,RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test /* ???????????? */
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("test1","1");
        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
        //???????????????json?????????
        System.out.println(documentFields.getSourceAsString());
        //????????????_source????????????json?????????
        System.out.println(documentFields);
    }

    @Test /* ???????????? */
    void testUpdate() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("test1","1");
        updateRequest.timeout("1s");
        Users users = new Users("??????",27,"???");
        updateRequest.doc(JSON.toJSONString(users),XContentType.JSON);
        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    @Test /* ???????????? */
    void testDelete() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("test1","1");
        deleteRequest.timeout("1s");
        DeleteResponse deleted = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleted.status());
    }

    @Test /* ?????????????????? */
    void testBulkInsert() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<Users> usersList = new ArrayList<>();
        usersList.add(new Users("??????",23,"???"));
        usersList.add(new Users("??????",20,"???"));
        usersList.add(new Users("??????",24,"???"));
        usersList.add(new Users("??????",21,"???"));
        usersList.add(new Users("??????",28,"???"));

        for (int i = 0; i < usersList.size(); i++) {
            bulkRequest.add(new IndexRequest("test1")
                    .id(i+1+"") //??????????????????
                    .source(JSON.toJSONString(usersList.get(i)),XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());
    }

    @Test /* ???????????? */
    void testQuerySelect() throws IOException {
        SearchRequest searchRequest = new SearchRequest("test1");
        //?????????????????????
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /*
            QueryBuilders????????????????????????
            term??????????????? ; match???????????????
        */
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "???");
        //???????????????????????????????????????????????????
        sourceBuilder.query(matchQueryBuilder);
        /* ???????????? */
        HighlightBuilder highlightBuilder = new HighlightBuilder(); //???????????????
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(false); //??????????????????
        highlightBuilder.preTags("<p style='color:red'>");
        highlightBuilder.postTags("</p>");
        //??????????????????????????????????????????
        sourceBuilder.highlighter(highlightBuilder);
        /*
            ????????????
            ????????????sourceBuilder.from(1);
            ???????????????sourceBuilder.size(2);
        */
        sourceBuilder.timeout(new TimeValue(30, TimeUnit.SECONDS));
        //??????????????????????????????????????????
        searchRequest.source(sourceBuilder);
        //????????????
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //??????Json?????????????????????
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        //??????hits?????????????????????
        SearchHit[] hits = searchResponse.getHits().getHits();
        //????????????????????????Map????????????????????????????????????
        for (SearchHit hit : hits) {
            //??????????????????
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //?????????????????????????????????name????????????
            HighlightField name = highlightFields.get("name");
            //???????????????
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //?????????????????????
            if(name!=null){
                //?????????????????????
                Text[] fragments = name.fragments();
                System.out.println(fragments.length);
                String highLightName = "";
                for (Text fragment : fragments) {
                    //?????????????????????
                    highLightName += fragment;
                }
                //??????????????????????????????
                sourceAsMap.put("name", highLightName);
            } System.out.println(sourceAsMap);
        }
    }
}
