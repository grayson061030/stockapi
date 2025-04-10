package com.demo.stock.dto.request

import com.demo.stock.config.TagType
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

/**
 * 주식 시뮬레이션 요청 DTO
 */
data class StockSimulationRequest(
    @field:NotNull(message = "태그 유형은 필수입니다")
    val tagType: TagType,

    @field:Min(1, message = "업데이트할 종목 수는 1 이상이어야 합니다")
    @field:Max(50, message = "업데이트할 종목 수는 50 이하여야 합니다")
    val count: Int = 10,

    @field:Min(1, message = "최소 변동률은 1% 이상이어야 합니다")
    @field:Max(100, message = "최소 변동률은 100% 이하여야 합니다")
    val minRate: Int = 5,

    @field:Min(1, message = "최대 변동률은 1% 이상이어야 합니다")
    @field:Max(100, message = "최대 변동률은 100% 이하여야 합니다")
    val maxRate: Int = 20
) {
    init {
        require(maxRate >= minRate) { "최대 변동률은 최소 변동률보다 크거나 같아야 합니다" }
    }
}