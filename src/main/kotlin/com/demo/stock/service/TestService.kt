package com.demo.stock.service

import com.demo.stock.config.TagType
import com.demo.stock.dto.request.StockSimulationRequest
import com.demo.stock.entity.Stock
import com.demo.stock.entity.StockPrice
import com.demo.stock.repository.StockRepository
import com.demo.stock.util.RandomDataGenerator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class TestService(
    private val stockRepository: StockRepository,
    private val randomDataGenerator: RandomDataGenerator,
    private val memoryCacheService: MemoryCacheService,
) {
    private val log = LoggerFactory.getLogger(TestService::class.java)

    /**
     * 테스트를 위해 랜덤으로 모든 주식 데이터 업데이트
     */
    @Transactional
    fun updateRandomStockData(): Int {
        val stocks = stockRepository.findAll()
        val updatedCount = stocks.size
        val today = LocalDate.now()

        stocks.forEach { stock ->
            updateStockData(stock, today)
        }

        // 데이터가 변경되었으므로 캐시 무효화
        memoryCacheService.evictAll()
        log.info("총 ${updatedCount}개 주식 데이터 랜덤 업데이트 완료 및 캐시 무효화")

        return updatedCount
    }

    /**
     * 특정 태그에 해당하는 주식 데이터를 업데이트
     *
     * @param request 업데이트할 태그, 종목 수, 변동 범위 등 정보
     * @return 업데이트된 주식 목록
     */
    @Transactional
    fun updateStocksByTag(request: StockSimulationRequest): List<Stock> {
        val today = LocalDate.now()
        val stocksToUpdate = when (request.tagType) {
            TagType.POPULAR -> getRandomStocks(request.count, "viewCount")
            TagType.RISING -> getRandomStocks(request.count, "priceIncrease")
            TagType.FALLING -> getRandomStocks(request.count, "priceDecrease")
            TagType.VOLUME -> getRandomStocks(request.count, "volume")
            TagType.UNKNOWN -> TODO()
        }

        log.info("${request.tagType} 태그에 대해 ${stocksToUpdate.size}개 종목 데이터 업데이트 시작")

        stocksToUpdate.forEach { stock ->
            when (request.tagType) {
                TagType.POPULAR -> updatePopularity(stock, request.minRate, request.maxRate)
                TagType.RISING -> updatePriceRise(stock, request.minRate, request.maxRate, today)
                TagType.FALLING -> updatePriceFall(stock, request.minRate, request.maxRate, today)
                TagType.VOLUME -> updateVolume(stock, request.minRate, request.maxRate, today)
                TagType.UNKNOWN -> TODO()
            }
        }

        // 데이터가 변경되었으므로 캐시 무효화
        memoryCacheService.evictAll()
        log.info("${request.tagType} 태그에 대해 ${stocksToUpdate.size}개 주식 데이터 업데이트 완료 및 캐시 무효화")

        return stocksToUpdate
    }

    // 주식 데이터 업데이트
    private fun updateStockData(stock: Stock, today: LocalDate) {
        // 가격 업데이트
        val latestPrice = stock.prices.maxByOrNull { it.priceDate }
            ?: throw IllegalStateException("주식 가격 정보가 없습니다: ${stock.name}")

        val priceChangeRate = randomDataGenerator.generatePriceChangeRate()
        val newPrice = (latestPrice.price * (1 + priceChangeRate / 100))
        val newVolume = randomDataGenerator.generateVolume(latestPrice.volume)

        val newStockPrice = StockPrice(
            stock = stock,
            price = newPrice,
            previousPrice = latestPrice.price,
            priceDate = today,
            volume = newVolume
        )

        // 통계 정보 업데이트
        val statistics = stock.statistics
            ?: throw IllegalStateException("주식 통계 정보가 없습니다: ${stock.name}")

        // 조회수 업데이트
        val viewCountIncrease = randomDataGenerator.generateViewCountIncrease()
        statistics.viewCount += viewCountIncrease

        // 매수/매도 잔량 업데이트
        statistics.updateBuyOrderVolume(randomDataGenerator.generateOrderVolume())
        statistics.updateSellOrderVolume(randomDataGenerator.generateOrderVolume())

        // 회전율 업데이트
        statistics.turnoverRate = randomDataGenerator.generateTurnoverRate()

        // 연관관계 설정 및 저장
        stock.addPrice(newStockPrice)
        stock.updateTimestamp()
    }

    // 특정 기준으로 랜덤 주식 선택
    private fun getRandomStocks(count: Int, criteria: String): List<Stock> {
        val allStocks = stockRepository.findAll()

        // 조건에 맞게 정렬 후 count 개수만큼 선택
        return when (criteria) {
            "viewCount" -> allStocks.sortedByDescending { it.statistics?.viewCount ?: 0 }
            "priceIncrease" -> {
                allStocks.filter { stock ->
                    val latestPrice = stock.prices.maxByOrNull { it.priceDate }
                    latestPrice?.let { it.price > it.previousPrice } ?: false
                }
            }
            "priceDecrease" -> {
                allStocks.filter { stock ->
                    val latestPrice = stock.prices.maxByOrNull { it.priceDate }
                    latestPrice?.let { it.price < it.previousPrice } ?: false
                }
            }
            "volume" -> {
                allStocks.sortedByDescending { stock ->
                    stock.prices.maxByOrNull { it.priceDate }?.volume ?: 0
                }
            }
            else -> allStocks.shuffled()
        }.take(count.coerceAtMost(allStocks.size))
    }

    // 인기도 업데이트
    private fun updatePopularity(stock: Stock, minRate: Int, maxRate: Int) {
        val statistics = stock.statistics
            ?: throw IllegalStateException("주식 통계 정보가 없습니다: ${stock.name}")

        val increaseRate = randomDataGenerator.generateRateInRange(minRate, maxRate)
        val currentViewCount = statistics.viewCount
        val addedViewCount = (currentViewCount * increaseRate / 100).toLong()

        statistics.viewCount += addedViewCount
        stock.updateTimestamp()

        log.debug("인기도 업데이트: ${stock.name} - ${currentViewCount} -> ${statistics.viewCount}")
    }

    // 주가 상승 업데이트
    private fun updatePriceRise(stock: Stock, minRate: Int, maxRate: Int, today: LocalDate) {
        val latestPrice = stock.prices.maxByOrNull { it.priceDate }
            ?: throw IllegalStateException("주식 가격 정보가 없습니다: ${stock.name}")

        val increaseRate = randomDataGenerator.generateRateInRange(minRate, maxRate)
        val newPrice = latestPrice.price * (1 + increaseRate / 100)
        val newVolume = randomDataGenerator.generateVolume(latestPrice.volume)

        val newStockPrice = StockPrice(
            stock = stock,
            price = newPrice,
            previousPrice = latestPrice.price,
            priceDate = today,
            volume = newVolume
        )

        stock.addPrice(newStockPrice)
        stock.updateTimestamp()

        log.debug("주가 상승 업데이트: ${stock.name} - ${latestPrice.price} -> ${newPrice} (${increaseRate}%)")
    }

    // 주가 하락 업데이트
    private fun updatePriceFall(stock: Stock, minRate: Int, maxRate: Int, today: LocalDate) {
        val latestPrice = stock.prices.maxByOrNull { it.priceDate }
            ?: throw IllegalStateException("주식 가격 정보가 없습니다: ${stock.name}")

        val decreaseRate = randomDataGenerator.generateRateInRange(minRate, maxRate)
        val newPrice = latestPrice.price * (1 - decreaseRate / 100)
        val newVolume = randomDataGenerator.generateVolume(latestPrice.volume.toLong())

        val newStockPrice = StockPrice(
            stock = stock,
            price = newPrice,
            previousPrice = latestPrice.price,
            priceDate = today,
            volume = newVolume
        )

        stock.addPrice(newStockPrice)
        stock.updateTimestamp()

        log.debug("주가 하락 업데이트: ${stock.name} - ${latestPrice.price} -> ${newPrice} (-${decreaseRate}%)")
    }

    // 거래량 업데이트
    private fun updateVolume(stock: Stock, minRate: Int, maxRate: Int, today: LocalDate) {
        val latestPrice = stock.prices.maxByOrNull { it.priceDate }
            ?: throw IllegalStateException("주식 가격 정보가 없습니다: ${stock.name}")

        val increaseRate = randomDataGenerator.generateRateInRange(minRate, maxRate)
        val newVolume = latestPrice.volume * (1 + increaseRate / 100)
        val priceChangeRate = randomDataGenerator.generatePriceChangeRate() // 가격도 약간 변동
        val newPrice = latestPrice.price * (1 + priceChangeRate / 100)

        val newStockPrice = StockPrice(
            stock = stock,
            price = newPrice,
            previousPrice = latestPrice.price,
            priceDate = today,
            volume = newVolume.toLong()
        )

        stock.addPrice(newStockPrice)
        stock.updateTimestamp()

        log.debug("거래량 업데이트: ${stock.name} - ${latestPrice.volume} -> ${newVolume} (+${increaseRate}%)")
    }
}