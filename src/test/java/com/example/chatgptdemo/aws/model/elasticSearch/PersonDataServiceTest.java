package com.example.chatgptdemo.aws.model.elasticSearch;

import com.example.chatgptdemo.aws.model.elasticSearch.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PersonDataServiceTest {

    @Autowired
    private PersonDataService personDataService;

//    @Test
//    void findById() {
//        Person person = personDataService.findById("1");
//        System.out.println(person.getName());
//
//    }


    @Test
    void findByName() {
        Person person = personDataService.findByName("Alice Johnson");
        System.out.println(person.getName());

    }
}