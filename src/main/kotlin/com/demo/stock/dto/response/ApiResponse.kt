package com.demo.stock.dto.response

import org.springframework.data.domain.Page

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorResponse? = null,
    val pagination: PaginationResponse? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data
            )
        }

        fun <T> success(data: T, page: Page<*>): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data,
                pagination = PaginationResponse(
                    page = page.number,
                    size = page.size,
                    totalElements = page.totalElements,
                    totalPages = page.totalPages,
                    hasNext = page.hasNext(),
                    hasPrevious = page.hasPrevious()
                )
            )
        }

        fun <T> error(code: String, message: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                error = ErrorResponse(code, message)
            )
        }
    }
}

data class PaginationResponse(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)