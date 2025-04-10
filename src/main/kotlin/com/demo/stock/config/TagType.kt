package com.demo.stock.config

/**
 * 주식 태그 유형
 */
enum class TagType {
    POPULAR,
    RISING,
    FALLING,
    VOLUME,
    UNKNOWN,
    ;

    companion object {
        fun findByTagType(tagName: String): TagType? {
            return TagType.valueOf(tagName.toUpperCase())
        }
    }
}