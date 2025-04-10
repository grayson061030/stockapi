package com.demo.stock.service

import com.demo.stock.config.TagType
import com.demo.stock.dto.request.StockSimulationRequest
import com.demo.stock.entity.Stock
import com.demo.stock.entity.StockPrice
import com.demo.stock.entity.StockStatistics
import com.demo.stock.repository.StockRepository
import com.demo.stock.util.RandomDataGenerator
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class TestServiceTest {

    @MockK
    private lateinit var stockRepository: StockRepository

    @MockK
    private lateinit var randomDataGenerator: RandomDataGenerator

    @MockK
    private lateinit var memoryCacheService: MemoryCacheService

    @InjectMockKs
    private lateinit var testService: TestService

    private lateinit var sampleStockList: List<Stock>
    private val now = LocalDateTime.now()
    private val today = LocalDate.now()

    @BeforeEach
    fun setUp() {
        // 샘플 주식 데이터 생성
        val stock1 = Stock(
            id = 1L,
            ticker = "A0001",
            name = "Apple Inc.",
            updatedAt = now
        )
        val statistics1 = StockStatistics(
            id = 1L,
            viewCount = 100,
            buyOrderVolume = 5000,
            sellOrderVolume = 3000,
            turnoverRate = 0.15,
            updatedAt = now
        )
        val price1 = StockPrice(
            id = 1L,
            price = 150.0,
            previousPrice = 145.0,
            priceDate = today,
            volume = 10000L,
            updatedAt = now
        )
        stock1.initStatistics(statistics1)
        stock1.addPrice(price1)

        val stock2 = Stock(
            id = 2L,
            ticker = "M0001",
            name = "Microsoft Corporation",
            updatedAt = now
        )
        val statistics2 = StockStatistics(
            id = 2L,
            viewCount = 80,
            buyOrderVolume = 4000,
            sellOrderVolume = 4200,
            turnoverRate = 0.12,
            updatedAt = now
        )
        val price2 = StockPrice(
            id = 2L,
            price = 305.0,
            previousPrice = 310.0,
            priceDate = today,
            volume = 8000L,
            updatedAt = now
        )
        stock2.initStatistics(statistics2)
        stock2.addPrice(price2)

        val stock3 = Stock(
            id = 3L,
            ticker = "G0001",
            name = "Alphabet Inc.",
            updatedAt = now
        )
        val statistics3 = StockStatistics(
            id = 3L,
            viewCount = 90,
            buyOrderVolume = 6000,
            sellOrderVolume = 5500,
            turnoverRate = 0.18,
            updatedAt = now
        )
        val price3 = StockPrice(
            id = 3L,
            price = 140.0,
            previousPrice = 142.0,
            priceDate = today,
            volume = 12000L,
            updatedAt = now
        )
        stock3.initStatistics(statistics3)
        stock3.addPrice(price3)

        sampleStockList = listOf(stock1, stock2, stock3)

        // 모든 목업 초기화
        clearAllMocks()
    }

    @Test
    fun `updateRandomStockData는 모든 주식 데이터를 업데이트하고 캐시를 무효화한다`() {
        // Given: 저장소에서 모든 주식을 가져오는 설정
        every { stockRepository.findAll() } returns sampleStockList
        every { memoryCacheService.evictAll() } just runs

        // 랜덤 데이터 생성 설정
        every { randomDataGenerator.generatePriceChangeRate() } returns 2.5 // 2.5% 변동
        every { randomDataGenerator.generateVolume(any()) } returns 11000L // 볼륨 증가
        every { randomDataGenerator.generateViewCountIncrease() } returns 10L // 조회수 증가
        every { randomDataGenerator.generateOrderVolume() } returns 5500L // 주문량
        every { randomDataGenerator.generateTurnoverRate() } returns 0.16 // 회전율

        // When: 모든 주식 데이터 랜덤 업데이트 요청
        val updatedCount = testService.updateRandomStockData()

        // Then: 모든 주식이 업데이트되고 캐시가 무효화되었는지 검증
        verify(exactly = 1) { stockRepository.findAll() }
        verify(exactly = 1) { memoryCacheService.evictAll() }
        assertEquals(sampleStockList.size, updatedCount)

        // 각 주식별 가격 업데이트 검증
        sampleStockList.forEach { stock ->
            val stockPrice = stock.prices.first()
            verify { randomDataGenerator.generatePriceChangeRate() }
            verify { randomDataGenerator.generateVolume(stockPrice.volume) }
            verify { randomDataGenerator.generateViewCountIncrease() }
            verify { randomDataGenerator.generateOrderVolume() }
            verify { randomDataGenerator.generateTurnoverRate() }
        }
    }

    @Test
    fun `updateStocksByTag은 POPULAR 태그 주식을 업데이트한다`() {
        // Given: 요청 및 설정
        val request = StockSimulationRequest(
            tagType = TagType.POPULAR,
            count = 2,
            minRate = 5,
            maxRate = 10
        )

        val selectedStocks = sampleStockList.take(2)

        every { stockRepository.findAll() } returns sampleStockList
        every { memoryCacheService.evictAll() } just runs
        every { randomDataGenerator.generateRateInRange(any(), any()) } returns 7.0 // 7% 증가

        // When: POPULAR 태그 주식 업데이트 요청
        val result = testService.updateStocksByTag(request)

        // Then: 해당 태그의 주식이 업데이트되고 캐시가 무효화되었는지 검증
        verify(exactly = 1) { stockRepository.findAll() }
        verify(exactly = 1) { memoryCacheService.evictAll() }
        verify(atLeast = 1) { randomDataGenerator.generateRateInRange(5, 10) }
    }

    @Test
    fun `updateStocksByTag은 RISING 태그 주식을 업데이트한다`() {
        // Given: 요청 및 설정
        val request = StockSimulationRequest(
            tagType = TagType.RISING,
            count = 1,
            minRate = 2,
            maxRate = 5
        )

        // 상승 주식 필터링
        val risingStocks = sampleStockList.filter { stock ->
            val latestPrice = stock.prices.first()
            latestPrice.price > latestPrice.previousPrice
        }

        every { stockRepository.findAll() } returns sampleStockList
        every { memoryCacheService.evictAll() } just runs
        every { randomDataGenerator.generateRateInRange(any(), any()) } returns 3.0 // 3% 상승
        every { randomDataGenerator.generateVolume(any()) } returns 11000L // 볼륨

        // When: RISING 태그 주식 업데이트 요청
        val result = testService.updateStocksByTag(request)

        // Then: 해당 태그의 주식이 업데이트되고 캐시가 무효화되었는지 검증
        verify(exactly = 1) { stockRepository.findAll() }
        verify(exactly = 1) { memoryCacheService.evictAll() }
        verify(atLeast = 1) { randomDataGenerator.generateRateInRange(2, 5) }
        verify(atLeast = 1) { randomDataGenerator.generateVolume(any()) }
    }

    @Test
    fun `updateStocksByTag은 FALLING 태그 주식을 업데이트한다`() {
        // Given: 요청 및 설정
        val request = StockSimulationRequest(
            tagType = TagType.FALLING,
            count = 1,
            minRate = 1,
            maxRate = 4
        )

        // 하락 주식 필터링
        val fallingStocks = sampleStockList.filter { stock ->
            val latestPrice = stock.prices.first()
            latestPrice.price < latestPrice.previousPrice
        }

        every { stockRepository.findAll() } returns sampleStockList
        every { memoryCacheService.evictAll() } just runs
        every { randomDataGenerator.generateRateInRange(any(), any()) } returns 2.0 // 2% 하락
        every { randomDataGenerator.generateVolume(any()) } returns 9000L // 볼륨

        // When: FALLING 태그 주식 업데이트 요청
        val result = testService.updateStocksByTag(request)

        // Then: 해당 태그의 주식이 업데이트되고 캐시가 무효화되었는지 검증
        verify(exactly = 1) { stockRepository.findAll() }
        verify(exactly = 1) { memoryCacheService.evictAll() }
        verify(atLeast = 1) { randomDataGenerator.generateRateInRange(1, 4) }
        verify(atLeast = 1) { randomDataGenerator.generateVolume(any()) }
    }

    @Test
    fun `updateStocksByTag은 VOLUME 태그 주식을 업데이트한다`() {
        // Given: 요청 및 설정
        val request = StockSimulationRequest(
            tagType = TagType.VOLUME,
            count = 1,
            minRate = 10,
            maxRate = 20
        )

        // 볼륨 기준 정렬
        val volumeStocks = sampleStockList.sortedByDescending { stock ->
            stock.prices.first().volume
        }

        every { stockRepository.findAll() } returns sampleStockList
        every { memoryCacheService.evictAll() } just runs
        every { randomDataGenerator.generateRateInRange(any(), any()) } returns 15.0 // 15% 증가
        every { randomDataGenerator.generatePriceChangeRate() } returns 1.2 // 1.2% 가격 변동

        // When: VOLUME 태그 주식 업데이트 요청
        val result = testService.updateStocksByTag(request)

        // Then: 해당 태그의 주식이 업데이트되고 캐시가 무효화되었는지 검증
        verify(exactly = 1) { stockRepository.findAll() }
        verify(exactly = 1) { memoryCacheService.evictAll() }
        verify(atLeast = 1) { randomDataGenerator.generateRateInRange(10, 20) }
        verify(atLeast = 1) { randomDataGenerator.generatePriceChangeRate() }
    }
}