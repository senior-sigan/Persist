package org.seniorsigan.downloader

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

@Controller
class HomeController
@Autowired constructor(
    val service: Service
) {
    @ResponseBody
    @RequestMapping(value = "/", method = arrayOf(RequestMethod.GET))
    fun home(): String {
        return "Hello"
    }

    @RequestMapping(value = "/vk/{id}", method = arrayOf(RequestMethod.GET))
    fun downloadVkAudio(
        response: HttpServletResponse,
        @PathVariable("id") id: String
    ) {
        println(id)
        response.contentType = "application/zip"
        try {
            response.setHeader("Content-Disposition", "attachment; filename=downloader-$id.zip")
            service.saveAudioToZip(id, response.outputStream)
            response.flushBuffer()
        } catch (e: Exception) {
            println(e.message)
            response.status = 404
        }
    }
}
