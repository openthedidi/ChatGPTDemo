package com.example.chatgptdemo.aws.model.logic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ResponseGenerationServiceTest {

    @Autowired
    private ResponseGenerationService responseGenerationService;


    @Test
    void generateResponse() throws IOException {
        String response = responseGenerationService.generateResponse("請幫我介紹住院醫療健康保險的商品");
        assertNotNull(response);

    }
}