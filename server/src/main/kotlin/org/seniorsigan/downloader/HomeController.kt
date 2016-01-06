package org.seniorsigan.downloader

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.servlet.http.HttpServletResponse

@Controller
class HomeController
@Autowired constructor(
    val service: Service
) {
    @RequestMapping(value = "/", method = arrayOf(RequestMethod.GET))
    fun home() = "index"

    @RequestMapping(value = "/vk/{id}", method = arrayOf(RequestMethod.GET))
    fun downloadVkAudio(
        response: HttpServletResponse,
        @PathVariable("id") id: String
    ) {
        println(id)
        try {
            response.contentType = "application/zip"
            response.setHeader("Content-Disposition", "attachment; filename=downloader-$id.zip")
            service.saveAudioToZip(id, response.outputStream)
            response.flushBuffer()
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
