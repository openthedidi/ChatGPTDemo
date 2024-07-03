package com.example.chatgptdemo.aws.model.config;

import com.example.chatgptdemo.aws.model.elasticSearch.model.QueryDetails;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .codecs(this::customCodecs)
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private void customCodecs(ClientCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new ObjectMapper().registerModule(new QueryDetailsModule()), MediaType.APPLICATION_JSON));
    }
}

class QueryDetailsModule extends SimpleModule {
    public QueryDetailsModule() {
        addDeserializer(QueryDetails.class, new QueryDetailsDeserializer());
    }
}

class QueryDetailsDeserializer extends StdDeserializer<QueryDetails> {

    protected QueryDetailsDeserializer() {
        super(QueryDetails.class);
    }

     /**
     * Deserialize the JSON response from the OpenAI API into a QueryDetails object.
     * @param p JSON parser
     * @param ctxt Deserialization context
     * @return QueryDetails object
     * @throws IOException If an error occurs during deserialization
      * example: {
      *     "age": "30",
      *     "title": "南山人壽全心溢靠2醫療保險(HPCHI2)",
      *     "product_description": "提供住院、手術及重大疾病三合一保障",
      *     "paymentMethod": "年繳/半年繳/季繳/月繳",
      *     "paymentPeriod": "10年",
      *     "insuranceAssureList": "滿期保險金, 身故"
      *     }
     */
    @Override
    public QueryDetails deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        QueryDetails details = new QueryDetails();
        details.setAge(node.has("age") ? Optional.of(node.get("insuranceFeature").asText()) : Optional.empty());
        details.setInsuranceFeature(node.has("insuranceFeature") ? Optional.of(node.get("insuranceFeature").asText()) : Optional.empty());
        details.setPaymentMethod(node.has("paymentMethod") ? Optional.of(node.get("paymentMethod").asText()) : Optional.empty());
        details.setPaymentPeriod(node.has("paymentPeriod") ? Optional.of(node.get("paymentPeriod").asText()) : Optional.empty());
        details.setAssureList(node.has("insuranceAssureList") ? Optional.of(node.get("insuranceAssureList").asText()) : Optional.empty());
        return details;
    }
}