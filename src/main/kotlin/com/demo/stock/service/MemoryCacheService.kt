package com.demo.stock.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * 메모리 기반 캐시 서비스
 */
@Service
class MemoryCacheService {
    private val log = LoggerFactory.getLogger(MemoryCacheService::class.java)

    // 캐시 데이터와 만료 시간을 저장할 맵
    private val cache = ConcurrentHashMap<String, CacheEntry<Any>>()

    /**
     * 캐시에 데이터를 저장
     * @param key 캐시 키
     * @param value 저장할 값
     * @param ttlSeconds 캐시 만료 시간(초), 기본값은 5분
     */
    fun put(key: String, value: Any, ttlSeconds: Long = 300) {
        val expireAt = LocalDateTime.now().plusSeconds(ttlSeconds)
        cache[key] = CacheEntry(value, expireAt)
        log.debug("Cache put: key={}, ttl={}s", key, ttlSeconds)
    }

    /**
     * 캐시에서 데이터 조회
     * @param key 캐시 키
     * @return 캐시된 데이터 또는 null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? {
        val entry = cache[key] ?: return null

        // 만료된 엔트리 확인
        if (LocalDateTime.now().isAfter(entry.expireAt)) {
            cache.remove(key)
            log.debug("Cache expired: key={}", key)
            return null
        }

        log.debug("Cache hit: key={}", key)
        return entry.value as? T
    }

    /**
     * 캐시에 데이터가 존재하는지 확인
     * @param key 캐시 키
     * @return 캐시 존재 여부
     */
    fun exists(key: String): Boolean {
        val entry = cache[key] ?: return false

        if (LocalDateTime.now().isAfter(entry.expireAt)) {
            cache.remove(key)
            return false
        }

        return true
    }

    /**
     * 캐시 데이터 삭제
     * @param key 캐시 키
     */
    fun evict(key: String) {
        cache.remove(key)
        log.debug("Cache evict: key={}", key)
    }

    /**
     * 모든 캐시 데이터 삭제
     */
    fun evictAll() {
        val size = cache.size
        cache.clear()
        log.debug("Cache evict all: count={}", size)
    }

    /**
     * 특정 패턴의 키를 가진 캐시 삭제
     * @param keyPattern 삭제할 키 패턴
     */
    fun evictByPattern(keyPattern: String) {
        val regex = keyPattern.replace("*", ".*").toRegex()
        val removedKeys = cache.keys().asSequence()
            .filter { it.matches(regex) }
            .onEach { cache.remove(it) }
            .toList()

        log.debug("Cache evict by pattern: pattern={}, count={}", keyPattern, removedKeys.size)
    }

    /**
     * 캐시 통계 정보 조회
     * @return 캐시 통계 정보
     */
    fun getCacheStats(): Map<String, Any> {
        val now = LocalDateTime.now()
        val totalEntries = cache.size
        val expiredEntries = cache.count { (_, entry) -> now.isAfter(entry.expireAt) }
        val validEntries = totalEntries - expiredEntries

        return mapOf(
            "totalEntries" to totalEntries,
            "validEntries" to validEntries,
            "expiredEntries" to expiredEntries
        )
    }

    /**
     * 만료된 캐시 데이터 정리 (5분마다 실행)
     */
    @Scheduled(fixedRate = 300000) // 5분마다 실행
    fun cleanupExpiredEntries() {
        val now = LocalDateTime.now()
        val expiredKeys = cache.entries
            .filter { (_, entry) -> now.isAfter(entry.expireAt) }
            .map { it.key }

        expiredKeys.forEach { cache.remove(it) }

        if (expiredKeys.isNotEmpty()) {
            log.debug("Cache cleanup: removed {} expired entries", expiredKeys.size)
        }
    }

    /**
     * 캐시 엔트리를 저장하는 내부 클래스
     */
    private data class CacheEntry<T>(
        val value: T,
        val expireAt: LocalDateTime
    )
}