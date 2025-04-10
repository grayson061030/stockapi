package com.demo.stock.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "stock_prices")
class StockPrice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    var stock: Stock? = null,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    val previousPrice: Double,

    @Column(nullable = false)
    val priceDate: LocalDate,

    @Column(nullable = false)
    val volume: Long,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // 가격 변동 비율 (%)
    fun getPriceChangeRate(): Double {
        if (previousPrice == 0.0) return 0.0
        return ((price - previousPrice) / previousPrice) * 100.0
    }

    // 가격 변동 (절대값)
    fun getPriceChange(): Double {
        return price - previousPrice
    }
}