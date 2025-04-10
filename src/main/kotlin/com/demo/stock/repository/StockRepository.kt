package com.demo.stock.repository

import com.demo.stock.entity.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StockRepository : JpaRepository<Stock, Long> {
    fun findByTicker(ticker: String): Optional<Stock>
}