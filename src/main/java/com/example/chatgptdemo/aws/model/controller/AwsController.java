package com.example.chatgptdemo.aws.model.controller;

import com.example.chatgptdemo.aws.model.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class AwsController {

    @Value("${openai.api.key}")  // 從application.properties或您的配置源讀取API密鑰
    private String apiKey;


    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @RequestMapping(value = "/getChatGpt", produces = {"application/json"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> getChatGpt(@RequestBody ChatRequest chatRequest) {
        System.out.println("model: " + chatRequest.getModel());

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.openai.com/v1/chat/completions";

        // 創建HTTP頭部，並添加Authorization頭部
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 封裝請求數據和頭部到HttpEntity
        HttpEntity<ChatRequest> requestEntity = new HttpEntity<>(chatRequest, headers);

        // 發送POST請求
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        // 返回從OpenAI API接收到的回應
        return response;
    }
}