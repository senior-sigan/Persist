package org.seniorsigan.downloader

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class RequestServise
@Autowired constructor(
    private val jdbcTemplate: JdbcTemplate,
    private val objectMapper: ObjectMapper
) {
    companion object {
        val LOG = LoggerFactory.getLogger(RequestServise::class.java)
    }

    fun save(data: Any) {
        try {
            val meta = objectMapper.writeValueAsString(data)
            LOG.info("Save to db: $meta")
            jdbcTemplate.update(
                "INSERT INTO request(meta) VALUES(?::jsonb)", meta)
        } catch(e: Exception) {
            LOG.error("Can't save to db: ${e.message}", e)
        }
    }
}
