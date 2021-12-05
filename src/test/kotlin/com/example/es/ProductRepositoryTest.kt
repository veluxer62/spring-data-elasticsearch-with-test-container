package com.example.es

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime
import java.util.UUID

@SpringBootTest(classes = [TestConfiguration::class, ProductRepository::class])
class ProductRepositoryTest {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setUp() {
        productRepository.deleteAll()
    }

    @Test
    @DisplayName("주어진 키워드를 포함한 모든 Index를 조회한다.")
    fun test1() {
        // Given
        createIndexes()

        // When
        val actual = productRepository.findByKeyword("국산", Pageable.ofSize(10))

        // Then
        assertEquals(5, actual.totalElements)
        assertEquals(5, actual.content.size)
    }

    @Test
    @DisplayName("조회 결과 중 스코어가 가장 높은 Index가 첫번째로 반환된다.")
    fun test2() {
        // Given
        createIndexes()

        // When
        val actual = productRepository.findByKeyword("국산", Pageable.ofSize(10))

        // Then
        // 검색 일치 가산점 중 이름이 가장 스코어 점수가 높고, 스코어 다음 등록일시를 우선순위로 정렬한다.
        assertEquals("사과/국산", actual.content.first().name)
    }

    @Test
    @DisplayName("페이징된 조회결과를 반환한다.")
    fun test3() {
        // Given
        createIndexes()

        // When
        val actual = productRepository.findByKeyword("국산", PageRequest.of(1, 2))

        // Then
        assertEquals(5, actual.totalElements)
        assertEquals(2, actual.content.size)
        // 첫번째 페이지는 이름에 대한 가산점이 가장 높은 "사과/국산", "배/국산" 이다.
        assertEquals("양상추", actual.content.first().name)
    }

    private fun createIndexes() {
        val indexes = listOf(
            Product(
                id = UUID.randomUUID(),
                createdAt = ZonedDateTime.now(),
                name = "사과/국산",
                category = "과일",
                standard = null,
                unit = null,
            ),
            Product(
                id = UUID.randomUUID(),
                createdAt = ZonedDateTime.now(),
                name = "배/국산",
                category = "과일",
                standard = null,
                unit = null,
            ),
            Product(
                id = UUID.randomUUID(),
                createdAt = ZonedDateTime.now(),
                name = "상추",
                category = "채소",
                standard = null,
                unit = null,
            ),
            Product(
                id = UUID.randomUUID(),
                createdAt = ZonedDateTime.now(),
                name = "양상추",
                category = "국산 채소",
                standard = null,
                unit = null,
            ),
            Product(
                id = UUID.randomUUID(),
                createdAt = ZonedDateTime.now(),
                name = "딸기",
                category = "과일",
                standard = "국산",
                unit = null,
            ),
            Product(
                id = UUID.randomUUID(),
                createdAt = ZonedDateTime.now(),
                name = "선지국",
                category = "국",
                standard = "1KG",
                unit = "국자",
            ),
        )
        productRepository.saveAll(indexes)
    }
}
