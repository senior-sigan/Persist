package org.seniorsigan.downloader

object Configs {
    private val pb = ProcessBuilder()

    val port: Int = try {
        pb.environment()["PORT"]?.toInt()
    } catch(e: Exception) {
        null
    } ?: 4567

    val jdbcURL = pb.environment()["SPRING_DB_URL"]
        ?: "jdbc:postgresql://localhost:5432/persist_dev"

    val dbUser = pb.environment()["SPRING_DB_USERNAME"]
        ?: "persist"

    val dbPassword = pb.environment()["SPRING_DB_PASSWORD"]
        ?: "persistpwd"
}
