package com.demo.stock.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "stock_statistics")
class StockStatistics(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistics_id")
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    var stock: Stock? = null,

    @Column(nullable = false)
    var viewCount: Long = 0,

    @Column(nullable = false)
    var buyOrderVolume: Long = 0,

    @Column(nullable = false)
    var sellOrderVolume: Long = 0,

    @Column(nullable = false)
    var turnoverRate: Double = 0.0,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // 조회수 증가
    fun incrementViewCount() {
        viewCount++
        updatedAt = LocalDateTime.now()
    }

    // 매수 잔량 업데이트
    fun updateBuyOrderVolume(volume: Long) {
        buyOrderVolume = volume
        updatedAt = LocalDateTime.now()
    }

    // 매도 잔량 업데이트
    fun updateSellOrderVolume(volume: Long) {
        sellOrderVolume = volume
        updatedAt = LocalDateTime.now()
    }

    // 잔량 차이
    fun getOrderVolumeDifference(): Long {
        return buyOrderVolume - sellOrderVolume
    }
}