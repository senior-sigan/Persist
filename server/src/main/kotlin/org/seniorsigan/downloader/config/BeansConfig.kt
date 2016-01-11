package org.seniorsigan.downloader.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.seniorsigan.downloader.Service
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BeansConfig {
    @Bean
    open fun service(): Service {
        return Service()
    }

    @Bean
    open fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}
