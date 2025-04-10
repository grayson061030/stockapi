package com.demo.stock.dto.request

import com.demo.stock.config.TagType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class StockSearchRequest(
    @field:NotNull
    val tag: TagType = TagType.POPULAR,

    @field:Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    val page: Int = 0,

    @field:Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    val size: Int = 10
)