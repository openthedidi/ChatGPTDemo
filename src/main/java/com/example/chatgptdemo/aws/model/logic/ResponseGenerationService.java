package com.example.chatgptdemo.aws.model.logic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.example.chatgptdemo.aws.model.elasticSearch.model.QueryDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResponseGenerationService {

    private final ElasticsearchClient elasticsearchClient;
    private final OpenAIService openAIService;

    public ResponseGenerationService(ElasticsearchClient elasticsearchClient, OpenAIService openAIService) {
        this.elasticsearchClient = elasticsearchClient;
        this.openAIService = openAIService;
    }


    public String generateResponse(String question) throws IOException {
        QueryDetails queryDetails = openAIService.generateQueryByString(question);
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("insurance_products")
                .query(q -> q
                        .bool(b -> {
                            BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
                            queryDetails.getAge().ifPresent(age -> {
                                System.out.println("age: " + age);
                                boolBuilder.must(m -> m
                                        .range(r -> r
                                                .field("age")
                                                .gte(JsonData.fromJson(age.toString()))
                                                .lte(JsonData.fromJson(age.toString()))
                                        )
                                );
                            });
                            queryDetails.getInsuranceFeature().ifPresent(insuranceFeature -> {
                                boolBuilder.must(m -> m
                                        .match(mt -> mt
                                                .field("product_info.product_description")
                                                .query(insuranceFeature)
                                        )
                                );
                            });
                            queryDetails.getPaymentMethod().ifPresent(paymentMethod -> {
                                System.out.println("paymentMethod: " + paymentMethod);
                                boolBuilder.must(m -> m
                                        .match(mt -> mt
                                                .field("product_info.payment_method")
                                                .query(paymentMethod)
                                        )
                                );
                            });
                            queryDetails.getPaymentPeriod().ifPresent(paymentPeriod -> {
                                System.out.println("paymentPeriod: " + paymentPeriod);
                                boolBuilder.must(m -> m
                                        .match(mt -> mt
                                                .field("product_info.payment_period")
                                                .query(paymentPeriod)
                                        )
                                );
                            });
                            queryDetails.getTitle().ifPresent(title -> {
                                System.out.println("title: " + title);
                                boolBuilder.must(m -> m
                                        .match(mt -> mt
                                                .field("product_info.title")
                                                .query(title)
                                        )
                                );
                            });
                            queryDetails.getAssureList().ifPresent(assureList -> {
                                System.out.println("assureList: " + assureList);
                                boolBuilder.must(m -> m
                                        .match(mt -> mt
                                                .field("product_info.assure_list")
                                                .query(assureList)
                                        )
                                );
                            });
                            return boolBuilder;
                        })
                )
        );
        SearchResponse<Map> response = elasticsearchClient.search(searchRequest, Map.class);
        String relevantContent = response.hits().hits().stream()
                .map(hit -> hit.source().get("product_info"))
                .map(productInfo -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.writeValueAsString(productInfo);  // 转换每个 product_info 为 JSON 字符串
                    } catch (JsonProcessingException e) {
                        return "Error processing JSON";  // 错误处理，返回错误信息
                    }
                })
                .collect(Collectors.joining("; "));  // 使用分号和空格作为分隔符连接所有字符串

        log.info("All content: {}", relevantContent);
        String formatContent = relevantContent
                .replace("title", "商品名稱")
                .replace("product_description", "商品特色")
                .replace("age", "年齡")
                .replace("payment_method", "繳費方式")
                .replace("payment_period", "繳費期間")
                .replace("assure_list", "保障項目");
        log.info("formatContentt: {}", formatContent);
        String responseText = openAIService.generateResponseByString(formatContent);
        return "responseText";

    }


}

