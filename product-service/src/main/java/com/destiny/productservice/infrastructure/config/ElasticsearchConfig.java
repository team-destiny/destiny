package com.destiny.productservice.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableElasticsearchRepositories(
    basePackages = "com.destiny.productservice.infrastructure.repository.query"
)
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    private final String hostUrl;

    public ElasticsearchConfig(
        @Value("${spring.data.elasticsearch.host}") String hostUrl
    ) {
        this.hostUrl = hostUrl;
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
            .connectedTo(hostUrl)
            .build();
    }
}
