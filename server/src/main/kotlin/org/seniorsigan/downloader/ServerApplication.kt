package org.seniorsigan.downloader

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class ServerApplication {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(ServerApplication::class.java, *args)
        }
    }
}
