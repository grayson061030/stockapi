package com.demo.stock.service

import com.demo.stock.config.TagType
import com.demo.stock.dto.response.PriceHistoryResponse
import com.demo.stock.dto.response.StockDetailResponse
import com.demo.stock.dto.response.StockListResponse
import com.demo.stock.dto.response.StockResponse
import com.demo.stock.exception.BadRequestException
import com.demo.stock.exception.ErrorCode
import com.demo.stock.exception.NotFoundException
import com.demo.stock.repository.StockQueryRepository
import com.demo.stock.repository.StockRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val stockQueryRepository: StockQueryRepository,
    private val memoryCacheService: MemoryCacheService
) {
    private val log = LoggerFactory.getLogger(StockService::class.java)

    // 캐시 키 생성
    private fun createTagListCacheKey(tag: TagType, page: Int, size: Int): String {
        return "stock:list:${tag.name}:$page:$size"
    }

    private fun createStockDetailCacheKey(stockId: String): String {
        return "stock:detail:$stockId"
    }

    /**
     * 태그별 주식 목록 조회
     * @param tagName 태그 이름 (인기, 상승, 하락, 거래량 등)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 태그별 주식 목록
     */
    @Transactional(readOnly = true)
    fun getStocksByTag(tag:TagType, page: Int, size: Int): Pair<StockListResponse, Page<*>> {
        val cacheKey = createTagListCacheKey(tag, page, size)

        // 캐시에서 조회
        @Suppress("UNCHECKED_CAST")
        val cachedResult = memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey)
        if (cachedResult != null) {
            log.debug("Cache hit for tag list: {}", cacheKey)
            return cachedResult
        }

        log.debug("Cache miss for tag list: {}", cacheKey)
        val pageable = PageRequest.of(page, size)
        val stockPage = when (tag) {
            TagType.POPULAR -> stockQueryRepository.findPopularStocks(pageable)
            TagType.RISING -> stockQueryRepository.findRisingStocks(pageable)
            TagType.FALLING -> stockQueryRepository.findFallingStocks(pageable)
            TagType.VOLUME -> stockQueryRepository.findHighVolumeStocks(pageable)
            else -> throw BadRequestException(ErrorCode.INVALID_TAG, "지원하지 않는 태그입니다: ${tag.name}")
        }

        val stockResponses = stockPage.content.map { StockResponse.from(it) }

        val result = Pair(
            StockListResponse(stockResponses, tag),
            stockPage
        )

        // 캐시에 저장 (1분 동안 유효)
        memoryCacheService.put(cacheKey, result, 60)

        return result
    }

    /**
     * 주식 상세 정보 조회
     * @param stockId 주식 ID
     * @return 주식 상세 정보
     */
    @Transactional
    fun getStockDetail(ticker: String): StockDetailResponse {
        val cacheKey = createStockDetailCacheKey(ticker)

        // 캐시에서 조회 (캐시에 있어도 조회수는 증가시켜야 함)
        val cachedResult = memoryCacheService.get<StockDetailResponse>(cacheKey)

        val stock = stockRepository.findByTicker(ticker)
            .orElseThrow { NotFoundException(ErrorCode.STOCK_NOT_FOUND, "ID가 ${ticker} 주식을 찾을 수 없습니다") }

        // 조회수 증가 (캐시 여부와 상관없이 항상 증가)
        stock.statistics?.incrementViewCount()

        if (cachedResult != null) {
            log.debug("Cache hit for stock detail: {}", cacheKey)
            // 캐시에 있던 정보의 조회수만 업데이트
            val updatedStock = cachedResult.stock.copy(viewCount = stock.statistics?.viewCount ?: 0)
            val updatedResult = cachedResult.copy(stock = updatedStock)

            // 캐시 업데이트 (30초 동안 유효)
            memoryCacheService.put(cacheKey, updatedResult, 30)

            return updatedResult
        }

        log.debug("Cache miss for stock detail: {}", cacheKey)

        // 가격 이력 정보 변환
        val priceHistory = stock.prices
            .sortedByDescending { it.priceDate }
            .map { PriceHistoryResponse.from(it) }

        val result = StockDetailResponse(
            stock = StockResponse.from(stock),
            priceHistory = priceHistory
        )

        // 캐시에 저장 (30초 동안 유효)
        memoryCacheService.put(cacheKey, result, 30)

        return result
    }

    /**
     * 캐시 무효화
     * 주식 데이터가 업데이트되면 관련 캐시를 모두 삭제
     */
    fun invalidateCache() {
        memoryCacheService.evictAll()
        log.debug("All stock caches invalidated")
    }
}