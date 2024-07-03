package com.example.chatgptdemo.aws.model.elasticSearch;

import com.example.chatgptdemo.aws.model.elasticSearch.model.Person;
import com.example.chatgptdemo.aws.model.elasticSearch.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonDataService {
    @Autowired
    private PersonRepository repository;



    public void save(Person person) {
        repository.save(person);
    }

    public Person findById(String id) {
        return repository.findById(id).get();
    }

    public Person findByName(String name) {
        return repository.findByName(name);
    }

}
