package com.demo.stock.util

import org.springframework.stereotype.Component
import kotlin.random.Random

/**
 * 테스트를 위한 랜덤 데이터 생성기
 */
@Component
class RandomDataGenerator {
    /**
     * 지정된 범위 내에서 랜덤 비율 생성
     *
     * @param minRate 최소 변동률(%)
     * @param maxRate 최대 변동률(%)
     * @return 지정된 범위 내의 랜덤 변동률
     */
    fun generateRateInRange(minRate: Int, maxRate: Int): Double {
        return minRate + (maxRate - minRate) * Random.nextDouble()
    }

    // 랜덤 가격 변동률 생성 (-5% ~ +5%)
    fun generatePriceChangeRate(): Double {
        return Random.nextDouble(-5.0, 5.0)
    }

    // 랜덤 거래량 생성 (기존 거래량 기준 +/- 20%)
    fun generateVolume(baseVolume: Long): Long {
        val changeRate = 1.0 + Random.nextDouble(-0.2, 0.2)
        return (baseVolume * changeRate).toLong().coerceAtLeast(1)
    }

    // 랜덤 조회수 증가량 생성 (0 ~ 100)
    fun generateViewCountIncrease(): Long {
        return Random.nextLong(0, 100)
    }

    // 랜덤 매수/매도 잔량 생성 (10000 ~ 100000)
    fun generateOrderVolume(): Long {
        return Random.nextLong(10000, 100000)
    }

    // 랜덤 회전율 생성 (0.5% ~ 10%)
    fun generateTurnoverRate(): Double {
        return Random.nextDouble(0.5, 10.0)
    }
}