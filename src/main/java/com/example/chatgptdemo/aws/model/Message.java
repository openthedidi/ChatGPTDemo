package com.example.chatgptdemo.aws.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class Message {

    private String role;
    private String content;
}
