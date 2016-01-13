package org.seniorsigan.downloader

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class HomeController
@Autowired constructor(
    val service: Service,
    val requestRepository: RequestRepository
) {
    @RequestMapping(value = "/", method = arrayOf(RequestMethod.GET))
    fun home() = "index"

    @RequestMapping(value = "/vk/{id}", method = arrayOf(RequestMethod.GET))
    fun downloadVkAudio(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @PathVariable("id") id: String
    ) {
        println(id)
        try {
            response.contentType = "application/zip"
            response.setHeader("Content-Disposition", "attachment; filename=downloader-$id.zip")
            val audios = service.saveAudioToZip(id, response.outputStream)
            response.flushBuffer()
            requestRepository.save(mapOf(
                "ip" to request.remoteAddr,
                "host" to request.remoteHost,
                "url" to "https://vk.com/wall$id",
                "provider" to "vk",
                "audios" to audios
            ))
        } catch (e: Exception) {
            println(e.message)
            if (!response.isCommitted) {
                response.reset()
                response.writer?.print(e.message)
                response.status = 404
            }
        }
    }
}
