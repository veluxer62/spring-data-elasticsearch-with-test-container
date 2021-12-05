package com.example.es

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.InnerField
import org.springframework.data.elasticsearch.annotations.MultiField
import org.springframework.data.elasticsearch.annotations.WriteTypeHint
import java.time.ZonedDateTime
import java.util.UUID

@Document(
    indexName = "product",
    writeTypeHint = WriteTypeHint.FALSE,
    createIndex = false,
)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class Product private constructor(
    @Id
    val id: UUID,

    @Field(name = "created_at", type = FieldType.Date)
    val createdAt: ZonedDateTime,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "korean"),
        otherFields = [InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "korean_ngram")]
    )
    val name: String,

    @Field(name = "name_keyword", type = FieldType.Keyword)
    val nameKeyword: String,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "korean"),
        otherFields = [InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "korean_ngram")]
    )
    val category: String,

    @Field(name = "category_keyword", type = FieldType.Keyword)
    val categoryKeyword: String,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "korean"),
        otherFields = [InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "korean_ngram")]
    )
    val standard: String?,

    @Field(name = "standard_keyword", type = FieldType.Keyword)
    val standardKeyword: String?,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "korean"),
        otherFields = [InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "korean_ngram")]
    )
    val unit: String?,

    @Field(name = "unit_keyword", type = FieldType.Keyword)
    val unitKeyword: String?,
) {
    constructor(
        id: UUID,
        createdAt: ZonedDateTime,
        name: String,
        category: String,
        standard: String?,
        unit: String?,
    ) : this(
        id = id,
        createdAt = createdAt,
        name = name,
        nameKeyword = name,
        category = category,
        categoryKeyword = category,
        standard = standard,
        standardKeyword = standard,
        unit = unit,
        unitKeyword = unit,
    )
}
