package com.task.platform

import com.task.platform.application.DiscountApplicationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication


@SpringBootApplication
@EnableConfigurationProperties(value = [DiscountApplicationProperties::class])
class PlatformApplication

fun main(args: Array<String>) {
    runApplication<PlatformApplication>(*args)
}
