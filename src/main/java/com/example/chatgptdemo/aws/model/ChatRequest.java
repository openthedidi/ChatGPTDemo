package com.example.chatgptdemo.aws.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
public class ChatRequest {
    private String model;
    private List<Message> messages;


}
