package org.seniorsigan.downloader.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
open class DatabaseConfig {
    @Autowired
    lateinit public var dataSource: DataSource

    @Bean(initMethod = "migrate")
    open fun flyway(): Flyway {
        val flyway = Flyway()
        flyway.dataSource = dataSource
        return flyway
    }
}
