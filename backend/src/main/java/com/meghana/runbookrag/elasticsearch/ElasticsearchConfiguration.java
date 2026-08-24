package com.meghana.runbookrag.elasticsearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("elasticsearch")
public class ElasticsearchConfiguration {

    @Bean
    RestClient elasticsearchRestClient(@Value("${rag.elasticsearch.url:http://localhost:9200}") String url) {
        return RestClient.builder().baseUrl(url).build();
    }
}
