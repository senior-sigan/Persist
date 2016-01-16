package org.seniorsigan.downloader

import com.google.gson.GsonBuilder
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import org.sql2o.Sql2o
import spark.ModelAndView
import spark.Spark.*
import spark.template.jade.JadeTemplateEngine

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        val LOG = LoggerFactory.getLogger(Application::class.java)
        LOG.info("Start Spark server on ${Configs.port} port")

        val service = Service()
        val gson = GsonBuilder().create()
        val sql2o = Sql2o(Configs.jdbcURL, Configs.dbUser, Configs.dbPassword)
        val flyway = Flyway()
        flyway.dataSource = sql2o.dataSource
        flyway.migrate()

        val jade = JadeTemplateEngine()
        val jadeConfig = jade.configuration()
        jadeConfig.isPrettyPrint = true
        jadeConfig.isCaching = true

        val model = RequestModel(sql2o, gson)

        port(Configs.port)
        staticFileLocation("static")

        get("/", { req, res ->
            ModelAndView(emptyMap<String, Any>(), "index")
        }, jade)

        get("/vk/:id", { req, res ->
            val id = req.params(":id")
            LOG.info(id)
            try {
                res.type("application/zip")
                res.header("Content-Disposition", "attachment; filename=downloader$id.zip")
                val collection = service.saveAudioToZip(id, res.raw().outputStream)
                model.create(mapOf(
                    "ip" to req.ip(),
                    "host" to req.host(),
                    "id" to id,
                    "url" to collection.url,
                    "provider" to "vk",
                    "audios" to collection.audios,
                    "cover" to (collection.coverUrl ?: "")
                ))
                res.raw().flushBuffer()
            } catch (e: Exception) {
                LOG.error(e.message, e)
                if (!res.raw().isCommitted) {
                    res.raw().reset()
                    res.body(e.message)
                    res.status(404)
                }
            }
        })

        get("/requests.json", { req, res ->
            val models = model.getAllRequests()
            gson.toJson(models.distinctBy { it.url })
        })
    }
}
