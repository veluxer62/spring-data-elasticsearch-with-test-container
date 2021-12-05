package com.example.es

import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@TestConfiguration
@EnableElasticsearchRepositories
class TestConfiguration : AbstractElasticsearchConfiguration() {
    @Bean
    override fun elasticsearchClient(): RestHighLevelClient {
        val hostAddress = "${TestContainer.CONTAINER.host}:${TestContainer.CONTAINER.getMappedPort(9200)}"
        return RestHighLevelClientFactory(hostAddress).client
    }
}

