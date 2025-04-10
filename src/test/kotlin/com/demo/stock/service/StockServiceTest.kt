package com.demo.stock.service

import com.demo.stock.config.TagType
import com.demo.stock.dto.response.StockDetailResponse
import com.demo.stock.dto.response.StockListResponse
import com.demo.stock.dto.response.StockResponse
import com.demo.stock.entity.Stock
import com.demo.stock.entity.StockPrice
import com.demo.stock.entity.StockStatistics
import com.demo.stock.exception.BadRequestException
import com.demo.stock.exception.ErrorCode
import com.demo.stock.exception.NotFoundException
import com.demo.stock.repository.StockQueryRepository
import com.demo.stock.repository.StockRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class StockServiceTest {

    @MockK
    private lateinit var stockRepository: StockRepository

    @MockK
    private lateinit var stockQueryRepository: StockQueryRepository

    @MockK
    private lateinit var memoryCacheService: MemoryCacheService

    @InjectMockKs
    private lateinit var stockService: StockService

    private lateinit var sampleStock: Stock
    private lateinit var sampleStockList: List<Stock>
    private lateinit var pageable: PageRequest
    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        // 샘플 주식 데이터 생성
        val statistics = StockStatistics(
            id = 1L,
            viewCount = 100,
            buyOrderVolume = 5000,
            sellOrderVolume = 3000,
            turnoverRate = 0.15,
            updatedAt = now
        )

        val prices = mutableListOf<StockPrice>()
        for (i in 0..5) {
            val price = StockPrice(
                id = i.toLong() + 1,
                price = 100.0 + i,
                previousPrice = 99.0 + i,
                priceDate = LocalDate.now().minusDays(i.toLong()),
                volume = 10000L + (i * 1000L),
                updatedAt = now
            )
            prices.add(price)
        }

        sampleStock = Stock(
            id = 1L,
            ticker = "A0001",
            name = "Apple Inc.",
            updatedAt = now
        )

        // 관계 설정
        sampleStock.initStatistics(statistics)
        prices.forEach { sampleStock.addPrice(it) }

        // 샘플 주식 목록 생성
        val stock2 = Stock(
            id = 2L,
            ticker = "M0001",
            name = "Microsoft Corporation",
            updatedAt = now
        )
        stock2.initStatistics(StockStatistics(
            id = 2L,
            viewCount = 80,
            buyOrderVolume = 4000,
            sellOrderVolume = 4200,
            turnoverRate = 0.12,
            updatedAt = now
        ))
        val price2 = StockPrice(
            id = 7L,
            price = 305.0,
            previousPrice = 300.0,
            priceDate = LocalDate.now(),
            volume = 8000L,
            updatedAt = now
        )
        stock2.addPrice(price2)

        val stock3 = Stock(
            id = 3L,
            ticker = "G0001",
            name = "Alphabet Inc.",
            updatedAt = now
        )
        stock3.initStatistics(StockStatistics(
            id = 3L,
            viewCount = 90,
            buyOrderVolume = 6000,
            sellOrderVolume = 5500,
            turnoverRate = 0.18,
            updatedAt = now
        ))
        val price3 = StockPrice(
            id = 8L,
            price = 140.0,
            previousPrice = 142.0,
            priceDate = LocalDate.now(),
            volume = 12000L,
            updatedAt = now
        )
        stock3.addPrice(price3)

        sampleStockList = listOf(sampleStock, stock2, stock3)

        pageable = PageRequest.of(0, 10)

        // 모든 목업 초기화
        clearAllMocks()
    }

    @Test
    fun `캐시에 결과가 있으면 태그별 주식 목록을 캐시에서 가져온다`() {
        // Given: 캐시에 결과가 있는 경우를 설정
        val tag = TagType.POPULAR
        val page = 0
        val size = 10
        val cacheKey = "stock:list:${tag.name}:$page:$size"

        val stockResponses = sampleStockList.map { StockResponse.from(it) }
        val stockPage = PageImpl(sampleStockList, pageable, sampleStockList.size.toLong())
        val expectedResult = Pair(
            StockListResponse(stockResponses, tag),
            stockPage
        )

        every { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) } returns expectedResult

        // When: 태그별 주식 목록 요청
        val result = stockService.getStocksByTag(tag, page, size)

        // Then: 캐시에서 결과를 가져오고 저장소에서 조회하지 않음을 검증
        verify(exactly = 1) { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) }
        verify(exactly = 0) { stockQueryRepository.findPopularStocks(any()) }
        assertEquals(expectedResult, result)
    }

    @Test
    fun `인기 태그 - 캐시에 결과가 없으면 저장소에서 주식 목록을 가져온다`() {
        // Given: 캐시에 결과가 없고 인기 태그를 요청하는 경우를 설정
        val tag = TagType.POPULAR
        val page = 0
        val size = 10
        val cacheKey = "stock:list:${tag.name}:$page:$size"

        val stockPage = PageImpl(sampleStockList, pageable, sampleStockList.size.toLong())

        every { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) } returns null
        every { stockQueryRepository.findPopularStocks(pageable) } returns stockPage
        every { memoryCacheService.put(cacheKey, any(), 60L) } just runs

        // When: 인기 태그의 주식 목록 요청
        val result = stockService.getStocksByTag(tag, page, size)

        // Then: 저장소에서 데이터를 가져오고 캐시에 저장함을 검증
        verify(exactly = 1) { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) }
        verify(exactly = 1) { stockQueryRepository.findPopularStocks(pageable) }
        verify(exactly = 1) { memoryCacheService.put(cacheKey, any(), 60L) }

        assertEquals(sampleStockList.size, result.first.stocks.size)
        assertEquals(tag, result.first.tag)
    }

    @Test
    fun `상승 태그 - 캐시에 결과가 없으면 저장소에서 주식 목록을 가져온다`() {
        // Given: 캐시에 결과가 없고 상승 태그를 요청하는 경우를 설정
        val tag = TagType.RISING
        val page = 0
        val size = 10
        val cacheKey = "stock:list:${tag.name}:$page:$size"

        val stockPage = PageImpl(sampleStockList, pageable, sampleStockList.size.toLong())

        every { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) } returns null
        every { stockQueryRepository.findRisingStocks(pageable) } returns stockPage
        every { memoryCacheService.put(cacheKey, any(), 60L) } just runs

        // When: 상승 태그의 주식 목록 요청
        val result = stockService.getStocksByTag(tag, page, size)

        // Then: 저장소에서 데이터를 가져오고 캐시에 저장함을 검증
        verify(exactly = 1) { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) }
        verify(exactly = 1) { stockQueryRepository.findRisingStocks(pageable) }
        verify(exactly = 1) { memoryCacheService.put(cacheKey, any(), 60L) }

        assertEquals(sampleStockList.size, result.first.stocks.size)
        assertEquals(tag, result.first.tag)
    }

    @Test
    fun `하락 태그 - 캐시에 결과가 없으면 저장소에서 주식 목록을 가져온다`() {
        // Given: 캐시에 결과가 없고 하락 태그를 요청하는 경우를 설정
        val tag = TagType.FALLING
        val page = 0
        val size = 10
        val cacheKey = "stock:list:${tag.name}:$page:$size"

        val stockPage = PageImpl(sampleStockList, pageable, sampleStockList.size.toLong())

        every { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) } returns null
        every { stockQueryRepository.findFallingStocks(pageable) } returns stockPage
        every { memoryCacheService.put(cacheKey, any(), 60L) } just runs

        // When: 하락 태그의 주식 목록 요청
        val result = stockService.getStocksByTag(tag, page, size)

        // Then: 저장소에서 데이터를 가져오고 캐시에 저장함을 검증
        verify(exactly = 1) { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) }
        verify(exactly = 1) { stockQueryRepository.findFallingStocks(pageable) }
        verify(exactly = 1) { memoryCacheService.put(cacheKey, any(), 60L) }

        assertEquals(sampleStockList.size, result.first.stocks.size)
        assertEquals(tag, result.first.tag)
    }

    @Test
    fun `거래량 태그 - 캐시에 결과가 없으면 저장소에서 주식 목록을 가져온다`() {
        // Given: 캐시에 결과가 없고 거래량 태그를 요청하는 경우를 설정
        val tag = TagType.VOLUME
        val page = 0
        val size = 10
        val cacheKey = "stock:list:${tag.name}:$page:$size"

        val stockPage = PageImpl(sampleStockList, pageable, sampleStockList.size.toLong())

        every { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) } returns null
        every { stockQueryRepository.findHighVolumeStocks(pageable) } returns stockPage
        every { memoryCacheService.put(cacheKey, any(), 60L) } just runs

        // When: 거래량 태그의 주식 목록 요청
        val result = stockService.getStocksByTag(tag, page, size)

        // Then: 저장소에서 데이터를 가져오고 캐시에 저장함을 검증
        verify(exactly = 1) { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) }
        verify(exactly = 1) { stockQueryRepository.findHighVolumeStocks(pageable) }
        verify(exactly = 1) { memoryCacheService.put(cacheKey, any(), 60L) }

        assertEquals(sampleStockList.size, result.first.stocks.size)
        assertEquals(tag, result.first.tag)
    }

    @Test
    fun `지원하지 않는 태그에 대해 BadRequestException을 던진다`() {
        // Given: 캐시에 결과가 없는 상황 설정
        val tag = TagType.UNKNOWN
        val page = 0
        val size = 10
        val cacheKey = "stock:list:${tag.name}:$page:$size"

        // 캐시 조회에 대한 응답 설정 (null 반환)
        every { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) } returns null

        // When/Then: 지원하지 않는 태그에 대해 BadRequestException이 발생하는지 검증
        val exception = assertThrows<BadRequestException> {
            stockService.getStocksByTag(tag, page, size)
        }

        verify(exactly = 1) { memoryCacheService.get<Pair<StockListResponse, Page<*>>>(cacheKey) }
        assertEquals(ErrorCode.INVALID_TAG, exception.errorCode)
        assertTrue(exception.message.contains("지원하지 않는 태그입니다"))
    }

    @Test
    fun `캐시에 결과가 없으면 저장소에서 주식 상세 정보를 가져온다`() {
        // Given: 캐시에 결과가 없는 경우를 설정
        val ticker = "A0001"
        val cacheKey = "stock:detail:$ticker"

        every { memoryCacheService.get<StockDetailResponse>(cacheKey) } returns null
        every { stockRepository.findByTicker(ticker) } returns Optional.of(sampleStock)
        every { memoryCacheService.put(cacheKey, any(), 30L) } just runs

        // When: 주식 상세 정보 요청
        val result = stockService.getStockDetail(ticker)

        // Then: 저장소에서 데이터를 가져오고 캐시에 저장함을 검증
        verify(exactly = 1) { memoryCacheService.get<StockDetailResponse>(cacheKey) }
        verify(exactly = 1) { stockRepository.findByTicker(ticker) }
        verify(exactly = 1) { memoryCacheService.put(cacheKey, any(), 30L) }

        assertEquals(sampleStock.ticker, result.stock.ticker)
        assertEquals(sampleStock.name, result.stock.name)
        assertEquals(sampleStock.prices.size, result.priceHistory.size)

        // 추가된 필드 검증
        val latestPrice = sampleStock.prices.maxByOrNull { it.priceDate }!!
        val stats = sampleStock.statistics!!

        assertEquals(latestPrice.price, result.stock.price)
        assertEquals(latestPrice.previousPrice, result.stock.previousPrice)
        assertEquals(latestPrice.getPriceChange(), result.stock.priceChange)
        assertEquals(latestPrice.getPriceChangeRate(), result.stock.priceChangeRate)
        assertEquals(stats.buyOrderVolume, result.stock.buyOrderVolume)
        assertEquals(stats.sellOrderVolume, result.stock.sellOrderVolume)
        assertEquals(stats.getOrderVolumeDifference(), result.stock.orderVolumeDifference)
        assertEquals(stats.turnoverRate, result.stock.turnoverRate)
        assertEquals(latestPrice.updatedAt, result.stock.lastUpdated)
    }

    @Test
    fun `존재하지 않는 주식에 대해 NotFoundException을 던진다`() {
        // Given: 존재하지 않는 주식을 요청하는 경우를 설정
        val ticker = "INVALID"
        val cacheKey = "stock:detail:$ticker"

        // 캐시에 결과가 없는 상태 설정
        every { memoryCacheService.get<StockDetailResponse>(cacheKey) } returns null
        every { stockRepository.findByTicker(ticker) } returns Optional.empty()

        // When/Then: NotFoundException이 발생하는지 검증
        val exception = assertThrows<NotFoundException> {
            stockService.getStockDetail(ticker)
        }

        verify(exactly = 1) { memoryCacheService.get<StockDetailResponse>(cacheKey) }
        verify(exactly = 1) { stockRepository.findByTicker(ticker) }
        assertEquals(ErrorCode.STOCK_NOT_FOUND, exception.errorCode)
        assertTrue(exception.message.contains("ID가 $ticker 주식을 찾을 수 없습니다"))
    }

    @Test
    fun `캐시 무효화 함수는 모든 캐시를 제거한다`() {
        // Given
        every { memoryCacheService.evictAll() } just runs

        // When: 캐시 무효화 요청
        stockService.invalidateCache()

        // Then: 모든 캐시가 제거되었는지 검증
        verify(exactly = 1) { memoryCacheService.evictAll() }
    }
}