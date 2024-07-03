package com.example.chatgptdemo.aws.model.logic;

import com.example.chatgptdemo.aws.model.elasticSearch.model.QueryDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OpenAIServiceTest {

    @Autowired
    private OpenAIService openAIService;


    @Test
    void generateResponseByString() {
        String response = openAIService.generateResponseByString("{  \"product_info\": {\n" +
                "    \"id\": \"115\",\n" +
                "    \"system_id\": \"HPCHI2\",\n" +
                "    \"title\": \"南山人壽全心溢靠2醫療保險(HPCHI2)\",\n" +
                "    \"product_description\": \"提供住院、手術及重大疾病三合一保障\",\n" +
                "    \"product_feature\": \"\",\n" +
                "    \"age\": \"10年期:0歲~60歲/20年期:0歲~50歲\",\n" +
                "    \"payment_method\": \"年繳/半年繳/季繳/月繳\",\n" +
                "    \"payment_period\": \"10年/20年\",\n" +
                "    \"currency\": \"新台幣\",\n" +
                "    \"main_list\": [\"主約\"],\n" +
                "    \"assure_list\": [\"滿期保險金\",\"身故\",\"重大疾病\",\"住院日額\",\"門診手術\",\"住院手術\",\"豁免保險費\",\"外溢回饋\"],\n" +
                "    \"tag_list\": [\"新台幣\"],\n" +
                "    \"detail_url\": {\n" +
                "      \"url\": \"/product/103/115\",\n" +
                "      \"url_blank\": false,\n" +
                "      \"url_type\": \"INSIDE\"\n" +
                "    }}");

        assertNotNull(response);
        System.out.println(response);
    }

    @Test
    void generateQueryByString() throws JsonProcessingException {
        QueryDetails queryDetails = openAIService.generateQueryByString("我是18歲，可以幫我推薦合適的住院保險商品嗎");
        assertNotNull(queryDetails);
        System.out.println(queryDetails.getAge());
        System.out.println(queryDetails.getPaymentMethod());
        System.out.println(queryDetails.getInsuranceFeature());
        System.out.println(queryDetails.getPaymentPeriod());
        System.out.println(queryDetails.getTitle());
        System.out.println(queryDetails.getAssureList());
    }
}