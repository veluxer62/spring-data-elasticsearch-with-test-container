package com.example.es

import org.testcontainers.containers.GenericContainer
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container

class TestContainer {
    companion object {
        @Container
        @JvmStatic
        val CONTAINER = GenericContainer<Nothing>(
            ImageFromDockerfile().withDockerfileFromBuilder {
                it.from("docker.elastic.co/elasticsearch/elasticsearch:7.8.1")
                    .run("bin/elasticsearch-plugin install analysis-nori")
                    .build()
            }
        )
            .apply { withEnv("discovery.type", "single-node") }
            .apply { addExposedPorts(9200, 9300) }
            .apply { start() }
    }
}
