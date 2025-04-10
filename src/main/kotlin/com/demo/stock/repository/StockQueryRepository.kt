package com.demo.stock.repository

import com.demo.stock.entity.QStockPrice
import com.demo.stock.entity.QStockStatistics
import com.demo.stock.entity.Stock
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class StockQueryRepository(private val queryFactory: JPAQueryFactory) {

    private val stock = com.demo.stock.entity.QStock.stock
    private val price = QStockPrice.stockPrice
    private val statistics = QStockStatistics.stockStatistics

    // 인기 주식 조회 (조회수 기준)
    fun findPopularStocks(pageable: Pageable): Page<Stock> {
        val query = queryFactory
            .selectFrom(stock)
            .join(stock.statistics, statistics).fetchJoin()
            .leftJoin(stock.prices, price).fetchJoin()
            .orderBy(statistics.viewCount.desc())

        val totalCount = queryFactory
            .selectFrom(stock)
            .fetch().size.toLong()

        val results = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(results, pageable, totalCount)
    }

    // 상승 주식 조회 (가격 상승률 기준)
    fun findRisingStocks(pageable: Pageable): Page<Stock> {
        val query = queryFactory
            .selectFrom(stock)
            .join(stock.prices, price).fetchJoin()
            .where(price.price.gt(price.previousPrice))
            .orderBy(
                price.price.subtract(price.previousPrice).divide(price.previousPrice).multiply(100.0).desc()
            )

        val totalCount = queryFactory
            .selectFrom(stock)
            .join(stock.prices, price)
            .where(price.price.gt(price.previousPrice))
            .fetch().size.toLong()

        val results = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(results, pageable, totalCount)
    }

    // 하락 주식 조회 (가격 하락률 기준)
    fun findFallingStocks(pageable: Pageable): Page<Stock> {
        val query = queryFactory
            .selectFrom(stock)
            .join(stock.prices, price).fetchJoin()
            .where(price.price.lt(price.previousPrice))
            .orderBy(
                price.previousPrice.subtract(price.price).divide(price.previousPrice).multiply(100.0).desc()
            )

        val totalCount = queryFactory
            .selectFrom(stock)
            .join(stock.prices, price)
            .where(price.price.lt(price.previousPrice))
            .fetch().size.toLong()

        val results = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(results, pageable, totalCount)
    }

    // 거래량 많은 주식 조회
    fun findHighVolumeStocks(pageable: Pageable): Page<Stock> {
        val query = queryFactory
            .selectFrom(stock)
            .join(stock.prices, price).fetchJoin()
            .orderBy(price.volume.desc())

        val totalCount = queryFactory
            .selectFrom(stock)
            .fetch().size.toLong()

        val results = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(results, pageable, totalCount)
    }
}