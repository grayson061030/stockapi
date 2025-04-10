package com.demo.stock.controller

import com.demo.stock.dto.request.StockSearchRequest
import com.demo.stock.dto.response.ApiResponse
import com.demo.stock.dto.response.StockDetailResponse
import com.demo.stock.dto.response.StockListResponse
import com.demo.stock.service.StockService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stocks")
class StockController(private val stockService: StockService) {

    @GetMapping
    fun getStocksByTag(@Valid stockSearchRequest: StockSearchRequest): ResponseEntity<ApiResponse<StockListResponse>> {
        val (result, page) = stockService.getStocksByTag(
            stockSearchRequest.tag,
            stockSearchRequest.page,
            stockSearchRequest.size
        )

        return ResponseEntity.ok(ApiResponse.success(result, page))
    }

    @GetMapping("/{ticker}")
    fun getStockDetail(
        @PathVariable ticker: String
    ): ResponseEntity<ApiResponse<StockDetailResponse>> {
        val result = stockService.getStockDetail(ticker)
        return ResponseEntity.ok(ApiResponse.success(result))
    }
}