package com.demo.stock.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "stocks")
class Stock(
    @Id
    @Column(name = "stock_id")
    val id: Long,

    @Column(nullable = false)
    val ticker: String,

    @Column(nullable = false)
    val name: String,

    @OneToMany(mappedBy = "stock", cascade = [CascadeType.ALL], orphanRemoval = true)
    val prices: MutableList<StockPrice> = mutableListOf(),

    @OneToOne(mappedBy = "stock", cascade = [CascadeType.ALL], orphanRemoval = true)
    var statistics: StockStatistics? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun addPrice(price: StockPrice) {
        prices.add(price)
        price.stock = this
    }

    fun initStatistics(stats: StockStatistics) {
        statistics = stats
        stats.stock = this
    }

    fun updateTimestamp() {
        updatedAt = LocalDateTime.now()
    }
}