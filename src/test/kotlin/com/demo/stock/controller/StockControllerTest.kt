package com.demo.stock.controller

import com.demo.stock.config.TagType
import com.demo.stock.dto.response.PriceHistoryResponse
import com.demo.stock.dto.response.StockDetailResponse
import com.demo.stock.dto.response.StockListResponse
import com.demo.stock.dto.response.StockResponse
import com.demo.stock.exception.BadRequestException
import com.demo.stock.exception.ErrorCode
import com.demo.stock.exception.NotFoundException
import com.demo.stock.service.StockService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(StockController::class)
class StockControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var stockService: StockService

    @Test
    fun `태그별 주식 목록 조회 성공 시 주식 목록을 반환한다`() {
        val tag = TagType.POPULAR
        val page = 0
        val size = 10

        val appleTicker = "A00001"
        val msTicker = "M0002"

        val now = LocalDateTime.now()
        val stockResponses = listOf(
            StockResponse(
                id = 1L,
                ticker = appleTicker,
                name = "애플",
                price = 150.0,
                previousPrice = 147.5,
                priceChange = 2.5,
                priceChangeRate = 1.69,
                volume = 10000000,
                viewCount = 1000,
                buyOrderVolume = 5000000,
                sellOrderVolume = 4500000,
                orderVolumeDifference = 500000,
                turnoverRate = 0.15,
                lastUpdated = now
            ),
            StockResponse(
                id = 2L,
                ticker = msTicker,
                name = "마이크로소프트",
                price = 300.0,
                previousPrice = 295.0,
                priceChange = 5.0,
                priceChangeRate = 1.69,
                volume = 8000000,
                viewCount = 950,
                buyOrderVolume = 4000000,
                sellOrderVolume = 3800000,
                orderVolumeDifference = 200000,
                turnoverRate = 0.12,
                lastUpdated = now
            )
        )

        val response = StockListResponse(stockResponses, tag)
        val pageable = PageRequest.of(page, size)
        val stockPage = PageImpl(stockResponses, pageable, stockResponses.size.toLong())

        doReturn(Pair(response, stockPage)).`when`(stockService).getStocksByTag(tag, page, size)

        mockMvc.perform(
            get("/stocks")
                .param("tag", tag.name)
                .param("page", page.toString())
                .param("size", size.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.stocks").isArray)
            .andExpect(jsonPath("$.data.stocks.length()").value(2))
            .andExpect(jsonPath("$.data.stocks[0].ticker").value(appleTicker))
            .andExpect(jsonPath("$.data.stocks[1].ticker").value(msTicker))
            .andExpect(jsonPath("$.data.tag").value(tag.name))
            .andExpect(jsonPath("$.pagination.page").value(page))
            .andExpect(jsonPath("$.pagination.size").value(size))
            .andExpect(jsonPath("$.pagination.totalElements").value(2))
    }

    @Test
    fun `유효하지 않은 태그로 조회 시 400 에러를 반환한다`() {

        val tag = TagType.POPULAR
        val page = 0
        val size = 10

        `when`(stockService.getStocksByTag(tag, page, size))
            .thenThrow(BadRequestException(ErrorCode.INVALID_TAG, "지원하지 않는 태그입니다: ${tag.name}"))

        mockMvc.perform(
            get("/stocks")
                .param("tag", tag.name)
                .param("page", page.toString())
                .param("size", size.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_TAG.code))
            .andExpect(jsonPath("$.error.message").value("지원하지 않는 태그입니다: ${tag.name}"))
    }

    @Test
    fun `요청 파라미터 검증 실패 시 400 에러를 반환한다`() {
        mockMvc.perform(
            get("/stocks")
                .param("tag", "INVALID_TAG")  // 유효하지 않은 태그
                .param("page", "-1")          // 유효하지 않은 페이지 번호
                .param("size", "0")           // 유효하지 않은 사이즈
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_REQUEST.code))
    }

    @Test
    fun `주식 상세 정보 조회 성공 시 주식 상세 정보를 반환한다`() {
        val ticker = "A00001"
        val now = LocalDateTime.now()
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val stockResponse = StockResponse(
            id = 1L,
            ticker = ticker,
            name = "애플",
            price = 150.0,
            previousPrice = 147.5,
            priceChange = 2.5,
            priceChangeRate = 1.69,
            volume = 10000000,
            viewCount = 1000,
            buyOrderVolume = 5000000,
            sellOrderVolume = 4500000,
            orderVolumeDifference = 500000,
            turnoverRate = 0.15,
            lastUpdated = now
        )

        val priceHistoryResponses = listOf(
            PriceHistoryResponse(
                date = today,
                price = 150.0,
                previousPrice = 147.5,
                priceChange = 2.5,
                priceChangeRate = 1.69,
                volume = 10000000
            ),
            PriceHistoryResponse(
                date = yesterday,
                price = 147.5,
                previousPrice = 145.0,
                priceChange = 2.5,
                priceChangeRate = 1.72,
                volume = 9000000
            )
        )

        val response = StockDetailResponse(stockResponse, priceHistoryResponses)

        doReturn(response).`when`(stockService).getStockDetail(ticker)

        mockMvc.perform(
            get("/stocks/$ticker")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.stock.ticker").value(ticker))
            .andExpect(jsonPath("$.data.stock.name").value("애플"))
            .andExpect(jsonPath("$.data.priceHistory").isArray)
            .andExpect(jsonPath("$.data.priceHistory.length()").value(2))
    }

    @Test
    fun `존재하지 않는 주식 조회 시 404 에러를 반환한다`() {
        val ticker = "UNKNOWN"

        `when`(stockService.getStockDetail(ticker))
            .thenThrow(NotFoundException(ErrorCode.STOCK_NOT_FOUND, "ID가 ${ticker} 주식을 찾을 수 없습니다"))

        mockMvc.perform(
            get("/stocks/$ticker")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value(ErrorCode.STOCK_NOT_FOUND.code))
            .andExpect(jsonPath("$.error.message").value("ID가 ${ticker} 주식을 찾을 수 없습니다"))
    }

    @Test
    fun `내부 서버 오류 발생 시 500 에러를 반환한다`() {
        val ticker = "A0001"

        `when`(stockService.getStockDetail(ticker))
            .thenThrow(RuntimeException("예상치 못한 오류 발생"))

        mockMvc.perform(
            get("/stocks/$ticker")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value(ErrorCode.INTERNAL_SERVER_ERROR.code))
            .andExpect(jsonPath("$.error.message").value(ErrorCode.INTERNAL_SERVER_ERROR.message))
    }
}