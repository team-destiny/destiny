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

    private final String username;

    private final String password;

    public ElasticsearchConfig(
        @Value("${spring.data.elasticsearch.host}") String hostUrl,
        @Value("${spring.data.elasticsearch.username}") String username,
        @Value("${spring.data.elasticsearch.password}") String password
    ) {
        this.hostUrl = hostUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
            .connectedTo(hostUrl)
            .withBasicAuth(username, password)
            .build();
    }
}
