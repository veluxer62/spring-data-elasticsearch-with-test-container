package com.example.es

import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.PutComponentTemplateRequest
import org.elasticsearch.client.indices.PutComposableIndexTemplateRequest
import org.elasticsearch.cluster.metadata.ComponentTemplate
import org.elasticsearch.cluster.metadata.ComposableIndexTemplate
import org.elasticsearch.cluster.metadata.Template
import org.elasticsearch.common.compress.CompressedXContent
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentType
import org.springframework.core.io.ClassPathResource
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.testcontainers.shaded.org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

internal class RestHighLevelClientFactory(address: String) {
    val client: RestHighLevelClient
        get() = RestClients.create(clientConfiguration).rest()
            .apply {
                putTimestampComponentTemplate()
                putKoreanAnalyzerComponentTemplate()
                putIndexTemplate()
                createIndex()
            }

    private fun RestHighLevelClient.createIndex() {
        val request = CreateIndexRequest("product")
        this.indices().create(request, RequestOptions.DEFAULT)
    }

    private val clientConfiguration = ClientConfiguration.builder()
        .connectedTo(address)
        .build()

    private fun RestHighLevelClient.putIndexTemplate() {
        val settings = settings("elasticsearch/index_template/product/settings.json")
        val mappings = mappings("elasticsearch/index_template/product/mappings.json")
        val template = Template(settings, mappings, null)

        val indexTemplate = ComposableIndexTemplate(
            listOf("product*"),
            template,
            listOf("timestamp", "korean_analyzer"),
            null,
            1,
            null,
        )
        val request = PutComposableIndexTemplateRequest().name("product")
        request.indexTemplate(indexTemplate)

        this.indices().putIndexTemplate(request, RequestOptions.DEFAULT)
    }

    private fun RestHighLevelClient.putTimestampComponentTemplate() {
        val mappings = mappings("elasticsearch/component_template/timestamp.json")
        val template = Template(null, mappings, null)

        val request = PutComponentTemplateRequest().name("timestamp")
        request.componentTemplate(ComponentTemplate(template, null, null))

        this.cluster().putComponentTemplate(request, RequestOptions.DEFAULT)
    }

    private fun RestHighLevelClient.putKoreanAnalyzerComponentTemplate() {
        val settings = settings("elasticsearch/component_template/korean_analyzer.json")
        val template = Template(settings, null, null)

        val request = PutComponentTemplateRequest().name("korean_analyzer")
        request.componentTemplate(ComponentTemplate(template, null, null))

        this.cluster().putComponentTemplate(request, RequestOptions.DEFAULT)
    }

    private fun mappings(path: String): CompressedXContent {
        val mappingsContent = resourceContent(path)
        return CompressedXContent(mappingsContent)
    }

    private fun settings(path: String): Settings {
        val settingsContent = resourceContent(path)
        return Settings.builder()
            .loadFromSource(settingsContent, XContentType.JSON)
            .build()
    }

    private fun resourceContent(path: String): String {
        val settingsResource = ClassPathResource(path)
        return IOUtils.toString(settingsResource.inputStream, StandardCharsets.UTF_8)
    }
}
