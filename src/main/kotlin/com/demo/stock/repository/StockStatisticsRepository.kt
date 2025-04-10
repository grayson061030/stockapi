package com.demo.stock.repository

import com.demo.stock.entity.StockStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockStatisticsRepository : JpaRepository<StockStatistics, Long> {
}