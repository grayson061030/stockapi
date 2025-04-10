package com.demo.stock.repository

import com.demo.stock.entity.StockPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockPriceRepository : JpaRepository<StockPrice, Long>