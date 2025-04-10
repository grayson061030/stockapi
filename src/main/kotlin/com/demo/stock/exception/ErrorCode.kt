package com.demo.stock.exception

enum class ErrorCode(val code: String, val message: String) {
    // 400 BAD_REQUEST
    INVALID_REQUEST("E400", "유효하지 않은 요청입니다."),
    INVALID_TAG("E401", "존재하지 않는 태그입니다."),

    // 404 NOT_FOUND
    STOCK_NOT_FOUND("E404", "존재하지 않는 주식입니다."),
    RESOURCE_NOT_FOUND("E405", "요청한 리소스를 찾을 수 없습니다."),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR("E500", "서버 내부 오류가 발생했습니다."),
    DATA_ACCESS_ERROR("E501", "데이터 액세스 오류가 발생했습니다.")
}