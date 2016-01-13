package org.seniorsigan.downloader

import com.google.api.client.extensions.appengine.http.UrlFetchTransport
import java.util.logging.Logger
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class VkDownloadServlet: HttpServlet() {
    val LOG = Logger.getLogger("VkDownloadServlet")
    val service = Service(NetworkService(UrlFetchTransport()))
    val requestRepository = RequestRepository()

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id") ?: ""
        LOG.info(id)
        val os = resp.outputStream
        resp.contentType = "application/zip"
        resp.setHeader("Content-Disposition", "attachment; filename=downloader-$id.zip")
        try {
            val audios = service.saveAudioToZip(id, os)
            resp.flushBuffer()
            requestRepository.save(mapOf(
                "ip" to req.remoteAddr,
                "host" to req.remoteHost,
                "url" to "https://vk.com/wall$id",
                "provider" to "vk",
                "audios" to audios
            ))
        } catch (e: Exception) {
            LOG.severe(e.message)
            if (!resp.isCommitted) {
                resp.reset()
                os.print(e.message)
                resp.setStatus(404)
            }
        }
    }
}
