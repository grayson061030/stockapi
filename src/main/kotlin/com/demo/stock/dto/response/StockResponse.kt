package com.demo.stock.dto.response

import com.demo.stock.config.TagType
import com.demo.stock.entity.Stock
import com.demo.stock.entity.StockPrice
import java.time.LocalDate
import java.time.LocalDateTime

data class StockResponse(
    val id: Long,
    val ticker: String,
    val name: String,
    val price: Double,
    val previousPrice: Double,
    val priceChange: Double,
    val priceChangeRate: Double,
    val volume: Long,
    val viewCount: Long,
    val buyOrderVolume: Long,
    val sellOrderVolume: Long,
    val orderVolumeDifference: Long,
    val turnoverRate: Double,
    val lastUpdated: LocalDateTime
) {
    companion object {
        fun from(stock: Stock): StockResponse {
            val latestPrice = stock.prices.maxByOrNull { it.priceDate }
                ?: throw IllegalStateException("주식 가격 정보가 없습니다: ${stock.name}")

            val stats = stock.statistics
                ?: throw IllegalStateException("주식 통계 정보가 없습니다: ${stock.name}")

            return StockResponse(
                id = stock.id,
                ticker = stock.ticker,
                name = stock.name,
                price = latestPrice.price,
                previousPrice = latestPrice.previousPrice,
                priceChange = latestPrice.getPriceChange(),
                priceChangeRate = latestPrice.getPriceChangeRate(),
                volume = latestPrice.volume,
                viewCount = stats.viewCount,
                buyOrderVolume = stats.buyOrderVolume,
                sellOrderVolume = stats.sellOrderVolume,
                orderVolumeDifference = stats.getOrderVolumeDifference(),
                turnoverRate = stats.turnoverRate,
                lastUpdated = latestPrice.updatedAt
            )
        }
    }
}

data class StockListResponse(
    val stocks: List<StockResponse>,
    val tag: TagType
)

data class StockDetailResponse(
    val stock: StockResponse,
    val priceHistory: List<PriceHistoryResponse>
)

data class PriceHistoryResponse(
    val date: LocalDate,
    val price: Double,
    val previousPrice: Double,
    val priceChange: Double,
    val priceChangeRate: Double,
    val volume: Long
) {
    companion object {
        fun from(price: StockPrice): PriceHistoryResponse {
            return PriceHistoryResponse(
                date = price.priceDate,
                price = price.price,
                previousPrice = price.previousPrice,
                priceChange = price.getPriceChange(),
                priceChangeRate = price.getPriceChangeRate(),
                volume = price.volume
            )
        }
    }
}