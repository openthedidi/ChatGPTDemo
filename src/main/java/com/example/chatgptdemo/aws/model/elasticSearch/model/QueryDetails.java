package com.example.chatgptdemo.aws.model.elasticSearch.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class QueryDetails {
    private Optional<String> age = Optional.empty();
    private Optional<String> title = Optional.empty();
    private Optional<String> insuranceFeature = Optional.empty();
    private Optional<String> paymentMethod = Optional.empty();
    private Optional<String> paymentPeriod = Optional.empty();
    private Optional<String> assureList = Optional.empty();

}

