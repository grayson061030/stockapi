package com.demo.stock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class StockApiApplication

fun main(args: Array<String>) {
	runApplication<StockApiApplication>(*args)
}
