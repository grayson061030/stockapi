package com.demo.stock.service

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service

/**
 * data.sql 파일을 통해 데이터 초기화를 수행하는 서비스
 */
@Service
class StockDataInitService : CommandLineRunner {

    private val log = LoggerFactory.getLogger(StockDataInitService::class.java)

    override fun run(vararg args: String?) {
        log.info("애플리케이션이 시작되었습니다. 데이터 초기화는 data.sql을 통해 수행됩니다.")
    }
}