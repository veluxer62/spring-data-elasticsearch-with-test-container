package com.example.es

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import java.util.UUID
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ProductRepository : ElasticsearchRepository<Product, UUID>, CustomProductRepository

interface CustomProductRepository {
    fun findByKeyword(keyword: String, pageable: Pageable): Page<Product>
}

@Suppress("unused")
class CustomProductRepositoryImpl(
    private val restHighLevelClient: RestHighLevelClient,
) : CustomProductRepository {
    override fun findByKeyword(keyword: String, pageable: Pageable): Page<Product> {
        val query = QueryBuilders.multiMatchQuery(keyword)
            .field("name", 10f)
            .field("category", 7f)
            .field("standard", 4f)
            .field("unit", 4f)
            .field("name.ngram", 3f)
            .field("category.ngram", 2f)
            .field("standard.ngram")
            .field("unit.ngram")

        val source = SearchSourceBuilder()
            .trackTotalHits(true)
            .query(query)
            .sort("_score")
            .sort("created_at")
            .from(pageable.offset.toInt())
            .size(pageable.pageSize)

        val request = SearchRequest()
        request.indices("product")
        request.source(source)

        val response = restHighLevelClient.search(request, RequestOptions.DEFAULT)
        val content = response.hits.hits.map {
            jacksonMapperBuilder().addModule(JavaTimeModule()).build()
                .readValue(it.sourceAsString, Product::class.java)
        }

        return PageImpl(content, pageable, response.hits.totalHits?.value ?: 0)
    }
}
