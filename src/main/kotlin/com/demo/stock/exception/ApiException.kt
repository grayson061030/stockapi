package com.demo.stock.exception

import org.springframework.http.HttpStatus

open class ApiException(
    val errorCode: ErrorCode,
    val httpStatus: HttpStatus,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)

// 400 Bad Request
class BadRequestException(
    errorCode: ErrorCode = ErrorCode.INVALID_REQUEST,
    message: String = errorCode.message,
    cause: Throwable? = null
) : ApiException(errorCode, HttpStatus.BAD_REQUEST, message, cause)

// 404 Not Found
class NotFoundException(
    errorCode: ErrorCode = ErrorCode.RESOURCE_NOT_FOUND,
    message: String = errorCode.message,
    cause: Throwable? = null
) : ApiException(errorCode, HttpStatus.NOT_FOUND, message, cause)

// 500 Internal Server Error
class InternalServerException(
    errorCode: ErrorCode = ErrorCode.INTERNAL_SERVER_ERROR,
    message: String = errorCode.message,
    cause: Throwable? = null
) : ApiException(errorCode, HttpStatus.INTERNAL_SERVER_ERROR, message, cause)