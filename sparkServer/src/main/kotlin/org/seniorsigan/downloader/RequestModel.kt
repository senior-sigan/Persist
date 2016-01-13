package org.seniorsigan.downloader

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.sql2o.Sql2o

class RequestModel(
    private val sql: Sql2o,
    private val gson: Gson
) {
    val LOG = LoggerFactory.getLogger(RequestModel::class.java)

    fun create(meta: Map<String, Any>) {
        val conn = sql.open()
        try {
            val json = gson.toJson(meta)
            LOG.info("Saving $json in DB")
            conn.createQuery("INSERT INTO request(meta) VALUES(:meta::jsonb)")
                .addParameter("meta", json)
                .executeUpdate()
        } finally {
            conn.close()
        }
    }
}
