package com.demo.stock.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class MemoryCacheServiceTest {

    private lateinit var memoryCacheService: MemoryCacheService

    @BeforeEach
    fun setUp() {
        memoryCacheService = MemoryCacheService()
        // 테스트 시작 시 캐시 초기화
        memoryCacheService.evictAll()
    }

    @Test
    fun `캐시에 데이터를 저장하고 조회할 수 있다`() {
        // Given
        val key = "testKey"
        val value = "testValue"

        // When
        memoryCacheService.put(key, value)
        val result = memoryCacheService.get<String>(key)

        // Then
        assertEquals(value, result)
    }

    @Test
    fun `TTL 설정한 캐시 데이터는 만료 후 조회되지 않는다`() {
        // Given
        val key = "shortLivedKey"
        val value = "expiringSoon"
        val ttlSeconds = 1L

        // When
        memoryCacheService.put(key, value, ttlSeconds)

        // 만료 전 확인
        val beforeExpiration = memoryCacheService.get<String>(key)

        // 만료 시간 대기
        TimeUnit.MILLISECONDS.sleep(1100)

        // 만료 후 확인
        val afterExpiration = memoryCacheService.get<String>(key)

        // Then
        assertEquals(value, beforeExpiration)
        assertNull(afterExpiration)
    }

    @Test
    fun `exists 메서드는 캐시 존재 여부를 정확히 반환한다`() {
        // Given
        val existingKey = "existingKey"
        val nonExistingKey = "nonExistingKey"
        val value = "someValue"

        // When
        memoryCacheService.put(existingKey, value)
        val existingResult = memoryCacheService.exists(existingKey)
        val nonExistingResult = memoryCacheService.exists(nonExistingKey)

        // Then
        assertTrue(existingResult)
        assertFalse(nonExistingResult)
    }

    @Test
    fun `exists 메서드는 만료된 캐시에 대해 false를 반환한다`() {
        // Given
        val key = "expiringKey"
        val value = "expiringValue"
        val ttlSeconds = 1L

        // When
        memoryCacheService.put(key, value, ttlSeconds)

        // 만료 전 확인
        val beforeExpiration = memoryCacheService.exists(key)

        // 만료 시간 대기
        TimeUnit.MILLISECONDS.sleep(1100)

        // 만료 후 확인
        val afterExpiration = memoryCacheService.exists(key)

        // Then
        assertTrue(beforeExpiration)
        assertFalse(afterExpiration)
    }

    @Test
    fun `evict 메서드는 특정 캐시를 삭제한다`() {
        // Given
        val key1 = "key1"
        val key2 = "key2"
        val value = "value"

        // When
        memoryCacheService.put(key1, value)
        memoryCacheService.put(key2, value)

        // 첫 번째 키만 삭제
        memoryCacheService.evict(key1)

        // Then
        assertNull(memoryCacheService.get<String>(key1))
        assertEquals(value, memoryCacheService.get<String>(key2))
    }

    @Test
    fun `evictAll 메서드는 모든 캐시를 삭제한다`() {
        // Given
        val keys = listOf("key1", "key2", "key3")
        val value = "value"

        // When
        keys.forEach { memoryCacheService.put(it, value) }

        // 모든 캐시 삭제
        memoryCacheService.evictAll()

        // Then
        keys.forEach {
            assertNull(memoryCacheService.get<String>(it))
            assertFalse(memoryCacheService.exists(it))
        }
    }

    @Test
    fun `evictByPattern 메서드는 패턴에 일치하는 키를 가진 캐시만 삭제한다`() {
        // Given
        val keyPattern = "test*"
        val matchingKeys = listOf("test1", "test2", "testABC")
        val nonMatchingKeys = listOf("abc", "xyz", "1test")
        val value = "value"

        // When
        (matchingKeys + nonMatchingKeys).forEach { memoryCacheService.put(it, value) }

        // 패턴에 일치하는 키만 삭제
        memoryCacheService.evictByPattern(keyPattern)

        // Then
        matchingKeys.forEach {
            assertNull(memoryCacheService.get<String>(it))
            assertFalse(memoryCacheService.exists(it))
        }

        nonMatchingKeys.forEach {
            assertEquals(value, memoryCacheService.get<String>(it))
            assertTrue(memoryCacheService.exists(it))
        }
    }

    @Test
    fun `cleanupExpiredEntries 메서드는 만료된 캐시 항목만 삭제한다`() {
        // Given
        val longLivedKey = "longLived"
        val shortLivedKey = "shortLived"
        val longLivedValue = "stayingAlive"
        val shortLivedValue = "dyingSoon"

        // When
        memoryCacheService.put(longLivedKey, longLivedValue, 300) // 5분
        memoryCacheService.put(shortLivedKey, shortLivedValue, 1) // 1초

        // 만료 시간 대기
        TimeUnit.MILLISECONDS.sleep(1100)

        // 만료된 항목 정리
        memoryCacheService.cleanupExpiredEntries()

        // Then
        assertNotNull(memoryCacheService.get<String>(longLivedKey))
        assertNull(memoryCacheService.get<String>(shortLivedKey))
    }

    @Test
    fun `getCacheStats 메서드는 정확한 캐시 통계 정보를 반환한다`() {
        // Given
        val validKey = "validKey"
        val expiredKey = "expiredKey"
        val value = "someValue"

        // When
        memoryCacheService.put(validKey, value, 300) // 5분
        memoryCacheService.put(expiredKey, value, 1) // 1초

        // 만료 시간 대기
        TimeUnit.MILLISECONDS.sleep(1100)

        // 통계 정보 조회
        val stats = memoryCacheService.getCacheStats()

        // Then
        assertEquals(2, stats["totalEntries"])
        assertEquals(1, stats["validEntries"])
        assertEquals(1, stats["expiredEntries"])
    }

    @Test
    fun `다양한 타입의 데이터를 캐시에 저장하고 조회할 수 있다`() {
        // Given
        val stringKey = "stringKey"
        val intKey = "intKey"
        val listKey = "listKey"
        val mapKey = "mapKey"
        val objectKey = "objectKey"

        val stringValue = "string value"
        val intValue = 42
        val listValue = listOf("item1", "item2", "item3")
        val mapValue = mapOf("key1" to "value1", "key2" to "value2")
        val objectValue = TestData("test", 123)

        // When
        memoryCacheService.put(stringKey, stringValue)
        memoryCacheService.put(intKey, intValue)
        memoryCacheService.put(listKey, listValue)
        memoryCacheService.put(mapKey, mapValue)
        memoryCacheService.put(objectKey, objectValue)

        // Then
        assertEquals(stringValue, memoryCacheService.get<String>(stringKey))
        assertEquals(intValue, memoryCacheService.get<Int>(intKey))
        assertEquals(listValue, memoryCacheService.get<List<String>>(listKey))
        assertEquals(mapValue, memoryCacheService.get<Map<String, String>>(mapKey))
        assertEquals(objectValue, memoryCacheService.get<TestData>(objectKey))
    }

    // 테스트용 데이터 클래스
    data class TestData(val name: String, val value: Int)
}