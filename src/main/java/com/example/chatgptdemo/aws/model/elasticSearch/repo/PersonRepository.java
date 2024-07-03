package com.example.chatgptdemo.aws.model.elasticSearch.repo;

import com.example.chatgptdemo.aws.model.elasticSearch.model.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PersonRepository extends ElasticsearchRepository<Person, String> {
    Person findByName(String name);
}
